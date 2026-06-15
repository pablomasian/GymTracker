# GymTracker — Seguimiento de entrenamientos

GymTracker permite a usuarios crear y gestionar rutinas, registrar sesiones de entrenamiento, seguir a otros usuarios y explorar rutinas públicas. Está diseñado para uso personal o en entornos pequeños de entrenamiento y coaching.

### Características:

- Registro e inicio de sesión con JWT.
- Crear, editar y compartir rutinas y ejercicios.
- Registrar sesiones de entrenamiento y ver estadísticas de usuario.
- Sistema de seguimiento entre usuarios (follow/block).
- API REST limpia para integración con clientes externos.
- Stack técnico
# 
- Backend: `Java 17`, `Spring Boot`, `Maven`, `JUnit` (tests), `JaCoCo` (coverage).
- Frontend: `React`, `npm/yarn`.
- Base de datos: `H2`, configuraciones para otros RDBMS en application.yml.
- Análisis de calidad: `SonarQube` .

### Requisitos:
- Java 17+
- Maven 3.6+
- Node.js 16+ y npm o yarn
- Git

### Ejecución rápida
Backend:
```
# Compilar y ejecutar tests
mvn clean test

# Ejecutar la aplicación
mvn spring-boot:run
```
Frontend:
```
cd frontend
npm install
npm start
```
Accede al frontend en http://localhost:3000 y al backend en http://localhost:8080.

### Ejecución de tests
Backend:
```
mvn test
```
Frontend:
```
cd frontend
npm test

