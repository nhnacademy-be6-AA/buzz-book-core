package store.buzzbook.core.util;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import store.buzzbook.core.common.util.ZonedDateTimeParser;

class ZonedDateTimeParserTest {
	private static final Logger log = LoggerFactory.getLogger(ZonedDateTimeParserTest.class);
	private String exDate;
	private String exDateTime;
	private ZonedDateTime zonedDateTime;

	@BeforeEach
	void setUp() {
		exDate = "2020-01-01";
		exDateTime = "2020-01-02 12:34:56";
		zonedDateTime = ZonedDateTime.now();
	}

	@Test
	void testParse() {

		Assertions.assertDoesNotThrow(() -> {
			ZonedDateTime parsedDate = ZonedDateTimeParser.toDate(exDate);
			ZonedDateTime parsedDateTime = ZonedDateTimeParser.toDateTime(exDateTime);
			String parsedStringDate = ZonedDateTimeParser.toStringDate(zonedDateTime);
			String parsedStringDateTime = ZonedDateTimeParser.toStringDateTime(zonedDateTime);
			log.info("parsed date: {}", parsedDate);
			log.info("parsed datetime: {}", parsedDateTime);
			log.info("parsed string date: {}", parsedStringDate);
			log.info("parsed string datetime: {}", parsedStringDateTime);
		});
	}
}
