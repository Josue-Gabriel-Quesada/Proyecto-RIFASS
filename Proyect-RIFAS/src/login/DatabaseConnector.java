/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author sgtom
 */
public class DatabaseConnector {
        private static final String url = "jdbc:oracle:thin:@//localhost:1521/XE";
    private static final String user = "C##USUARIO_MESSI";
    private static final String password = "12345";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
