package DAO;

import CONEXION.Conexion;
import MODELS.Tarea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TareaDao {
    private final Conexion conexion;

    public TareaDao(Conexion conexion) {
        this.conexion = conexion;
    }

    public boolean insert(Tarea dato) throws SQLException {
        String sql = "INSERT INTO tarea(titulo,descripcion,estado,usuario_id) VALUES(?,?,?,?)";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dato.getTitulo());
            ps.setString(2, dato.getDescripcion());
            ps.setString(3, dato.getEstado());
            ps.setInt(4, dato.getUsuarioId());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Tarea> selectAll() throws SQLException {
        String sql = "SELECT t.id,t.titulo,t.descripcion,t.estado,t.usuario_id,"
                + "u.nombre AS usuario_nombre FROM tarea t "
                + "INNER JOIN usuario u ON t.usuario_id=u.id ORDER BY t.id";
        List<Tarea> lista = new ArrayList<>();
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(leerTarea(rs));
            }
        }
        return lista;
    }

    public Tarea selectById(int id) throws SQLException {
        String sql = "SELECT t.id,t.titulo,t.descripcion,t.estado,t.usuario_id,"
                + "u.nombre AS usuario_nombre FROM tarea t "
                + "INNER JOIN usuario u ON t.usuario_id=u.id WHERE t.id=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerTarea(rs) : null;
            }
        }
    }

    public boolean update(Tarea dato) throws SQLException {
        String sql = "UPDATE tarea SET titulo=?,descripcion=?,estado=?,usuario_id=? WHERE id=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dato.getTitulo());
            ps.setString(2, dato.getDescripcion());
            ps.setString(3, dato.getEstado());
            ps.setInt(4, dato.getUsuarioId());
            ps.setInt(5, dato.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tarea WHERE id=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Tarea leerTarea(ResultSet rs) throws SQLException {
        Tarea tarea = new Tarea(rs.getInt("id"));
        tarea.setTitulo(rs.getString("titulo"));
        tarea.setDescripcion(rs.getString("descripcion"));
        tarea.setEstado(rs.getString("estado"));
        tarea.setUsuarioId(rs.getInt("usuario_id"));
        tarea.setUsuarioNombre(rs.getString("usuario_nombre"));
        return tarea;
    }
}
