package utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import core.Emotion;
import exceptions.*;

/**
 * 여러 유틸 기능을 한데 모은 통합 유틸 클래스.
 * <p>
 * CSV 파일 읽기, 문자열 파싱, 랜덤 범위 계산, 감정 데이터 처리 등의
 * 공용 메서드를 제공합니다.
 */
public class Utils {

    // ==============================
    // CSV 유틸
    // ==============================

    /**
     * 클래스패스 기준으로 UTF-8 인코딩의 CSV 파일을 엽니다.
     *
     * @param fileName 리소스 경로 기준 CSV 파일 이름
     * @return BufferedReader 객체 (파일을 찾지 못하면 null 반환)
     */
    public static BufferedReader openCsv(String fileName) {
        try {
            InputStream in = Utils.class.getResourceAsStream("/" + fileName);
            if (in == null)
                throw new FileNotFoundException("리소스 파일을 찾을 수 없습니다: " + fileName);
            return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            ExceptionHandler.handle(BeanException.csvLoad(fileName, e));
            return null;
        } catch (Exception e) {
            ExceptionHandler.handle(BeanException.beanAction("CSV 오픈 중 예기치 못한 오류: " + fileName));
            return null;
        }
    }

    /**
     * 한 줄의 CSV 문자열을 콤마(,) 기준으로 분리합니다.
     * 따옴표로 감싸진 구간은 안전하게 처리합니다.
     *
     * @param line CSV 한 줄
     * @return 분리된 컬럼 문자열 배열
     */
    public static String[] parseLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuote = !inQuote;
            } else if (c == ',' && !inQuote) {
                parts.add(clean(sb));
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        parts.add(clean(sb));
        return parts.toArray(new String[0]);
    }

    /**
     * CSV 전체를 읽어 각 행을 String 배열로 반환합니다.
     *
     * @param fileName   CSV 파일 이름
     * @param skipHeader 첫 줄이 헤더라면 true
     * @return CSV 데이터의 리스트
     */
    public static List<String[]> loadCsv(String fileName, boolean skipHeader) {
        List<String[]> list = new ArrayList<>();
        try (BufferedReader br = openCsv(fileName)) {
            if (br == null) return list;

            if (skipHeader) br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) list.add(parseLine(line));
            }
        } catch (IOException e) {
            ExceptionHandler.handle(BeanException.csvLoad(fileName, e));
        } catch (Exception e) {
            ExceptionHandler.handle(BeanException.beanAction("CSV 파싱 중 오류 발생: " + fileName));
        }
        return list;
    }

    /**
     * 문자열에서 공백과 개행 문자를 제거합니다.
     *
     * @param s 정리할 문자열
     * @return 공백과 개행이 제거된 문자열
     */
    private static String clean(CharSequence s) {
        return s.toString().trim().replaceAll("[\\r\\n]", "");
    }

    // ==============================
    // 랜덤 & 범위 유틸
    // ==============================

    private static final Random random = new Random();

    /**
     * 주어진 확률(rate)에 따라 true 또는 false를 반환합니다.
     *
     * @param rate 0.0 ~ 1.0 사이의 확률 값
     * @return 랜덤 결과 (true: 성공, false: 실패)
     */
    public static boolean chance(double rate) {
        return random.nextDouble() < rate;
    }

    /**
     * 지정한 최소~최대 범위의 정수 중 랜덤 값을 반환합니다.
     *
     * @param min 최소값
     * @param max 최대값
     * @return 랜덤 정수
     */
    public static int range(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * "a~b" 형태의 문자열을 해석하여 정수 배열로 반환합니다.
     * 범위 구분자가 없으면 a=b로 간주합니다.
     *
     * @param text 범위를 나타내는 문자열 (예: "+3~+5" 또는 "2")
     * @return [min, max] 배열
     */
    public static int[] parseRange(String text) {
        String[] r = text.replace("+", "").trim().split("~");
        int min = Integer.parseInt(r[0].trim());
        int max = (r.length > 1) ? Integer.parseInt(r[1].trim()) : min;
        return new int[]{min, max};
    }

    // ==============================
    // 감정 파싱 유틸
    // ==============================

    /**
     * "JOY:+2~+5|SAD:-3" 형식의 문자열을 파싱하여 감정 변화량 맵으로 반환합니다.
     *
     * @param data 감정 변화 데이터를 담은 문자열
     * @return Emotion 열거형을 키로 하는 변화량 맵
     */
    public static Map<Emotion, Integer> parseEmotionChanges(String data) {
        Map<Emotion, Integer> map = new EnumMap<>(Emotion.class);
        if (data == null || data.isBlank()) return map;

        String[] tokens = data.split("\\|");
        for (String token : tokens) {
            String[] parts = token.split(":");
            if (parts.length < 2) continue;

            try {
                Emotion emo = Emotion.valueOf(parts[0].trim());
                int[] range = parseRange(parts[1]);
                map.put(emo, range(range[0], range[1]));
            } catch (Exception e) {
                ExceptionHandler.handle(BeanException.beanAction("감정 파싱 실패: " + token));
            }
        }
        return map;
    }

    // ==============================
    // 문자열 치환 유틸
    // ==============================

    /**
     * 문자열 내의 {name} 태그를 실제 이름으로 치환합니다.
     *
     * @param text 치환 대상 문자열
     * @param name 이름 값
     * @return 치환된 문자열
     */
    public static String replaceName(String text, String name) {
        if (text == null) return "";
        return text.replace("{name}", name);
    }
}
