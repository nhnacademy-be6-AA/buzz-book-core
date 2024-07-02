package store.buzzbook.core.util;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UUIDTest {

	private static final Logger log = LoggerFactory.getLogger(UUIDTest.class);

	private byte[] createUUIDtoByte() {
		UUID uuid = UUID.randomUUID();
		log.info("Created UUID: {}", uuid);

		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());

		return buffer.array();
	}

	private String uuidByteToString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}

		return sb.toString();
	}

	@Test
	void testUUIDToByte() {
		byte[] uuidByte = createUUIDtoByte();

		log.info("uuid byte to string : {}", uuidByteToString(uuidByte));

	}
}
