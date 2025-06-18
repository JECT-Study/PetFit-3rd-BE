package ject.petfit.global.exception;

import ject.petfit.domain.user.exception.AuthUserException;
import ject.petfit.domain.user.exception.InvalidGrantException;
import ject.petfit.global.common.ApiResponse;
import ject.petfit.global.jwt.exception.TokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀할 예외 처리 핸들러
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ApiResponse<Void> response = ApiResponse.fail(
                e.getHttpStatus().value(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AuthUserException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(AuthUserException e) {
        ApiResponse<Void> response = ApiResponse.fail(
                e.getHttpStatus().value(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(TokenException e) {
        ApiResponse<Void> response = ApiResponse.fail(
                e.getHttpStatus().value(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(InvalidGrantException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(InvalidGrantException e) {
        ApiResponse<Void> response = ApiResponse.fail(
                e.getHttpStatus().value(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    // 그외 모든 예외 처리 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ApiResponse<Void> response = ApiResponse.fail(
                500,
                "서버 내부 오류가 발생하였습니다."
        );
        return ResponseEntity.status(500).body(response);
    }
}
