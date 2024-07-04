package store.buzzbook.core.common.dto;

import lombok.Getter;

@Getter
public class SecretResponse {
	private Body body;

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
}
