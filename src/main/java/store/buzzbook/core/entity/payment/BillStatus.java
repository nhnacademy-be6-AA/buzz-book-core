package store.buzzbook.core.entity.payment;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BillStatus {
	BEFORE, COMPLETED, CANCELED, REFUND, DONE, PARTIAL_CANCELED;

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
