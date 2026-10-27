package uet.edu.net.booking_service.module.inventory.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;
import uet.edu.net.booking_service.module.inventory.service.port.RoomUseCases;

@Transactional
public class SpringTransactionalRoomUseCases implements RoomUseCases {
    private final RoomUseCases delegate;

    public SpringTransactionalRoomUseCases(RoomUseCases delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getActiveRooms(RoomType roomType, Integer minCapacity, BigDecimal minPrice, BigDecimal maxPrice) {
        return delegate.getActiveRooms(roomType, minCapacity, minPrice, maxPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public Room getActiveRoomById(Long id) {
        return delegate.getActiveRoomById(id);
    }

    @Override
    public Room createRoom(Room requestedRoom) {
        return delegate.createRoom(requestedRoom);
    }

    @Override
    public Room updateRoom(Long id, Room requestedRoom) {
        return delegate.updateRoom(id, requestedRoom);
    }

    @Override
    public Room deactivateRoom(Long id) {
        return delegate.deactivateRoom(id);
    }
}
