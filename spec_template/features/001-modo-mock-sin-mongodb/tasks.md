# 001 · Modo mock sin MongoDB — Tareas

## Preparación

- [x] Leer `plan.md` y confirmar el enfoque antes de tocar código.
- [x] Verificar que `mvn test` pasa en estado actual (línea base).

## Implementación

- [x] Crear `src/main/resources/application-mock.properties` excluyendo la autoconfiguración de MongoDB.
- [x] Crear `src/main/java/com/comics/backend/mock/InMemoryComicRepository.java` con `ConcurrentHashMap` como almacén y `@Primary @Profile("mock")`.
  - Implementar: `findAll()`, `findAll(Pageable)`, `findById()`, `save()`, `deleteById()`, `existsById()`, `findByTitleAndNumber()`, `findByTitle()`, `findByTitleContainsIgnoreCase()`.
- [x] Crear `src/main/java/com/comics/backend/mock/InMemoryUserRepository.java` con el mismo patrón.
  - Implementar los métodos que usa `UserService`.
- [x] Crear `src/main/java/com/comics/backend/mock/MockDataInitializer.java` (`ApplicationRunner @Profile("mock")`) con al menos 3 cómics y 1 usuario de ejemplo.
- [x] Mover `@EnableMongoAuditing` de `MainApplication` a `MongoConfig` (`@Profile("!mock")`) para evitar que se requiera `mongoMappingContext` en el perfil mock.

## Validación

- [x] Ejecutar `mvn spring-boot:run -Dspring-boot.run.profiles=mock` — arranca sin errores de conexión a MongoDB.
- [x] `Started MainApplication` confirmado — mock data loaded: 5 comics, 1 user.
- [ ] Verificar `GET http://localhost:8080/api/v1/comics` — debe devolver la lista mock (≥ 3 ítems).
- [ ] Verificar `GET http://localhost:8080/api/v1/comics/{id}` — con un ID válido devuelve el cómic; con uno inválido devuelve `404`.
- [ ] Verificar `POST http://localhost:8080/api/v1/comics` — añade un cómic y lo devuelve con ID.
- [ ] Verificar `GET http://localhost:8080/api/v1/users` — devuelve al menos 1 usuario.
- [ ] Verificar Swagger UI en `http://localhost:8080/swagger-ui.html` con perfil mock activo.
- [x] Ejecutar `mvn test` — todos los tests pasan sin cambios (53 tests, 0 fallos).
- [ ] Arrancar sin perfil (`mvn spring-boot:run`) — comportamiento igual que antes (intenta conectar a MongoDB real).
- [ ] Validar contra los criterios de aceptación de `spec.md`.

## Cierre

- [ ] Actualizar estado en `spec.md` a `implementado ✅`.
- [ ] Mover feature a "Hecho" en `../../constitution/roadmap.md`.
