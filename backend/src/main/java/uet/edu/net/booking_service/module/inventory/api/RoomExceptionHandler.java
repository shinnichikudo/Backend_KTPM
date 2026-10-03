package uet.edu.net.booking_service.module.inventory.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uet.edu.net.booking_service.module.inventory.service.exception.RoomNotFoundException;
import uet.edu.net.booking_service.module.inventory.service.exception.RoomNumberAlreadyExistsException;

@RestControllerAdvice(basePackageClasses = RoomController.class)
public class RoomExceptionHandler {

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(RoomNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("ROOM_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(RoomNumberAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDuplicate(RoomNumberAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("ROOM_NUMBER_ALREADY_EXISTS", exception.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiError> handleValidation(Exception exception) {
        return ResponseEntity.badRequest()
                .body(new ApiError("VALIDATION_ERROR", "Request validation failed"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleInvalidArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(new ApiError("INVALID_ARGUMENT", exception.getMessage()));
    }

    public record ApiError(String code, String message) {
    }
}