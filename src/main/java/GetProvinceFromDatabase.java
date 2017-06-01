import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

class DBHelper {

    public static final String DBURL = "jdbc:mysql://127.0.0.1:3306/test"; //数据库名
    public static final String DBNAME = "com.mysql.jdbc.Driver";
    public static final String DBUSER = "root";                        //用户名
    public static final String DBPASSWORD = "123456";                    //密码

    public Connection conn = null;
    public PreparedStatement pst = null;

    public DBHelper(String sql) throws SQLException {
        try {
            Class.forName(DBNAME);
            conn = (Connection) DriverManager.getConnection(DBURL, DBUSER, DBPASSWORD);
            pst = (PreparedStatement) conn.prepareStatement(sql);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.conn.close();
            this.pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


public class GetProvinceFromDatabase {

    public DBHelper db1 = null;
    public ResultSet ret = null;

    public String getProvince(String cityName) throws SQLException {

        String sql = "select * from location c where c.city=? ";
        StringBuffer province = new StringBuffer();
        db1 = new DBHelper(sql);
        try {
            db1.pst.setString(1, cityName);
            ret = db1.pst.executeQuery();
            while (ret.next()) {
                province.append(ret.getString("province"));
                province.append(",");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ret.close();
            db1.close();
        }
        return province.toString();
    }

}


