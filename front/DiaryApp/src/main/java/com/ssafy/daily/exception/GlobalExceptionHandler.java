package com.ssafy.daily.exception;

import com.ssafy.daily.common.StatusResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientFundsException(InsufficientFundsException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    }

    @ExceptionHandler(StickerNotFoundException.class)
    public ResponseEntity<String> handleStickerNotFoundException(StickerNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(MyNotFoundException.class)
    public ResponseEntity<String> handleMyNotFoundException(MyNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(AlreadyOwnedException.class)
    public ResponseEntity<StatusResponse> handleAlreadyOwnedException(AlreadyOwnedException e) {
        StatusResponse response = new StatusResponse(409, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StatusResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        StatusResponse response = new StatusResponse(400, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(QuestNotFoundException.class)
    public ResponseEntity<String> handleQuestNotFoundException(QuestNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<String> handleLoginFailedException(LoginFailedException e) {
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<String> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(EmptyOcrResultException.class)
    public ResponseEntity<StatusResponse> handleEmptyOcrResultException(EmptyOcrResultException ex) {
        StatusResponse response = new StatusResponse(400, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WordMismatchException.class)
    public ResponseEntity<StatusResponse> handleWordMismatchException(WordMismatchException ex) {
        StatusResponse response = new StatusResponse(422, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(S3UploadException.class)
    public ResponseEntity<StatusResponse> handleS3UploadException(S3UploadException ex) {
        StatusResponse response = new StatusResponse(500, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
