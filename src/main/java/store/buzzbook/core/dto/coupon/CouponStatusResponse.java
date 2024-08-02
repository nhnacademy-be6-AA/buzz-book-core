package store.buzzbook.core.dto.coupon;

public record CouponStatusResponse(

	String couponCode,

	String createDate,

	String expireDate,

	String status
) {

}
