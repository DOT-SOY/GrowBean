package core;

/**
 * 콩(Bean)의 공통 행동 규약 인터페이스
 * 모든 콩은 다음 6가지 행동을 수행할 수 있어야 합니다.
 * 각 행동의 결과는 ActionResult 형태로 반환됩니다.
 */

public interface Behavior {

    /** 먹기 */
    ActionResult eat();

    /** 휴식 */
    ActionResult rest();

    /** 놀기 */
    ActionResult play();

    /** 치료 */
    ActionResult heal();

    /** 외출 */
    ActionResult goOut();

    /** 일하기 */
    ActionResult work();
}
