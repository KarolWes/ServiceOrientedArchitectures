package uni.aznu.booking;

import static org.apache.camel.model.rest.RestParamType.body;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import uni.aznu.booking.exceptions.EquipmentException;
import uni.aznu.booking.exceptions.VisitException;
import uni.aznu.booking.model.*;
import uni.aznu.booking.state.ProcessingEvent;
import uni.aznu.booking.state.ProcessingState;
import uni.aznu.booking.state.StateService;
import org.springframework.stereotype.Component;


@Component
public class BookingService extends RouteBuilder {
	
	@Autowired
	BookingIdentifierService bookingIdentifierService;
	
	@Autowired
	PaymentService paymentService;		

	@Autowired
	StateService equipmentStateService;
	
	@Autowired
	StateService visitStateService;

	@Override
	public void configure() throws Exception {
		bookVisitExceptionHandlers();
		bookEquipmentExceptionHandlers();
		gateway();
		visit();
		equipment();
		payment();
	}
	
	private void bookVisitExceptionHandlers() {
		onException(VisitException.class)
		.process((exchange) -> {
					ExceptionResponse er = new ExceptionResponse();
					er.setTimestamp(OffsetDateTime.now());
					Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
					er.setMessage(cause.getMessage());
					exchange.getMessage().setBody(er);
				}
				)
        .marshal().json()
		.to("stream:out")
		.setHeader("serviceType", constant("visit"))
		.to("kafka:lBookingFailTopic?brokers=localhost:9092")
		.handled(true)
		;	
	}	

	private void bookEquipmentExceptionHandlers() {
		onException(EquipmentException.class)
		.process((exchange) -> {
					ExceptionResponse er = new ExceptionResponse();
					er.setTimestamp(OffsetDateTime.now());
					Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
					er.setMessage(cause.getMessage());
					exchange.getMessage().setBody(er);
				}
				)
	    .marshal().json()
		.to("stream:out")
		.setHeader("serviceType", constant("equipment"))
		.to("kafka:BookingFailTopic?brokers=localhost:9092")
		.handled(true)
		;	
	}
	
	private void gateway() {
        restConfiguration()
        .component("servlet")
        .bindingMode(RestBindingMode.json)
        .dataFormatProperty("prettyPrint", "true")
        .enableCORS(true)
        .contextPath("/api")
        // turn on swagger api-doc
        .apiContextPath("/api-doc")
        .apiProperty("api.title", "Micro booking API")
        .apiProperty("api.version", "1.0.0");
        
		rest("/equipment").description("Micro booking REST service")
		.consumes("application/json")
		.produces("application/json")        
		.post("/booking").description("Book a travel").type(BookingRequest.class).outType(BookingInfo.class)
		.param().name("body").type(body).description("The equipment to book").endParam()
		.responseMessage().code(200).message("Equipment successfully booked").endResponseMessage()
		.to("direct:bookEquipment")
		;
		
		from("direct:bookEquipment").routeId("bookEquipment")
		.log("bookEquipment fired")
		.process((exchange) -> {
				exchange.getMessage().setHeader("bookingId", bookingIdentifierService.getBookingIdentifier());
		})
		.to("direct:EquipmentBookRequest")
		.to("direct:bookRequester");

		from("direct:bookRequester").routeId("bookRequester")
		.log("bookRequester fired")
		.process(
			(exchange) -> {
				exchange.getMessage().setBody(Utils.prepareBookingInfo(
					exchange.getMessage().getHeader("bookingId", String.class), null));
				}
				)
		;   

		from("direct:EquipmentBookRequest").routeId("EquipmentBookRequest")
		.log("brokerTopic fired")
		.marshal().json()
		.to("kafka:EquipmentReqTopic?brokers=localhost:9092")
		;

	}
	
