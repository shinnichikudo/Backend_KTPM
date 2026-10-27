package uet.edu.net.booking_service.module.reservation.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import uet.edu.net.booking_service.module.reservation.domain.Booking;
import uet.edu.net.booking_service.module.reservation.persistence.entity.BookingDetail;
import uet.edu.net.booking_service.module.reservation.service.port.BookingStore;

@Repository
public class BookingRepositoryAdapter implements BookingStore {
    private final SpringDataBookingRepository bookings;
    private final SpringDataBookingDetailRepository details;
    private final BookingMapper mapper;

    public BookingRepositoryAdapter(SpringDataBookingRepository bookings,
            SpringDataBookingDetailRepository details, BookingMapper mapper) {
        this.bookings = bookings;
        this.details = details;
        this.mapper = mapper;
    }

    @Override
    public Optional<Booking> findById(Long bookingId) {
        return bookings.findById(bookingId).map(mapper::toDomain);
    }

    @Override
    public List<Booking> findByUserId(Long userId) {
        return bookings.findByUserIdOrderByCreatedAtDesc(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Booking save(Booking booking) {
        if (booking.id() == null) {
            var entity = bookings.save(mapper.toEntity(booking));
            BookingDetail detail = BookingDetail.builder()
                    .bookingId(entity.getId())
                    .booking(entity)
                    .roomId(booking.roomId())
                    .priceAtBooking(booking.priceAtBooking())
                    .build();
            entity.getBookingDetails().add(details.save(detail));
            return mapper.toDomain(entity);
        }

        var entity = bookings.findById(booking.id()).orElseThrow();
        entity.setStatus(booking.status());
        return mapper.toDomain(bookings.save(entity));
    }
}
