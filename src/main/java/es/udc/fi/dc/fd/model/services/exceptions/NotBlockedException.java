package es.udc.fi.dc.fd.model.services.exceptions;

@SuppressWarnings("serial")
public class NotBlockedException extends Exception {
    private final Long blockerId;
    private final Long blockedId;

    public NotBlockedException(Long blockerId, Long blockedId) {
        super("User " + blockerId + " has not blocked user " + blockedId);
        this.blockerId = blockerId;
        this.blockedId = blockedId;
    }

    public Long getBlockerId() {
        return blockerId;
    }

    public Long getBlockedId() {
        return blockedId;
    }
}
