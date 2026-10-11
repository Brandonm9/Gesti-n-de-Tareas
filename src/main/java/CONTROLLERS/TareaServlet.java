package CONTROLLERS;

import CONEXION.Conexion;
import DAO.TareaDao;
import DAO.UsuarioDao;
import MODELS.Tarea;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class TareaServlet extends HttpServlet {
    private final Conexion conexion = new Conexion();
    private final TareaDao tareaDao = new TareaDao(conexion);
    private final UsuarioDao usuarioDao = new UsuarioDao(conexion);

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
                    mostrarFormulario(request, response, null, "insertar");
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
            response.sendRedirect(request.getContextPath() + "/tarea?action=consultar");
        } catch (SQLException ex) {
            getServletContext().log("Error en CRUD de tareas", ex);
            if ("GET".equals(request.getMethod())) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo consultar la base de datos.");
            } else {
                mensaje(request, "No se pudo completar la operación. Revise los datos y la conexión.");
                response.sendRedirect(request.getContextPath() + "/tarea?action=consultar");
            }
        }
    }

    protected void insert(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        Tarea tarea = leerFormulario(request, false);
        boolean respuesta = tareaDao.insert(tarea);
        mensaje(request, respuesta ? "Tarea creada correctamente." : "No se creó la tarea.");
        response.sendRedirect(request.getContextPath() + "/tarea?action=consultar");
    }

    protected void select(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        request.setAttribute("lista", tareaDao.selectAll());
        mostrarMensaje(request);
        request.getRequestDispatcher("/WEB-INF/vistas/tareas/mostrar.jsp")
                .forward(request, response);
    }

    protected void selectById(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Tarea tarea = tareaDao.selectById(id);
        if (tarea == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tarea no encontrada");
            return;
        }
        mostrarFormulario(request, response, tarea, "actualizar");
    }

    protected void update(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        Tarea tarea = leerFormulario(request, true);
        boolean respuesta = tareaDao.update(tarea);
        mensaje(request, respuesta ? "Tarea actualizada correctamente." : "No se encontró la tarea.");
        response.sendRedirect(request.getContextPath() + "/tarea?action=consultar");
    }

    protected void delete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean respuesta = tareaDao.delete(id);
        mensaje(request, respuesta ? "Tarea eliminada correctamente." : "No se encontró la tarea.");
        response.sendRedirect(request.getContextPath() + "/tarea?action=consultar");
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
            Tarea tarea, String accion) throws SQLException, ServletException, IOException {
        request.setAttribute("tarea", tarea);
        request.setAttribute("usuarios", usuarioDao.selectAll());
        request.setAttribute("accionFormulario", accion);
        request.getRequestDispatcher("/WEB-INF/vistas/tareas/nuevo.jsp")
                .forward(request, response);
    }

    private Tarea leerFormulario(HttpServletRequest request, boolean editar) {
        Tarea tarea = new Tarea();
        if (editar) {
            tarea.setId(Integer.parseInt(request.getParameter("id")));
        }
        String titulo = request.getParameter("titulo");
        String descripcion = request.getParameter("descripcion");
        String estado = request.getParameter("estado");
        int usuarioId = Integer.parseInt(request.getParameter("usuarioId"));
        if (titulo == null || titulo.isBlank() || titulo.length() > 120
                || descripcion != null && descripcion.length() > 500 || usuarioId < 1) {
            throw new IllegalArgumentException("Revise el título, la descripción y el usuario de la tarea.");
        }
        if (!"PENDIENTE".equals(estado) && !"EN_PROCESO".equals(estado)
                && !"FINALIZADA".equals(estado)) {
            throw new IllegalArgumentException("Seleccione un estado válido.");
        }
        tarea.setTitulo(titulo.trim());
        tarea.setDescripcion(descripcion == null ? "" : descripcion.trim());
        tarea.setEstado(estado);
        tarea.setUsuarioId(usuarioId);
        return tarea;
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
