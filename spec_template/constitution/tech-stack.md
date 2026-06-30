# Tech stack y convenciones

## Tecnologías

- **Lenguaje:** Java 17
- **Framework:** Spring Boot 3.5.x
- **Persistencia:** Spring Data MongoDB (`MongoRepository`)
- **Base de datos:** MongoDB 6+ (en prod/dev); in-memory mock (en perfil `mock`)
- **Seguridad:** Spring Security + JWT (JJWT 0.12.x)
- **Mapeo DTO:** MapStruct 1.5.x
- **Reducción de boilerplate:** Lombok
- **Documentación API:** springdoc-openapi 2.x (Swagger UI)
- **Testing:** JUnit 5, Mockito, Spring Boot Test, Spring Security Test

## Archivos / módulos clave

```
src/
├── main/
│   ├── java/com/comics/backend/
│   │   ├── controllers/   ← endpoints REST (@RestController)
│   │   ├── services/      ← lógica de negocio (@Service)
│   │   ├── repository/    ← acceso a MongoDB (MongoRepository)
│   │   ├── models/        ← entidades (@Document)
│   │   ├── dto/           ← objetos de transferencia
│   │   ├── mappers/       ← EntityMapper (MapStruct)
│   │   └── exceptions/    ← excepciones de dominio
│   └── resources/
│       ├── application.properties       ← configuración base
│       └── application-mock.properties  ← perfil sin MongoDB (spec 001)
└── test/
    └── java/com/comics/backend/
        └── services/      ← tests unitarios con Mockito
```

## Comandos

- `mvn spring-boot:run` — arranca contra MongoDB real (requiere BD en `192.168.0.20:27017`).
- `mvn spring-boot:run -Dspring-boot.run.profiles=mock` — arranca sin MongoDB con datos en memoria.
- `mvn test` — ejecuta todos los tests (no requiere MongoDB gracias a Mockito).
- `mvn clean package` — compila y empaqueta el JAR.

## URLs de referencia (en local)

- API base: `http://localhost:8080/api/v1/`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Actuator health: `http://localhost:8080/actuator/health`

## Convenciones

- Una clase por responsabilidad; controllers delegán todo en services.
- DTOs para entrada (`CreateXxxDTO`) y salida (`XxxResponseDTO`) — nunca exponer entidades directamente.
- Excepción de dominio para cada error de negocio; el handler global las convierte en respuestas HTTP.
- Nombre de tests: `when_<condición>_expect_<resultado>` (estilo BDD).
- No se hace `System.out.println`; usar el logger SLF4J de Lombok (`@Slf4j`).

## Perfiles de Spring

| Perfil   | MongoDB           | Cuándo usarlo                            |
|----------|-------------------|------------------------------------------|
| (ninguno)| real (27017)      | Desarrollo con BD levantada              |
| `mock`   | ninguna (in-mem)  | Desarrollo rápido, demos, CI sin BD      |
| `test`   | Mockito / Embed   | Tests automáticos (no requiere proceso)  |
