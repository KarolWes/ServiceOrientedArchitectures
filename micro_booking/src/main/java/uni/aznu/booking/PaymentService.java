package uni.aznu.booking;


import java.util.HashMap;

import javax.annotation.PostConstruct;

import uni.aznu.booking.model.BookProcessRequest;
import uni.aznu.booking.model.BookingInfo;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
	private HashMap<String, PaymentData> payments;
	
	@PostConstruct
	void init() {
		payments=new HashMap<>();
	}
	
	public static class PaymentData {
		BookProcessRequest bookProcessRequest;
		BookingInfo visitBookingInfo;
		BookingInfo equipmentBookingInfo;
		public boolean isReady() {
			return bookProcessRequest !=null && visitBookingInfo !=null && equipmentBookingInfo !=null;
		}
	}
	
	public synchronized boolean addBookRequest(String bookId, BookProcessRequest bookProcessRequest) {
		PaymentData paymentData = getPaymentData(bookId);
		paymentData.bookProcessRequest = bookProcessRequest;
		return paymentData.isReady();
	}
	

	public synchronized boolean addBookingInfo(String bookId, BookingInfo bookingInfo, String serviceType) {
		PaymentData paymentData = getPaymentData(bookId);
		if (serviceType.equals("equipment"))
			paymentData.equipmentBookingInfo = bookingInfo;
		else 
			paymentData.visitBookingInfo = bookingInfo;
		return paymentData.isReady();
	}	
	
	
	public synchronized PaymentData getPaymentData(String bookId) {
		PaymentData paymentData = payments.get(bookId);
		if (paymentData==null) {
			paymentData = new PaymentData();
			payments.put(bookId, paymentData);
		}
		return paymentData;
	}

	


	

}
