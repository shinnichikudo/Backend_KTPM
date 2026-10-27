package uet.edu.net.booking_service.module.reservation.persistence;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import uet.edu.net.booking_service.module.reservation.domain.Payment;
import uet.edu.net.booking_service.module.reservation.domain.PaymentAlreadyExistsException;
import uet.edu.net.booking_service.module.reservation.service.port.PaymentStore;

@Repository
public class PaymentRepositoryAdapter implements PaymentStore {

    private final SpringDataPaymentRepository repository;
    private final PaymentMapper mapper;

    public PaymentRepositoryAdapter(SpringDataPaymentRepository repository, PaymentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Payment> findByBookingId(Long bookingId) {
        return repository.findByBookingId(bookingId).map(mapper::toDomain);
    }

    @Override
    public Payment save(Payment payment) {
        try {
            return mapper.toDomain(repository.saveAndFlush(mapper.toEntity(payment)));
        } catch (DataIntegrityViolationException exception) {
            throw new PaymentAlreadyExistsException(payment.bookingId());
        }
    }
}
