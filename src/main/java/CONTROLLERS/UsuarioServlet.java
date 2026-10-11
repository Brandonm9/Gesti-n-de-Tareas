package CONTROLLERS;

import CONEXION.Conexion;
import DAO.UsuarioDao;
import MODELS.Usuario;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class UsuarioServlet extends HttpServlet {
    private final UsuarioDao usuarioDao = new UsuarioDao(new Conexion());

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("action");
        if (accion == null) {
            accion = "consultar";
        }
        try {
            switch (accion) {
                case "consultar":
                    if (!esGet(request, response)) return;
                    select(request, response);
                    break;
                case "nuevo":
                    if (!esGet(request, response)) return;
                    request.setAttribute("accionFormulario", "insertar");
                    request.getRequestDispatcher("/WEB-INF/vistas/usuarios/nuevo.jsp")
                            .forward(request, response);
                    break;
                case "consultarById":
                    if (!esGet(request, response)) return;
                    selectById(request, response);
                    break;
                case "insertar":
                    if (!esPost(request, response)) return;
                    insert(request, response);
                    break;
                case "actualizar":
                    if (!esPost(request, response)) return;
                    update(request, response);
                    break;
                case "borrar":
                    if (!esPost(request, response)) return;
                    delete(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (IllegalArgumentException ex) {
            mensaje(request, ex.getMessage());
            response.sendRedirect(request.getContextPath() + "/usuario?action=consultar");
        } catch (SQLException ex) {
            getServletContext().log("Error en CRUD de usuarios", ex);
            if ("GET".equals(request.getMethod())) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo consultar la base de datos.");
            } else {
                String texto = "No se pudo completar la operación. Revise si el correo ya existe o si el usuario tiene tareas.";
                mensaje(request, texto);
                response.sendRedirect(request.getContextPath() + "/usuario?action=consultar");
            }
        }
    }

    protected void insert(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        Usuario usuario = leerFormulario(request, false);
        boolean respuesta = usuarioDao.insert(usuario);
        mensaje(request, respuesta ? "Usuario creado correctamente." : "No se creó el usuario.");
        response.sendRedirect(request.getContextPath() + "/usuario?action=consultar");
    }

    protected void select(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        request.setAttribute("lista", usuarioDao.selectAll());
        mostrarMensaje(request);
        request.getRequestDispatcher("/WEB-INF/vistas/usuarios/mostrar.jsp")
                .forward(request, response);
    }

    protected void selectById(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Usuario usuario = usuarioDao.selectById(id);
        if (usuario == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
            return;
        }
        request.setAttribute("usuario", usuario);
        request.setAttribute("accionFormulario", "actualizar");
        request.getRequestDispatcher("/WEB-INF/vistas/usuarios/nuevo.jsp")
                .forward(request, response);
    }

    protected void update(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        Usuario usuario = leerFormulario(request, true);
        boolean respuesta = usuarioDao.update(usuario);
        HttpSession sesion = request.getSession(false);
        if (respuesta && sesion != null && (Integer) sesion.getAttribute("usuarioId") == usuario.getId()) {
            sesion.setAttribute("usuarioNombre", usuario.getNombre());
        }
        mensaje(request, respuesta ? "Usuario actualizado correctamente." : "No se encontró el usuario.");
        response.sendRedirect(request.getContextPath() + "/usuario?action=consultar");
    }

    protected void delete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        int actual = (Integer) request.getSession(false).getAttribute("usuarioId");
        if (id == actual) {
            mensaje(request, "No puede eliminar el usuario con el que inició sesión.");
        } else {
            boolean respuesta = usuarioDao.delete(id);
            mensaje(request, respuesta ? "Usuario eliminado correctamente." : "No se encontró el usuario.");
        }
        response.sendRedirect(request.getContextPath() + "/usuario?action=consultar");
    }

    private Usuario leerFormulario(HttpServletRequest request, boolean editar) {
        Usuario usuario = new Usuario();
        if (editar) {
            usuario.setId(Integer.parseInt(request.getParameter("id")));
        }
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String clave = request.getParameter("clave");
        if (nombre == null || nombre.isBlank() || nombre.length() > 100
                || correo == null || correo.isBlank() || correo.length() > 150 || !correo.contains("@")) {
            throw new IllegalArgumentException("Escriba un nombre y correo válidos.");
        }
        if ((!editar || (clave != null && !clave.isBlank())) && (clave == null || clave.length() < 8)) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        usuario.setNombre(nombre.trim());
        usuario.setCorreo(correo.trim());
        usuario.setClave(clave);
        return usuario;
    }

    private void mensaje(HttpServletRequest request, String texto) {
        request.getSession(false).setAttribute("msg", texto);
    }

    private void mostrarMensaje(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        Object texto = sesion.getAttribute("msg");
        if (texto != null) {
            request.setAttribute("msg", texto);
            sesion.removeAttribute("msg");
        }
    }

    private boolean esGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("GET".equals(request.getMethod())) return true;
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return false;
    }

    private boolean esPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("POST".equals(request.getMethod())) return true;
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
