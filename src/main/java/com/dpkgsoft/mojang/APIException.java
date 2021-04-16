package com.dpkgsoft.mojang;

public class APIException extends Exception {
    private int code = 0;
    public APIException(String message, int code) {
        super(message);
        this.code = code;
    }
    public APIException(int code) {
        super();
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
