package uet.edu.net.booking_service.module.reservation.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import uet.edu.net.booking_service.module.reservation.dto.BookingResponse;
import uet.edu.net.booking_service.module.reservation.dto.CreateBookingRequest;
import uet.edu.net.booking_service.module.reservation.entity.Booking;
import uet.edu.net.booking_service.module.reservation.service.BookingService;



@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking( 
        @RequestParam Long userId, 
        @RequestBody CreateBookingRequest request) { 
            Booking booking = bookingService.createBooking( 
                userId, 
                request.getRoomId(), 
                request.getCheckInDate(), 
                request.getCheckOutDate(), 
                request.getTotalGuest()
            );

            BookingResponse response = new BookingResponse();

            response.setId(booking.getId()); 
            response.setUserId(booking.getUserId()); 
            response.setCheckInDate(booking.getCheckInDate()); 
            response.setCheckOutDate(booking.getCheckOutDate()); 
            response.setTotalGuest(booking.getTotalGuest()); 
            response.setTotalPrice(booking.getTotalPrice()); 
            response.setStatus(booking.getStatus()); 
            response.setCreatedAt(booking.getCreatedAt());

            if (!booking.getBookingDetails().isEmpty()) { 
                response.setRoomId( 
                    booking.getBookingDetails().get(0).getRoomId() ); 
                }

            return response;
    }

    @GetMapping(path = "/{id}")
    public BookingResponse getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);

        BookingResponse response = new BookingResponse();

        response.setId(booking.getId()); 
        response.setUserId(booking.getUserId()); 
        response.setCheckInDate(booking.getCheckInDate()); 
        response.setCheckOutDate(booking.getCheckOutDate()); 
        response.setTotalGuest(booking.getTotalGuest()); 
        response.setTotalPrice(booking.getTotalPrice()); 
        response.setStatus(booking.getStatus()); 
        response.setCreatedAt(booking.getCreatedAt());

        if (!booking.getBookingDetails().isEmpty()) {
            response.setRoomId( 
                booking.getBookingDetails().get(0).getRoomId() 
            ); 
        }
        return response;
    }
    
}
