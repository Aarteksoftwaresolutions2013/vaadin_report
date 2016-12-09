package org.jdal.samples.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class MDemo {

    public static void main(String[] args) {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager
                            .getConnection("jdbc:sqlserver://192.168.1.70\\SQLEXPRESS;user=test;password=test;database=crystalreport_poc");
            System.out.println("test");
            Statement sta = conn.createStatement();

            // 4. Write the query`
            String sql = "Select * from user";
            // 5. Execute the statement and
            ResultSet rs = sta.executeQuery(sql);
            // 6. Process the result set

            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
