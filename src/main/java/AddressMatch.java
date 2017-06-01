import org.apache.log4j.Logger;
import java.util.*;

/**
 * Created by yangqiao on 1/6/17.
 */
class AddressInfo {
    private HashMap<Integer, String> infoList;

    private double Latitude;

    private double longitude;

    public Boolean isSameAddress(AddressInfo another){
        HashMap<Integer, String> anotherInfo = another.getInfoList();
        double metrics = 0;
        Iterator it = infoList.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, String> entry = (Map.Entry) it.next();
            Integer level = entry.getKey();
            String addressSplit = entry.getValue();
            String anotherSplit = anotherInfo.get(level);

            if(anotherSplit == null)
                continue;
            if(level < 4 && !addressSplit.equals(anotherSplit))
                return false;
            else{

                double likelihood = AddressCleanMatchUtils.getSimilarityOfAddress(addressSplit, anotherSplit);
                metrics += BaiduMapAPIQueryTools.weightList[level] * likelihood;
            }
        }

        if(metrics < 0.8)
            return false;
        else
            return true;
    }

    public HashMap<Integer, String> getInfoList() {
        return infoList;
    }
}


public class AddressMatch {
    public static void main(String[] args) {
        AddressStringParser parser = new AddressStringParser();
        ArrayList<String> result = parser.stringAnalyze("山东省花小区");
        System.out.println(result);
        HashMap<Integer, String> level =  GetLevelInfoOfAddress.addressMapCreate(result);
        GetLevelInfoOfAddress.printLevelMap(result);
        try {
            System.out.println(BaiduMapAPIQueryTools.getblurLocation("杭州浙大玉泉校区"));
            System.out.println(BaiduMapAPIQueryTools.getLatAndLong("杭州浙大玉泉校区")[0]);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
