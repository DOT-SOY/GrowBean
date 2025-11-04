# GrowBean 프로젝트 구조 및 설계 요약


## 👤 제작자 소개

**제작자:** [DOT-SOY](https://github.com/DOT-SOY)

본 프로젝트는 자바 콘솔 기반의 다마고치형 게임을 직접 설계 및 구현한 개인 프로젝트이다.  
객체 지향 프로그래밍의 핵심 원칙인 **캡슐화, 다형성, 예외 처리 구조**를 실제 코드로 구현하며,  
성격별로 다른 행동 반응을 보이는 인터랙티브 구조를 통해 학습적·실험적 목적을 함께 달성하고자 하였다.  

자세한 개발 기록 및 추가 프로젝트는 아래 노션 페이지에서 확인할 수 있다.  
👉 [개발 노션 페이지](https://www.notion.so/28c7d69c762a80bf8001d4c7fc7fac61?source=copy_link)

---

## 0. 프로젝트 설계 의도

프로젝트 설계에서 중점적으로 생각한 항목은 다음과 같다.

1. **성격별 대화 및 반응 차이 구현**  
   - 동일한 행동(`eat`, `rest`, `play` 등)에 대해 캐릭터의 성격에 따라 서로 다른 대사와 결과를 출력하도록 설계하였다.  
   - 이를 통해 사용자에게 보다 생동감 있고 개성 있는 상호작용을 제공하고자 하였다.

2. **다형성 학습 및 실습**  
   - 추상 클래스(`Beans`)와 인터페이스(`Behavior`)를 기반으로, 다양한 하위 클래스(`KindBean`, `LazyBean`, `SnappyBean`)가 고유의 행동을 구현하도록 하였다.  
   - 동일한 메서드 호출이 실제 객체의 타입에 따라 다른 결과를 출력하도록 하여 런타임 다형성(polymorphism) 을 명확히 체험할 수 있도록 구성하였다.

3. **객체 간 역할 분리 및 책임 분담**  
   - `Farmer`는 행동 명령을 전달하는 컨트롤러로, `Beans`는 상태와 로직을 담당하는 모델로 구분하였다.  
   - `FarmManager`는 전체 게임의 흐름을 관리하는 엔트리 포인트 역할을 수행한다.  
   - 이러한 구조를 통해 객체 간 결합도를 낮추고, 각 클래스의 책임을 명확히 구분하였다.

---

### 1. 클래스 다이어그램

![Image](https://github.com/user-attachments/assets/2a4f1b23-d774-4134-b335-7be2ab1face9)

- FarmManager는 게임의 전체 흐름을 제어하는 클래스이다.
- Farmer는 Bean 객체를 포함하며, 행동 명령을 중계한다.
- Beans는 추상 클래스이며, 성격별 Bean은 이를 상속받아 행동을 구체화한다.
- Behavior 인터페이스는 Bean의 행동 규약을 정의한다.

## 2. 플레이 화면

### 캐릭터 생성
![Image](https://github.com/user-attachments/assets/08cb4099-e553-4f36-8430-a580dade84d2)  
플레이어는 농부의 이름을 입력한 뒤, 콩의 이름과 성격을 선택하여 자신만의 캐릭터를 생성할 수 있다.  
선택한 성격에 따라 콩의 대사, 반응, 감정 변화가 달라진다.

---

### 게임 플레이
![Image](https://github.com/user-attachments/assets/57c7c757-1e26-440e-aa92-b2af8b244dd2)  
각 콩은 성격에 따라 동일한 행동이라도 다른 결과를 보인다.  
예를 들어, 착한 콩은 ‘가출’ 이벤트에서 화해를 시도하고, 게으른 콩은 행동할수록 의욕이 감소하며, 까칠한 콩은 쉽게 스트레스를 받는다.  

행동 스크립트는 **CSV 파일로 외부 관리**되며, 성격별로 다른 CSV를 참조한다.  
이 때, 매번 CSV 파일을 불러오면 성능 저하가 발생할 수 있으므로,  
최초 한 번만 로드한 뒤 **Map을 이용한 캐시 구조**에 저장하여 재사용하도록 구현하였다.  

---

### 특수 이벤트
![Image](https://github.com/user-attachments/assets/48ced750-fac2-4fd9-b04e-f0bdf2a3371d)  
게임 내 특수 이벤트는 **Custom Exception**을 통해 감지하고 출력한다.  
이벤트 메시지는 일반 로그와 구분하기 위해 노란색으로 표시된다.  
예외를 활용한 이벤트 처리 방식은, 향후 새로운 이벤트를 추가할 때의 **확장성과 재사용성**을 높이기 위한 설계적 선택이다.

---

### 저장 및 불러오기
![Image](https://github.com/user-attachments/assets/d9227d0f-4f71-4448-b625-1b0d65c956e6)  
플레이한 데이터는 게임 종료 시 자동으로 `save.dat` 파일에 저장된다.  
프로그램 실행 시, 저장된 세이브 파일이 존재할 경우 자동으로 불러와 이전 상태에서 이어서 진행할 수 있다.

---

## 3. 문제 해결 (Troubleshooting)

### Index 6 out of bounds for length 6
<img width="444" height="124" alt="Image" src="https://github.com/user-attachments/assets/63bc4ad6-b082-4702-9dfb-2956345ff895" />  

인덱스 범위를 초과한 접근으로 발생한 오류였다.  
코드 내에서 비교 연산자의 범위 조건이 잘못 설정되어 있었으며, 해당 로직을 수정하여 해결하였다.

---

### 에너지 범위 초과 문제
<img width="268" height="277" alt="Image" src="https://github.com/user-attachments/assets/42d6c226-28c2-4151-9625-b2e64a1f9edb" />  

감정이나 에너지 수치가 100을 초과하거나 0 미만으로 떨어지는 문제가 발생하였다.  
이는 감정 변화 메서드의 실행 순서 문제였으며,  
감정 변화를 적용한 후 **0~100 사이로 보정하는 메서드(`clampStats`)**를 호출하도록 수정하여 해결하였다.

---

### stream classdesc serialVersionUID = xxxxx, local class serialVersion = ooooo
<img width="1289" height="113" alt="Image" src="https://github.com/user-attachments/assets/93eacad2-a389-4243-9cd9-25a738066ef1" />  
이 오류는 `Beans` 클래스 구조가 변경되었음에도 이전 버전의 구조를 기준으로 직렬화된 `save.dat`을 불러오려 할 때 발생한다.  
즉, 필드가 추가되거나 이름이 변경되었거나, 상속 구조나 패키지 경로가 달라진 경우  
기존 세이브 파일의 `serialVersionUID`와 현재 클래스의 UID가 일치하지 않아 복원할 수 없게 된다.  

현재는 미니 프로젝트 단계이므로 `save.dat` 파일을 삭제한 뒤 재실행하는 방식으로 해결하였다.  
다만, 프로젝트 규모가 커질 경우에는 `Beans` 클래스 내 이벤트 처리 메서드를 별도의 클래스로 분리하고,  
명시적인 `serialVersionUID`를 지정하여 버전 간 호환성을 유지하는 방식으로 개선할 수 있다.

---

### 이벤트 상태 체크
<div align="center">
  <img width="455" height="467" src="https://github.com/user-attachments/assets/0514c3f4-0d33-4818-9eb7-7f5cc7cd6f47" />
  <img width="477" height="716" src="https://github.com/user-attachments/assets/ee028872-0e59-470a-bb28-9ce55e39a614" />
</div>
 
이벤트별로 감정 변화나 특수 상태가 올바르게 반영되는지를 검증한 테스트 과정이다.  
테스트용 `Beans`와 `Farmer` 객체를 생성하여 각 이벤트 발생 시 감정 수치 변화를 확인하였으며,  
모든 이벤트가 의도한 대로 작동함을 확인하였다.


## 4. 사용된 기술 요약

### a. 캡슐화 (Encapsulation)
- Beans 클래스의 필드를 private 혹은 protected로 제한하였다.
- 외부에서는 getter, setter 또는 내부 메서드를 통해서만 접근이 가능하다.

```
protected int energy;
public int getEnergy() { return energy; }
public void changeEnergy(int delta) { ... }
```

내부 상태를 직접 수정하지 못하게 하여 데이터 무결성을 유지하였다.

### b. 다형성 (Polymorphism)
- Behavior 인터페이스를 통해 모든 Bean이 동일한 행동 메서드를 가진다.
- 실제 실행되는 결과는 Bean의 성격별 구현에 따라 달라진다.

```
@Override
public ActionResult play() {
    return DialogLoader.getFromCsv("lazy.csv", "play", getName());
}
```

동일한 메서드 호출(bean.play())이라도 클래스에 따라 다른 결과를 반환하도록 설계하였다.

### c. 예외 처리 (Exception Handling)
- BeanEvents(자연 이벤트)와 BeanException(오류)를 분리하였다.
- ExceptionHandler를 통해 예외 처리를 중앙 집중화하였다.

```
catch (BeanException e) {
    ExceptionHandler.handle(e);
}
```

- try-with-resources를 사용하여 파일 입출력 시 자원을 안전하게 닫도록 하였다.
- 게임 진행을 멈추지 않고 오류를 처리할 수 있도록 설계하였다.

---

## 5. 기술 선택의 근거

### Exception 클래스
- BeanEvents : 게임 내 자연 이벤트를 담당한다. (예: 콩이 화가 났습니다!)
- BeanException : 파일 로드 실패, CSV 구문 오류 등 실제 오류를 담당한다.
- ExceptionHandler : 예외 출력 및 색상 강조를 담당한다.

#### 분리 이유

| 구분 | 역할 |
|------|-----------------|
| BeanEvents | 게임 내에서 ‘자연스러운 상태 변화’를 알리는 이벤트. 오류가 아닌 상태 변화를 표현하므로, 게임 흐름을 멈추지 않기 위해 분리하였다. |
| BeanException | 시스템 또는 로직 오류를 나타내는 예외. 실제 오류 상황을 별도로 처리하기 위해 분리하였다. |
| ExceptionHandler | 콘솔 출력 및 예외 메시지 표준화. 예외 처리 코드를 중앙 집중화하여 유지보수성을 높이기 위해 사용하였다. |

#### 예시
```
catch (BeanException e) {
    ExceptionHandler.handle(e);
}
catch (Exception e) {
    ExceptionHandler.handle(BeanException.csvLoad(fileName, e));
}
```

게임 이벤트와 시스템 오류를 의미적으로 분리하여, 정상 흐름을 유지하면서도 예외를 명확하게 처리할 수 있도록 설계되었다.

---

### 상속과 인터페이스

- Behavior : 모든 콩이 수행해야 하는 행동 규약을 정의한 인터페이스이다.
- Beans : 콩의 공통 속성과 기본 로직을 정의한 추상 클래스이다.
- KindBean, LazyBean, SnappyBean : Beans를 상속받아 성격별 행동을 구현한 클래스이다.

#### 사용 이유

| 개념 | 사용 이유 |
|------|-----------|
| 인터페이스 (Behavior) | 모든 콩이 동일한 행동 메서드를 가지도록 강제하여 행동의 일관성을 확보하였다. |
| 추상 클래스 (Beans) | 이름, 에너지, 감정 등 공통 속성과 로직을 공유하고 일부만 하위 클래스가 직접 구현하도록 하였다. |
| 상속 클래스 | Beans의 공통 구조를 유지하면서, 성격에 따른 반응을 오버라이드하여 다형성을 구현하였다. |

#### 예시
```
Beans bean = new LazyBean("콩콩이");
ActionResult result = bean.play(); // LazyBean의 play()가 실행된다.
```

모든 콩이 동일한 행동을 하지만, 결과는 성격에 따라 다르게 표현되도록 다형성을 적용하였다.

---

### Map

감정 상태를 관리하기 위해 Map<Emotion, Integer>를 사용하였다.

#### 코드 예시
```
protected Map<Emotion, Integer> emotions;

public void changeEmotion(Emotion emotion, int delta) {
    int current = emotions.getOrDefault(emotion, 50);
    int next = Math.max(0, Math.min(100, current + delta));
    emotions.put(emotion, next);
}
```

#### 사용 이유
- 감정의 이름(종류)과 수치값을 동시에 관리해야 하므로 Key-Value 구조인 Map을 선택하였다.
- 특정 감정을 이름으로 빠르게 찾고 수정할 수 있다.
- 이 때, 감정은 변하지 않는 상수이므로 enum을 통해 정의하였다.

#### Map vs List vs Set 비교 요약표

| 자료구조 | 특징 | 적합 여부 | 설명 |
|-----------|-------|------------|------|
| Map | Key-Value 쌍 저장, 빠른 검색 (O(1)) | 적합 | 감정명(Key)과 감정 수치(Value)를 매핑하기에 이상적이다. |
| List | 순서 기반, 중복 허용 | 부적합 | 감정의 이름으로 접근하기 어렵다. |
| Set | 중복 불가, 값만 저장 | 부적합 | 감정의 수치값을 저장할 수 없다. |

요약하자면, 감정 상태를 관리하기 위해 감정명(Key)과 감정 수치(Value)를 한 쌍으로 다루는 Map이 가장 적절하였다.

---

## 6. 프로젝트 강점 요약

| 항목 | 내용 |
|------|------|
| 구조적 완성도 | 추상 클래스와 인터페이스를 조합하여 Beans 계층 구조를 체계적으로 구성하였다. |
| 유연한 확장성 | 새로운 성격의 Bean을 추가할 때, CSV 파일과 Bean 클래스만 추가하면 된다. |
| 예외 안정성 | BeanEvents와 BeanException을 분리하여 오류와 이벤트를 명확히 구분하였다. |
| 데이터 주도 설계 | 행동 결과를 CSV로 외부화하여 밸런스 조정이 용이하다. |
| 캡슐화와 은닉성 | Bean의 내부 상태를 메서드를 통해서만 변경할 수 있다. |
| 다형성 구현 | Bean의 행동은 동일하지만, 결과는 성격에 따라 다르게 동작한다. |
| 유지보수성 | 패키지 구조 분리로 인하여 코드 확장 및 테스트가 용이하다. |
