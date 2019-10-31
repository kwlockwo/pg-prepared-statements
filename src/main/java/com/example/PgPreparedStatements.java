package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.jdbc.PgStatement;

/**
 * Hello world!
 *
 */
public class PgPreparedStatements
{
    public static void main( String[] args )
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        String dbUser = System.getenv("JDBC_DATABASE_USERNAME");
        String dbPass = System.getenv("JDBC_DATABASE_PASSWORD");

        String prepareThreshold = args[0];

        try {
            con = DriverManager.getConnection(dbUrl,dbUser,dbPass);
            con.setAutoCommit(false);
            pstmt = con.prepareStatement("SELECT 1");
            ((PgStatement) pstmt).setPrepareThreshold(Integer.parseInt(prepareThreshold));
            pstmt.setFetchSize(1);
            pstmt.executeQuery();
            pstmt.close();

            pstmt = con.prepareStatement("select count(*) from pg_prepared_statements where statement = 'SELECT 1'");
            rs = pstmt.executeQuery();

            if(rs.next()) {
                int count = rs.getInt(1);
                if(count > 0) {
                    System.out.println("Prepared statement was created, count: " + count);
                } else {
                    System.out.println("No prepared statements");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error code: " + e.getErrorCode() + "SQL state: " + e.getSQLState());
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
                con.close();
            } catch (SQLException e) {
                //ignore
            }
        }

    }
}
