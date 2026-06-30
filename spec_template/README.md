# spec/ — Comics Backend (Spring Boot 3 + MongoDB)

> Especificaciones del backend REST **comics** que sirve la API al micro sitio React `front_comics`.
>
> Flujo: primero spec → plan → tareas → código. La constitución manda sobre cualquier feature.

## Estructura

```
spec/
├── constitution/            ← reglas estables del proyecto (cambian poco)
│   ├── mission.md           ← qué construimos y para quién
│   ├── tech-stack.md        ← tecnologías, convenciones y límites
│   └── roadmap.md           ← orden de las features
└── features/                ← una carpeta por feature
    └── NNN-nombre-feature/
        ├── spec.md          ← qué hace + criterios de aceptación
        ├── plan.md          ← cómo se implementa
        └── tasks.md         ← checklist de tareas
```

## Flujo para una feature nueva

1. Crear `features/NNN-nombre-feature/` con el siguiente número libre (`001`, `002`, …).
2. Escribir `spec.md`: qué hace, por qué y criterios de aceptación medibles.
3. Escribir `plan.md`: enfoque técnico y decisiones, respetando `constitution/tech-stack.md`.
4. Desglosar en `tasks.md` y marcar el progreso.
5. Implementar y validar (`mvn test`, `mvn spring-boot:run`, etc.).
6. Actualizar `constitution/roadmap.md` (mover la feature a "Hecho").

> La constitución manda: si una feature choca con `mission.md` o `tech-stack.md`, se replantea la feature, no la constitución.
