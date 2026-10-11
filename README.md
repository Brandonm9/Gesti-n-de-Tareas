# Gestor de tareas web

Aplicación de clase con **JSP, Servlets, DAO, JSTL y MySQL**. Tiene Login y CRUD completo de dos tablas relacionadas: `usuario` y `tarea`. Un usuario puede tener muchas tareas; cada tarea pertenece a un usuario.

## Organización

El recorrido de una solicitud sigue el ejemplo visto en clase:

`JSP → Servlet → DAO → MySQL → Servlet → JSP`

- `MODELS`: clases `Usuario` y `Tarea` con atributos, constructores, getters y setters.
- `CONEXION`: clase `Conexion` que abre una conexión JDBC.
- `DAO`: consultas SQL preparadas para crear, consultar, actualizar y borrar.
- `CONTROLLERS`: Servlets con `action`, `switch`, `doGet` y `doPost`, más el filtro de Login.
- `src/main/webapp`: formularios y tablas JSP; JSTL muestra las listas.
- `WEB-INF/web.xml`: rutas de los Servlets y del filtro.
- `base_de_datos.sql`: tablas, relación y cuenta inicial de prueba.

La carpeta original del profesor contiene archivos de varios proyectos y una introducción separada a JPA/Hibernate. Este proyecto usa **JDBC + DAO**, como el CRUD web mostrado en los videos. No copia el `pom.xml` de Hibernate.

## Requisitos para ejecutarlo

1. JDK 17.
2. Maven o NetBeans con Maven.
3. MySQL 8.0 o posterior.
4. Apache Tomcat 9.0.65. El proyecto usa Servlet 4 (`javax.servlet`) y JSTL 1.2, compatibles con el servidor que ya está registrado en NetBeans.

## Preparación

1. Abra MySQL Workbench y ejecute **todo** el archivo `base_de_datos.sql`. El script crea `gestor_tareas`, `usuario`, `tarea`, la clave foránea y el usuario inicial.
   Asegúrese de que el servidor MySQL esté iniciado; tener Workbench o los binarios instalados no significa que la base esté escuchando.
2. En el entorno desde el que arrancará Tomcat, configure la conexión. En este equipo, MySQL Workbench confirmó `localhost:3306`, usuario `root` y contraseña `123456`; los valores predeterminados ya coinciden. Si usa otra configuración, defina estas variables de entorno:

   ```powershell
   $env:APP_DB_URL = 'jdbc:mysql://127.0.0.1:3306/gestor_tareas?useSSL=false&serverTimezone=UTC'
   $env:APP_DB_USER = 'root'
   $env:APP_DB_PASSWORD = 'su_contraseña_local'
   ```

   Inicie Tomcat **desde esa misma terminal** para que reciba las variables. También puede configurar esas variables en el servidor que usa NetBeans.

3. En NetBeans, abra la carpeta `GestorTareasWeb` como proyecto Maven. Si ya estaba abierta durante el cambio de versión, pulse clic derecho en el proyecto y seleccione **Reload Project** o ciérrelo y ábralo de nuevo.
4. Pulse **Run Project**. Cuando NetBeans pregunte por el servidor, seleccione **Apache Tomcat or TomEE**, marque **Remember Permanently** y pulse **OK**. NetBeans construirá y desplegará el WAR con el contexto `GestorTareasWeb`.
5. Si el servidor todavía no aparece, vuelva a registrarlo en **Tools → Servers → Add Server → Apache Tomcat or TomEE** y elija esta carpeta:

   `C:\Users\yared\Downloads\apache-tomcat-9.0.65-windows-x64\apache-tomcat-9.0.65`

6. Abra `http://localhost:8080/GestorTareasWeb/`.

**Cuenta inicial de demostración:** `admin@clase.local` / `Curso2026!`. Es una contraseña de prueba para el entorno local; puede cambiarla desde **Usuarios → Editar**. Las contraseñas se guardan como hash PBKDF2 con sal individual, nunca como texto plano.

## Qué comprobar antes de grabar

- Una contraseña incorrecta no permite entrar.
- Sin sesión, abrir directamente `/usuario?action=consultar` o `/tarea?action=consultar` redirige a Login.
- Se puede crear, consultar, editar y eliminar un usuario distinto del que inició sesión.
- Se puede crear, consultar, editar y eliminar una tarea.
- La tarea muestra el nombre del usuario asignado gracias al `JOIN` del DAO.
- MySQL impide eliminar un usuario que todavía tiene tareas.
- Al salir, la sesión deja de dar acceso al CRUD.

## Verificación realizada

El proyecto se compiló como WAR con Java 17. La funcionalidad se verificó con MySQL 8.0.42. La versión para Servlet 4/JSTL 1.2 también se desplegó en el Tomcat 9.0.65 instalado en este equipo: el servidor respondió correctamente y mostró el formulario de Login.

## Investigación y decisiones técnicas

- Apache Tomcat 9 implementa Servlet 4.0 y JSP 2.3: https://tomcat.apache.org/whichversion.html
- JSTL 1.2 y URI clásica `http://java.sun.com/jsp/jstl/core`: https://tomcat.apache.org/taglibs/standard/
- MySQL Connector/J para Maven: https://dev.mysql.com/doc/connector-j/en/connector-j-installing-maven.html
- Claves foráneas de MySQL: https://dev.mysql.com/doc/refman/8.4/en/create-table-foreign-keys.html
- Filtros de Servlet para autenticación: https://javaee.github.io/javaee-spec/javadocs/javax/servlet/Filter.html
- Almacenamiento seguro de contraseñas: https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html

## Entrega

Según la consigna, **entregue únicamente el video explicativo, no este proyecto ni el código fuente**. Use `GUIA_VIDEO.md` para preparar la grabación. La explicación debe ser suya y demostrar que comprende el código.
