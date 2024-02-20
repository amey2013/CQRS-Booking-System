package org.amaap.cqrs.hotel.booking;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    static Connection getDbConnection(String url) {
        Connection con=null;
        try {
            con = DriverManager.getConnection(url, "root", "Amey@1302");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return con;
    }
}
