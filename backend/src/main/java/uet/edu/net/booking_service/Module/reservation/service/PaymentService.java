package uet.edu.net.booking_service.Module.reservation.service;

import uet.edu.net.booking_service.Module.reservation.entity.Payment;

import java.math.BigDecimal;

public class PaymentService {
    public Payment processPayment(Long bookingId, BigDecimal amount, String cardNumber, String paymentMethod) {
        Payment payment = new Payment();
        return payment;
    }
}