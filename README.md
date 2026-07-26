# Mosify Backend — Sistema de Recompensas y Tareas

¡Bienvenido al backend de **Mosify**! Este proyecto es un sistema gamificado de gestión de tareas y recompensas, diseñado bajo principios de ingeniería de software modernos. Permite a grupos de usuarios colaborar en tableros compartidos donde completar tareas otorga puntos, y esos puntos pueden canjearse por recompensas o privilegios.

---

## 📌 1. El Proyecto: ¿Qué es Mosify?

**Mosify** es una plataforma orientada a la gamificación de actividades diarias, ideal para entornos como la convivencia del hogar (reparto de tareas de limpieza, compras), la educación (metas de estudio para niños o estudiantes), equipos de trabajo pequeños que deseen incentivar buenas prácticas o incluso para individuales que quieran mejorar sus hábitos. 

### 🧩 Elementos Principales del Dominio

*   **Tableros (Boards):** Espacios colaborativos cerrados. Cada tablero agrupa un conjunto de categorías, tareas y usuarios que participan en él.
*   **Usuarios del Tablero (BoardUsers):** Relación de membresía entre un usuario y un tablero. Cada miembro tiene un **alias** personalizado dentro de ese tablero y un **saldo de puntos** (`pointsBalance`) específico que sube o baja según sus acciones.
*   **Categorías (Categories):** Clasificaciones para agrupar y organizar las tareas dentro del tablero (ej. *"Limpieza"*, *"Estudios"*, *"Ocio"*).
*   **Tareas (Tasks):** Acciones concretas asociadas a una categoría y que poseen un valor en puntos (`pointsValue`).
    *   **Puntos Positivos (+):** Tareas que otorgan puntos al completarse (ej. *"Estudiar matemáticas por 1 hora"* ➡️ `+50 pts`).
    *   **Puntos Negativos (-):** Recompensas, premios o consumos en los que el usuario gasta sus puntos acumulados (ej. *"Ver una película por la noche"* ➡️ `-100 pts`).
    *   **Tipo de Tarea:**
        *   `SINGLE_USE` (Un solo uso): Se desactiva automáticamente tras ser completada por primera vez.
        *   `RECURRENT` (Recurrente): Se puede realizar múltiples veces siguiendo una frecuencia establecida.
    *   **Frecuencia (TaskFrequency):** Define el límite temporal para tareas recurrentes:
        *   `DAILY` (Diaria): Se puede completar una vez al día.
        *   `WEEKLY` (Semanal): Se puede completar una vez por semana.
        *   `MONTHLY` (Mensual): Se puede completar una vez por mes.
        *   `NONE` (Ninguna): Se puede repetir indefinidamente.
*   **Transacciones (Transactions):** Historial inmutable de auditoría. Registra qué usuario ejecutó qué tarea, cuántos puntos afectó (`pointsAffected`) y la fecha exacta de la acción.

### 🔄 Flujo de Ejecución de una Tarea

Cuando un usuario marca una tarea como realizada a través del endpoint `/tasks/{id}/execute`, el backend sigue las siguientes reglas de negocio críticas de forma atómica:
1.  **Validación de Membresía:** Se verifica que tanto quien ejecuta la tarea como quien llama a la API pertenezcan al tablero correspondiente.
2.  **Validación de Recurrencia:** Si la tarea es recurrente y tiene frecuencia (`DAILY`, `WEEKLY`, `MONTHLY`), el sistema busca en el histórico de transacciones si el usuario ya la ha completado en el periodo actual. De ser así, deniega la ejecución.
3.  **Validación de Saldo Suficiente:** Si la tarea es de puntos negativos (una recompensa a canjear), se verifica que el saldo de puntos actual del miembro en el tablero sea suficiente. Si el saldo queda negativo, se rechaza la transacción.
4.  **Actualización de Saldo:** Se recalcula y guarda el nuevo saldo del usuario en el tablero.
5.  **Registro de Transacción:** Se inserta un registro en el historial de transacciones para auditoría.
6.  **Desactivación de Tarea:** Si la tarea es de tipo `SINGLE_USE`, se marca como inactiva para que no pueda volver a ejecutarse.

---

## 🛠️ 2. Arquitectura y Detalles Técnicos

El backend está desarrollado utilizando **Java 21** y **Spring Boot 3**, estructurado bajo el patrón de **Arquitectura Hexagonal (Clean Architecture / Puertos y Adaptadores)**. Esto garantiza el desacoplamiento total de las reglas de negocio del framework, la persistencia y los protocolos de entrega.

### 📁 Estructura de Paquetes (`com.mosify`)

*   **`domain` (Dominio):**
    *   **`model`:** Contiene las entidades de dominio puras (`User`, `Board`, `BoardUser`, `Category`, `Task`, `Transaction`) estructuradas con objetos inmutables utilizando anotaciones de Lombok como `@Value` y `@Builder`. Solo contiene lógica muy pura de la clase en la que se encuentre el método, ej. `TaskFrequency.calculateRange()`.
    *   **`exception`:** Definición de excepciones de negocio (`MosifyException`) y códigos de error reutilizables (`ErrorCode`).
    *   *Nota: Esta capa no tiene ninguna dependencia de Spring ni de bibliotecas externas de persistencia, manteniendo el corazón del negocio intacto.*

*   **`application` (Aplicación - Casos de Uso):**
    *   **`port.in` (Puertos de Entrada):** Interfaces de negocio que definen qué operaciones se pueden realizar (ej. `TaskExecutePort`).
    *   **`port.out` (Puertos de Salida):** Interfaces SPI que definen qué servicios requiere la aplicación para persistir datos u comunicarse externamente (ej. `TaskRepository`, `BoardUserRepository`).
    *   **`service`:** Implementación de los puertos de entrada. Aquí reside la lógica de orquestación de negocio (ej. `TaskExecutionService`), manejando transacciones atómicas mediante `@Transactional` de Spring.

