package uni.aznu.process;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import uni.aznu.process.model.BookProcessRequest;
import uni.aznu.process.model.BookingInfo;

import java.math.BigDecimal;

import static org.apache.camel.model.rest.RestParamType.body;

public class BookingProcessService extends RouteBuilder {

    @Autowired
    private BookingIdentifierService bookingIdentifierService;

    @Override
    public void configure() throws Exception {
        gateway();
        equipment();
    }

    private void gateway(){
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .contextPath("api")
                // turn on swagger api-doc
                .apiContextPath("api-doc")
                .apiProperty("api.title", "Micro Equipment booking API")
                .apiProperty("api.version", "1.0.0");

        rest("/process").description("Micro booking process REST service")
                .consumes("application/json")
                .produces("application/json")
                .post("/booking").description("Book an equipment").type(BookProcessRequest.class).outType(BookingInfo.class)
                .param().name("body").type(body).description("The equipment to book").endParam()
                .responseMessage().code(200).message("Equipment successfully booked").endResponseMessage()
                .to("direct:bookProcess");

        from("direct:bookProcess").routeId("bookProcess")
                .log("bookProcess fired")
                .process((exchange) -> exchange.getMessage().setHeader("bookingId",
                        bookingIdentifierService.getBookingIdentifier()))
                .to("direct:ProcessBookRequest")
                .to("direct:bookRequester");

        from("direct:bookRequester").routeId("bookRequester")
                .log("bookRequester fired")
                .process(
                        (exchange) -> exchange.getMessage().setBody(Utils.prepareBookingInfo(
                                exchange.getMessage().getHeader("bookingId", String.class), null))
                );

        from("direct:ProcessBookRequest").routeId("ProcessBookRequest")
                .log("brokerTopic fired")
                .marshal().json()
                .to("kafka:ProcessReqTopic?brokers=localhost:9092");
    }

    private void equipment(){
        from("kafka:ProcessReqTopic?brokers=localhost:9092").routeId("bookEquipment")
                .log("fired bookEquipment")
                .unmarshal().json(JsonLibrary.Jackson, BookProcessRequest.class)
                .process(
                        (exchange) -> {
                            BookingInfo bookingInfo = new BookingInfo();
                            bookingInfo.setId(bookingIdentifierService.getBookingIdentifier());
                            BookProcessRequest request = exchange.getMessage().getBody(BookProcessRequest.class);
                            //TODO: logic
                            exchange.getMessage().setBody(bookingInfo);
                        }
                )
                .marshal().json()
                .to("stream:out")
                .setHeader("serviceType", constant("equipment"))
                .to("kafka:BookingInfoTopic?brokers=localhost:9092");
    }
}
