package uet.edu.net.booking_service.module.reservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import uet.edu.net.booking_service.module.auth.contract.AuthServiceContract;
import uet.edu.net.booking_service.module.inventory.contract.RoomServiceContract;
import uet.edu.net.booking_service.module.reservation.service.BookingService;
import uet.edu.net.booking_service.module.reservation.service.PaymentService;
import uet.edu.net.booking_service.module.reservation.service.port.BookingNightStore;
import uet.edu.net.booking_service.module.reservation.service.port.BookingStore;
import uet.edu.net.booking_service.module.reservation.service.port.BookingUseCases;
import uet.edu.net.booking_service.module.reservation.service.port.PaymentStore;

@Configuration
public class ReservationServiceConfiguration {

    @Bean
    PaymentService paymentService(PaymentStore paymentStore) {
        return new PaymentService(paymentStore);
    }

    @Bean
    BookingService bookingService(BookingStore bookingStore, BookingNightStore bookingNightStore,
            AuthServiceContract authService, RoomServiceContract roomService, PaymentService paymentService) {
        return new BookingService(bookingStore, bookingNightStore, authService, roomService, paymentService);
    }

    @Bean
    @Primary
    BookingUseCases transactionalBookingUseCases(BookingService bookingService) {
        return new SpringTransactionalBookingUseCases(bookingService);
    }
}
