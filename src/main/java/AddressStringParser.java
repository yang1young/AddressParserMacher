import java.util.*;
import org.apache.log4j.*;

/**
 * Created by yangqiao on 1/8/14.
 */

public class AddressStringParser {
    private static final int THRESHOLD = 100;

    private static Logger logger = Logger.getRootLogger();

    private static long WARNINGCOUNT = 0;

    private static final Character[] RANK_ONE_VALUE = new Character[]{
            '路', '道', '街', '巷', '条', '里', '号', '楼', '寨', '斋',
            '馆', '堂', '园', '坊', '居', '苑', '场', '局', '城', '桥',
            '舍', '幢', '室', '厂', '寺', '院', '座', '层', '房', '栋',
            '户', '庄', '屯', '省', '县', '镇', '村', '市', '坪', '区',
            '所'
    };
    private static final String [] RANK_TWO_VALUE = new String[]{
            "公司", "小区", "大道", "胡同", "广场", "酒店", "公寓", "公司",
            "大学", "中学", "社区", "支弄", "宾馆", "市场", "饭店", "花园",
            "特区", "中心", "医院", "银行", "委会", "大厦", "单元", "学院",
            "大道"
    };

    private static final ArrayList<AddressMatchingRule> MATCHING_RULE = new ArrayList<AddressMatchingRule>(){{
        this.add(new AddressMatchingRule("010", 1));
        this.add(new AddressMatchingRule("020", 1));
        this.add(new AddressMatchingRule("0221", 1));
        this.add(new AddressMatchingRule("022", 2));
        this.add(new AddressMatchingRule("0210", 2));
        this.add(new AddressMatchingRule("021", 1));
        this.add(new AddressMatchingRule("0110", 2));
        this.add(new AddressMatchingRule("0111", 1));
        this.add(new AddressMatchingRule("0120", 2));
        this.add(new AddressMatchingRule("0121", 1));
        this.add(new AddressMatchingRule("0113", 1));
        this.add(new AddressMatchingRule("1110", 2));
        this.add(new AddressMatchingRule("1101", 3));
        this.add(new AddressMatchingRule("1120", 2));
        this.add(new AddressMatchingRule("1301", 3));
        this.add(new AddressMatchingRule("130", 2));
        this.add(new AddressMatchingRule("210", 1));
        this.add(new AddressMatchingRule("019", 1));
        this.add(new AddressMatchingRule("029", 1));
        this.add(new AddressMatchingRule("09", 0));
        this.add(new AddressMatchingRule("1129", 2));
        this.add(new AddressMatchingRule("0122", 1));
        this.add(new AddressMatchingRule("220", 1));
        this.add(new AddressMatchingRule("0112", 3));

    }};

    private static final Set<Character> RANK_ONE = new HashSet<Character>(Arrays.asList(RANK_ONE_VALUE));

    private static final Set<String> RANK_TWO = new HashSet<String>(Arrays.asList(RANK_TWO_VALUE));

    class AddressCode {
        String code;
        ArrayList<Integer> codeIndex = new ArrayList<Integer>();

        public AddressCode(String address) {
            code = addressEncoder(address);
        }

        public String addressEncoder(String address) {
            char[] charAddress = address.toCharArray();
            StringBuilder addressCode = new StringBuilder();

            char lastChar = '5';
            StringBuilder rankTwo = new StringBuilder().append(lastChar);
            int index = 0;
            for (char c : charAddress) {
                rankTwo.append(c);
                if(RANK_TWO.contains(rankTwo.toString())){
                    addressCode.deleteCharAt(addressCode.length() - 1);
                    addressCode.append('2');
                    codeIndex.remove(codeIndex.size() - 1);
                    codeIndex.add(index);
                } else {
                    if (RANK_ONE.contains(c)) {
                        addressCode.append('1');
                        codeIndex.add(index);
                        if (lastChar == c){
                            addressCode.append('3');
                            codeIndex.add(index);
                        }
                    } else {
                            addressCode.append('0');
                            codeIndex.add(index);
                    }
                }
                rankTwo.deleteCharAt(0);
                lastChar = c;
                index ++;
            }

            lastChar = '1';
            char[] res = addressCode.toString().toCharArray();
            int shift = 0;

            for(int i = 0;i < res.length;i++){
                if(res[i] == '0' && lastChar == '0'){
                    addressCode.deleteCharAt(i - 1 - shift);
                    codeIndex.remove(i - shift - 1);
                    shift ++;
                }
                lastChar = res[i];
            }

            addressCode.append('9');
            codeIndex.add(charAddress.length);

            return  addressCode.toString();
        }

        public int getIndex(int index){
            return codeIndex.get(index);
        }

