package store.buzzbook.core.common.util;

import java.nio.ByteBuffer;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UuidUtil {

	public static byte[] createUuidToByte() {
		UUID uuid = UUID.randomUUID();

		return convertToByte(uuid);
	}

	public static byte[] convertToByte(UUID uuid) {
		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
		return buffer.array();
	}

	public static String formatUuid(String uuidString) {
		StringBuilder sb = new StringBuilder(uuidString);
		sb.insert(8, "-");
		sb.insert(13, "-");
		sb.insert(18, "-");
		sb.insert(23, "-");
		return sb.toString();
	}

	public static byte[] stringToByte(String uuidString) {
		String formatted = formatUuid(uuidString);
		return convertToByte(UUID.fromString(formatted));
	}

	public static String uuidByteToString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}

		return sb.toString();
	}
}
