package uet.edu.net.booking_service.core.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Yeu cau dang nhap"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Request body khong hop le"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email đã được sử dụng"),
    WEAK_PASSWORD(HttpStatus.BAD_REQUEST, "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa và số"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy phòng đang hoạt động"),
    ROOM_NOT_AVAILABLE(HttpStatus.CONFLICT, "Phòng không còn trống trong khoảng ngày yêu cầu"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này"),
    WRONG_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "Mật khẩu hiện tại không đúng");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