	private void visit() {
		//TODO: logic here
		from("kafka:EquipmentReqTopic?brokers=localhost:9092").routeId("bookVisit")
		.log("fired bookVisit")			
		.unmarshal().json(JsonLibrary.Jackson, BookingRequest.class)
		.process(
				(exchange) -> {
					String bookingTravelId = exchange.getMessage().getHeader("bookingTravelId", String.class);
					ProcessingState previousState = visitStateService.sendEvent(bookingTravelId, ProcessingEvent.START);
					if (previousState!=ProcessingState.CANCELLED) {        			
		    			BookingInfo bi = new BookingInfo();
		    			bi.setId(bookingIdentifierService.getBookingIdentifier());
		
		    			BookingRequest request= exchange.getMessage().getBody(BookingRequest.class);
		    			if (request!=null && request.getVisit()!=null 
		    					&& request.getVisit().getVType() != null ) {
		    				String vType = request.getVisit().getVType();
		    				if (vType.equals("return")) {

		    					bi.setCost(new BigDecimal(999));
		    				}
		    				else if (vType.equals("borrow")){
								bi.setCost(new BigDecimal(0));
		    				}	        				
		    				else {
								throw new VisitException("Type of visit is not serviced: "+vType);
		    				}
		    			}        				
		    			exchange.getMessage().setBody(bi);
		    			previousState = visitStateService.sendEvent(bookingTravelId, ProcessingEvent.FINISH);
		    			}
					exchange.getMessage().setHeader("previousState", previousState);        		}
				)
		.marshal().json()
		.to("stream:out")
		.choice()
			.when(header("previousState").isEqualTo(ProcessingState.CANCELLED))
			.to("direct:bookVisitCompensationAction")
		.otherwise()
			.setHeader("serviceType", constant("visit"))
			.to("kafka:BookingInfoTopic?brokers=localhost:9092")
		.endChoice()		
		;

		from("kafka:BookingFailTopic?brokers=localhost:9092").routeId("bookVisitCompensation")
		.log("fired bookVisitCompensation")	
		.unmarshal().json(JsonLibrary.Jackson, ExceptionResponse.class)	
		.choice()
			.when(header("serviceType").isNotEqualTo("visit"))
		    .process((exchange) -> {
				String bookingId = exchange.getMessage().getHeader("bookingId", String.class);
				ProcessingState previousState = visitStateService.sendEvent(bookingId, ProcessingEvent.CANCEL);
				exchange.getMessage().setHeader("previousState", previousState);
		    })
		    .choice()
		    	.when(header("previousState").isEqualTo(ProcessingState.FINISHED))
				.to("direct:bookVisitCompensationAction")
			.endChoice()            
		 .endChoice();

		from("direct:bookVisitCompensationAction").routeId("bookVisitCompensationAction")
		.log("fired bookVisitCompensationAction")
		.to("stream:out");			
	}
	
