package uet.edu.net.booking_service.module.reservation.persistence;

import org.springframework.stereotype.Component;
import uet.edu.net.booking_service.module.reservation.domain.Booking;
import uet.edu.net.booking_service.module.reservation.entity.BookingDetail;

@Component
public class BookingMapper {
    public Booking toDomain(uet.edu.net.booking_service.module.reservation.entity.Booking entity) {
        BookingDetail detail = entity.getBookingDetails().stream().findFirst().orElse(null);
        return new Booking(
                entity.getId(), entity.getUserId(), entity.getCheckInDate(), entity.getCheckOutDate(),
                entity.getTotalGuest(), entity.getTotalPrice(), entity.getStatus(), entity.getCreatedAt(),
                detail == null ? null : detail.getRoomId(),
                detail == null ? null : detail.getPriceAtBooking());
    }

    public uet.edu.net.booking_service.module.reservation.entity.Booking toEntity(Booking booking) {
        return uet.edu.net.booking_service.module.reservation.entity.Booking.builder()
                .id(booking.id())
                .userId(booking.userId())
                .checkInDate(booking.checkInDate())
                .checkOutDate(booking.checkOutDate())
                .totalGuest(booking.totalGuest())
                .totalPrice(booking.totalPrice())
                .status(booking.status())
                .createdAt(booking.createdAt())
                .build();
    }
}
