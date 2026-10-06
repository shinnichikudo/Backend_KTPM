package uet.edu.net.booking_service.module.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "booking_nights",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_night", columnNames = {"room_id", "night_date"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingNight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "night_date", nullable = false)
    private LocalDate nightDate;
}

