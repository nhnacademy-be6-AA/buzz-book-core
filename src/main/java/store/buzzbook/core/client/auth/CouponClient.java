package store.buzzbook.core.client.auth;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import store.buzzbook.core.dto.coupon.CouponLogRequest;
import store.buzzbook.core.dto.coupon.CouponResponse;
import store.buzzbook.core.dto.coupon.CreateCouponRequest;
import store.buzzbook.core.dto.coupon.CreateCouponResponse;
import store.buzzbook.core.dto.coupon.OrderCouponResponse;
import store.buzzbook.core.dto.coupon.UpdateCouponRequest;

@FeignClient(name = "couponClient", url = "http://${api.gateway.host}:" + "${api.gateway.port}/api/coupons")
public interface CouponClient {

	@PostMapping("/condition")
	List<CouponResponse> getUserCoupons(@RequestBody List<CouponLogRequest> request,
		@RequestParam String couponStatusName);

	@PostMapping("/order")
	List<OrderCouponResponse> getUserCoupons(@RequestBody List<CouponLogRequest> request);

	@PostMapping
	CreateCouponResponse createCoupon(@RequestBody CreateCouponRequest request);

	@PutMapping
	CouponResponse updateCoupon(@Valid @RequestBody UpdateCouponRequest request);
}
