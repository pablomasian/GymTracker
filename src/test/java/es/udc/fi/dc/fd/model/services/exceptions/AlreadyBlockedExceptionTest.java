package es.udc.fi.dc.fd.model.services.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AlreadyBlockedExceptionTest {

	@Test
	void testConstructorAndGetters() {
		Long blockerId = 1L;
		Long blockedId = 2L;

		AlreadyBlockedException exception = new AlreadyBlockedException(blockerId, blockedId);

		assertEquals(blockerId, exception.getBlockerId());
		assertEquals(blockedId, exception.getBlockedId());
	}

	@Test
	void testGettersReturnCorrectValues() {
		AlreadyBlockedException exception = new AlreadyBlockedException(100L, 200L);

		assertEquals(100L, exception.getBlockerId());
		assertEquals(200L, exception.getBlockedId());
	}
}
