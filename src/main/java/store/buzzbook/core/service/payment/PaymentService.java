package store.buzzbook.core.service.payment;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
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

	public ReadBillLogResponse createBillLog(ReadPaymentResponse readPaymentResponse) {
		Order order = orderRepository.findById(Long.valueOf(readPaymentResponse.getOrderId())).orElseThrow(() -> new IllegalArgumentException("Order not found"));
		List<ReadOrderDetailResponse> readOrderDetailResponses = orderDetailRepository.findAllByOrder_Id(order.getId()).stream().map(
			OrderDetailMapper::toDto).toList();
		BillLog billLog = billLogRepository.save(BillLog.builder().price(readPaymentResponse.getTotalAmount()).paymentKey(
				UUID.fromString(readPaymentResponse.getPaymentKey())).order(order)
			.status(BillStatus.valueOf(readPaymentResponse.getStatus())).payment(readPaymentResponse.getMethod()).paymentDate(ZonedDateTime.now()).build());
		UserInfo userInfo = UserInfo.builder().grade(order.getUser().getGrade()).email(order.getUser().getEmail())
			.loginId(order.getUser().getLoginId()).isAdmin(order.getUser().isAdmin()).contactNumber(order.getUser().getContactNumber())
			.birthday(order.getUser().getBirthday()).build();

		return BillLogMapper.toDto(billLog, OrderMapper.toDto(order, readOrderDetailResponses, userInfo));
	}

	public Page<ReadBillLogResponse> readMyBillLogs(String loginId, Pageable pageable) {
		Page<BillLog> billLogs = billLogRepository.findAllByLoginId(loginId, pageable);
		List<ReadBillLogResponse> readBillLogRespons = new ArrayList<>();

		for (BillLog billLog : billLogs) {
			Order order = orderRepository.findById(billLog.getOrder().getId()).orElseThrow(() -> new IllegalArgumentException("Order not found"));
			List<ReadOrderDetailResponse> readOrderDetailResponses = orderDetailRepository.findAllByOrder_Id(order.getId()).stream().map(
				OrderDetailMapper::toDto).toList();
			UserInfo userInfo = UserInfo.builder().grade(order.getUser().getGrade()).email(order.getUser().getEmail())
				.loginId(order.getUser().getLoginId()).isAdmin(order.getUser().isAdmin()).contactNumber(order.getUser().getContactNumber())
				.birthday(order.getUser().getBirthday()).build();

			readBillLogRespons.add(BillLogMapper.toDto(billLog,OrderMapper.toDto(order, readOrderDetailResponses, userInfo)));
		}

		return new PageImpl<>(readBillLogRespons, pageable, billLogs.getTotalElements());
	}

	public Page<ReadBillLogResponse> readBillLogs(Pageable pageable) {
		Page<BillLog> billLogs = billLogRepository.findAll(pageable);
		List<ReadBillLogResponse> readBillLogRespons = new ArrayList<>();

		for (BillLog billLog : billLogs) {
			Order order = orderRepository.findById(billLog.getOrder().getId()).orElseThrow(() -> new IllegalArgumentException("Order not found"));
			List<ReadOrderDetailResponse> readOrderDetailResponses = orderDetailRepository.findAllByOrder_Id(order.getId()).stream().map(
				OrderDetailMapper::toDto).toList();
			UserInfo userInfo = UserInfo.builder().grade(order.getUser().getGrade()).email(order.getUser().getEmail())
				.loginId(order.getUser().getLoginId()).isAdmin(order.getUser().isAdmin()).contactNumber(order.getUser().getContactNumber())
				.birthday(order.getUser().getBirthday()).build();

			readBillLogRespons.add(BillLogMapper.toDto(billLog,OrderMapper.toDto(order, readOrderDetailResponses, userInfo)));
		}

		return new PageImpl<>(readBillLogRespons, pageable, billLogs.getTotalElements());
	}

	public ReadBillLogResponse readBillLog(long userId, long orderId) {
		BillLog billLog = billLogRepository.findByUserIdAndId(userId, orderId);
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
		List<ReadOrderDetailResponse> readOrderDetailResponses = orderDetailRepository.findAllByOrder_Id(order.getId()).stream().map(
			OrderDetailMapper::toDto).toList();
		UserInfo userInfo = UserInfo.builder().grade(order.getUser().getGrade()).email(order.getUser().getEmail())
			.loginId(order.getUser().getLoginId()).isAdmin(order.getUser().isAdmin()).contactNumber(order.getUser().getContactNumber())
			.birthday(order.getUser().getBirthday()).build();

		return BillLogMapper.toDto(billLog, OrderMapper.toDto(order, readOrderDetailResponses, userInfo));
	}
}
