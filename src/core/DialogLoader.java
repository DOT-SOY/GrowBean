package core;

import utils.*;
import java.util.*;
import exceptions.*;

/**
 * CSV에서 행동 정보를 읽어 ActionResult로 변환하는 클래스.
 * CSV 구조: action, actionMsg, successMsg, failMsg, successRate, successChanges, failChanges
 */
public class DialogLoader {

    /** CSV 파일별 캐시 (중복 로딩 방지) */
    private static final Map<String, List<String[]>> cache = new HashMap<>();

    /**
     * CSV에서 특정 행동(action)의 결과를 읽어 ActionResult로 반환합니다.
     *
     * @param fileName  CSV 파일 이름 (예: "lazy.csv")
     * @param action    찾을 행동 이름 (예: "eat", "rest" 등)
     * @param beanName  콩 이름
     * @return ActionResult 결과 객체 (예외 발생 시 null)
     */
    public static ActionResult getFromCsv(String fileName, String action, String beanName) {
        try {
            // ===== 캐시 확인 및 로드 =====
            List<String[]> lines = cache.get(fileName);
            if (lines == null) {
                lines = Utils.loadCsv(fileName, true);
                cache.put(fileName, lines);
            }

            // ===== 해당 action 라인 필터링 =====
            List<String[]> matches = new ArrayList<>();
            for (String[] cols : lines) {
                if (cols.length > 0 && cols[0].equals(action)) {
                    matches.add(cols);
                }
            }

            // ===== 예외 처리: CSV에 행동이 없을 경우 =====
            if (matches.isEmpty()) {
                throw BeanException.beanAction("CSV에서 [" + action + "] 항목을 찾을 수 없습니다.");
            }

            // ===== 랜덤 선택 및 결과 생성 =====
            String[] pick = matches.get(Utils.range(0, matches.size() - 1));
            return buildResult(pick, beanName);

        } catch (BeanException e) {
            // Bean 관련 예외 (CSV, 로직 오류 등)
            ExceptionHandler.handle(e);
            return null;

        } catch (Exception e) {
            // 예기치 못한 일반 예외
            ExceptionHandler.handle(BeanException.csvLoad(fileName, e));
            return null;
        }
    }

    /** ActionResult 조립 */
    private static ActionResult buildResult(String[] cols, String beanName) {
        String actionMsg = Utils.replaceName(cols[1], beanName);
        String resultSuccess = Utils.replaceName(cols[2], beanName);
        String resultFail = Utils.replaceName(cols[3], beanName);

        boolean success = Utils.chance(Double.parseDouble(cols[4]));
        String resultMsg = success ? resultSuccess : resultFail;

        Map<Emotion, Integer> emotionChanges =
                Utils.parseEmotionChanges(success ? cols[5] : cols[6]);

        return new ActionResult(beanName, actionMsg, resultMsg, emotionChanges, success);
    }
}
