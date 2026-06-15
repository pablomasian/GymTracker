package es.udc.fi.dc.fd.model.services.exceptions;

@SuppressWarnings("serial")
public class AlreadyBlockedException extends Exception {
    private final Long blockerId;
    private final Long blockedId;

    public AlreadyBlockedException(Long blockerId, Long blockedId) {
        super("User " + blockerId + " has already blocked user " + blockedId);
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
