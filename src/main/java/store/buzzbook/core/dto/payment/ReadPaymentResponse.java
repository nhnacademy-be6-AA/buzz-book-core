package store.buzzbook.core.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadPaymentResponse {
	@JsonProperty("version")
	private String version;

	@JsonProperty("paymentKey")
	private String paymentKey;

	@JsonProperty("type")
	private String type;

	@JsonProperty("orderId")
	private String orderId;

	@JsonProperty("orderName")
	private String orderName;

	@JsonProperty("mId")
	private String mId;

	@JsonProperty("currency")
	private String currency;

	@JsonProperty("method")
	private String method;

	@JsonProperty("totalAmount")
	private Integer totalAmount;

	@JsonProperty("balanceAmount")
	private Integer balanceAmount;

	@JsonProperty("status")
	private String status;

	@JsonProperty("requestedAt")
	private String requestedAt;

	@JsonProperty("approvedAt")
	private String approvedAt;

	@JsonProperty("useEscrow")
	private boolean useEscrow;

	@JsonProperty("lastTransactionKey")
	private String lastTransactionKey;

	@JsonProperty("suppliedAmount")
	private Integer suppliedAmount;

	@JsonProperty("vat")
	private Integer vat;

	@JsonProperty("cultureExpense")
	private boolean cultureExpense;

	@JsonProperty("taxFreeAmount")
	private Integer taxFreeAmount;

	@JsonProperty("taxExemptionAmount")
	private Integer taxExemptionAmount;

	@JsonProperty("cancels")
	private Cancel[] cancels;

	@JsonProperty("isPartialCancelable")
	private boolean isPartialCancelable;

	@JsonProperty("card")
	private Card card;

	@JsonProperty("virtualAccount")
	private VirtualAccount virtualAccount;

	@JsonProperty("secret")
	private String secret;

	@JsonProperty("mobilePhone")
	private MobilePhone mobilePhone;

	@JsonProperty("giftCertificate")
	private GiftCertificate giftCertificate;

	@JsonProperty("transfer")
	private Transfer transfer;

	@JsonProperty("receipt")
	private Receipt receipt;

	@JsonProperty("checkout")
	private Checkout checkout;

	@JsonProperty("easyPay")
	private EasyPay easyPay;

	@JsonProperty("country")
	private String country;

	@JsonProperty("failure")
	private Failure failure;

	@JsonProperty("cashReceipt")
	private CashReceipt cashReceipt;

	@JsonProperty("cashReceipts")
	private CashReceipt[] cashReceipts;

	@JsonProperty("discount")
	private Discount discount;

	@Getter
	public static class Cancel {
		@JsonProperty("cancelAmount")
		private Integer cancelAmount;

		@JsonProperty("cancelReason")
		private String cancelReason;

		@JsonProperty("taxFreeAmount")
		private Integer taxFreeAmount;

		@JsonProperty("taxExemptionAmount")
		private Integer taxExemptionAmount;

		@JsonProperty("refundableAmount")
		private Integer refundableAmount;

		@JsonProperty("easyPayDiscountAmount")
		private Integer easyPayDiscountAmount;

		@JsonProperty("canceledAt")
		private String canceledAt;

		@JsonProperty("transactionKey")
		private String transactionKey; // 취소 건의 키 값입니다. 여러 건의 취소 거래를 구분하는 데 사용됩니다. 최대 길이는 64자입니다.

		@JsonProperty("receiptKey")
		private String receiptKey;

		@JsonProperty("cancelStatus")
		private String cancelStatus;

		@JsonProperty("cancelRequestId")
		private String cancelRequestId;
	}

	public static class Card {
		@JsonProperty("amount")
		private Integer amount;

		@JsonProperty("issuerCode")
		private String issuerCode;

		@JsonProperty("acquirerCode")
		private String acquirerCode;

		@JsonProperty("number")
		private String number;

		@JsonProperty("installmentPlanMonths")
		private Integer installmentPlanMonths;

		@JsonProperty("approveNo")
		private String approveNo;

		@JsonProperty("useCardPoint")
		private Boolean useCardPoint;

		@JsonProperty("cardType")
		private String cardType;

		@JsonProperty("ownerType")
		private String ownerType;

		@JsonProperty("acquireStatus")
		private String acquireStatus;

		@JsonProperty("isInterestFree")
		private Boolean isInterestFree;

		@JsonProperty("interestPayer")
		private String interestPayer;
	}

	public static class VirtualAccount {
		@JsonProperty("accountType")
		private String accountType;

		@JsonProperty("accountNumber")
		private String accountNumber;

		@JsonProperty("bankCode")
		private String bankCode;

		@JsonProperty("customerName")
		private String customerName;

		@JsonProperty("dueDate")
		private String dueDate;

		@JsonProperty("refundStatus")
		private String refundStatus;

		@JsonProperty("expired")
		private Boolean expired;

		@JsonProperty("refundReceiveAccount")
		private RefundReceiveAccount refundReceiveAccount;
	}

	public static class RefundReceiveAccount {
		@JsonProperty("bankCode")
		private String bankCode;

		@JsonProperty("accountNumber")
		private String accountNumber;

		@JsonProperty("holderName")
		private String holderName;
	}

	public static class MobilePhone {
		@JsonProperty("customerMobilePhone")
		private CustomerMobilePhone customerMobilePhone;

		@JsonProperty("settlementStatus")
		private String settlementStatus;

		@JsonProperty("receiptUrl")
		private String receiptUrl;
	}

	public static class CustomerMobilePhone {
		@JsonProperty("plain")
		private String plain;

		@JsonProperty("masking")
		private String masking;
	}

	public static class GiftCertificate {
		@JsonProperty("approveNo")
		private String approveNo;

		@JsonProperty("settlementStatus")
		private String settlementStatus;
	}

	public static class Transfer {
		@JsonProperty("bankCode")
		private String bankCode;

		@JsonProperty("settlementStatus")
		private String settlementStatus;
	}

	public static class Receipt {
		@JsonProperty("url")
		private String url;
	}

	public static class Checkout {
		@JsonProperty("url")
		private String url;
	}

	public static class EasyPay {
		@JsonProperty("provider")
		private String provider;

		@JsonProperty("amount")
		private Integer amount;

		@JsonProperty("discountAmount")
		private Integer discountAmount;
	}

	public static class Failure {
		@JsonProperty("code")
		private String code;

		@JsonProperty("message")
		private String message;
	}

	public static class CashReceipt {
	}

	public static class Discount {
		@JsonProperty("amount")
		private Integer amount;
	}

	// 여기에 추가적인 필드나 클래스를 정의할 수 있습니다.

	// 예를 들어, 추가적인 정보가 있다면 아래와 같이 정의할 수 있습니다.

	@JsonProperty("customField1")
	private String customField1;

	@JsonProperty("customField2")
	private String customField2;

	@JsonProperty("customNestedClass")
	private CustomNestedClass customNestedClass;

	public static class CustomNestedClass {
		@JsonProperty("nestedField1")
		private String nestedField1;

		@JsonProperty("nestedField2")
		private Integer nestedField2;
	}
}
