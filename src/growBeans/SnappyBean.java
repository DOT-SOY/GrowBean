package growBeans;

import core.ActionResult;
import core.Beans;
import core.DialogLoader;
import core.Emotion;

/**
 * 까칠한 성격의 콩
 * - 감정 기복이 크고 스트레스와 분노가 잘 오름
 * - 성공 시에도 냉소적, 실패 시 쉽게 폭발함
 * - 각 행동의 결과는 snappy.csv 에서 로드됨
 */
public class SnappyBean extends Beans {

    public SnappyBean(String name) {
        super(name);
    }

    // =============================
    // 행동 정의 (CSV 로드)
    // =============================
    @Override public ActionResult eat()   { return DialogLoader.getFromCsv("snappy.csv", "eat", getName()); }
    @Override public ActionResult rest()  { return DialogLoader.getFromCsv("snappy.csv", "rest", getName()); }
    @Override public ActionResult play()  { return DialogLoader.getFromCsv("snappy.csv", "play", getName()); }
    @Override public ActionResult heal()  { return DialogLoader.getFromCsv("snappy.csv", "heal", getName()); }
    @Override public ActionResult goOut() { return DialogLoader.getFromCsv("snappy.csv", "goOut", getName()); }
    @Override public ActionResult work()  { return DialogLoader.getFromCsv("snappy.csv", "work", getName()); }

    @Override
    public String getPersonalityName() { return "까칠한 콩"; }

    // =============================
    // 이벤트 반응 오버라이드
    // =============================
    @Override
    protected void checkSpecialConditions() {

        // 공통 이벤트 우선 실행
        super.checkSpecialConditions();

        // 기본 성격 효과 (행동 후 자동 스트레스 상승)
        changeEmotion(Emotion.STRESS, +2);
        changeEmotion(Emotion.ANGER, +1);

        // 평균 감정 계산
        int total = 0, count = 0;
        for (Emotion e : emotions.keySet()) {
            total += emotions.get(e);
            count++;
        }
        int avg = (count > 0) ? total / count : 50;

        // 무기력
        if (avg <= 25) {
            System.out.println(name + ": ...하, 진짜 다 귀찮네. 나 건드리지 마.");
            changeEmotion(Emotion.MOTIVATION, -5);
            changeEmotion(Emotion.STRESS, +5);
            changeEmotion(Emotion.ANGER, +10);
            energy -= 5;
        }

        // 행복 ↓, 슬픔 ↑
        int happy = emotions.getOrDefault(Emotion.HAPPY, 50);
        int sad   = emotions.getOrDefault(Emotion.SAD, 50);
        if (happy <= 10 || sad >= 90) {
            System.out.println(name + ": ...그냥 좀 내버려둘래?");
            changeEmotion(Emotion.ANGER, +15);
            changeEmotion(Emotion.STRESS, +10);
            changeEmotion(Emotion.TRUST, -10);
        }

        // 에너지 부족
        if (energy < 25 && !recentlyHealed) {
            System.out.println(name + ": 으으... 몸도 짜증나고 기분도 엉망이야!");
            changeEmotion(Emotion.STRESS, +10);
            changeEmotion(Emotion.HAPPY, -5);
            changeEmotion(Emotion.MOTIVATION, -5);
            energy -= 5;
        }

        // 신뢰 낮을 때 폭언
        int trust = emotions.getOrDefault(Emotion.TRUST, 50);
        if (trust <= 10) {
            System.out.println(name + ": 나한테 너무 기대하지 마라. 진짜 화내기 전에.");
            changeEmotion(Emotion.TRUST, -5);
            changeEmotion(Emotion.STRESS, +10);
            changeEmotion(Emotion.ANGER, +10);
        }

        // 신뢰 높을 때
        if (trust >= 90) {
            System.out.println(name + ": ...흥, 뭐. 당신이 싫진 않아. 딱히 고맙지도 않고.");
            changeEmotion(Emotion.HAPPY, +5);
            changeEmotion(Emotion.MOTIVATION, +3);
            changeEmotion(Emotion.ANGER, -5);
        }

        // 분노
        int anger = emotions.getOrDefault(Emotion.ANGER, 50);
        if (anger >= 90) {
            System.out.println(name + "이(가) 폭발했습니다! 주변이 살벌해집니다...");
            changeEmotion(Emotion.STRESS, +10);
            changeEmotion(Emotion.HAPPY, -15);
            energy -= 10;
        }

        // 스트레스 낮을 때
        int stress = emotions.getOrDefault(Emotion.STRESS, 50);
        if (stress <= 15) {
            System.out.println(name + ": ...뭐, 지금은 그럭저럭 괜찮네.");
            changeEmotion(Emotion.MOTIVATION, +5);
            changeEmotion(Emotion.HAPPY, +3);
        }

        // 10회 행동마다 진정
        if (actionsCount > 0 && actionsCount % 10 == 0) {
            System.out.println(name + ": ...하, 조금 진정됐어.");
            changeEmotion(Emotion.STRESS, -10);
            changeEmotion(Emotion.ANGER, -10);
            changeEmotion(Emotion.HAPPY, +5);
        }

        // 5회마다 짜증 완화
        if (actionsCount > 0 && actionsCount % 5 == 0) {
            changeEmotion(Emotion.ANGER, -3);
        }
    }
}
