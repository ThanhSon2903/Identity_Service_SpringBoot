package com.SonNguyen.identity_service.exception;

import org.springframework.jdbc.datasource.init.UncategorizedScriptException;

public enum ErrorCode{
    USER_EXISTED(1001,"User exists"),
    User_CreateAccepted(1002,"User create accepted"),
    USERNAME_INVALID(1003,"Tên đăng nhập của bạn quá ngắn"),
    PASSWORD_INVALID(1004,"Mật khẩu của bạn quá ngắn"),
    USER_NOT_EXISTED(1005,"User Not Exists"),
    UNAUTHENCATED(1006,"Unauthencated")
    ;

    // USER_EXISTED chính là một enum constant trong kiểu ErrorCode.
    // Enum constant thường đại diện cho một hằng số hoặc một giá trị không thay đổi
    // Mỗi enum constant là một đối tượng của kiểu enum mà bạn đã định nghĩa.

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
