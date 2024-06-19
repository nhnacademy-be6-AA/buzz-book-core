package store.buzzbook.core.entity.payment;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BillStatus {
	BEFORE, COMPLETED, CANCELLED, REFUND;

	@JsonCreator
	public static BillStatus jsonCreator(String value) {
		value = value.toUpperCase();
		for (BillStatus status : BillStatus.values()) {
			if (status.toString().equals(value)) {
				return status;
			}
		}
		throw new IllegalArgumentException();
	}
}