        @Override
        public String toString() {
            return "AddressCode{" +
                    "code='" + code + '\'' +
                    '}' + '\n' + codeIndex.toString();
        }

        public String getCode() {
            return code;
        }
    }

    // split address according to rules an keyword
    public ArrayList<String> stringAnalyze(String address){
        AddressCode code = new AddressCode(address);
        String numbers = code.getCode();
        ArrayList<String> res = new ArrayList<String>();
        int index = -1;
        int shift = 0;
        int lastIndex = 0;
        int count = 0;

        while(!numbers.equals("9") && count < THRESHOLD){
            index = -1;
            Iterator<AddressMatchingRule> it = MATCHING_RULE.iterator();

            while(it.hasNext()) {
                AddressMatchingRule rule = it.next();
                if(numbers.indexOf(rule.getPattern()) == 0){
                    index = rule.getSplitIndex();
                    break;
                }
            }

            if(index != -1){
                String flag1 = address.substring(lastIndex, code.getIndex(index + shift) + 1);
                res.add(flag1);
                numbers = numbers.substring(index + 1);
                lastIndex = code.getIndex(index + shift) + 1;
                shift += (index + 1);
            }
            count ++;
        }

        if(count == THRESHOLD){
            WARNINGCOUNT ++;
            logger.warn("The Address:" + address + " can not be analyzed!!! " + WARNINGCOUNT);
            return new ArrayList<String>();
        }

        return res;
    }

}



class AddressMatchingRule {
    private String pattern;
    private int splitIndex;

    public AddressMatchingRule(String pattern, int index) {
        this.pattern = pattern;
        this.splitIndex = index;
    }

    public String getPattern() {
        return pattern;
    }

    public int getSplitIndex() {
        return splitIndex;
    }
}



class GetLevelInfoOfAddress {

    public static final String[] level1 = {"省", "自治区"};
    public static final String[] level2 = {"市"};
    public static final String[] level3 = {"县", "区"};
    public static final String[] level4 = {"乡", "镇"};
    public static final String[] level5 = {"村", "寨"};
    public static final String[] level6 = {"大道", "路", "道", "大街", "街", "巷", "胡同", "条", "里", "支弄", "弄"};
    public static final String[] level7 = {"号"};
    public static final String[] level8 = {
            "大楼", "公司", "小区", "广场", "酒店", "公寓", "大学", "中学", "社区", "宾馆",
            "市场", "饭店", "花园", "特区", "中心", "医院", "银行", "委会", "大厦", "学院",
            "楼", "园", "坊", "居", "苑", "场", "局", "城", "桥", "厂", "寺", "院",
            "庄", "屯", "坪", "所"};
    public static final String[] level9 = {"号楼", "号", "楼", "座", "斋", "馆", "堂", "座", "幢", "部", "栋"};
    public static final String[] level10 = {"单元", "层", "楼"};
    public static final String[] level11 = {"室", "号", "房", "户"};


    private static Logger logger = Logger.getLogger(GetLevelInfoOfAddress.class);

    public static HashMap<Integer, String> addressMapCreate(ArrayList<String> location) {

        HashMap<Integer, String> map = new HashMap<Integer, String>();
        Iterator<String> it = location.iterator();
        String[][] tableName = {level1, level2, level3, level4, level5, level6, level7, level8, level9, level10, level11};
        final int sectionTotalNum = location.size();

        int beginSearchTableIndex = 0;//search from which table
        boolean isFind = false;
        int sectionNum = 0;//which section is processing now

        while (it.hasNext()) {
            isFind = false;
            String locationSection = it.next();
            for (int i = beginSearchTableIndex; i < 11; i++) {
                for (int j = 0; j < tableName[i].length; j++) {
                    int index = locationSection.indexOf(tableName[i][j]);
                    if (index != -1) {//if found in the ith table
                        map.put(i + 1, locationSection);
                        beginSearchTableIndex = i + 1;//next search is start from i+1 table
                        isFind = true;
                        break;
                    }
                }
                if (isFind)
                    break;//if found ,not necessary searching the rest tables
                if (i == 10 && sectionNum < sectionTotalNum) {
                    logger.warn(location + "can't be processed");
                    return null;
                }
            }
            sectionNum++;
            //if the last section not contains suffix,it should belong to the last level
            if (sectionNum == sectionTotalNum && (!isFind))
                map.put(11, locationSection);

        }
        return map;
    }


    public static void printLevelMap(ArrayList<String> location) {

        Map<Integer, String> map = addressMapCreate(location);
        if (map != null) {
            Set<Integer> keySet = map.keySet();
            Iterator<Integer> it = keySet.iterator();
            while (it.hasNext()) {
                Integer key = it.next();
                String value = map.get(key);
                System.out.println(key + "," + value);
            }
        }
    }

}




