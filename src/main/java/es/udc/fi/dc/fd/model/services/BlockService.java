package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.services.exceptions.AlreadyBlockedException;
import es.udc.fi.dc.fd.model.services.exceptions.NotBlockedException;

public interface BlockService {
    
    void blockUser(Long blockerId, Long blockedId) throws InstanceNotFoundException, AlreadyBlockedException;
    
    void unblockUser(Long blockerId, Long blockedId) throws InstanceNotFoundException, NotBlockedException;
    
    boolean isBlocked(Long userId, Long targetUserId);
    
    boolean hasBlockedMe(Long myUserId, Long otherUserId);
}