*   **`infrastructure` (Infraestructura - Adaptadores y Configuración):**
    *   **`in` (Adaptadores de Entrada):**
        *   **`controller`:** Controladores REST (ej. `TaskController`, `BoardController`) que implementan las interfaces autogeneradas por el plugin de OpenAPI. Se encargan de validar la entrada HTTP y delegar a los casos de uso.
        *   **`mapper` (Web Converters):** Conversores MapStruct que transforman los DTOs de la API web a entidades de dominio y viceversa (ej. `TaskWebConverter`).
    *   **`out` (Adaptadores de Salida):**
        *   **`db` (Persistencia):** Repositorios JPA tradicionales de Spring Data (`UserJpaRepository`) acoplados a la base de datos física y adaptadores de persistencia (`UserPersistenceAdapter`) que implementan los puertos de salida. Contiene mappers específicos (`EntityConverter`) para traducir de entidad JPA (`UserEntity`) a modelo de dominio (`User`).
    *   **`security`:** Configuración de Spring Security con autenticación sin estado (Stateless) mediante tokens JWT. Contiene filtros de interceptación (`JwtAuthenticationFilter`), utilidades de hashing (`BCryptPasswordEncoder`) y lógica de extracción de credenciales (`CustomUserDetailsService`).
    *   **`config`:** Clases de configuración global del framework.

---

## 🚀 3. Stack Tecnológico

El proyecto se sustenta en las siguientes tecnologías principales:

1.  **Java 21:** Uso de características modernas como registros (`records`) y expresiones `switch` mejoradas con asignación dinámica.
2.  **Spring Boot 3.5.7:**
    *   **Spring Web:** Para la exposición del API REST.
    *   **Spring Data JPA:** Abstracción de base de datos con Hibernate.
    *   **Spring Security:** Control de accesos y seguridad a nivel de endpoint.
    *   **Spring Boot Actuator:** Endpoints de monitorización listos para producción (como `/actuator/health`).
3.  **Autenticación JWT (JJWT 0.12.6):** Emisión y validación segura de tokens en cada petición HTTP, usando algoritmos robustos de clave simétrica.
4.  **Bases de Datos:**
    *   **H2 Database:** Base de datos en memoria para entornos locales rápidos y ejecución de suites de tests integrados.
    *   **MySQL / MariaDB:** Driver oficial configurado para persistencia estable en producción.
5.  **MapStruct 1.6.3:** Procesador de anotaciones para la generación eficiente de mapeadores en tiempo de compilación, eliminando la necesidad de código de conversión repetitivo.
6.  **OpenAPI Generator (Plugin Maven):** Enfoque **API-First**. La API completa se diseña en el archivo [api.yaml](file:///c:/Users/Oscar/IdeaProjects/mosify/src/main/resources/api.yaml). El plugin compila y genera automáticamente las interfaces de los controladores y los DTOs (`Web*`), forzando una sincronización perfecta entre el contrato y el código.
7.  **Lombok:** Reducción de código boilerplate (getters, setters, constructores, patrones builder).

---

## 🏃 4. Cómo Iniciar el Proyecto en Local

### Requisitos Previos

*   Java Development Kit (JDK) 21 instalado.
*   Maven 3.9+ instalado.
*   (Opcional) Docker y Docker Compose para levantar base de datos en producción.

### Variables de Entorno Disponibles

El archivo `application.yaml` lee variables del entorno con valores por defecto seguros para desarrollo local (que utiliza H2 o MySQL local):

| Variable de Entorno | Propósito | Valor por Defecto |
| :--- | :--- | :--- |
| `DB_HOST` | Host de la base de datos | `localhost` |
| `DB_PORT` | Puerto de la base de datos | `3306` |
| `DB_NAME` | Nombre de la base de datos | `mosify` |
| `DATASOURCE_USER` | Usuario de base de datos | `root` |
| `DATASOURCE_PASS` | Contraseña de base de datos | `root` |
| `JWT_SECRET` | Clave secreta para firmar tokens JWT | *Clave en Base64 por defecto* |
| `SPRING_JPA_HIBERNATE_DIALECT` | Dialecto JPA | `org.hibernate.dialect.MySQLDialect` |

### Pasos para Ejecutar

1.  **Compilar y Generar Clases (OpenAPI y MapStruct):**
    Ejecuta el siguiente comando para generar los controladores OpenAPI y mapeadores MapStruct:
    ```bash
    mvn clean compile
    ```

2.  **Ejecutar los Tests:**
    ```bash
    mvn test
    ```

3.  **Iniciar la Aplicación:**
    Puedes arrancar el servidor embebido de Spring Boot:
    ```bash
    mvn spring-boot:run
    ```
    El backend se levantará por defecto en el puerto **`8080`**.

4.  **Acceso a Documentación Interactiva (Swagger):**
    Una vez encendido, puedes probar todos los endpoints y ver la documentación interactiva en:
    👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Ejecución con Docker

Puedes compilar y empaquetar la aplicación en un contenedor Docker ligero usando el archivo multi-stage [Dockerfile](src/main/resources/Dockerfile):

```bash
# Compilar y construir la imagen docker
docker build -t mosify-backend -f src/main/resources/Dockerfile .

# Ejecutar el contenedor
docker run -p 8080:8080 -e DB_HOST=host.docker.internal mosify-backend
```
