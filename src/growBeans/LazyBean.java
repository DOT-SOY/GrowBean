package growBeans;

import core.ActionResult;
import core.Beans;
import core.DialogLoader;
import core.Emotion;

/**
 * 게으른 성격의 콩
 * - 행동 시 의욕이 서서히 떨어지고 스트레스가 누적됨
 * - 각 행동 결과는 lazy.csv에서 로드됨
 */
public class LazyBean extends Beans {

    public LazyBean(String name) {
        super(name);
    }

    // =============================
    // 행동 정의 (CSV 로드)
    // =============================
    @Override public ActionResult eat()   { return DialogLoader.getFromCsv("lazy.csv", "eat", getName()); }
    @Override public ActionResult rest()  { return DialogLoader.getFromCsv("lazy.csv", "rest", getName()); }
    @Override public ActionResult play()  { return DialogLoader.getFromCsv("lazy.csv", "play", getName()); }
    @Override public ActionResult heal()  { return DialogLoader.getFromCsv("lazy.csv", "heal", getName()); }
    @Override public ActionResult goOut() { return DialogLoader.getFromCsv("lazy.csv", "goOut", getName()); }
    @Override public ActionResult work()  { return DialogLoader.getFromCsv("lazy.csv", "work", getName()); }

    @Override
    public String getPersonalityName() { return "게으른 콩"; }

    // =============================
    // 이벤트 반응 오버라이드
    // =============================
    @Override
    protected void checkSpecialConditions() {

        // 공통 이벤트 우선 적용
        super.checkSpecialConditions();

        // 기본 성격 효과 (행동마다)
        changeEmotion(Emotion.MOTIVATION, -2);
        changeEmotion(Emotion.STRESS, +1);

        // 평균 감정 계산
        int total = 0, count = 0;
        for (Emotion e : emotions.keySet()) {
            total += emotions.get(e);
            count++;
        }
        int avg = (count > 0) ? total / count : 50;

        // 무기력 반응
        if (avg <= 25) {
            System.out.println(name + ": 너무 귀찮아서 아무것도 하기 싫네요...");
            changeEmotion(Emotion.MOTIVATION, -5);
            energy -= 5;
        }

        // 우울함 반응
        int happy = emotions.getOrDefault(Emotion.HAPPY, 50);
        int sad   = emotions.getOrDefault(Emotion.SAD, 50);
        if (happy <= 10 || sad >= 90) {
            System.out.println(name + ": 몸에 아무런 힘이 없어요... 너무 우울해요...");
            changeEmotion(Emotion.SAD, +5);
            changeEmotion(Emotion.HAPPY, -5);
        }

        // 병듦 반응
        if (energy < 25 && !recentlyHealed) {
            System.out.println(name + "은(는) 숨 쉬는 것조차 귀찮을 만큼 몸이 아파요...");
            changeEmotion(Emotion.STRESS, +10);
            energy -= 5;
        }

        // 가출 무시
        int trust = emotions.getOrDefault(Emotion.TRUST, 50);
        if (trust <= 10)
            System.out.println(name + ": 귀찮아서 그냥 눕기로 했어요.");

        // 충만 반응 약함
        if (trust >= 100) {
            System.out.println(name + ": 사랑을 느끼지만... 움직이기 귀찮아요.");
            changeEmotion(Emotion.HAPPY, +3);
        }

        // 성장(행동 10회)
        if (actionsCount > 0 && actionsCount % 10 == 0) {
            System.out.println(name + ": 저는 저만의 페이스가 있어요...");
            energy += 2;
            changeEmotion(Emotion.MOTIVATION, +2);
        }
    }
}
