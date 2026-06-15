package es.udc.fi.dc.fd.model.services.exceptions;

/**
 * Exception thrown when a non-premium coach tries to propose/publish a routine.
 */
public class RoutineProposalNotAllowedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor with English message.
     */
    public RoutineProposalNotAllowedException() {
        super("Non-premium coaches cannot propose/publish routines");
    }

    /**
     * Constructor with custom message.
     *
     * @param message error message
     */
    public RoutineProposalNotAllowedException(String message) {
        super(message);
    }

}
