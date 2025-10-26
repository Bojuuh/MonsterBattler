package app.security.exceptions;

import app.utils.Utils;

public class ApiException extends RuntimeException {
    private int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
