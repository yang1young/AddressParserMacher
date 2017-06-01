import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import java.io.IOException;
import java.util.*;


public class AddressMatchingUtils {
    public static final double[] weightList = {1.0};

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public static double getLikelihood(String ori, String ano) {
        return 0.0;
    }


    public static double[] getLatAndLong(String address) throws IOException {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });

        CoordinateURL url = new CoordinateURL(address);
        HttpRequest request = requestFactory.buildGetRequest(url);
        CoordinateResult res = request.execute().parseAs(CoordinateResult.class);

        double lat = res.result.location.lat;
        double lng = res.result.location.lng;
        double con = res.result.confidence;
        double pre = res.result.precise;

        double[] result = new double[]{lat, lng, con, pre};
        return result;

    }


    public static String getblurLocation(String address) throws IOException {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });
        PlaceQueryURL url = new PlaceQueryURL(address);
        HttpRequest request = requestFactory.buildGetRequest(url);
        PlaceQueryResult res = request.execute().parseAs(PlaceQueryResult.class);
        ArrayList<String> blurLocation = new ArrayList<String>();
        ArrayList<Address> location = (ArrayList) res.result;
        if (!location.isEmpty()) {
            Address myAddress = location.get(0);
            String provence = getblurLocationInter(myAddress);
            blurLocation.add(myAddress.district);
            blurLocation.add(myAddress.city);
            blurLocation.add(provence);
            String blurLocationResult = blurStringSplit(address, blurLocation);
            return blurLocationResult;
        }
        return null;
    }


    private static String getblurLocationInter(Address myAddress) throws IOException {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });
        if (myAddress != null) {
            String address = myAddress.city + myAddress.district;
            PlaceQueryURL url = new PlaceQueryURL(address);
            HttpRequest request = requestFactory.buildGetRequest(url);
            PlaceQueryResult res = request.execute().parseAs(PlaceQueryResult.class);

            ArrayList<Address> location = (ArrayList) res.result;
            if (!location.isEmpty()) {
                Address getProvence = location.get(0);
                return getProvence.city;
            }
        }
        return null;
    }

    private static String blurStringSplit(String originPlace,
                                          ArrayList<String> exactPlace) {

        String output;
        Iterator<String> it = exactPlace.iterator();
        while (it.hasNext()) {
            String currentSection = it.next();
            for (int j = currentSection.length(); j > 1; j--) {
                String temp = currentSection.substring(0, j);
                int index = originPlace.indexOf(temp);
                if (index != -1) {
                    output = originPlace.substring(index + temp.length());
                    return output;
                }
            }
        }
        return originPlace;
    }


    public static class PlaceQueryURL extends GenericUrl {
        public PlaceQueryURL(String query) {
            super("http://api.map.baidu.com/place/v2/suggestion");
            this.query = query;
        }

        @Key
        public final String query;
        @Key
        public final String region = "全国";
        @Key
        public final String output = "json";
        @Key
        public final String ak = "EAta4Mx5BnrqA8Id1Ew3tx5N";
    }

    public static class CoordinateURL extends GenericUrl {
        public CoordinateURL(String address) {
            super("http://api.map.baidu.com/geocoder/v2/");
            this.address = address;
        }

        @Key
        public final String address;
        @Key
        public final String output = "json";
        @Key
        public final String ak = "EAta4Mx5BnrqA8Id1Ew3tx5N";
    }

    public static class CoordinateResult {
        @Key
        public int status;
        @Key
        public Coordinate result;
    }

    public static class Coordinate {
        @Key
        public Location location;
        @Key
        public int precise;
        @Key
        public int confidence;
        @Key
        public String level;
    }

    public static class PlaceQueryResult {
        @Key
        public int status;
        @Key
        public String message;
        @Key
        public List<Address> result;
    }

    public static class Address {
        @Key
        public String name;
        @Key("object")
        public Location location;
        @Key
        public String uid;
        @Key
        public String city;
        @Key
        public String district;
        @Key
        public String business;
        @Key
        public String cityid;
    }

    public static class Location {
        @Key
        public double lat;
        @Key
        public double lng;
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

                double likelihood = AddressMatchingUtils.getLikelihood(addressSplit, anotherSplit);
                metrics += AddressMatchingUtils.weightList[level] * likelihood;
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