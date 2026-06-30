# Roadmap

## Hecho ✅

_(ninguna feature implementada aún según este sistema de specs)_

## En curso 🚧

1. **001 · Modo mock sin MongoDB** — perfil `mock` que arranca la app sin base de datos real; en-memoria para desarrollo y demos.

## Siguiente 🔜

_(pendiente de completar 001)_

## Backlog / ideas 💡

- **002 · Tests de controladores** — `@WebMvcTest` + `@MockBean` para probar los endpoints HTTP sin Spring contexto completo.
- **003 · Seed de datos de desarrollo** — `DataInitializer` que carga un catálogo de cómics de ejemplo al arrancar en perfil `mock`.
- **004 · Endpoint de búsqueda** — `GET /api/v1/comics/search?q=` con búsqueda por título case-insensitive.
- **005 · Paginación en respuesta** — metadatos de página (`totalElements`, `totalPages`) en todos los listados.
- **006 · Autenticación completa** — registro, login y renovación de token JWT.

> Cada feature nueva se crea como `features/NNN-nombre-feature/` con `spec.md`, `plan.md` y `tasks.md` antes de tocar código.
