# Misión

## Qué construimos

Backend REST para gestión de un catálogo de cómics y sus usuarios. Expone una API HTTP que consume el micro sitio React `front_comics`.

1. **Catálogo de cómics** — CRUD completo: listar, obtener por ID, crear, actualizar y eliminar cómics.
2. **Gestión de usuarios** — CRUD completo de usuarios del sistema con contraseñas encriptadas.
3. **Autenticación** — JWT para proteger los endpoints de escritura.
4. **Documentación automática** — Swagger UI disponible en `/swagger-ui.html`.

## Para quién

- **Desarrolladores** que practican arquitectura REST con Spring Boot y MongoDB.
- **El frontend** (`front_comics`) que consume la API para mostrar y gestionar el catálogo.

## Principios

- **Capas claras** — Controller → Service → Repository. La lógica de negocio vive en el servicio.
- **Incrementalidad** — se arranca con lo mínimo funcional y se añade complejidad feature a feature.
- **Testeabilidad** — cada capa se prueba de forma independiente; no se requiere MongoDB en ejecución para correr los tests unitarios.

## Qué NO es

- No es un frontend: toda la lógica de presentación vive en `front_comics`.
- No gestiona pagos, inventario real ni integración con distribuidores en esta fase.
