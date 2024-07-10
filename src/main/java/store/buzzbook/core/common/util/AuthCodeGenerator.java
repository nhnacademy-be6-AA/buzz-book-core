package store.buzzbook.core.common.util;

import java.security.SecureRandom;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthCodeGenerator {
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String CHAR_LIST = "qpwoeirutyalskdjfhgmznxbcv1029384756QPWOEIRUTYALSKDJFHGZMXNCBV";

	public static String generate() {
		StringBuilder code = new StringBuilder();

		for (int i = 0; i < 5; i++) {
			code.append(CHAR_LIST.charAt(RANDOM.nextInt(CHAR_LIST.length())));
		}

		return code.toString();
	}
}
