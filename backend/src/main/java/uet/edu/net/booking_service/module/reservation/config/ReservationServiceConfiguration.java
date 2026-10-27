package uet.edu.net.booking_service.module.reservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uet.edu.net.booking_service.module.reservation.service.PaymentService;
import uet.edu.net.booking_service.module.reservation.service.port.PaymentStore;

@Configuration
public class ReservationServiceConfiguration {

    @Bean
    PaymentService paymentService(PaymentStore paymentStore) {
        return new PaymentService(paymentStore);
    }
}
