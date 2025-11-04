package exceptions;

public class ExceptionHandler {

    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";

    public static void handle(Exception e) {
        if (e instanceof BeanEvents) {
            System.out.println(YELLOW + e.getMessage() + RESET);
            return;
        }
        if (e instanceof BeanException) {
            System.err.println(RED + "[오류] " + e.getMessage() + RESET);
            if (e.getCause() != null) e.getCause().printStackTrace();
            return;
        }

        System.err.println(RED + "[시스템 예외] " + e.getMessage() + RESET);
        e.printStackTrace();
    }
}
