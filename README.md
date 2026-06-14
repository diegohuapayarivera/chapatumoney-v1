# ChapatuMoney (Backend) - Chapatuticket API

El núcleo transaccional y administrativo de la plataforma de eventos **chapatuticket**. 
Este proyecto proporciona una API RESTful robusta, construida para manejar operaciones complejas entre organizadores, artistas y fans.

## 🚀 Arquitectura y Stack Tecnológico

*   **Framework:** Java 17 + Spring Boot 3
*   **Base de Datos:** PostgreSQL (con extensiones a través de Supabase)
*   **Seguridad y Autenticación:** Supabase Auth (JWT Headers), integrado con Spring Security (Custom JWT Filters).
*   **Gestor de Dependencias:** Gradle

## 🛡️ Características Técnicas y Optimizaciones Clave

Este backend no es un simple CRUD. Se han implementado estrategias de grado de producción para garantizar fiabilidad y rendimiento:

1.  **Prevención Estricta de Overselling (Condiciones de Carrera):** 
    Utilizamos mecanismos de concurrencia avanzados a nivel de base de datos. Las compras de boletos aplican `PESSIMISTIC_WRITE` (Bloqueo Pesimista) en la base de datos durante el proceso de confirmación de órdenes. Esto garantiza que las compras simultáneas no excedan el aforo máximo de los boletos.
2.  **Optimización contra el Problema N+1:**
    Se diseñaron proyecciones avanzadas (`EventAggregateProjection`) y procesamiento en bloque (Bulk Fetching) para resolver cuellos de botella de rendimiento. Al listar eventos, el sistema recupera conteos (precios mínimos, asientos totales, seguidores) en $O(1)$ consultas optimizadas, eliminando las lentas llamadas de $O(N)$.
3.  **Gestión Segura de Roles:**
    Sincronización automatizada entre los metadatos de usuario en Supabase Auth y la capa transaccional a través de Webhooks y validación rigurosa de JWT.
4.  **Resiliencia frente a Excepciones:**
    Programación defensiva para el manejo de valores nulos (Null Pointer Exceptions) y clases asesoras (`@RestControllerAdvice`) para mantener un flujo limpio hacia el cliente frontend.

## ⚙️ Requisitos Previos

*   **Java 17** o superior.
*   Conexión a un proyecto de **Supabase** (PostgreSQL).

## 🛠️ Instalación y Ejecución Local

1.  **Clonar y configurar el entorno**:
    Asegúrate de definir las variables de entorno para tu base de datos de desarrollo y Supabase en `application.yml` o a través de exportación local:
    ```bash
    export DB_URL="jdbc:postgresql://<tu-db-host>:5432/postgres"
    export DB_USERNAME="postgres"
    export DB_PASSWORD="tu-password"
    export SUPABASE_JWT_SECRET="tu-secreto-jwt"
    ```

2.  **Compilar el proyecto**:
    Navega al directorio raíz del backend y compila usando el wrapper de Gradle:
    ```bash
    ./gradlew build -x test
    ```

3.  **Ejecutar la API**:
    ```bash
    ./gradlew bootRun
    ```
    El servidor se levantará por defecto en `http://localhost:8080`.

## 📂 Estructura del Código

El proyecto sigue un patrón sólido de MVC (Modelo-Vista-Controlador) y Separación de Preocupaciones (Separation of Concerns):
*   `controller/`: Controladores REST.
*   `service/`: Capa de lógica de negocio (aquí viven las lógicas de overselling y validaciones).
*   `repository/`: Interacciones con Spring Data JPA e Hibernate (Bloqueos pesimistas y Querys personalizadas).
*   `entity/`: Mapeos ORM de base de datos.
*   `security/`: Validaciones de JWT y configuraciones CORS.
