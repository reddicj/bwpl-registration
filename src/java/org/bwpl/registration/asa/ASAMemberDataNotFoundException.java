package org.bwpl.registration.asa;

public class ASAMemberDataNotFoundException extends RuntimeException {

    public ASAMemberDataNotFoundException() {
        super();
    }

    public ASAMemberDataNotFoundException(String message) {
        super(message);
    }

    public ASAMemberDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ASAMemberDataNotFoundException(Throwable cause) {
        super(cause);
    }
}
