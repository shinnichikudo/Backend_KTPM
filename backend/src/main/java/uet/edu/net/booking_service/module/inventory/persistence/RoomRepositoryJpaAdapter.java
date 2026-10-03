package uet.edu.net.booking_service.module.inventory.persistence;

import org.springframework.stereotype.Repository;
import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;
import uet.edu.net.booking_service.module.inventory.service.port.RoomRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepositoryJpaAdapter implements RoomRepository {

    private final SpringDataRoomRepository springDataRepository;
    private final RoomMapper mapper;

    public RoomRepositoryJpaAdapter(
            SpringDataRoomRepository springDataRepository,
            RoomMapper mapper
    ) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Room> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Room> findActiveRooms(
            RoomType roomType,
            Integer minCapacity,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return springDataRepository.findActiveRooms(
                        RoomStatus.ACTIVE,
                        roomType,
                        minCapacity,
                        minPrice,
                        maxPrice
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Room save(Room room) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(room)));
    }

    @Override
    public boolean existsByRoomNumber(String roomNumber) {
        return springDataRepository.existsByRoomNumber(roomNumber);
    }

    @Override
    public boolean existsByRoomNumberAndIdNot(String roomNumber, Long id) {
        return springDataRepository.existsByRoomNumberAndIdNot(roomNumber, id);
    }
}