package store.buzzbook.core.client.auth;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.core.dto.coupon.CouponLogRequest;
import store.buzzbook.core.dto.coupon.CouponResponse;

@FeignClient(name = "couponClient", url = "http://${api.gateway.host}:" + "${api.gateway.port}/api/coupons")
public interface CouponClient {

	List<CouponResponse> getUserCoupons(@RequestBody List<CouponLogRequest> request,
		@RequestBody String couponStatusName);
}
