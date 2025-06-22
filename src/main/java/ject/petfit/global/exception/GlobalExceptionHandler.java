package ject.petfit.global.exception;

import ject.petfit.domain.entry.exception.EntryException;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.remark.exception.RemarkException;
import ject.petfit.domain.schedule.exception.ScheduleException;
import ject.petfit.domain.user.exception.AuthUserException;
import ject.petfit.domain.user.exception.InvalidGrantException;
import ject.petfit.global.common.ApiResponse;
import ject.petfit.global.jwt.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀할 예외 처리 핸들러
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.info(e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.fail(
                e.getCode(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AuthUserException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(AuthUserException e) {
        log.info(e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.fail(
                e.getCode(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(TokenException e) {
        ApiResponse<Void> response = ApiResponse.fail(
                e.getCode(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(InvalidGrantException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(InvalidGrantException e) {
        ApiResponse<Void> response = ApiResponse.fail(
                e.getCode(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(PetException.class)
    public ResponseEntity<ApiResponse<Void>> handlePetException(PetException e) {
        log.info(e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.fail(
                e.getCode(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(ScheduleException.class)
    public ResponseEntity<ApiResponse<Void>> handleScheduleException(ScheduleException e) {
        log.info(e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.fail(
                e.getCode(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(RemarkException.class)
    public ResponseEntity<ApiResponse<Void>> handleRemarkException(RemarkException e) {
        log.info(e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.fail(
                e.getCode(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(EntryException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntryException(EntryException e) {
        log.info(e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.fail(
                e.getCode(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    // 유효성 검사 예외 처리 핸들러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        ApiResponse<Void> response = ApiResponse.fail("INPUT-400", errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    // 그외 모든 예외 처리 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ApiResponse<Void> response = ApiResponse.fail(
                "SERVER-500",
                "서버 내부 오류가 발생하였습니다."
        );
        return ResponseEntity.status(500).body(response);
    }
}
