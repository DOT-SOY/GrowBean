package exceptions;

/**
 * 실제 오류 상황(파일 불러오기 실패, 잘못된 Bean 동작 등)을 처리하는 예외 클래스.
 * <p>게임 진행을 멈춰야 하거나, 복구가 필요한 상황에서 사용됩니다.</p>
 */
public class BeanException extends RuntimeException {

    public BeanException(String message) {
        super(message);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }

    /** CSV 로드 실패 */
    public static BeanException csvLoad(String fileName, Throwable cause) {
        return new BeanException("CSV 파일 로드 실패: " + fileName, cause);
    }

    /** Bean 동작 중 예외 */
    public static BeanException beanAction(String action) {
        return new BeanException("Bean 동작 중 오류 발생: " + action);
    }
}
