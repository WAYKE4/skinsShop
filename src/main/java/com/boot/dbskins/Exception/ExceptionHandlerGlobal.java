package com.boot.dbskins.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice

public class ExceptionHandlerGlobal {

    @ExceptionHandler(CustomValidException.class)
    public ResponseEntity<HttpStatus> customValidExceptionHandler(Exception exception) {
        log.error("Valid exception" + exception);
        return new ResponseEntity<>(HttpStatus.valueOf(401));
    }

    @ExceptionHandler(SameUserInDatabaseLogin.class)
    public ResponseEntity<HttpStatus> sameUserInDatabase(Exception exception) {
        log.error("User already exist with this login! " + exception);
        return new ResponseEntity<>(HttpStatus.valueOf(409));
    }

    @ExceptionHandler(InsufficientFunds.class)
    public ResponseEntity<HttpStatus> insufficientFunds(Exception exception) {
        log.error("Insufficient Funds! " + exception);
        return new ResponseEntity<>(HttpStatus.valueOf(400));
    }

    @ExceptionHandler(UserOrSkinNotExist.class)
    public ResponseEntity<HttpStatus> userOrSkinNotExist(Exception exception) {
        log.error(exception + "");
        return new ResponseEntity<>(HttpStatus.valueOf(400));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<HttpStatus> forbiddenException(Exception exception) {
        log.error(exception + "");
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SameUserInDatabaseEmail.class)
    public ResponseEntity<HttpStatus> sameUserInDatabaseEmail(Exception exception) {
        log.error("User already exist with this email! " + exception);
        return new ResponseEntity<>(HttpStatus.valueOf(409));
    }
}
