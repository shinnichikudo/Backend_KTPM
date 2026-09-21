package uet.edu.net.booking_service.Module.inventory.contract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Long id;
    private String roomNumber;
    private BigDecimal basePrice;
    private int capacity;
    private String status;
}
