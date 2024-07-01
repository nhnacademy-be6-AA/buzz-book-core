package store.buzzbook.core.common.service;

import store.buzzbook.core.dto.coupon.CreateWelcomeCouponRequest;
import store.buzzbook.core.dto.coupon.DownloadCouponRequest;

public interface UserProducerService {

	void sendWelcomeCouponRequest(CreateWelcomeCouponRequest request);

	void sendDownloadCouponRequest(DownloadCouponRequest request);
}
