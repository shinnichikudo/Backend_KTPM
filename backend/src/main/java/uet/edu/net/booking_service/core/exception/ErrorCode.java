package uet.edu.net.booking_service.core.exception;

public enum ErrorCode {
    UNAUTHORIZED("Yeu cau dang nhap"),
    INVALID_REQUEST("Request body khong hop le"),
    EMAIL_ALREADY_EXISTS("Email đã được sử dụng"),
    WEAK_PASSWORD("Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa và số"),
    INVALID_CREDENTIALS("Email hoặc mật khẩu không đúng"),
    USER_NOT_FOUND("Không tìm thấy người dùng"),
    BOOKING_NOT_FOUND("Không tìm thấy đơn đặt phòng"),
    ROOM_NOT_FOUND("Không tìm thấy phòng đang hoạt động"),
    ROOM_NOT_AVAILABLE("Phòng không còn trống trong khoảng ngày yêu cầu"),
    PAYMENT_ALREADY_EXISTS("Đơn đặt phòng này đã được thanh toán"),
    FORBIDDEN("Bạn không có quyền thực hiện thao tác này"),
    WRONG_OLD_PASSWORD("Mật khẩu hiện tại không đúng");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
