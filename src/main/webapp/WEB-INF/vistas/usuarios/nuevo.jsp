<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Formulario de usuario</title>
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
            <a href="${pageContext.request.contextPath}/usuario?action=consultar">← Volver a usuarios</a>
            <div class="tarjeta formulario">
                <h1><c:choose><c:when test="${accionFormulario eq 'actualizar'}">Editar usuario</c:when><c:otherwise>Nuevo usuario</c:otherwise></c:choose></h1>
                <form action="${pageContext.request.contextPath}/usuario" method="post">
                    <input type="hidden" name="action" value="${accionFormulario}">
                    <c:if test="${accionFormulario eq 'actualizar'}"><input type="hidden" name="id" value="${usuario.id}"></c:if>
                    <label for="nombre">Nombre</label>
                    <input id="nombre" name="nombre" maxlength="100" value="<c:out value='${usuario.nombre}' />" required>
                    <label for="correo">Correo electrónico</label>
                    <input id="correo" name="correo" type="email" maxlength="150" value="<c:out value='${usuario.correo}' />" required>
                    <label for="clave">Contraseña</label>
                    <input id="clave" name="clave" type="password" minlength="8">
                    <p class="ayuda"><c:choose><c:when test="${accionFormulario eq 'actualizar'}">Déjela vacía para conservar la contraseña actual.</c:when><c:otherwise>Escriba al menos 8 caracteres.</c:otherwise></c:choose></p>
                    <div class="botones"><button type="submit" class="boton primario">Guardar</button>
                        <a class="boton secundario" href="${pageContext.request.contextPath}/usuario?action=consultar">Cancelar</a>
                    </div>
                </form>
            </div>
        </main>
    </body>
</html>
