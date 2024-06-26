package store.buzzbook.core.common.service;

import store.buzzbook.core.dto.coupon.CreateWelcomeCouponRequest;

public interface CouponProducerService {

	void sendWelcomeCouponRequest(CreateWelcomeCouponRequest request);
}
