import CONEXION.Conexion;
import DAO.TareaDao;
import DAO.UsuarioDao;
import MODELS.Tarea;
import MODELS.Usuario;
import java.sql.SQLException;

/** Prueba manual de la base de datos temporal; no forma parte del WAR. */
public class PruebaIntegracion {
    public static void main(String[] args) throws Exception {
        Conexion conexion = new Conexion();
        UsuarioDao usuarios = new UsuarioDao(conexion);
        TareaDao tareas = new TareaDao(conexion);
        exigir(usuarios.autenticar("admin@clase.local", "Curso2026!") != null,
                "El Login inicial debe funcionar");
        exigir(usuarios.autenticar("admin@clase.local", "incorrecta") == null,
                "El Login debe rechazar una clave incorrecta");

        String sufijo = Long.toString(System.currentTimeMillis());
        Usuario usuario = new Usuario();
        usuario.setNombre("Prueba CRUD");
        usuario.setCorreo("prueba" + sufijo + "@clase.local");
        usuario.setClave("Prueba123!");
        exigir(usuarios.insert(usuario), "Crear usuario");
        usuario = usuarios.selectAll().stream()
                .filter(u -> u.getCorreo().equals("prueba" + sufijo + "@clase.local"))
                .findFirst().orElseThrow();
        int usuarioId = usuario.getId();
        int tareaId = 0;
        try {
            exigir(usuarios.autenticar(usuario.getCorreo(), "Prueba123!") != null,
                    "Login del usuario creado");
            usuario.setNombre("Prueba actualizada");
            usuario.setClave("");
            exigir(usuarios.update(usuario), "Actualizar usuario");
            exigir("Prueba actualizada".equals(usuarios.selectById(usuarioId).getNombre()),
                    "Consultar usuario actualizado");

            Tarea tarea = new Tarea();
            tarea.setTitulo("Tarea " + sufijo);
            tarea.setDescripcion("Prueba de relación");
            tarea.setEstado("PENDIENTE");
            tarea.setUsuarioId(usuarioId);
            exigir(tareas.insert(tarea), "Crear tarea");
            tarea = tareas.selectAll().stream()
                    .filter(t -> t.getTitulo().equals("Tarea " + sufijo))
                    .findFirst().orElseThrow();
            tareaId = tarea.getId();
            exigir("Prueba actualizada".equals(tarea.getUsuarioNombre()), "JOIN de las tablas");
            tarea.setEstado("FINALIZADA");
            exigir(tareas.update(tarea), "Actualizar tarea");
            exigir("FINALIZADA".equals(tareas.selectById(tareaId).getEstado()),
                    "Consultar tarea actualizada");

            boolean protegida = false;
            try {
                usuarios.delete(usuarioId);
            } catch (SQLException ex) {
                protegida = true;
            }
            exigir(protegida, "La clave foránea debe impedir borrar un usuario con tareas");
            exigir(tareas.delete(tareaId), "Eliminar tarea");
            tareaId = 0;
            exigir(usuarios.delete(usuarioId), "Eliminar usuario");
            usuarioId = 0;
            System.out.println("OK: Login, CRUD de dos tablas, JOIN y clave foránea");
        } finally {
            if (tareaId != 0) tareas.delete(tareaId);
            if (usuarioId != 0) usuarios.delete(usuarioId);
        }
    }

    private static void exigir(boolean condicion, String descripcion) {
        if (!condicion) throw new AssertionError(descripcion);
    }
}
