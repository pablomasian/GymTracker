package es.udc.fi.dc.fd.model.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class BlockTest {

	@Test
	void testBlockConstructorAndGetters() {
		User blocker = new User();
		blocker.setId(1L);
		blocker.setUsername("blocker");

		User blocked = new User();
		blocked.setId(2L);
		blocked.setUsername("blocked");

		Block block = new Block(blocker, blocked);

		assertNotNull(block);
		assertEquals(blocker, block.getBlocker());
		assertEquals(blocked, block.getBlocked());
		assertNotNull(block.getBlockedAt());
	}

	@Test
	void testBlockSetters() {
		Block block = new Block();
		
		User blocker = new User();
		blocker.setId(1L);
		
		User blocked = new User();
		blocked.setId(2L);
		
		LocalDateTime now = LocalDateTime.now();

		block.setId(10L);
		block.setBlocker(blocker);
		block.setBlocked(blocked);
		block.setBlockedAt(now);

		assertEquals(10L, block.getId());
		assertEquals(blocker, block.getBlocker());
		assertEquals(blocked, block.getBlocked());
		assertEquals(now, block.getBlockedAt());
	}

	@Test
	void testBlockDefaultConstructor() {
		Block block = new Block();
		
		assertNotNull(block);
		assertNull(block.getId());
		assertNull(block.getBlocker());
		assertNull(block.getBlocked());
		assertNull(block.getBlockedAt());
	}
}
