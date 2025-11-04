package exceptions;

/**
 * 게임 내 '상태 변화 이벤트'를 나타내는 클래스.
 * <p>오류가 아닌, 감정/상태 변화 등 게임 내 자연스러운 이벤트를 전달합니다.</p>
 */
public class BeanEvents extends RuntimeException {
    public BeanEvents(String message) { super(message); }

    // === 기본 에너지 이벤트 ===
    public static BeanEvents energyDepleted(int v) { return new BeanEvents("에너지가 고갈되었습니다 (" + v + ")"); }

    // === 감정/상태 이벤트 ===
    public static BeanEvents beanFainted() { return new BeanEvents("기절했습니다. 치료가 필요합니다!"); }
    public static BeanEvents beanRanAway() { return new BeanEvents("신뢰를 잃고 집을 나가버렸습니다..."); }
    public static BeanEvents beanDepressed() { return new BeanEvents("너무 우울해요... 기분 전환이 필요합니다."); }
    public static BeanEvents beanNauseous() { return new BeanEvents("공포로 인해 메스꺼움을 느낍니다..."); }
    public static BeanEvents beanAngry() { return new BeanEvents("화가 잔뜩 났습니다!"); }
    public static BeanEvents beanInsomnia() { return new BeanEvents("스트레스로 잠들지 못했습니다."); }
    public static BeanEvents beanLoveful() { return new BeanEvents("사랑이 가득합니다!"); }
    public static BeanEvents beanExhausted() { return new BeanEvents("기운이 빠져 있습니다."); }
    public static BeanEvents beanGrowth() { return new BeanEvents("키가 조금 자랐습니다!"); }
    public static BeanEvents beanSick() { return new BeanEvents("몸 상태가 나빠지고 있습니다."); }
}
