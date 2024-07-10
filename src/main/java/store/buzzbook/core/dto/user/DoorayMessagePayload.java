package store.buzzbook.core.dto.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class DoorayMessagePayload {
	private String botName;
	private String botIconImage;
	private String text;
	private List<Attachment> attachments;

	@NoArgsConstructor
	@Getter
	@Setter
	public static class Attachment {
		private String title = "test";
		private String titleLink = "http://naver.com";
		private String text = "text";
		private String color = "red";

	}
}
