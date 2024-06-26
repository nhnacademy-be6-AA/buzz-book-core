package store.buzzbook.core.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.dto.payment.toss.Cancel;
import store.buzzbook.core.dto.payment.toss.Card;
import store.buzzbook.core.dto.payment.toss.CashReceipt;
import store.buzzbook.core.dto.payment.toss.Checkout;
import store.buzzbook.core.dto.payment.toss.Discount;
import store.buzzbook.core.dto.payment.toss.EasyPay;
import store.buzzbook.core.dto.payment.toss.Failure;
import store.buzzbook.core.dto.payment.toss.GiftCeritificate;
import store.buzzbook.core.dto.payment.toss.MobilePhone;
import store.buzzbook.core.dto.payment.toss.Receipt;
import store.buzzbook.core.dto.payment.toss.Transfer;
import store.buzzbook.core.dto.payment.toss.VirtualAccount;

@Getter
@NoArgsConstructor
public class ReadPaymentResponse {
	private String version;
	private String paymentKey;
	private String type;
	private String orderId;
	private String orderName;
	private String mId;
	private String currency;
	private String method;
	private Integer totalAmount;
	private Integer balanceAmount;
	private String status;
	private String requestedAt;
	private String approvedAt;
	private boolean useEscrow;
	private String lastTransactionKey;
	private Integer suppliedAmount;
	private Integer vat;
	private boolean cultureExpense;
	private Integer taxFreeAmount;
	private Integer taxExemptionAmount;
	private Cancel[] cancels;
	private boolean isPartialCnacelable;
	private Card card;
	private VirtualAccount virtualAccount;
	private String secret;
	private MobilePhone mobilePhone;
	private GiftCeritificate giftCeritificate;
	private Transfer transfer;
	private Receipt receipt;
	private Checkout checkout;
	private EasyPay easyPay;
	private String country;
	private Failure failure;
	private CashReceipt cashReceipt;
	private CashReceipt[] cashReceipts;
	private Discount discount;
}
