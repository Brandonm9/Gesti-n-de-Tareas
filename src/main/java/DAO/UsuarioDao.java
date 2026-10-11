package DAO;

import CONEXION.Conexion;
import MODELS.Usuario;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UsuarioDao {
    private static final int ITERACIONES = 600_000;
    private final Conexion conexion;

    public UsuarioDao(Conexion conexion) {
        this.conexion = conexion;
    }

    public boolean insert(Usuario dato) throws SQLException {
        String sql = "INSERT INTO usuario(nombre,correo,clave_hash,sal) VALUES(?,?,?,?)";
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        String hash = calcularHash(dato.getClave(), sal);
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getCorreo());
            ps.setString(3, hash);
            ps.setString(4, HexFormat.of().formatHex(sal));
            return ps.executeUpdate() > 0;
        }
    }

    public List<Usuario> selectAll() throws SQLException {
        String sql = "SELECT id,nombre,correo FROM usuario ORDER BY id";
        List<Usuario> lista = new ArrayList<>();
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(leerUsuario(rs));
            }
        }
        return lista;
    }

    public Usuario selectById(int id) throws SQLException {
        String sql = "SELECT id,nombre,correo FROM usuario WHERE id=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerUsuario(rs) : null;
            }
        }
    }

    public boolean update(Usuario dato) throws SQLException {
        String clave = dato.getClave();
        boolean cambiaClave = clave != null && !clave.isBlank();
        String sql = cambiaClave
                ? "UPDATE usuario SET nombre=?,correo=?,clave_hash=?,sal=? WHERE id=?"
                : "UPDATE usuario SET nombre=?,correo=? WHERE id=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getCorreo());
            if (cambiaClave) {
                byte[] sal = new byte[16];
                new SecureRandom().nextBytes(sal);
                ps.setString(3, calcularHash(clave, sal));
                ps.setString(4, HexFormat.of().formatHex(sal));
                ps.setInt(5, dato.getId());
            } else {
                ps.setInt(3, dato.getId());
            }
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Usuario autenticar(String correo, String clave) throws SQLException {
        String sql = "SELECT id,nombre,correo,clave_hash,sal FROM usuario WHERE correo=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                byte[] sal = HexFormat.of().parseHex(rs.getString("sal"));
                byte[] esperado = HexFormat.of().parseHex(rs.getString("clave_hash"));
                byte[] recibido = HexFormat.of().parseHex(calcularHash(clave, sal));
                return MessageDigest.isEqual(esperado, recibido) ? leerUsuario(rs) : null;
            }
        }
    }

    private Usuario leerUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setCorreo(rs.getString("correo"));
        return usuario;
    }

    private String calcularHash(String clave, byte[] sal) {
        PBEKeySpec especificacion = new PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, 256);
        try {
            byte[] resultado = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(especificacion).getEncoded();
            return HexFormat.of().formatHex(resultado);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("No fue posible proteger la contraseña", ex);
        } finally {
            especificacion.clearPassword();
        }
    }
}
