package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.Block;
import es.udc.fi.dc.fd.model.entities.BlockDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.services.exceptions.AlreadyBlockedException;
import es.udc.fi.dc.fd.model.services.exceptions.NotBlockedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BlockServiceImpl implements BlockService {

    private static final String USER_ENTITY = "entities.user";

    private final BlockDao blockDao;
    private final UserDao userDao;

    public BlockServiceImpl(BlockDao blockDao, UserDao userDao) {
        this.blockDao = blockDao;
        this.userDao = userDao;
    }

    @Override
    public void blockUser(Long blockerId, Long blockedId) throws InstanceNotFoundException, AlreadyBlockedException {
        User blocker = userDao.findById(blockerId)
            .orElseThrow(() -> new InstanceNotFoundException(USER_ENTITY, blockerId));
        User blocked = userDao.findById(blockedId)
            .orElseThrow(() -> new InstanceNotFoundException(USER_ENTITY, blockedId));

        if (blockDao.existsByBlockerAndBlocked(blocker, blocked)) {
            throw new AlreadyBlockedException(blockerId, blockedId);
        }

        Block block = new Block(blocker, blocked);
        blockDao.save(block);
    }

    @Override
    public void unblockUser(Long blockerId, Long blockedId) throws InstanceNotFoundException, NotBlockedException {
        User blocker = userDao.findById(blockerId)
            .orElseThrow(() -> new InstanceNotFoundException(USER_ENTITY, blockerId));
        User blocked = userDao.findById(blockedId)
            .orElseThrow(() -> new InstanceNotFoundException(USER_ENTITY, blockedId));

        Block block = blockDao.findByBlockerAndBlocked(blocker, blocked)
            .orElseThrow(() -> new NotBlockedException(blockerId, blockedId));

        blockDao.delete(block);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBlocked(Long userId, Long targetUserId) {
        User user = userDao.findById(userId).orElse(null);
        User target = userDao.findById(targetUserId).orElse(null);
        
        if (user == null || target == null) {
            return false;
        }
        
        return blockDao.existsByBlockerAndBlocked(user, target);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasBlockedMe(Long myUserId, Long otherUserId) {
        User me = userDao.findById(myUserId).orElse(null);
        User other = userDao.findById(otherUserId).orElse(null);
        
        if (me == null || other == null) {
            return false;
        }
        
        return blockDao.existsByBlockerAndBlocked(other, me);
    }
}
