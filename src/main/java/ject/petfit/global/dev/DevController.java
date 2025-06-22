package ject.petfit.global.dev;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.entry.service.EntryService;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.global.common.ApiResponse;
import ject.petfit.global.exception.CustomException;
import ject.petfit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Tag(name = "개발용 테스트 API")
@RequestMapping("/dev")
@RequiredArgsConstructor
public class DevController {

    private final EntryService entryService;
    private final PetRepository petRepository;

    @Operation(summary = "스웨거 동작 확인",
            description = "상세 설명")
    @GetMapping()
    public ResponseEntity<String> swaggerTest() {
        return ResponseEntity.ok("Swagger Test");
    }

    @Operation(summary = "ApiResponse 확인용 예제",
            description = "ApiResponse 적용 후 성공/실패, 예외처리 응답 형태 확인")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> getUserName(@PathVariable Long id) {
        if (id > 0) {
            // 성공 응답
            return ResponseEntity
                    .ok(ApiResponse.success("김철수"));
        }else if(id == 0){
            // 실패 응답
            return ResponseEntity
                    .status(404)
                    .body(ApiResponse.fail("DEV-404", "사용자를 찾을 수 없습니다(직접 기재)"));
        }
        // 예외처리 응답
        throw new CustomException(ErrorCode.DEV_NOT_FOUND);
    }

    @PostMapping("/entries/{petId}/{entryDate}")
    public ResponseEntity<ApiResponse<String>> createEntry(
            @PathVariable Long petId,
            @PathVariable String entryDate
    ) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        entryService.createEntry(pet, LocalDate.parse(entryDate));
        return ResponseEntity
                .ok(ApiResponse.success("Entry 생성 성공"));
    }
}
