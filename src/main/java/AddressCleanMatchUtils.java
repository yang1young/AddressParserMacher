import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * tools to clean and match Address
 */
public class AddressCleanMatchUtils {

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


    public static double getSimilarityOfAddress(String address1, String address2) {
        return getLikelihoodDistance(address1,address2);
    }

    /*
      this method is used to computing string similarity by Levenshtein Distance
    */
    public static double getLikelihoodDistance(String source, String target) {

        char[] s = source.toCharArray();
        char[] t = target.toCharArray();
        int slen = source.length();
        int tlen = target.length();
        int d[][] = new int[slen + 1][tlen + 1];
        for (int i = 0; i <= slen; i++) {
            d[i][0] = i;
        }
        for (int i = 0; i <= tlen; i++) {
            d[0][i] = i;
        }
        for (int i = 1; i <= slen; i++) {
            for (int j = 1; j <= tlen; j++) {
                if (s[i - 1] == t[j - 1]) {
                    d[i][j] = d[i - 1][j - 1];
                } else {
                    int insert = d[i][j - 1] + 1;
                    int del = d[i - 1][j] + 1;
                    int modify = d[i - 1][j - 1] + 1;
                    d[i][j] = Math.min(insert, del) > Math.min(del, modify) ? Math
                            .min(del, modify) : Math.min(insert, del);
                }
            }
        }
        double similarPercent = 1 - d[slen][tlen]
                / (double) Math.max(slen, tlen);
        return similarPercent;
    }

}
