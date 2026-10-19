# Prakticá — Sistema de Gestión Publicitaria

## 1. Descripción del proyecto

Prakticá es un sistema web desarrollado para apoyar la gestión de servicios publicitarios de Praktica Publicitaria S.A.C.

La aplicación permite administrar usuarios, empleados, pedidos, servicios y comunicaciones relacionadas con los trabajos solicitados por los clientes.

El proyecto está dividido en un frontend desarrollado con Angular, un backend desarrollado con Spring Boot y una base de datos MySQL.

Como parte del curso de Evolución y Configuración de Software, se realizaron actividades de diagnóstico, ingeniería inversa y mantenimiento para mejorar la seguridad y el control de acceso del sistema.

## 2. Tecnologías utilizadas

| Componente | Tecnología |
|---|---|
| Frontend | Angular 20 |
| Lenguaje del frontend | TypeScript, HTML y CSS |
| Backend | Java 21 |
| Framework del backend | Spring Boot 3.5.6 |
| Base de datos | MySQL |
| Gestión de dependencias | npm y Maven |
| Control de versiones | Git y GitHub |
| Herramientas de desarrollo | Visual Studio Code y MySQL Workbench |

## 3. Estructura del proyecto

El repositorio contiene los siguientes elementos:

- **frontend/**: componentes, páginas, servicios, rutas e interfaces de Angular.
- **backend/**: controladores, servicios, repositorios, entidades y configuración de Spring Boot.
- **database/**: archivo SQL que contiene la estructura de las tablas y sus relaciones.
- **README.md**: documentación técnica general del proyecto.
- **.gitignore**: configuración de los archivos que no deben incorporarse al repositorio.

## 4. Funcionalidades principales

El sistema incluye funcionalidades relacionadas con:

- Inicio de sesión y control de acceso según los roles.
- Gestión de usuarios y registro de empleados.
- Administración de servicios y paquetes publicitarios.
- Gestión y seguimiento de pedidos.
- Comunicación mediante chats asociados a pedidos.
- Registro de pagos en efectivo desde el panel.
- Consulta de información desde el panel administrativo.

Los roles contemplados son Cliente, Administrador, Ventas y Catalogador.

Las funciones disponibles dependen de los permisos establecidos para cada rol.

## 5. Requisitos para ejecutar el proyecto

Para ejecutar Prakticá en un entorno local se necesita:

- Node.js y npm compatibles con Angular 20.
- Java JDK 21.
- Maven o Maven Wrapper.
- MySQL Server.
- Un editor de código, como Visual Studio Code.

## 6. Configuración de la base de datos

La base de datos utilizada por el proyecto se denomina `praktica` y cuenta con 11 tablas.

El archivo `database/praktica_solo_estructura.sql` contiene la definición de las tablas, sus claves e índices y las relaciones entre ellas.

Para preparar la base de datos:

1. Iniciar MySQL Server.
2. Abrir MySQL Workbench.
3. Crear un esquema llamado `praktica`.
4. Abrir el archivo SQL de la carpeta `database`.
5. Seleccionar el esquema correspondiente y ejecutar el script.
6. Verificar que se hayan creado las tablas correctamente.

El archivo SQL publicado no incluye registros de usuarios, contraseñas ni información personal.

**Nota:** el script procede de un respaldo anterior generado con MariaDB. Se debe comprobar su compatibilidad e importación en la versión de MySQL utilizada. Al contener únicamente la estructura, será necesario crear datos de prueba para utilizar las funcionalidades que los requieran.

## 7. Configuración y ejecución del backend

El backend se encuentra en la carpeta `backend` y utiliza Java con Spring Boot.

Antes de ejecutarlo, es necesario configurar localmente la conexión a MySQL mediante el archivo `application.properties`.

También se debe configurar la variable de entorno `JWT_SECRET` con una clave segura para la autenticación JWT.

Las credenciales de la base de datos y las claves privadas no deben publicarse en GitHub.

Para iniciar el backend, abrir una terminal dentro de la carpeta `backend` y ejecutar:

`mvn spring-boot:run`

Este comando requiere Maven instalado y la configuración local correspondiente.

El backend está configurado para trabajar en el puerto 8080 en el entorno utilizado durante el proyecto.

## 8. Configuración y ejecución del frontend

El frontend está desarrollado con Angular y se encuentra en la carpeta `frontend`.

Para instalar las dependencias, abrir una terminal dentro de esa carpeta y ejecutar:

`npm ci`

Después, iniciar la aplicación mediante:

`npx ng serve`

Una vez iniciado correctamente, se puede acceder desde el navegador mediante:

`http://localhost:4200`

Para utilizar las funciones que requieren información del servidor, el backend y la base de datos también deben estar en ejecución.

## 9. Mantenimiento realizado en la T2

Durante el mantenimiento se identificaron problemas relacionados con el registro de empleados, las contraseñas y el acceso según los roles.

Las principales modificaciones implementadas fueron:

- Eliminación de la contraseña predeterminada utilizada al registrar empleados.
- Incorporación de campos para ingresar y confirmar la contraseña del nuevo empleado.
- Validación de la contraseña en el backend y almacenamiento mediante BCrypt.
- Corrección de la navegación del inicio de sesión para permitir el acceso de los roles Ventas y Catalogador.
- Restricción de la creación de empleados al rol Administrador.
- Ocultamiento de opciones administrativas para usuarios que no tienen ese rol.

También se analizaron oportunidades de refactorización relacionadas con DRY, SRP, OCP e inversión de control. Algunas de estas mejoras quedaron planteadas para futuras modificaciones y no se consideran implementadas por completo.

## 10. Control de versiones

Para registrar la evolución del proyecto se utilizó Git y GitHub.

En el repositorio se conservaron dos estados principales del código:

| Versión de trabajo | Descripción |
|---|---|
| Versión inicial | Código del sistema antes del mantenimiento T2. |
| Versión actualizada | Código con las mejoras realizadas en contraseñas y control de roles. |

Los cambios se registraron mediante los commits:

- `Versión inicial de Prakticá antes del mantenimiento`
- `Mantenimiento T2 - mejoras en contraseñas y control de roles`

Esto permite consultar el historial del proyecto y comparar los archivos modificados.

## 11. Seguridad y recomendaciones

Para mantener la seguridad del proyecto se recomienda:

- No publicar contraseñas, tokens ni claves JWT.
- Mantener las configuraciones privadas fuera del repositorio.
- Utilizar contraseñas individuales para las cuentas de empleados.
- Aplicar las restricciones de acceso también en el backend.
- Revisar periódicamente las dependencias y sus vulnerabilidades.
- Crear respaldos protegidos de la base de datos.
- Realizar pruebas después de modificar las funciones principales.

El archivo `.gitignore` permite excluir carpetas generadas y determinados archivos de configuración, pero las claves escritas directamente en el código deben revisarse por separado.

## 12. Repositorio

**GitHub:** https://github.com/Leo-Verses/Praktica

El repositorio contiene el código de Prakticá y el historial de las modificaciones realizadas durante el mantenimiento T2.