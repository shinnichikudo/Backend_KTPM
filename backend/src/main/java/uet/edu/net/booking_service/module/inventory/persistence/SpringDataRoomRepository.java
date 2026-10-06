package uet.edu.net.booking_service.module.inventory.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;

import java.math.BigDecimal;
import java.util.List;

public interface SpringDataRoomRepository extends JpaRepository<RoomEntity, Long> {

    @Query("""
            select room from RoomEntity room
            where room.status = :status
              and (:roomType is null or room.roomType = :roomType)
              and (:minCapacity is null or room.capacity >= :minCapacity)
              and (:minPrice is null or room.basePrice >= :minPrice)
              and (:maxPrice is null or room.basePrice <= :maxPrice)
            order by room.roomNumber
            """)
    List<RoomEntity> findActiveRooms(
            @Param("status") RoomStatus status,
            @Param("roomType") RoomType roomType,
            @Param("minCapacity") Integer minCapacity,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    boolean existsByRoomNumber(String roomNumber);

    boolean existsByRoomNumberAndIdNot(String roomNumber, Long id);
}