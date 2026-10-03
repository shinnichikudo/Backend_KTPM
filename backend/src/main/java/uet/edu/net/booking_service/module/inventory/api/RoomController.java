package uet.edu.net.booking_service.module.inventory.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;
import uet.edu.net.booking_service.module.inventory.service.RoomService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Validated
public class RoomController {

    private final RoomService roomService;
    private final RoomApiMapper mapper;

    public RoomController(RoomService roomService, RoomApiMapper mapper) {
        this.roomService = roomService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<RoomResponse> getRooms(
            @RequestParam(required = false) RoomType roomType,
            @RequestParam(required = false) @Min(1) Integer capacity,
            @RequestParam(required = false) @DecimalMin("0.01") BigDecimal minPrice,
            @RequestParam(required = false) @DecimalMin("0.01") BigDecimal maxPrice
    ) {
        return roomService.getActiveRooms(roomType, capacity, minPrice, maxPrice)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public RoomResponse getRoom(@PathVariable @Positive Long id) {
        return mapper.toResponse(roomService.getActiveRoomById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return mapper.toResponse(roomService.createRoom(mapper.toDomain(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse updateRoom(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateRoomRequest request
    ) {
        return mapper.toResponse(roomService.updateRoom(id, mapper.toDomain(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateRoom(@PathVariable @Positive Long id) {
        roomService.deactivateRoom(id);
    }
}