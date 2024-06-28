package store.buzzbook.core.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Card {
	private Integer amount;
	private String issuerCode;
	private String acquirerCode;
	private String number;
	private Integer installmentPlanMonths;
	private String approveNo;
	private boolean useCardPoint;
	private String cardType;
	private String ownerType;
	private String acquireStatus;
	private boolean isInterestFree;
	private String interestPayer;
}
