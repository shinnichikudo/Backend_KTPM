package uet.edu.net.booking_service.module.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Khóa ngoại mềm trỏ sang Module Auth (Không dùng @ManyToOne)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "total_guest", nullable = false)
    private int totalGuest;

    // Tổng tiền của cả hóa đơn
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<BookingDetail> bookingDetails = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}