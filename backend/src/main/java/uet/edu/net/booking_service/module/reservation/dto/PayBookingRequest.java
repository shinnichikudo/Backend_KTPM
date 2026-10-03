package uet.edu.net.booking_service.module.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayBookingRequest {

    @NotBlank(message = "Số thẻ không được để trống")
    private String cardNumber;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;
}
