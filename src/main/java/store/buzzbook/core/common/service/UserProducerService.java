package store.buzzbook.core.common.service;

import store.buzzbook.core.dto.coupon.CreateWelcomeCouponRequest;

public interface UserProducerService {

	void sendWelcomeCouponRequest(CreateWelcomeCouponRequest request);
}
