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

public class LoginServlet extends HttpServlet {
    private final UsuarioDao usuarioDao = new UsuarioDao(new Conexion());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion != null && sesion.getAttribute("usuarioId") != null) {
            response.sendRedirect(request.getContextPath() + "/tarea?action=consultar");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String correo = request.getParameter("correo");
        String clave = request.getParameter("clave");
        if (correo == null || clave == null || correo.isBlank() || clave.isBlank()) {
            request.setAttribute("msg", "Escriba su correo y contraseña.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        try {
            Usuario usuario = usuarioDao.autenticar(correo.trim(), clave);
            if (usuario == null) {
                request.setAttribute("msg", "Correo o contraseña incorrectos.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }
            HttpSession sesion = request.getSession();
            request.changeSessionId();
            sesion.setAttribute("usuarioId", usuario.getId());
            sesion.setAttribute("usuarioNombre", usuario.getNombre());
            sesion.setMaxInactiveInterval(30 * 60);
            response.sendRedirect(request.getContextPath() + "/tarea?action=consultar");
        } catch (SQLException ex) {
            getServletContext().log("Error en el Login", ex);
            request.setAttribute("msg", "No se pudo conectar con la base de datos.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
