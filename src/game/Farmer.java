package game;

import java.io.Serializable;
import core.ActionResult;
import core.Beans;

/**
 * Farmer 클래스
 * - 플레이어(농부)를 나타냅니다.
 * - 콩을 하나 이상 소유할 수 있습니다.
 * - 행동 명령을 콩에게 전달합니다.
 */
public class Farmer implements Serializable {

    private String name;    // 농부 이름
    private Beans bean;     // 현재 키우는 콩 (Beans 하위 객체)

    // === 생성자 ===
    public Farmer(String name, Beans bean) {
        this.name = name;
        this.bean = bean;
    }

    // =============================
    // 행동 명령
    // =============================
    public void feed()  { perform("eat",   "먹이 주기",  bean.eat());  }
    public void rest()  { perform("rest",  "휴식",      bean.rest());  }
    public void play()  { perform("play",  "놀기",      bean.play());  }
    public void heal()  { perform("heal",  "치료",      bean.heal());  }
    public void goOut() { perform("goOut", "외출",      bean.goOut()); }
    public void work()  { perform("work",  "일하기",    bean.work());  }

    // 공통 수행 메서드
    private void perform(String actionKey, String displayName, ActionResult result) {
        System.out.println("──────────────────");
        System.out.println("[행동] " + displayName);
        System.out.println();
        bean.applyActionResult(result, actionKey); // ✅ 수정됨!
    }

    // =============================
    // 상태 출력
    // =============================
    public void showStatus() {
        System.out.println("──────────────────");
        System.out.println("농부: " + name);
        System.out.println("키우는 콩: " + bean.getName() + " (" + bean.getPersonalityName() + ")");
        System.out.println();
        bean.showStatus();
    }

    // === Getter / Setter ===
    public String getName() { return name; }
    public Beans getBean() { return bean; }
    public void setBean(Beans bean) { this.bean = bean; }
}
