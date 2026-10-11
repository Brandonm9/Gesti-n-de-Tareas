<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Formulario de tarea</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
    </head>
    <body>
        <header class="barra"><div class="contenedor barra-interior">
            <strong>Gestor de tareas</strong>
            <nav><a href="${pageContext.request.contextPath}/tarea?action=consultar">Tareas</a>
                 <a href="${pageContext.request.contextPath}/usuario?action=consultar">Usuarios</a></nav>
            <div class="sesion"><span><c:out value="${sessionScope.usuarioNombre}" /></span>
                <form action="${pageContext.request.contextPath}/logout" method="post"><button type="submit" class="enlace">Salir</button></form>
            </div>
        </div></header>
        <main class="contenedor formulario-pagina">
            <a href="${pageContext.request.contextPath}/tarea?action=consultar">← Volver a tareas</a>
            <div class="tarjeta formulario">
                <h1><c:choose><c:when test="${accionFormulario eq 'actualizar'}">Editar tarea</c:when><c:otherwise>Nueva tarea</c:otherwise></c:choose></h1>
                <form action="${pageContext.request.contextPath}/tarea" method="post">
                    <input type="hidden" name="action" value="${accionFormulario}">
                    <c:if test="${accionFormulario eq 'actualizar'}"><input type="hidden" name="id" value="${tarea.id}"></c:if>
                    <label for="titulo">Título</label>
                    <input id="titulo" name="titulo" maxlength="120" value="<c:out value='${tarea.titulo}' />" required>
                    <label for="descripcion">Descripción</label>
                    <textarea id="descripcion" name="descripcion" maxlength="500" rows="4"><c:out value="${tarea.descripcion}" /></textarea>
                    <label for="estado">Estado</label>
                    <select id="estado" name="estado" required>
                        <option value="PENDIENTE" <c:if test="${tarea.estado eq 'PENDIENTE'}">selected</c:if>>Pendiente</option>
                        <option value="EN_PROCESO" <c:if test="${tarea.estado eq 'EN_PROCESO'}">selected</c:if>>En proceso</option>
                        <option value="FINALIZADA" <c:if test="${tarea.estado eq 'FINALIZADA'}">selected</c:if>>Finalizada</option>
                    </select>
                    <label for="usuarioId">Usuario asignado</label>
                    <select id="usuarioId" name="usuarioId" required>
                        <option value="">Seleccione un usuario</option>
                        <c:forEach items="${usuarios}" var="u">
                            <option value="${u.id}" <c:if test="${u.id eq tarea.usuarioId}">selected</c:if>><c:out value="${u.nombre}" /></option>
                        </c:forEach>
                    </select>
                    <div class="botones"><button type="submit" class="boton primario">Guardar</button>
                        <a class="boton secundario" href="${pageContext.request.contextPath}/tarea?action=consultar">Cancelar</a>
                    </div>
                </form>
            </div>
        </main>
    </body>
</html>
