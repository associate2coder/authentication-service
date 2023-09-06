package ua.com.associate2coder.authenticationservice.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException e, WebRequest request) {
        String body = "Something went wrong with your request.\n" +
                "Request description: "  + request.getDescription(false) + "\n" +
                "Exception message: " + e.getMessage();
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StatusAlreadyAppliesException.class)
    public ResponseEntity<String> handleStatusAlreadyAppliesException(StatusAlreadyAppliesException e, WebRequest request) {
        String body = "Status, which you are trying to set, has already been set. \n" +
                "Request description: "  + request.getDescription(false) + "\n" +
                "Exception message: " + e.getMessage();
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<String> handleElementNotFoundException(ElementNotFoundException e, WebRequest request) {
        String body = "Element, which you are looking for, has not been found. \n" +
                "Request description: "  + request.getDescription(false) + "\n" +
                "Exception message: " + e.getMessage();
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleUnprocessableFeedbackException(MethodArgumentNotValidException e, WebRequest request) {
        String body = "Your input data failed to satisfy validation criteria. \n" +
                "Request description: "  + request.getDescription(false) + "\n" +
                "Exception message: " + e.getMessage();
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
