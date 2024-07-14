package store.buzzbook.core.util;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.util.UuidUtil;

@Slf4j
public class UUIDTest {

	@Test
	void testUUIDToByte() {
		byte[] uuidByte = UuidUtil.createUuidToByte();
		String uuidString = UuidUtil.uuidByteToString(uuidByte);
		byte[] uuidByte2 = UuidUtil.stringToByte(uuidString);

		Assertions.assertAll(() -> {
			for (int i = 0; i < uuidByte.length; i++) {
				Assertions.assertEquals(uuidByte[i], uuidByte2[i]);
			}

			Assertions.assertEquals(uuidByte.length, uuidByte2.length);
		});

	}

	@Test
	void testConvertToByteAndFormatString() {
		UUID uuid = UUID.randomUUID();
		byte[] uuidByte = UuidUtil.convertToByte(uuid);
		String uuidString = uuid.toString();
		String uuidString2 = UuidUtil.uuidByteToString(uuidByte);
		String formatedString = UuidUtil.formatUuid(uuidString2);

		Assertions.assertEquals(uuidString, formatedString);
	}
}
