# Gestión de reservas de aulas

Aplicación REST para administrar aulas, profesores, franjas horarias y reservas de un centro educativo.

## Arquitectura

- **Backend:** Java 21, Spring Boot, Spring Data JPA, Hibernate, MySQL y Lombok.
- **Frontend:** HTML5, CSS3 y JavaScript vanilla en `frontend/`.
- **API:** CRUD en `/aulas`, `/profesores`, `/franjas-horarias` y `/reservas`. Las reservas rechazan fechas pasadas (400) y aulas ocupadas (409).

## Ejecución

Configura la conexión MySQL en `src/main/resources/application.properties` y ejecuta:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Abre `frontend/index.html` con el backend disponible en `http://localhost:8080`.

## Reflexiones pendientes

- Reflexión sobre el uso de IA:

  He usado la IA como una herramienta de apoyo en el desarrollo del proyecto. Principalmente, me ha servido para estructurar de forma rápida los paquetes basándome en un proyecto anterior (`MatriculasSpring`), asegurando la consistencia en la arquitectura. Asimismo, recurrí a la asistencia de la IA de forma puntual dentro de algunas clases para depurar errores de sintaxis o lógica que no lograba identificar a simple vista, así como para agilizar la redacción de fragmentos repetitivos de código.

- Prompts utilizados:

  "Genera la Estructura de un programa SpringBoot con la misma estructura que MatriculaSpring y que siga los siguientes requisitos: Debe tener gestión de aulas, profesores, franjas horarias y reservas con validación de fechas pasadas y solapamiento de aulas."

- Problemas encontrados y soluciones:

    1. **Conflicto de puertos en el entorno local (Fedora):** Al intentar levantar el contenedor de Docker para MySQL, el puerto 3306 ya estaba ocupado por un servicio local de la máquina.
        - *Solución:* Modifiqué el mapeo de puertos de Docker para exponer MySQL en el puerto 3307 (`-p 3307:3306`), actualizando posteriormente la URL de conexión en el archivo `application.properties`.
    2. **Gestión de dependencias del gestor DNF en Fedora 41+:** Al intentar instalar Docker Desktop, se produjeron conflictos de dependencias con `moby-engine`.
        - *Solución:* Utilicé el parámetro `--allowerasing` de DNF para permitir la sustitución limpia de los paquetes conflictivos y completar la instalación correctamente.
    3. **Validación de solapamiento de reservas:** Garantizar que un aula no fuera reservada dos veces en la misma franja horaria y fecha.
        - *Solución:* Se implementó un método personalizado en el repositorio (`existsByAulaIdAndFechaReservaAndFranjaHorariaId`) mediante Spring Data JPA, devolviendo un código HTTP 409 (Conflict) desde el controlador cuando la validación fallaba.