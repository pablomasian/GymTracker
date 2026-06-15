package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.Block;
import es.udc.fi.dc.fd.model.entities.BlockDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.services.exceptions.AlreadyBlockedException;
import es.udc.fi.dc.fd.model.services.exceptions.NotBlockedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockServiceImplTest {

    @Mock
    private BlockDao blockDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private BlockServiceImpl blockService;

    @Test
    void testBlockUser_Success() throws InstanceNotFoundException, AlreadyBlockedException {
        User blocker = new User();
        blocker.setId(1L);
        User blocked = new User();
        blocked.setId(2L);

        when(userDao.findById(1L)).thenReturn(Optional.of(blocker));
        when(userDao.findById(2L)).thenReturn(Optional.of(blocked));
        when(blockDao.existsByBlockerAndBlocked(blocker, blocked)).thenReturn(false);

        blockService.blockUser(1L, 2L);

        verify(blockDao).save(any(Block.class));
    }

    @Test
    void testBlockUser_AlreadyBlocked() {
        User blocker = new User();
        blocker.setId(1L);
        User blocked = new User();
        blocked.setId(2L);

        when(userDao.findById(1L)).thenReturn(Optional.of(blocker));
        when(userDao.findById(2L)).thenReturn(Optional.of(blocked));
        when(blockDao.existsByBlockerAndBlocked(blocker, blocked)).thenReturn(true);

        assertThrows(AlreadyBlockedException.class, () -> blockService.blockUser(1L, 2L));
    }

    @Test
    void testUnblockUser_Success() throws InstanceNotFoundException, NotBlockedException {
        User blocker = new User();
        blocker.setId(1L);
        User blocked = new User();
        blocked.setId(2L);
        Block block = new Block(blocker, blocked);

        when(userDao.findById(1L)).thenReturn(Optional.of(blocker));
        when(userDao.findById(2L)).thenReturn(Optional.of(blocked));
        when(blockDao.findByBlockerAndBlocked(blocker, blocked)).thenReturn(Optional.of(block));

        blockService.unblockUser(1L, 2L);

        verify(blockDao).delete(block);
    }

    @Test
    void testIsBlocked_True() {
        User user = new User();
        user.setId(1L);
        User target = new User();
        target.setId(2L);

        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userDao.findById(2L)).thenReturn(Optional.of(target));
        when(blockDao.existsByBlockerAndBlocked(user, target)).thenReturn(true);

        assertTrue(blockService.isBlocked(1L, 2L));
    }

    @Test
    void testIsBlocked_False() {
        User user = new User();
        user.setId(1L);
        User target = new User();
        target.setId(2L);

        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userDao.findById(2L)).thenReturn(Optional.of(target));
        when(blockDao.existsByBlockerAndBlocked(user, target)).thenReturn(false);

        assertFalse(blockService.isBlocked(1L, 2L));
    }
}
