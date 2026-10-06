package uet.edu.net.booking_service.module.reservation.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uet.edu.net.booking_service.core.exception.AppException;
import uet.edu.net.booking_service.core.exception.ErrorCode;
import uet.edu.net.booking_service.module.reservation.domain.PaymentPolicy;
import uet.edu.net.booking_service.module.reservation.entity.Payment;
import uet.edu.net.booking_service.module.reservation.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentPolicy paymentPolicy = new PaymentPolicy();

    public Payment processPayment(Long bookingId, BigDecimal amount, String cardNumber, String paymentMethod) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán không hợp lệ");
        }

        // Kiểm tra payment đã tồn tại trước khi tạo
        if (paymentRepository.findByBookingId(bookingId).isPresent()) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        if (cardNumber == null || !paymentPolicy.isValidLuhn(cardNumber)) {
            throw new IllegalArgumentException("Số thẻ không hợp lệ.");
        }

        if (!"CREDIT_CARD".equals(paymentMethod)) {
            throw new IllegalArgumentException("Phương thức thanh toán không được hỗ trợ.");
        }

        Payment payment = Payment.builder()
                .bookingId(bookingId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .transactionRef(UUID.randomUUID().toString())
                .status("SUCCESS")
                .build();

        try {
            return paymentRepository.saveAndFlush(payment);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }
    }

}