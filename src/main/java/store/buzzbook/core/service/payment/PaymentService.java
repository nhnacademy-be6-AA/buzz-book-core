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
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.payment.BillLogResponse;
import store.buzzbook.core.dto.payment.PaymentLogResponse;
import store.buzzbook.core.dto.payment.PaymentResponse;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.payment.BillLog;
import store.buzzbook.core.entity.payment.BillStatus;
import store.buzzbook.core.entity.payment.PaymentLog;
import store.buzzbook.core.mapper.payment.BillLogMapper;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.payment.BillLogRepository;
import store.buzzbook.core.repository.payment.PaymentLogRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final BillLogRepository billLogRepository;
	private final PaymentLogRepository paymentLogRepository;
	private final OrderRepository orderRepository;

	public BillLogResponse createBillLog(PaymentResponse paymentResponse) {
		Order order = orderRepository.findById(Long.valueOf(paymentResponse.getOrderId())).orElseThrow(() -> new IllegalArgumentException("Order not found"));
		BillLog billLog = billLogRepository.save(BillLog.builder().price(paymentResponse.getTotalAmount()).paymentKey(
				UUID.fromString(paymentResponse.getPaymentKey())).order(order)
			.status(BillStatus.valueOf(paymentResponse.getStatus())).payment(paymentResponse.getMethod()).paymentDate(ZonedDateTime.now()).build());

		return BillLogMapper.toDto(billLog, order);
	}

	public Page<BillLogResponse> readBillLogs(Pageable pageable) {
		Page<BillLog> billLogs = billLogRepository.findAll(pageable);
		List<BillLogResponse> billLogResponses = new ArrayList<>();

		for (BillLog billLog : billLogs) {
			billLogResponses.add(BillLogMapper.toDto(billLog,
				orderRepository.findById(billLog.getOrder().getId()).orElseThrow(() -> new IllegalArgumentException("Order not found"))));
		}

		return new PageImpl<>(billLogResponses, pageable, billLogs.getTotalElements());
	}

	public BillLogResponse readBillLog(long orderId) {
		BillLog billLog = billLogRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
		return BillLogMapper.toDto(billLog, orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found")));
	}
}
