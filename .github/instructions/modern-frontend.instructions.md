---
description: Desarrollo Frontend Moderno con Angular 20 y TailwindCSS v4
applyTo: '**'
---

## Reglas de Versiones de Framework

### Angular 20 - Control Flow Moderno
- **SIEMPRE usar** la nueva sintaxis de control flow de Angular 20:
  - `@if (condicion) { ... }` en lugar de `*ngIf="condicion"`
  - `@for (item of items; track item.id) { ... }` en lugar de `*ngFor="let item of items"`
  - `@switch (expresion) { @case (valor) { ... } @default { ... } }` en lugar de `[ngSwitch]`
- **NUNCA usar** directivas estructurales legacy (`*ngIf`, `*ngFor`, `*ngSwitch`)
- **USAR** componentes standalone por defecto
- **PREFERIR** imports modernos de Angular y patrones de inyección de dependencias

### TailwindCSS v4 - Sintaxis Moderna de Opacidad y Color
- **SIEMPRE usar** sintaxis moderna de opacidad con notación slash:
  - `bg-black/50` en lugar de `bg-black bg-opacity-50`
  - `text-white/80` en lugar de `text-white text-opacity-80`
  - `border-gray-300/40` en lugar de `border-gray-300 border-opacity-40`
- **EVITAR** clases de utilidad de opacidad legacy (`bg-opacity-*`, `text-opacity-*`, `border-opacity-*`)
- **USAR** valores arbitrarios modernos con corchetes cuando sea necesario
- **PREFERIR** escalas de colores semánticas y patrones de utilidades modernas

### Mejores Prácticas
- **Diseño Responsive**: Siempre considerar enfoque mobile-first
- **Efectos Glassmorphism**: Usar `backdrop-blur-*` con transparencia para UI moderna
- **Rendimiento**: Implementar lazy loading para módulos y componentes
- **Accesibilidad**: Incluir labels ARIA apropiados y navegación por teclado
- **TypeScript**: Usar tipado estricto y características modernas de ES6+

### Estándares de Calidad de Código
- **Estructura de Componentes**: Usar componentes standalone con interfaces claras
- **Estilos**: Preferir CSS utility-first con TailwindCSS v4
- **Manejo de Estado**: Usar Angular signals para estado reactivo cuando aplique
- **Manejo de Errores**: Implementar boundaries de error apropiados y feedback de usuario
- **Testing**: Escribir pruebas unitarias para componentes y servicios

## Patrones Comunes a Evitar
- Sintaxis legacy de Angular (`*ngIf`, `*ngFor`)
- Clases de opacidad antiguas de TailwindCSS (`bg-opacity-*`)
- Estilos inline en lugar de clases de utilidad
- Componentes no-standalone sin justificación
- Falta de consideraciones de diseño responsive

## Tecnologías Requeridas
- Angular 20+ (versión más reciente)
- TailwindCSS v4+ (versión más reciente)
- TypeScript 5+
- Características modernas de JavaScript ES6+
- FontAwesome para iconos (cuando se especifique)