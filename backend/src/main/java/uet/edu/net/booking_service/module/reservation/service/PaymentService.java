package uet.edu.net.booking_service.module.reservation.service;

import java.math.BigDecimal;
import java.util.UUID;

import uet.edu.net.booking_service.core.exception.AppException;
import uet.edu.net.booking_service.core.exception.ErrorCode;
import uet.edu.net.booking_service.module.reservation.domain.Payment;
import uet.edu.net.booking_service.module.reservation.domain.PaymentAlreadyExistsException;
import uet.edu.net.booking_service.module.reservation.domain.PaymentPolicy;
import uet.edu.net.booking_service.module.reservation.service.port.PaymentStore;

public class PaymentService {

    private final PaymentStore paymentStore;

    private final PaymentPolicy paymentPolicy = new PaymentPolicy();

    public PaymentService(PaymentStore paymentStore) {
        this.paymentStore = paymentStore;
    }

    public Payment processPayment(Long bookingId, BigDecimal amount, String cardNumber, String paymentMethod) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán không hợp lệ");
        }

        // Kiểm tra payment đã tồn tại trước khi tạo
        if (paymentStore.findByBookingId(bookingId).isPresent()) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        if (cardNumber == null || !paymentPolicy.isValidLuhn(cardNumber)) {
            throw new IllegalArgumentException("Số thẻ không hợp lệ.");
        }

        if (!"CREDIT_CARD".equals(paymentMethod)) {
            throw new IllegalArgumentException("Phương thức thanh toán không được hỗ trợ.");
        }

        try {
            Payment payment = new Payment(
                    null,
                    bookingId,
                    amount,
                    paymentMethod,
                    UUID.randomUUID().toString(),
                    "SUCCESS",
                    null);
            return paymentStore.save(payment);
        } catch (PaymentAlreadyExistsException ex) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }
    }

}
