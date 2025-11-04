package game;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

import core.Beans;
import growBeans.*;

/**
 * FarmManager
 * - 콘솔 기반 게임의 메인 흐름을 제어
 * - 자동 저장, 하루 시스템, 성장 및 엔딩 평가, 자동 행동 모드 포함
 */
public class FarmManager {

    private final Scanner sc = new Scanner(System.in);
    private Farmer farmer;

    private static final String SAVE_FILE = "save.dat";
    private static final int TURNS_PER_DAY = 5;

    private int day = 1;
    private int turnCount = 0;

    /** 프로그램 시작점 */
    public static void main(String[] args) {
        new FarmManager().start();
    }

    /** 게임 시작 */
    public void start() {
        printLine();

        File file = new File(SAVE_FILE);
        if (file.exists()) {
            System.out.print("기존 저장 파일이 있습니다. 불러오시겠습니까? (Y/N): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("Y") && loadGame()) {
                System.out.println("\n세이브 데이터를 불러왔습니다.\n");
                gameLoop();
                return;
            }
        }

        createNewGame();
        gameLoop();
    }

    /** 새 게임 생성 */
    private void createNewGame() {
        System.out.print("당신의 이름을 입력하세요: ");
        String farmerName = sc.nextLine().trim();

        System.out.print("키울 콩의 이름을 정하세요: ");
        String beanName = sc.nextLine().trim();

        Beans bean = chooseBeanType(beanName);
        farmer = new Farmer(farmerName, bean);

        day = 1;
        turnCount = 0;

        System.out.println();
        System.out.println("농부 " + farmerName + "님, " + bean.getName() + "를 잘 키워주세요!");
        System.out.println();
    }

    /** 콩의 성격 선택 */
    private Beans chooseBeanType(String beanName) {
        while (true) {
            printLine();
            System.out.println("콩의 성격을 선택하세요");
            System.out.println("1. 상냥한 콩");
            System.out.println("2. 게으른 콩");
            System.out.println("3. 까칠한 콩");
            System.out.print("선택: ");

            switch (sc.nextLine().trim()) {
                case "1": return new KindBean(beanName);
                case "2": return new LazyBean(beanName);
                case "3": return new SnappyBean(beanName);
                default:
                    System.out.println("잘못된 입력입니다. 다시 선택하세요.\n");
            }
        }
    }

    /** 메인 게임 루프 */
    private void gameLoop() {
        while (true) {
            printLine();
            System.out.println(day + "일차  (턴 " + (turnCount % TURNS_PER_DAY + 1) + "/" + TURNS_PER_DAY + ")");
            System.out.println("무엇을 하시겠습니까?");
            System.out.println(
                "1. 먹이 주기\t2. 휴식\n" +
                "3. 놀기\t\t4.치료\n" +
                "5. 외출\t\t6.일하기\n" +
                "7. 상태 보기\t8. 종료\n" +
                "9. 새 게임"
            );
            System.out.print("선택: ");

            String input = sc.nextLine().trim();

            boolean acted = false;

            switch (input) {
                case "1": farmer.feed();  acted = true; break;
                case "2": farmer.rest();  acted = true; break;
                case "3": farmer.play();  acted = true; break;
                case "4": farmer.heal();  acted = true; break;
                case "5": farmer.goOut(); acted = true; break;
                case "6": farmer.work();  acted = true; break;
                case "7": farmer.showStatus(); break;
                case "8":
                    printLine();
                    saveGame();
                    System.out.println("게임이 자동으로 저장되었습니다.");
                    System.out.println("프로그램을 종료합니다.");
                    return;
                case "9":
                    printLine();
                    System.out.print("현재 게임을 초기화하시겠습니까? (Y/N): ");
                    if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
                        createNewGame();
                    } else {
                        System.out.println("초기화를 취소했습니다.\n");
                    }
                    break;
                default:
                    System.out.println("잘못된 입력입니다.\n");
            }

            if (acted) advanceTime();
        }
    }
    
    /** 턴 경과 처리 */
    private void advanceTime() {
        turnCount++;
        if (turnCount % TURNS_PER_DAY == 0) {
            day++;
            endOfDay();
        }
    }

    /** 하루가 끝났을 때의 변화 */
    private void endOfDay() {
        printLine();
        System.out.println("하루가 저물었습니다. " + farmer.getBean().getName() + "은(는) 잠이 듭니다...");
        Beans bean = farmer.getBean();
        bean.changeEmotion(core.Emotion.HUNGER, +5);
        bean.changeEmotion(core.Emotion.STRESS, +5);
        bean.changeEmotion(core.Emotion.MOTIVATION, -5);
        bean.changeEmotion(core.Emotion.HAPPY, -3);
        System.out.println("다음 날이 밝았습니다.\n");
    }

    /** 게임 저장 */
    private void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(farmer);
            oos.writeInt(day);
            oos.writeInt(turnCount);
        } catch (IOException e) {
            System.out.println("저장 중 오류: " + e.getMessage());
        }
    }

    /** 게임 불러오기 */
    private boolean loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            farmer = (Farmer) ois.readObject();
            day = ois.readInt();
            turnCount = ois.readInt();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("불러오기 실패: " + e.getMessage());
        }
        return false;
    }

    /** 구분선 출력 */
    private void printLine() {
        System.out.println("────────────────────────");
    }

    /** 잠시 대기 (ms) */
    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
