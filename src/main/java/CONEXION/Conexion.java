package CONEXION;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL_PREDETERMINADA =
            "jdbc:mysql://localhost:3306/gestor_tareas?useSSL=false&serverTimezone=UTC";

    public Connection conectar() throws SQLException {
        String url = valor("APP_DB_URL", URL_PREDETERMINADA);
        String usuario = valor("APP_DB_USER", "root");
        String clave = valor("APP_DB_PASSWORD", "123456");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("No se encontró MySQL Connector/J", ex);
        }
        return DriverManager.getConnection(url, usuario, clave);
    }

    private String valor(String nombre, String predeterminado) {
        String configurado = System.getenv(nombre);
        return configurado == null ? predeterminado : configurado;
    }
}
