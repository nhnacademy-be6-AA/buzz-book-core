package store.buzzbook.core.common.exception.order;

public class DeliveryPolicyNotFoundException extends RuntimeException {
	public DeliveryPolicyNotFoundException() {
		super("Delivery Policy Not Found");
	}
}
