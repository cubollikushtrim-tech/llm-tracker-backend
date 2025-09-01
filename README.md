# LLM Usage Tracker Backend

This is the Spring Boot backend for the LLM Usage Tracker project.

## Features
- REST API for usage events, analytics, customers, users
- JWT authentication and role-based access
- Random event seeding and demo data initialization
- PostgreSQL database support

## Getting Started
1. Install Java 17+ and Maven.
2. Set up PostgreSQL database (or use Render.com managed database).
3. Configure database credentials in `src/main/resources/application.properties` or via environment variables:
   - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
4. Build and run:
   ```bash
   mvn clean package
   java -jar target/*.jar
   ```

## Docker Deployment
- Use the provided `Dockerfile` for containerized deployment.
- Environment variables for database credentials are recommended for production.

## Render.com Deployment
- Add a `render.yaml` for service configuration.
- Set environment variables in the Render dashboard.

## API Docs
- Swagger UI: `/swagger-ui.html`
- OpenAPI: `/api-docs`

## Demo Accounts
- SUPERADMIN: admin@llmtracker.com / password123
- Admin: john.smith@techcorp.com / password123
- User: sarah.johnson@techcorp.com / password123
- Admin: mike.davis@dataflow.com / password123
- User: lisa.wang@aiinnovations.com / password123
