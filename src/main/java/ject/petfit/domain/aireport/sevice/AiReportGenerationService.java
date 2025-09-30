package ject.petfit.domain.aireport.sevice;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiReportGenerationService {

    @Value("${openai.api-key}")
    private String apiKey;

    private static final String MODEL = "gpt-4o-mini";
    private static final double TEMPERATURE = 0.7;

    public String generateAiReport(String inputJson) {
        try {
            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));
            
            String prompt = createPrompt(inputJson);
            
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(MODEL)
                    .messages(List.of(
                            // 프롬프팅 내용
                            new ChatMessage(ChatMessageRole.SYSTEM.value(), 
                            """
                                당신은 반려동물 건강 관리 및 행동 분석 전문가이자 수의학자, 수의사입니다.
                                주어진 데이터를 분석하여 상세하고 유용하며 이해하기 쉬운 반려동물 건강 보고서를 작성하세요.
                                """),
                            
                            // 입력 내용
                            new ChatMessage(ChatMessageRole.USER.value(), prompt)
                    ))
                    .temperature(TEMPERATURE)
                    .maxTokens(1000)
                    .build();

            String response = service.createChatCompletion(completionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            log.info("AI 리포트 생성 완료");
            return response;

        } catch (Exception e) {
            log.error("AI 리포트 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("AI 리포트 생성에 실패했습니다: " + e.getMessage());
        }
    }

    public String generateSummaryTitle(String aiReportContent) {
        try {
            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(MODEL)
                    .messages(List.of(
                            new ChatMessage(ChatMessageRole.SYSTEM.value(),
                                """
                                    당신은 보고서 요약 전문가입니다. 주어진 보고서를 요약하여 제목을 작성해주세요
                                    제목은 5~7 단어 이내로 작성하고, 이상 징후 1~2가지 내용 혹은 건강 추세 및 관리 팁에 대한 핵심 내용을 반드시 반영하며,
                                    '반려동물' 이라는 단어는 포함하지 않고, 반려 동물의 이름(pet_info의 name)을 포함하지 않는 형태로 한 줄로 끝내주세요.
                                    """),
                            new ChatMessage(ChatMessageRole.USER.value(), aiReportContent)
                    ))
                    .temperature(TEMPERATURE)
                    .maxTokens(30)
                    .build();

            String response = service.createChatCompletion(completionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            return response;

        } catch (Exception e) {
            log.error("AI 리포트 요약 제목 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("AI 리포트 요약 제목 생성에 실패했습니다: " + e.getMessage());
        }
    }
    

    private String createPrompt(String inputJson) {
        return String.format("""
            다음은 반려동물의 일일 건강 기록 데이터입니다.
            반려동물의 종(species)과 나이(age)를 고려하여 분석하세요.
            그러나 분석 시에 기존 반려동물의 급여 상식으로 단정하지 않고 사용자가 제시하는 target amount 대비 actual amount를 우선시하여 분석하세요.
            (예: 기존 반려동물 급여 상식으로 하루 100g이 적정하더라도, 사용자가 target amount를 150g으로 설정했다면, 150g을 기준으로 분석)
            아래 JSON 데이터를 기반으로 최근 며칠간의 건강 패턴을 분석하고,\s
            보호자가 이해하기 쉬운 보고서를 작성하세요.
            
            [입력 Json 데이터 설명]
            - daily_records는 날짜별 반려동물 상태 기록입니다.
            - "date", "remark" 를 제외한 daily_records의 하위 키 값은 슬롯의 카테고리 이름입니다.
            - "date": 해당 날짜
            - "normal": true(정상)/false(이상)
            - "target_amount": 각 슬롯의 목표량
            - "actual_amount": 각 슬롯의 실제 기록량
            - "issue": 이상 발생 시(normal=false 일 때만 해당) 텍스트 설명 (사용자가 메모한 값)
            - "remark": 특이사항 내용
            
            [분석 및 보고서 작성 지침]
            0. 이상징후 발생 날짜에 대한 총 합산 횟수 및 지속 발생 여부, 유의미한 이상 징후에 대해서만 분석할 것.
            1. 데이터를 바탕으로 **날짜별로 나열하지 말고** 전체적인 추세를 분석, 요약해서 1~2문장으로 작성할 것.
            	기록의 전반적 경향만 강조하고, 목표량이나 정상 범위와 비교해 부족/초과/정상 여부를 설명할 것. 불필요한 횟수, 수치, 날짜별 빈도 나열은 하지 말 것.
            	이상 징후의 발생 일자를 구체적으로 명시하지 말 것.
            2. remark는 보호자가 직접 입력한 특이사항이므로 반드시 분석에는 반영한다.
            	remark의 이상징후 내용과 issue의 이상징후 내용이 비슷한 맥락으로 중복되는 경우에는 하나로 간주한다.
            3. 이상 징후 발생 설명 규칙
            	- issue 항목에 대해 설명한다.
            		- "normal": false 이면서 issue가 작성된 경우에는 issue 내용을 기준으로 이상 징후를 설명한다.
            		- "normal": false 이면서 issue가 없는 경우에는 "이상 징후 없음"으로 간주, 이상 징후에 포함하지 않는다.
            	- 따라서 이상 징후 분석은 "종류별 이상 징후 발생 경우를 명확히 구분해서 표기할 것
            4. 건강 점수 평점은 0.5점 단위로 0점부터 5점까지 채점할 것
            
            [출력 목표]
            1. 오늘의 건강 요약: 최근 데이터 기반 2~3줄 요약
            2. 이상 징후 분석: 이상 징후를 추출해 종류별로 상세 설명
            3. 질병 조기발견 가능성 분석: 이상 징후 분석 근거로 의심 질환을 진료 과목 별 평가 및 검사 권장
            4. 건강 추세 분석: 최근 며칠간 변화나 패턴이 있으면 언급
            5. 관리 팁과 권장 행동: 보호자가 취할 수 있는 조언 제공
            6. 건강 점수: 0~5점 (5점은 최상)
            
            [작성 스타일]
            - 따뜻하고 친절한 어조
            - 보호자가 쉽게 이해할 수 있도록 작성
            - 데이터 기반 근거를 명시
            - 필요한 경우 수의사 상담 권장
            
            데이터:
            %s
            
            보고서는 한국어로 작성하고, 반려동물 주인이 이해하기 쉽고 실용적인 내용으로 구성해주세요.
            """, inputJson);
    }
}
