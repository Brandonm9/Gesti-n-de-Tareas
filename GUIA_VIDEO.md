# Guía para grabar la entrega

Duración sugerida: **8 a 10 minutos**. Grabe la pantalla y explique con su propia voz. Aumente el tamaño de letra en NetBeans antes de mostrar código. Entregue el archivo de video que pida el profesor; no adjunte el proyecto.

## Preparación de la demostración

1. Importe `base_de_datos.sql`, inicie MySQL y Tomcat desde NetBeans, y abra la aplicación.
2. Deje NetBeans abierto con el proyecto visible y MySQL Workbench preparado para mostrar las tablas.
3. Use los siguientes datos de prueba durante la grabación:
   - Usuario: **Ana López**, `ana@demo.local`, contraseña `Demo12345!`.
   - Tarea: **Preparar presentación**, descripción **Explicar el proyecto en video**, asignada a Ana.
4. Si ya usó esos datos, elimínelos antes de empezar. Borre primero la tarea y después el usuario.

## Secuencia sugerida

### 0:00–0:45 | Presentación

> «Esta es mi aplicación web de gestión de tareas. Usa JSP para las pantallas, Servlets para recibir las solicitudes, clases DAO para las consultas y MySQL para almacenar los datos. El proyecto tiene Login y CRUD de dos tablas relacionadas.»

Muestre brevemente la pantalla de Login y, si cabe, el árbol de paquetes en NetBeans.

### 0:45–1:30 | Relación de las tablas

Muestre en MySQL Workbench `usuario` y `tarea`. Señale `tarea.usuario_id`, la clave foránea hacia `usuario.id` y la regla que impide eliminar un usuario con tareas.

> «Un usuario puede tener varias tareas; cada tarea pertenece a un usuario. La relación se guarda con una clave foránea, no solo con un texto en la pantalla.»

### 1:30–2:15 | Login

Pruebe una contraseña incorrecta y luego ingrese con `admin@clase.local` / `Curso2026!`. Explique que `LoginServlet` consulta `UsuarioDao`, verifica el hash y crea una sesión. El filtro revisa esa sesión antes de permitir `/usuario` o `/tarea`.

### 2:15–3:45 | CRUD de usuarios

Abra **Usuarios**. Cree a Ana, compruebe que aparece en la lista y edite su nombre, por ejemplo a **Ana María López**. Señale que cada formulario envía `action` al `UsuarioServlet`, que llama al método correspondiente del DAO.

### 3:45–5:15 | CRUD de tareas

Abra **Tareas**. Cree la tarea de prueba y seleccione a Ana como responsable. Señale su nombre en la tabla. Edite la tarea, cambie el estado a **Finalizada** y compruebe que la lista se actualiza.

### 5:15–5:50 | Integridad de la relación

Vuelva a **Usuarios** e intente borrar a Ana mientras tiene una tarea. Muestre el mensaje de rechazo y explique que la clave foránea protege la relación. Después borre la tarea y finalmente borre a Ana. Así quedan demostradas las operaciones **Eliminar** de las dos tablas.

### 5:50–8:30 | Explicación del código

Abra y explique, en este orden:

1. `MODELS/Usuario.java` y `MODELS/Tarea.java`: representan los registros y contienen getters/setters.
2. `CONEXION/Conexion.java`: abre la conexión JDBC a MySQL.
3. `DAO/TareaDao.java`: muestre un `INSERT` con `PreparedStatement`, el `JOIN` de `selectAll` y un `UPDATE` o `DELETE`.
4. `CONTROLLERS/TareaServlet.java`: muestre `processRequest`, `switch(action)`, `request.getParameter`, el DAO y `forward`/`sendRedirect`.
5. `mostrar.jsp`: muestre `<c:forEach>` y `<c:out>` de JSTL.
6. `LoginServlet.java`, `FiltroAutenticacion.java` y `LogoutServlet.java`: explique creación, comprobación e invalidación de la sesión.

Evite leer todo el código línea por línea. Explique qué responsabilidad tiene cada capa y siga una solicitud concreta desde el formulario hasta MySQL y de regreso a la lista.

### 8:30–9:00 | Cierre

Pulse **Salir**. Escriba directamente la URL `/tarea?action=consultar` y muestre que redirige al Login.

> «Así quedan demostrados el Login, las cuatro operaciones en ambas tablas, su relación y las tecnologías solicitadas.»

## Comprobación final antes de enviar

- La grabación muestra la aplicación funcionando, no solo fragmentos de código.
- Se ven claramente Crear, Consultar, Actualizar y Eliminar en ambas tablas.
- Se explica la clave foránea y se demuestra su efecto.
- Se ve un Login fallido, uno correcto y el cierre de sesión.
- La voz y el código se entienden; no aparecen contraseñas personales ni ventanas ajenas a la tarea.
- La entrega contiene el video, no el código fuente.
