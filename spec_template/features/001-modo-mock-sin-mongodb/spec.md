# 001 · Modo mock sin MongoDB

**Estado:** implementado ✅

## Qué hace

Añade un perfil Spring `mock` que permite arrancar el backend **sin tener MongoDB en ejecución**. Los repositorios se sustituyen por implementaciones en memoria que contienen datos de ejemplo. La API sigue respondiendo en `http://localhost:8080` con el mismo contrato que la versión real.

## Por qué

El punto de dolor actual es que `mvn spring-boot:run` falla si el servidor MongoDB (`192.168.0.20:27017`) no está accesible. Esto bloquea el desarrollo del frontend `front_comics` y cualquier prueba rápida de los endpoints, ya que obliga a tener la BD levantada para cualquier trabajo.

Con el perfil `mock`:
- El frontend puede consumir la API sin necesitar MongoDB.
- Los demos o revisiones de código se hacen sin infraestructura externa.
- Los tests de integración de controladores funcionan sin BD.

## Criterios de aceptación

- [ ] `mvn spring-boot:run -Dspring-boot.run.profiles=mock` arranca sin errores aunque MongoDB no esté disponible.
- [ ] `GET /api/v1/comics` devuelve una lista con al menos 3 cómics de ejemplo (datos hardcodeados).
- [ ] `GET /api/v1/comics/{id}` devuelve el cómic correspondiente o `404` si no existe en los datos mock.
- [ ] `POST /api/v1/comics` añade el cómic a la lista en memoria y lo devuelve con un ID generado.
- [ ] `GET /api/v1/users` devuelve al menos 1 usuario de ejemplo.
- [ ] El perfil por defecto (sin `-Dspring-boot.run.profiles=mock`) sigue funcionando igual que antes: se conecta a MongoDB real.
- [ ] Los tests existentes (`mvn test`) pasan sin ningún cambio.
- [ ] Swagger UI (`/swagger-ui.html`) sigue accesible con el perfil `mock` activo.

## Fuera de alcance

- Persistencia entre reinicios en modo mock (los datos se pierden al parar la app; es por diseño).
- Tests automáticos de los controladores (se hará en la feature 002).
- Autenticación JWT en modo mock (los endpoints protegidos pueden requerir token o quedar abiertos en este perfil).
