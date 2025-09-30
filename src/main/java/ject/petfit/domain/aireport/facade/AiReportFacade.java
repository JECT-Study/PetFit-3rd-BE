package ject.petfit.domain.aireport.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ject.petfit.domain.aireport.dto.request.DateRequestDto;
import ject.petfit.domain.aireport.dto.response.AiReportResponseDto;
import ject.petfit.domain.aireport.entity.AiReport;
import ject.petfit.domain.aireport.sevice.AiReportCommandService;
import ject.petfit.domain.aireport.sevice.AiReportQueryService;
import ject.petfit.domain.aireport.sevice.AiReportGenerationService;
import ject.petfit.domain.entry.dto.EntryDailyResponse;
import ject.petfit.domain.entry.facade.EntryFacade;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.service.PetQueryService;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.enums.RoutineStatus;
import ject.petfit.domain.remark.dto.response.RemarkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AiReportFacade {

    private final EntryFacade entryFacade;
    private final AiReportQueryService aiReportQueryService;
    private final PetQueryService petQueryService;
    private final AiReportGenerationService aiReportGenerationService;
    private final AiReportCommandService aiReportCommandService;

    public String generateInputJson(DateRequestDto dateRequestDto)
            throws JsonProcessingException {

        LocalDate start = dateRequestDto.getStartDate();
        LocalDate end = dateRequestDto.getEndDate();
        
        // Pet 정보 조회
        Pet pet = petQueryService.getPetOrThrow(dateRequestDto.getPetId());
        
        // 전체 JSON 구조 생성
        Map<String, Object> inputJson = new HashMap<>();
        
        // pet_info 추가
        Map<String, Object> petInfo = new HashMap<>();
        petInfo.put("name", pet.getName());
        petInfo.put("species", pet.getType());
        petInfo.put("age", calculateAge(pet.getBirthDate()));
        inputJson.put("pet_info", petInfo);
        
        // daily_records 생성
        List<Map<String, Object>> dailyRecords = new ArrayList<>();

        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            EntryDailyResponse dailyEntry = entryFacade.getDailyEntries(dateRequestDto.getPetId(), d);
            
            // 각 날짜별 데이터 생성
            Map<String, Object> dailyRecord = new HashMap<>();
            dailyRecord.put("date", d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            // 루틴 데이터를 카테고리별로 변환
            Map<String, Object> routineData = convertRoutinesToCategoryMap(dailyEntry.getRoutineResponseList());
            dailyRecord.putAll(routineData);
            
            // 특이사항 추가
            List<String> remarks = getRemarkContent(dailyEntry.getRemarkResponseList());
            if (!remarks.isEmpty()) {
                dailyRecord.put("remark", remarks);
            }
            
            dailyRecords.add(dailyRecord);
        }
        
        inputJson.put("daily_records", dailyRecords);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(inputJson);
    }

    public AiReportResponseDto createAiReport(DateRequestDto dateRequestDto) throws JsonProcessingException {
        // 입력 JSON 생성
        String inputJson = generateInputJson(dateRequestDto);
        
        // AI 리포트 생성
        String aiReportContent = aiReportGenerationService.generateAiReport(inputJson);
        String summaryTitle = aiReportGenerationService.generateSummaryTitle(aiReportContent);

        // AI 리포트 저장 
        AiReport aiReport = aiReportCommandService.saveAiReport(
            dateRequestDto.getPetId(), summaryTitle, aiReportContent,
            dateRequestDto.getStartDate(), dateRequestDto.getEndDate()
        );
        
        // 응답 DTO 생성 
        return AiReportResponseDto.from(aiReport);
    }
    
    private Map<String, Object> convertRoutinesToCategoryMap(List<RoutineResponse> routineList) {
        Map<String, Object> categoryMap = new HashMap<>();
        
        // 각 카테고리별로 데이터 변환
        for (RoutineResponse routine : routineList) {
            String category = routine.getCategory();
            Map<String, Object> categoryData = new HashMap<>();
            
            // normal 상태 판단 (CHECKED면 true, 그 외는 false)
            boolean isNormal = routine.getStatus() == RoutineStatus.CHECKED;
            categoryData.put("normal", isNormal);
            
            // amount 설정 (target_amount 또는 actual_amount)
            if (routine.getTargetAmount() != null) {
                categoryData.put("amount", routine.getActualAmount() != null ? 
                    routine.getActualAmount() : routine.getTargetAmount());
            }
            
            // issue 설정 (MEMO 상태이거나 content가 있는 경우)
            if (routine.getStatus() == RoutineStatus.MEMO && routine.getContent() != null) {
                categoryData.put("issue", routine.getContent());
            } else if (!isNormal && routine.getContent() != null) {
                categoryData.put("issue", routine.getContent());
            }

            categoryMap.put(category, categoryData);
        }
        
        return categoryMap;
    }
    
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    private List<String> getRemarkContent(List<RemarkResponse> remarkList) {
        if (remarkList == null || remarkList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return remarkList.stream()
                .map(remark -> remark.getTitle() + ": " + remark.getContent())
                .toList();
    }

    public AiReportResponseDto getAiReport(Long reportId) {
        AiReport aiReport = aiReportQueryService.getAiReportOrThrow(reportId);
        return AiReportResponseDto.from(aiReport);
    }

    public List<AiReportResponseDto> getAiReportsByPetId(Long petId) {
        List<AiReport> aiReports = aiReportQueryService.getAiReportsByPetId(petId);
        return aiReports.stream()
                .map(AiReportResponseDto::from)
                .toList();
    }

    public void deleteAiReport(Long reportId) {
        aiReportCommandService.deleteAiReport(reportId);
    }
}
