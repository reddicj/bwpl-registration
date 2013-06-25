package org.bwpl.registration.asa;

public class ASAMemberDataValidationException extends RuntimeException {

    public ASAMemberDataValidationException() {
        super();
    }

    public ASAMemberDataValidationException(String message) {
        super(message);
    }

    public ASAMemberDataValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ASAMemberDataValidationException(Throwable cause) {
        super(cause);
    }
}
