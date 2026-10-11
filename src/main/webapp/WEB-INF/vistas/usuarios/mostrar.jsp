<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Usuarios</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
    </head>
    <body>
        <header class="barra">
            <div class="contenedor barra-interior">
                <strong>Gestor de tareas</strong>
                <nav>
                    <a href="${pageContext.request.contextPath}/tarea?action=consultar">Tareas</a>
                    <a aria-current="page" href="${pageContext.request.contextPath}/usuario?action=consultar">Usuarios</a>
                </nav>
                <div class="sesion"><span><c:out value="${sessionScope.usuarioNombre}" /></span>
                    <form action="${pageContext.request.contextPath}/logout" method="post"><button type="submit" class="enlace">Salir</button></form>
                </div>
            </div>
        </header>
        <main class="contenedor">
            <div class="titulo-fila">
                <div><h1>Usuarios</h1><p>Administre las personas que pueden ingresar y recibir tareas.</p></div>
                <a class="boton primario" href="${pageContext.request.contextPath}/usuario?action=nuevo">Nuevo usuario</a>
            </div>
            <c:if test="${not empty msg}"><div class="mensaje" role="status"><c:out value="${msg}" /></div></c:if>
            <div class="tarjeta tabla-contenedor">
                <table>
                    <thead><tr><th>ID</th><th>Nombre</th><th>Correo</th><th>Acciones</th></tr></thead>
                    <tbody>
                        <c:forEach items="${lista}" var="ver">
                            <tr>
                                <td><c:out value="${ver.id}" /></td>
                                <td><c:out value="${ver.nombre}" /></td>
                                <td><c:out value="${ver.correo}" /></td>
                                <td class="acciones">
                                    <a href="${pageContext.request.contextPath}/usuario?action=consultarById&amp;id=${ver.id}">Editar</a>
                                    <c:if test="${ver.id ne sessionScope.usuarioId}">
                                        <form action="${pageContext.request.contextPath}/usuario" method="post"
                                              onsubmit="return confirm('¿Eliminar este usuario? Si tiene tareas, la base de datos lo impedirá.');">
                                            <input type="hidden" name="action" value="borrar">
                                            <input type="hidden" name="id" value="${ver.id}">
                                            <button type="submit" class="enlace peligro">Eliminar</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty lista}"><tr><td colspan="4">No hay usuarios registrados.</td></tr></c:if>
                    </tbody>
                </table>
            </div>
        </main>
    </body>
</html>
