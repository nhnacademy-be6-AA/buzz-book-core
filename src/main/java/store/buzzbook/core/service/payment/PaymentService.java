package store.buzzbook.core.service.payment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.dto.payment.ReadBillLogProjectionResponse;
import store.buzzbook.core.dto.payment.ReadBillLogsRequest;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.dto.payment.ReadPaymentLogRequest;
import store.buzzbook.core.dto.payment.ReadPaymentLogResponse;
import store.buzzbook.core.dto.payment.ReadPaymentResponse;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.entity.payment.BillLog;
import store.buzzbook.core.entity.payment.BillStatus;
import store.buzzbook.core.entity.payment.PaymentLog;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.mapper.order.OrderDetailMapper;
import store.buzzbook.core.mapper.order.OrderMapper;
import store.buzzbook.core.mapper.order.WrappingMapper;
import store.buzzbook.core.mapper.payment.BillLogMapper;
import store.buzzbook.core.mapper.payment.PaymentLogMapper;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.order.WrappingRepository;
import store.buzzbook.core.repository.payment.BillLogRepository;
import store.buzzbook.core.repository.payment.PaymentLogRepository;
import store.buzzbook.core.repository.product.ProductRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final BillLogRepository billLogRepository;
	private final PaymentLogRepository paymentLogRepository;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final ObjectMapper objectMapper;
	private final WrappingRepository wrappingRepository;
	private final ProductRepository productRepository;
	private final OrderStatusRepository orderStatusRepository;

	private final static int ORDERSTATUS_PAID = 4;

	public ReadBillLogResponse createBillLog(JSONObject billLogRequestObject) {

		ReadPaymentResponse readPaymentResponse = objectMapper.convertValue(billLogRequestObject,
			ReadPaymentResponse.class);

		Order order = orderRepository.findByOrderStr(readPaymentResponse.getOrderId());
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());



		List<ReadOrderDetailResponse> readOrderDetailResponses = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetails) {
			orderDetail.setOrderStatus(orderStatusRepository.findById(ORDERSTATUS_PAID).orElseThrow(()->new RuntimeException("Order status not found")));
			Product product = productRepository.findById(orderDetail.getProduct().getId()).orElseThrow(() -> new RuntimeException("Product not found"));
			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new IllegalArgumentException("Wrapping not found"));
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);
			readOrderDetailResponses.add(OrderDetailMapper.toDto(orderDetail, ProductResponse.convertToProductResponse(product), readWrappingResponse));
		}

		BillLog billLog = billLogRepository.save(
			BillLog.builder()
				.price(readPaymentResponse.getTotalAmount())
				.paymentKey(
					readPaymentResponse.getPaymentKey())
				.order(order)
				.status(BillStatus.valueOf(readPaymentResponse.getStatus()))
				.payment(readPaymentResponse.getMethod())
				.payAt(
					LocalDateTime.now())
				.build());
		UserInfo userInfo = UserInfo.builder()
			.email(order.getUser().getEmail())
			.loginId(order.getUser().getLoginId())
			.isAdmin(order.getUser().isAdmin())
			.contactNumber(order.getUser().getContactNumber())
			.birthday(order.getUser().getBirthday())
			.build();

		return BillLogMapper.toDto(billLog, OrderMapper.toDto(order, readOrderDetailResponses, userInfo.loginId()));
	}

	public Map<String, Object> readBillLogs(ReadBillLogsRequest request) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Page<ReadBillLogProjectionResponse> pageBillLogs = billLogRepository.findAll(request, pageable);
		List<ReadBillLogProjectionResponse> billLogs = pageBillLogs.getContent();

		data.put("responseData", billLogs);
		data.put("total", pageBillLogs.getTotalElements());

		return data;
	}

	public List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrder(long userId, String orderStr) {
		List<ReadBillLogWithoutOrderResponse> responses = new ArrayList<>();
		List<BillLog> billLogs = billLogRepository.findByUserIdAndOrderStr(userId, orderStr);

		for (BillLog newBillLog : billLogs) {
			responses.add(BillLogMapper.toDtoWithoutOrder(newBillLog));
		}

		return responses;
	}

	public List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrderWithAdmin(String orderId) {
		List<ReadBillLogWithoutOrderResponse> responses = new ArrayList<>();
		List<BillLog> billLogs = billLogRepository.findByOrder_OrderStr(orderId);

		for (BillLog newBillLog : billLogs) {
			responses.add(BillLogMapper.toDtoWithoutOrder(newBillLog));
		}

		return responses;
	}

	// public List<ReadPaymentLogResponse> readPaymentLogs(ReadPaymentLogRequest request) {
	//
	// 	List<PaymentLog> paymentLogs = paymentLogRepository.findByOrder_OrderStrAndOrder_User_LoginId(request.getOrderStr(), request.getLoginId());
	// 	List<OrderDetail> details = orderDetailRepository.findAllByOrder_OrderStr(request.getOrderStr());
	// 	List<ReadOrderDetailResponse> readOrderDetailResponses = new ArrayList<>();
	// 	for (OrderDetail orderDetail : details) {
	// 		Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new IllegalArgumentException("Wrapping not found"));
	// 		ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);
	//
	// 		Product product = productRepository.findById(orderDetail.getProduct().getId()).orElseThrow(() -> new RuntimeException("Product not found"));
	// 		readOrderDetailResponses.add(OrderDetailMapper.toDto(orderDetail, ProductResponse.convertToProductResponse(product), readWrappingResponse));
	// 	}
	// 	ReadOrderResponse readOrderResponse = OrderMapper.toDto(orderRepository.findByOrderStr(request.getOrderStr()), readOrderDetailResponses, request.getLoginId());
	// 	return paymentLogs.stream().map(pl->PaymentLogMapper.toDto(pl, readOrderResponse)).toList();
	// }
	//
	// public List<ReadPaymentLogResponse> readPaymentLogs(String orderStr) {
	//
	// 	Order order = orderRepository.findByOrderStr(orderStr);
	// 	List<PaymentLog> paymentLogs = paymentLogRepository.findByOrder_OrderStr(orderStr);
	// 	List<OrderDetail> details = orderDetailRepository.findAllByOrder_OrderStr(orderStr);
	// 	List<ReadOrderDetailResponse> readOrderDetailResponses = new ArrayList<>();
	// 	for (OrderDetail orderDetail : details) {
	// 		Product product = productRepository.findById(orderDetail.getProduct().getId()).orElseThrow(() -> new RuntimeException("Product not found"));
	// 		Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new IllegalArgumentException("Wrapping not found"));
	// 		ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);
	// 		readOrderDetailResponses.add(OrderDetailMapper.toDto(orderDetail, ProductResponse.convertToProductResponse(product), readWrappingResponse));
	// 	}
	// 	ReadOrderResponse readOrderResponse = OrderMapper.toDto(orderRepository.findByOrderStr(orderStr), readOrderDetailResponses, order.getUser().getLoginId());
	//
	// 	return paymentLogs.stream().map(pl->PaymentLogMapper.toDto(pl, readOrderResponse)).toList();
	// }

	// public ReadPaymentLogResponse createPaymentLog(JSONObject paymentRequestObject) {
	// 	CreatePaymentLogRequest createPaymentLogRequest = objectMapper.convertValue(paymentRequestObject, CreatePaymentLogRequest.class);
	//
	//
	//
	// 	PaymentLog paymentLog = paymentLogRepository.save(PaymentLogMapper.toEntity(createPaymentLogRequest, billLog));
	//
	// 	Order order = orderRepository.findByOrderStr(createPaymentLogRequest.getOrderId());
	//
	// 	UserInfo userInfo = UserInfo.builder().email(order.getUser().getEmail())
	// 		.loginId(order.getUser().getLoginId()).isAdmin(order.getUser().isAdmin()).contactNumber(order.getUser().getContactNumber())
	// 		.birthday(order.getUser().getBirthday()).build();
	//
	// 	return PaymentLogMapper.toDto(paymentLog, readBillLog(userInfo.id(), order.getOrderStr()));
	// }
}
