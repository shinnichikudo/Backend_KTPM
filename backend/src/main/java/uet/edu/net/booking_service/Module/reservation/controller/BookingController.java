package uet.edu.net.booking_service.Module.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import uet.edu.net.booking_service.Module.reservation.dto.BookingResponse;
import uet.edu.net.booking_service.Module.reservation.dto.CreateBookingRequest;
import uet.edu.net.booking_service.Module.reservation.entity.Booking;
import uet.edu.net.booking_service.Module.reservation.service.BookingService;



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

    @DeleteMapping("/{id}/cancel")
    public BookingResponse cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
        Booking booking = bookingService.cancelBooking(id, userId);

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
