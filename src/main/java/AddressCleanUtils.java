import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddressCleanUtils {

    private static final int CONVERT_STEP = 65248; // 全角半角转换间隔

    private static final HashMap<String, String> digitalMap = new HashMap<String, String>() {{
        this.put("0", "零");
        this.put("1", "一");
        this.put("2", "二");
        this.put("3", "三");
        this.put("4", "四");
        this.put("5", "五");
        this.put("6", "六");
        this.put("7", "七");
        this.put("8", "八");
        this.put("9", "九");
    }};

    private boolean removeSpecialChars(String address) {
        String regEx = "[;.:,；。：，（）#() 、-]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(address);
        return m.find();
    }

    private String filterDigitAndLower(String address) {
        String regEx = "[a-z0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(address);
        if (!m.find())
            return address;
        else {
            char[] res = address.toCharArray();
            for (int i = 0; i < res.length; i++) {
                if (Character.isDigit(res[i]))
                    res[i] = digitalMap.get(String.valueOf(res[i])).charAt(0);
                if (Character.isLowerCase(res[i]))
                    res[i] = Character.toUpperCase(res[i]);
            }
            return String.valueOf(res);
        }
    }

    //banjiao to quanjiao
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + CONVERT_STEP);

            }
        }
        return new String(c);
    }

    //quanjiao to banjiao
    public static String ToDBC(String input) {


        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - CONVERT_STEP);

            }
        }
        return new String(c);

    }


}
