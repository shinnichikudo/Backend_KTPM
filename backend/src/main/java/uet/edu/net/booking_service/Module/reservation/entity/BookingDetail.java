package uet.edu.net.booking_service.Module.reservation.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    // Lưu giá chốt tại thời điểm đặt, không phụ thuộc vào giá gốc của bảng Room sau này
    @Column(name = "price_at_booking", nullable = false)
    private BigDecimal priceAtBooking;
}
