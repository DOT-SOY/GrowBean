package core;

//콩의 감정 상태를 정의하는 enum 입니다.

public enum Emotion {
    HAPPY("행복"),
    SAD("슬픔"),
    ANGER("분노"),
    HUNGER("허기"),
    STRESS("긴장"),
    TRUST("신뢰"),
    MOTIVATION("의욕"),
    FEAR("공포");

    private final String koreanEmotion;

    Emotion(String koreanEmotion) {
        this.koreanEmotion = koreanEmotion;
    }

    @Override
    public String toString() {
        return koreanEmotion;
    }
}
