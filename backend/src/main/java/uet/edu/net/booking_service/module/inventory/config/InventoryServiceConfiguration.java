package uet.edu.net.booking_service.module.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import uet.edu.net.booking_service.module.inventory.service.RoomService;
import uet.edu.net.booking_service.module.inventory.service.port.RoomRepository;
import uet.edu.net.booking_service.module.inventory.service.port.RoomUseCases;

@Configuration
public class InventoryServiceConfiguration {

    @Bean
    RoomService roomService(RoomRepository roomRepository) {
        return new RoomService(roomRepository);
    }

    @Bean
    @Primary
    RoomUseCases transactionalRoomUseCases(RoomService roomService) {
        return new SpringTransactionalRoomUseCases(roomService);
    }
}
