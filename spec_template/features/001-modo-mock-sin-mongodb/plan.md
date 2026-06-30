# 001 · Modo mock sin MongoDB — Plan

## Enfoque

Crear un perfil Spring `mock` que:

1. Excluye la autoconfiguración de MongoDB para que la app no intente conectarse.
2. Proporciona implementaciones en memoria de `ComicRepository` y `UserRepository` mediante beans `@Profile("mock")`.
3. Precarga datos de ejemplo en esas implementaciones.

No se modifica ninguna clase existente de producción; todo el código mock vive en `src/main/java` bajo el paquete `mock` y solo se activa con el perfil `mock`.

## Implementación

### 1. `application-mock.properties`

```properties
# Excluir autoconfiguración de MongoDB
spring.autoconfigure.exclude=\
  org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration

# Identificar claramente el perfil en los logs
spring.application.name=comics [MOCK]
```

### 2. `InMemoryComicRepository`

Clase `@Repository @Profile("mock")` que implementa `ComicRepository` (la interfaz existente de `MongoRepository`).

> **Problema:** `MongoRepository` hereda de `PagingAndSortingRepository`, que a su vez es gestionada por Spring Data MongoDB. Al excluir la autoconfiguración de Mongo, Spring no sabrá cómo crear beans de `MongoRepository`.
>
> **Solución:** Crear una interfaz intermedia `ComicRepositoryPort` con solo los métodos que usa `ComicService`, e implementarla con `InMemoryComicRepository`. Alternativamente (más simple), crear la clase mock directamente sin heredar de `MongoRepository` y anotar como `@Primary @Profile("mock")`.

Enfoque elegido: **crear `InMemoryComicRepository` como `@Component @Primary @Profile("mock")`** que implementa `ComicRepository` sobreescribiendo todos los métodos con lógica en memoria (`ConcurrentHashMap` como almacén).

### 3. `InMemoryUserRepository`

Mismo patrón que `InMemoryComicRepository` pero para `UserRepository`.

### 4. `MockDataInitializer`

`@Component @Profile("mock")` que implementa `ApplicationRunner` e inserta los datos de ejemplo (3 cómics + 1 usuario) llamando directamente a los repositorios en memoria al arrancar.

### 5. Seguridad en modo mock (opcional)

Si Spring Security bloquea los endpoints sin token, añadir `MockSecurityConfig` `@Configuration @Profile("mock")` que deshabilita la autenticación para facilitar el desarrollo.

## Árbol de archivos nuevos

```
src/main/
├── resources/
│   └── application-mock.properties               ← (1)
└── java/com/comics/backend/mock/
    ├── InMemoryComicRepository.java               ← (2)
    ├── InMemoryUserRepository.java                ← (3)
    ├── MockDataInitializer.java                   ← (4)
    └── MockSecurityConfig.java                    ← (5)
```

## Decisiones

- **Por qué no Flapdoodle (embedded MongoDB):** Flapdoodle es ideal para `@DataMongoTest` / `@SpringBootTest` pero descarga un binario de MongoDB (~50 MB) y hace más lenta la primera ejecución. El perfil in-memory es inmediato y sin dependencias externas.
- **Por qué `@Primary`:** Permite que `ComicService` y `UserService` reciban el mock sin cambiar una línea de código en las clases de producción.
- **Por qué `ConcurrentHashMap`:** Simple, thread-safe y suficiente para datos de demo en desarrollo local.
- **Separación de paquete `mock/`:** Todo el código no productivo queda aislado; se puede excluir fácilmente del JAR de producción si se usa un módulo separado en el futuro.

## Riesgos

- **Métodos de `MongoRepository` no implementados:** `MongoRepository` tiene ~15 métodos heredados. Los que no usa `ComicService` pueden lanzar `UnsupportedOperationException`. Se implementan solo los que usan los servicios actuales.
- **Paginación:** `findAll(Pageable)` devuelve un `PageImpl` construido manualmente a partir de la lista en memoria.
- **Seguridad:** Si JWT está habilitado, habrá que o bien proporcionar un token en las peticiones o bien deshabilitar la seguridad en el perfil mock. Se opta por deshabilitar en mock para simplificar el desarrollo.
