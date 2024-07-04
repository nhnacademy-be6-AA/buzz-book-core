package store.buzzbook.core.dto.coupon;

import java.time.LocalDate;

import store.buzzbook.core.entity.coupon.CouponStatus;

/**
 * 쿠폰의 응답 데이터를 담는 클래스입니다.
 * <p>
 * 이 클래스는 쿠폰의 ID, 생성일, 만료일, 상태, 쿠폰 정책 응답 데이터를 포함합니다.
 * </p>
 *
 * @param id 쿠폰의 ID
 * @param createDate 쿠폰 생성일
 * @param expireDate 쿠폰 만료일
 * @param status 쿠폰 상태
 * @param couponPolicyResponse 쿠폰 정책 응답 데이터
 */
public record CouponResponse(
	long id,
	LocalDate createDate,
	LocalDate expireDate,
	CouponStatus status,
	CouponPolicyResponse couponPolicyResponse
) {
}
