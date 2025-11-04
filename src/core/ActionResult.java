package core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 콩(Bean)의 한 번의 행동 결과를 저장하고 표현하는 데이터 객체
 */
public class ActionResult {

    private LocalDateTime timestamp;
    private String subject;
    private String actionDescription;
    private String resultMessage;
    private Map<Emotion, Integer> emotionChanges;
    private boolean success;

    /**
     * {@code ActionResult} 객체를 생성합니다.
     *
     * @param subject           행동의 주체
     * @param actionDescription 행동 설명 문자열
     * @param resultMessage     행동의 결과 메시지
     * @param emotionChanges    감정 변화 맵
     * @param success           성공 여부
     */
    public ActionResult(String subject, String actionDescription,
                        String resultMessage, Map<Emotion, Integer> emotionChanges, boolean success) {
        this.timestamp = LocalDateTime.now();
        this.subject = subject;
        this.actionDescription = actionDescription;
        this.resultMessage = resultMessage;
        this.emotionChanges = emotionChanges;
        this.success = success;
    }

    /**
     * 감정 변화 맵을 반환.
     */
    public Map<Emotion, Integer> getEmotionChanges() {
        return emotionChanges;
    }

    /**
     * 행동 결과를 사람이 읽기 좋은 문자열 형태로 반환
     */
    public String formatForDisplay() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(timestamp.format(fmt)).append("]\n");
        sb.append(actionDescription).append("\n");
        sb.append(resultMessage).append("\n");
        sb.append(formatEmotionChanges());
        return sb.toString();
    }

    /**
     * 감정 변화 목록을 문자열로 변환합니다.
     * 변화가 0인 항목은 생략
     */
    private String formatEmotionChanges() {
        StringBuilder sb = new StringBuilder();
        for (var entry : emotionChanges.entrySet()) {
            int delta = entry.getValue();
            if (delta != 0) {
                String sign = delta > 0 ? "+" : "";
                sb.append(entry.getKey()).append(" ").append(sign).append(delta).append(", ");
            }
        }
        if (sb.length() > 2) sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    /**
     * 행동의 주체를 반환
     */
    public String getSubject() { return subject; }

    /**
     * 행동이 성공했는지 여부를 반환
     */
    public boolean isSuccess() { return success; }
}