	private void equipment() {
		//TODO: logic here
		from("kafka:EquipmentReqTopic?brokers=localhost:9092").routeId("bookEquipment")
		.log("fired bookEquipment")	
		.unmarshal().json(JsonLibrary.Jackson, BookingRequest.class)
		.process(
				(exchange) -> {
					String bookingId = exchange.getMessage().getHeader("bookingId", String.class);
					ProcessingState previousState = equipmentStateService.sendEvent(bookingId, ProcessingEvent.START);
					if (previousState!=ProcessingState.CANCELLED) {
		    			BookingInfo bi = new BookingInfo();
		    			bi.setId(bookingIdentifierService.getBookingIdentifier());
		    			BookingRequest request = exchange.getMessage().getBody(BookingRequest.class);
		    			if (request!=null && request.getEquipment()!=null && request.getEquipment().getState()!=null) {
		    				String state=request.getEquipment().getState();
		    				if (state.equals("Borrowed")){
		    					throw new EquipmentException("This is currently borrowed.");
		    				}
		    				else if (state.equals("Booked")) {
								throw new EquipmentException("This is currently booked. Check again on " + request.getEquipment().getDateFrom());
		    				}
		    				else {
		    					bi.setCost(new BigDecimal(600));        					
		    				}
		    			}
		    				
		    			exchange.getMessage().setBody(bi);
		    			previousState = equipmentStateService.sendEvent(bookingId, ProcessingEvent.FINISH);
		    			}
					exchange.getMessage().setHeader("previousState", previousState);
				}
				)		
		.marshal().json()
		.to("stream:out")
		.choice()
			.when(header("previousState").isEqualTo(ProcessingState.CANCELLED))
			.to("direct:bookEquipmentCompensationAction")
		.otherwise()
			.setHeader("serviceType", constant("equipment"))
			.to("kafka:BookingInfoTopic?brokers=localhost:9092")
		.endChoice()
		;
		
		from("kafka:TravelBookingFailTopic?brokers=localhost:9092").routeId("bookEquipmentCompensation")
		.log("fired bookEquipmentCompensation")	
		.unmarshal().json(JsonLibrary.Jackson, ExceptionResponse.class)	
        .choice()
    		.when(header("serviceType").isNotEqualTo("equipment"))
            .process((exchange) -> {
    			String bookingId = exchange.getMessage().getHeader("bookingId", String.class);
    			ProcessingState previousState = equipmentStateService.sendEvent(bookingId, ProcessingEvent.CANCEL);
    			exchange.getMessage().setHeader("previousState", previousState);
            })
            .choice()
            	.when(header("previousState").isEqualTo(ProcessingState.FINISHED))
    			.to("direct:bookEquipmentCompensationAction")
    		.endChoice()            
         .endChoice();
		
		from("direct:bookEquipmentCompensationAction").routeId("bookEquipmentCompensationAction")
		.log("fired bookEquipmentCompensationAction")
		.to("stream:out");		
	}
	
	private void payment() {
		from("kafka:BookingInfoTopic?brokers=localhost:9092").routeId("paymentBookingInfo")
		.log("fired paymentBookingInfo")
		.unmarshal().json(JsonLibrary.Jackson, BookingInfo.class)
		.process(
				(exchange) -> {
					String bookingId = exchange.getMessage().getHeader("bookingId", String.class);
					boolean isReady= paymentService.addBookingInfo(
							bookingId,
							exchange.getMessage().getBody(BookingInfo.class),
							exchange.getMessage().getHeader("serviceType", String.class));
					exchange.getMessage().setHeader("isReady", isReady);
				}
				)
		.choice()
			.when(header("isReady").isEqualTo(true)).to("direct:finalizePayment")
		    .endChoice();
		
		from("kafka:EquipmentReqTopic?brokers=localhost:9092").routeId("paymentReq")
		.log("fired paymentReq")
		.unmarshal().json(JsonLibrary.Jackson, BookingRequest.class)
		.process(
				(exchange) -> {
					String bookingId = exchange.getMessage().getHeader("bookingId", String.class);
					 boolean isReady= paymentService.addBookRequest(
							bookingId,
							exchange.getMessage().getBody(BookingRequest.class));
					 exchange.getMessage().setHeader("isReady", isReady);
				}
				)
		.choice()
			.when(header("isReady").isEqualTo(true)).to("direct:finalizePayment")
		.endChoice();
	
		from("direct:finalizePayment").routeId("finalizePayment")
		.log("fired finalizePayment")
		.process(
				(exchange) -> {
					String bookingId = exchange.getMessage().getHeader("bookingId", String.class);
					 PaymentService.PaymentData paymentData = paymentService.getPaymentData(bookingId);
					 BigDecimal visitCost=paymentData.visitBookingInfo.getCost();
					 BigDecimal equipmentCost=paymentData.equipmentBookingInfo.getCost();
					 BigDecimal totalCost=visitCost.add(equipmentCost);
					 BookingInfo travelBookingInfo = Utils.prepareBookingInfo(bookingId, totalCost);
					 exchange.getMessage().setBody(travelBookingInfo);
				}
				)
		.to("direct:notification")
		;

		from("direct:notification").routeId("notification")
		.log("fired notification")
		.to("stream:out");
		
	}

	
}
