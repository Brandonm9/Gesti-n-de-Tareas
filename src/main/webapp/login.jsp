<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Iniciar sesión</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
    </head>
    <body class="pagina-login">
        <main class="tarjeta login">
            <h1>Gestor de tareas</h1>
            <p>Inicie sesión para administrar usuarios y tareas.</p>
            <c:if test="${not empty msg}">
                <div class="mensaje error" role="alert"><c:out value="${msg}" /></div>
            </c:if>
            <form action="${pageContext.request.contextPath}/login" method="post">
                <label for="correo">Correo electrónico</label>
                <input id="correo" name="correo" type="email" maxlength="150" required autofocus>
                <label for="clave">Contraseña</label>
                <input id="clave" name="clave" type="password" required>
                <button type="submit" class="boton primario">Entrar</button>
            </form>
        </main>
    </body>
</html>
