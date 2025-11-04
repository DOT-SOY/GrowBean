package growBeans;

import core.ActionResult;
import core.Beans;
import core.DialogLoader;
import core.Emotion;

/**
 * 착한 성격의 콩
 * - 배려심 많고, 긍정적인 감정을 잘 회복함
 * - 각 행동의 결과는 kind.csv 에서 로드됨
 */
public class KindBean extends Beans {

    public KindBean(String name) {
        super(name);
    }

    // =============================
    // 행동 정의 (CSV 로드)
    // =============================
    @Override public ActionResult eat()   { return DialogLoader.getFromCsv("kind.csv", "eat", getName()); }
    @Override public ActionResult rest()  { return DialogLoader.getFromCsv("kind.csv", "rest", getName()); }
    @Override public ActionResult play()  { return DialogLoader.getFromCsv("kind.csv", "play", getName()); }
    @Override public ActionResult heal()  { return DialogLoader.getFromCsv("kind.csv", "heal", getName()); }
    @Override public ActionResult goOut() { return DialogLoader.getFromCsv("kind.csv", "goOut", getName()); }
    @Override public ActionResult work()  { return DialogLoader.getFromCsv("kind.csv", "work", getName()); }

    @Override
    public String getPersonalityName() { return "착한 콩"; }

    // =============================
    // 이벤트 반응 오버라이드
    // =============================
    @Override
    protected void checkSpecialConditions() {

        // 공통 이벤트 우선 실행
        super.checkSpecialConditions();

        // 기본 성격 효과 (행동 후 긍정적 회복)
        changeEmotion(Emotion.STRESS, -2);
        changeEmotion(Emotion.HAPPY, +2);

        // 평균 감정 계산
        int total = 0, count = 0;
        for (Emotion e : emotions.keySet()) {
            total += emotions.get(e);
            count++;
        }
        int avg = (count > 0) ? total / count : 50;

        // 무기력해도 금세 회복
        if (avg <= 25) {
            System.out.println(name + ": 힘들지만... 그래도 다시 해볼게요!");
            changeEmotion(Emotion.MOTIVATION, +5);
            changeEmotion(Emotion.HAPPY, +3);
        }

        // 우울함이 와도 위로로 회복
        int happy = emotions.getOrDefault(Emotion.HAPPY, 50);
        int sad   = emotions.getOrDefault(Emotion.SAD, 50);
        if (happy <= 10 || sad >= 90) {
            System.out.println(name + ": 모두 덕분에 조금 나아졌어요.");
            changeEmotion(Emotion.SAD, -10);
            changeEmotion(Emotion.HAPPY, +10);
            changeEmotion(Emotion.TRUST, +5);
        }

        // 병듦에도 긍정적 태도
        if (energy < 25 && !recentlyHealed) {
            System.out.println(name + ": 괜찮아요! 금방 나을 거예요!");
            changeEmotion(Emotion.MOTIVATION, +5);
            changeEmotion(Emotion.STRESS, -5);
            energy += 3;
        }

        // 신뢰 낮을 때: 가출 대신 화해 시도
        int trust = emotions.getOrDefault(Emotion.TRUST, 50);
        if (trust <= 10) {
            System.out.println(name + ": 내가 뭘 잘못했을까요...? 다시 믿음을 쌓고 싶어요.");
            changeEmotion(Emotion.TRUST, +10);
            changeEmotion(Emotion.HAPPY, +5);
        }

        // 사랑 충만 반응 강화
        if (trust >= 100) {
            System.out.println(name + ": 정말 행복해요! 모두가 소중해요!");
            changeEmotion(Emotion.HAPPY, +10);
            changeEmotion(Emotion.MOTIVATION, +5);
        }

        // 성장 (10회 행동마다)
        if (actionsCount > 0 && actionsCount % 10 == 0) {
            System.out.println(name + ": 조금 더 성장한 기분이에요!");
            energy += 5;
            changeEmotion(Emotion.MOTIVATION, +5);
            changeEmotion(Emotion.HAPPY, +5);
        }

        // 자주 행동할수록 스트레스 저항 강화
        if (actionsCount > 0 && actionsCount % 5 == 0) {
            changeEmotion(Emotion.STRESS, -3);
        }
    }
}
