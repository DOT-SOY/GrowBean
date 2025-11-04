package core;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import exceptions.*;

/**
 * Beans (콩의 추상 클래스)
 * - 공통 속성 및 감정 상태 관리
 * - 행동 결과 처리, 이벤트 감시, 출력 기능 포함
 * - SOLID 원칙을 간결하게 반영 (클래스 추가 없음)
 */
public abstract class Beans implements Behavior, Serializable {

    private static final long serialVersionUID = 1L;

    // ==============================
    // 기본 속성
    // ==============================
    protected String name;
    protected int energy;
    protected Map<Emotion, Integer> emotions;

    protected String lastAction = "";
    protected int actionsCount = 0; 
    protected boolean recentlyHealed = false;

    // 이벤트 핸들러 (DIP 적용)
    private transient Consumer<BeanEvents> eventHandler = ExceptionHandler::handle;

    // ==============================
    // 생성자
    // ==============================
    public Beans(String name) {
        this.name = name;
        this.energy = 100;
        this.emotions = new EnumMap<>(Emotion.class);
        initEmotions();
    }

    // ==============================
    // 초기화
    // ==============================
    private void initEmotions() {
        for (Emotion e : Emotion.values()) {
            emotions.put(e, 50);
        }
    }

    // ==============================
    // 상태 관리 (에너지, 감정)
    // ==============================

    /** 감정 수치 변경 (0~100 범위 제한) */
    public void changeEmotion(Emotion emotion, int delta) {
        int current = emotions.getOrDefault(emotion, 50);
        int next = Math.max(0, Math.min(100, current + delta));
        emotions.put(emotion, next);
    }

    /** 에너지 수치 변경 (0~100 사이로 유지, 이벤트 발생) */
    public void changeEnergy(int delta) {
        int before = energy;
        int after = before + delta;

        if (after > 100) {
            energy = 100;
        } else if (after < 0) {
            energy = 0;
            throw BeanEvents.energyDepleted(after);
        } else {
            energy = after;
        }
    }

    /** 전체 상태값을 0~100 사이로 보정 */
    private void clampStats() {
        if (energy > 100) energy = 100;
        if (energy < 0) energy = 0;

        for (Emotion e : emotions.keySet()) {
            int val = emotions.get(e);
            if (val > 100) val = 100;
            if (val < 0) val = 0;
            emotions.put(e, val);
        }
    }

    // ==============================
    // 행동 결과 처리
    // ==============================
    public void applyActionResult(ActionResult result, String actionName) {
        if (result == null) return;

        // 행동 불가 조건
        if (energy == 0 && !"heal".equalsIgnoreCase(actionName)) {
            System.out.println(name + "은(는) 기절해서 아무 행동도 할 수 없습니다... 치료가 필요합니다.\n");
            return;
        }

        actionsCount++;
        lastAction = actionName;

        // 감정 변화 적용
        for (Map.Entry<Emotion, Integer> entry : result.getEmotionChanges().entrySet()) {
            changeEmotion(entry.getKey(), entry.getValue());
        }

        // 치료 행동
        if ("heal".equalsIgnoreCase(actionName)) {
            int recovery = result.isSuccess() ? 50 : 30;
            try {
                changeEnergy(recovery);
                recentlyHealed = true;
                System.out.println(name + "의 체력이 " + recovery + " 회복되었습니다!");
            } catch (BeanEvents e) {
                eventHandler.accept(e);
            }
        } else {
            recentlyHealed = false;
        }

        // 결과 출력
        System.out.println(result.formatForDisplay() + "\n");

        // 상태 감시
        checkSpecialConditions();
    }

    // ==============================
    // 특수 상태 감시 (이벤트)
    // ==============================
    protected void checkSpecialConditions() {
        try {
            if (energy <= 0) throw BeanEvents.beanFainted();

            int trust = emotions.getOrDefault(Emotion.TRUST, 50);
            int happy = emotions.getOrDefault(Emotion.HAPPY, 50);
            int sad = emotions.getOrDefault(Emotion.SAD, 50);
            int fear = emotions.getOrDefault(Emotion.FEAR, 50);
            int anger = emotions.getOrDefault(Emotion.ANGER, 50);
            int stress = emotions.getOrDefault(Emotion.STRESS, 50);

            if (trust <= 10) { changeEmotion(Emotion.HAPPY, -10); changeEnergy(-15); throw BeanEvents.beanRanAway(); }
            if (happy <= 0 || sad >= 100) { changeEmotion(Emotion.MOTIVATION, -10); throw BeanEvents.beanDepressed(); }
            if (fear >= 100) { changeEmotion(Emotion.HUNGER, -15); throw BeanEvents.beanNauseous(); }
            if (anger >= 90) { changeEnergy(-10); throw BeanEvents.beanAngry(); }
            if ("rest".equalsIgnoreCase(lastAction) && stress >= 80) { changeEmotion(Emotion.STRESS, +5); throw BeanEvents.beanInsomnia(); }
            if (trust >= 100) { changeEmotion(Emotion.HAPPY, +10); throw BeanEvents.beanLoveful(); }

            // 평균 기반 무기력
            int total = 0, count = 0;
            for (int value : emotions.values()) { total += value; count++; }
            double avg = (count > 0) ? (double) total / count : 50.0;
            if (avg <= 20) { changeEmotion(Emotion.MOTIVATION, -5); throw BeanEvents.beanExhausted(); }

            // 성장 / 병듦
            if (actionsCount > 0 && actionsCount % 10 == 0) { changeEnergy(+5); changeEmotion(Emotion.MOTIVATION, +5); throw BeanEvents.beanGrowth(); }
            if (energy < 20 && !recentlyHealed) { changeEmotion(Emotion.STRESS, +10); throw BeanEvents.beanSick(); }

        } catch (BeanEvents e) {
            eventHandler.accept(e);
        } finally {
            clampStats(); // 항상 범위 보정
        }
    }

    // ==============================
    // 출력 (상태 표시)
    // ==============================
    public void showStatus() {
        System.out.println("=== " + name + "의 현재 상태 ===");
        System.out.println("에너지: " + energy);
        for (var e : emotions.entrySet()) {
            System.out.printf("%-10s : %d%n", e.getKey(), e.getValue());
        }
        System.out.println("============================\n");
    }

    // ==============================
    // 유틸
    // ==============================
    public void setEventHandler(Consumer<BeanEvents> handler) {
        this.eventHandler = handler;
    }

    // ==============================
    // Getter
    // ==============================
    public String getName() { return name; }
    public int getEnergy() { return energy; }
    public Map<Emotion, Integer> getEmotions() { return emotions; }

    public abstract String getPersonalityName();
}
