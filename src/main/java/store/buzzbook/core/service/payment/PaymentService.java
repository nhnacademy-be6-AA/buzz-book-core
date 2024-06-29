package store.buzzbook.core.service.payment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.payment.ReadBillLogProjectionResponse;
import store.buzzbook.core.dto.payment.ReadBillLogRequest;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.dto.payment.ReadPaymentResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.payment.BillLog;
import store.buzzbook.core.entity.payment.BillStatus;
import store.buzzbook.core.mapper.order.OrderDetailMapper;
import store.buzzbook.core.mapper.order.OrderMapper;
import store.buzzbook.core.mapper.payment.BillLogMapper;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.payment.BillLogRepository;
import store.buzzbook.core.repository.payment.PaymentLogRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final BillLogRepository billLogRepository;
	private final PaymentLogRepository paymentLogRepository;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final ObjectMapper objectMapper;

	public ReadBillLogResponse createBillLog(JSONObject billLogRequestObject) {

		ReadPaymentResponse readPaymentResponse = objectMapper.convertValue(billLogRequestObject, ReadPaymentResponse.class);

		Order order = orderRepository.findByOrderStr(readPaymentResponse.getOrderId());
		List<ReadOrderDetailResponse> readOrderDetailResponses = orderDetailRepository.findAllByOrder_Id(order.getId()).stream().map(
			OrderDetailMapper::toDto).toList();
		BillLog billLog = billLogRepository.save(BillLog.builder().price(readPaymentResponse.getTotalAmount()).paymentKey(
				readPaymentResponse.getPaymentKey()).order(order)
			.status(BillStatus.valueOf(readPaymentResponse.getStatus())).payment(readPaymentResponse.getMethod()).payAt(
				LocalDateTime.now()).build());
		UserInfo userInfo = UserInfo.builder().email(order.getUser().getEmail())
			.loginId(order.getUser().getLoginId()).isAdmin(order.getUser().isAdmin()).contactNumber(order.getUser().getContactNumber())
			.birthday(order.getUser().getBirthday()).build();

		return BillLogMapper.toDto(billLog, OrderMapper.toDto(order, readOrderDetailResponses, userInfo.loginId()));
	}

	// public Page<ReadBillLogResponse> readMyBillLogs(String loginId, Pageable pageable) {
	// 	Page<BillLog> billLogs = billLogRepository.findAllByLoginId(loginId, pageable);
	// 	List<ReadBillLogResponse> readBillLogRespons = new ArrayList<>();
	//
	// 	for (BillLog billLog : billLogs) {
	// 		Order order = orderRepository.findById(billLog.getOrder().getId()).orElseThrow(() -> new IllegalArgumentException("Order not found"));
	// 		List<ReadOrderDetailResponse> readOrderDetailResponses = orderDetailRepository.findAllByOrder_Id(order.getId()).stream().map(
	// 			OrderDetailMapper::toDto).toList();
	// 		UserInfo userInfo = UserInfo.builder().email(order.getUser().getEmail())
	// 			.loginId(order.getUser().getLoginId()).isAdmin(order.getUser().isAdmin()).contactNumber(order.getUser().getContactNumber())
	// 			.birthday(order.getUser().getBirthday()).build();
	//
	// 		readBillLogRespons.add(BillLogMapper.toDto(billLog,OrderMapper.toDto(order, readOrderDetailResponses, userInfo.loginId())));
	// 	}
	//
	// 	return new PageImpl<>(readBillLogRespons, pageable, billLogs.getTotalElements());
	// }

	public Map<String, Object> readBillLogs(ReadBillLogRequest request) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Page<ReadBillLogProjectionResponse> pageBillLogs = billLogRepository.findAll(request, pageable);
		List<ReadBillLogProjectionResponse> billLogs = new ArrayList<>();

		data.put("responseData", billLogs);
		data.put("total", pageBillLogs.getTotalElements());

		return data;
	}

	public List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrder(long userId, String orderId) {
		List<ReadBillLogWithoutOrderResponse> responses = new ArrayList<>();
		List<BillLog> billLogs = billLogRepository.findByUserIdAndId(userId, orderId);

		for (BillLog newBillLog : billLogs) {
			responses.add(BillLogMapper.toDtoWithoutOrder(newBillLog));
		}

		return responses;
	}

	// public ReadPaymentLogResponse createPaymentLog(JSONObject paymentRequestObject) {
	// 	CreatePaymentLogRequest createPaymentLogRequest = objectMapper.convertValue(paymentRequestObject, CreatePaymentLogRequest.class);
	//
	// 	BillLog billLog = billLogRepository.findById(createPaymentLogRequest.getBillLogId()).orElseThrow(() -> new IllegalArgumentException("Bill log not found"));
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
