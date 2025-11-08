# CRUD Design Specification
## Estándar de Diseño para Operaciones CRUD en GENIA TON IFS

### Versión: 1.0
### Fecha: 8 de noviembre de 2025
### Basado en: Personal Finance Module

---

## 📋 Resumen Ejecutivo

Este documento define los estándares y mejores prácticas para implementar operaciones CRUD en la plataforma GENIA TON IFS, basado en la implementación exitosa del módulo de finanzas personales. El enfoque prioriza la simplicidad, consistencia visual y experiencia de usuario óptima.

---

## 🎨 Principios de Diseño

### 1. Minimalismo Funcional
- **Una sola tarjeta principal** para mostrar datos y acciones
- **Eliminación de elementos innecesarios** (filtros complejos, múltiples tarjetas)
- **Enfoque en contenido esencial** sin distracciones visuales

### 2. Glassmorphism Consistente
- **Efecto glass**: `bg-white/40 backdrop-blur-md border border-white/30`
- **Sombras elegantes**: `shadow-xl` para profundidad
- **Bordes redondeados**: `rounded-2xl` para suavidad
- **Transparencias controladas**: `/20`, `/30`, `/40` para layers

### 3. Jerarquía Visual Clara
- **Header prominente** con título, descripción y acción principal
- **Contenido central** con datos organizados en grid
- **Footer funcional** con controles de navegación minimalistas

---

## 🏗️ Estructura de Componentes

### Arquitectura Angular 20
```typescript
// Componente principal standalone
@Component({
  selector: 'app-[module]-crud',
  standalone: true,
  imports: [CommonModule, FormsModule, PageableGridComponent],
  // ...
})
```

### Organización de Archivos
```
module-crud/
├── module-crud.ts          # Lógica del componente
├── module-crud.html        # Template minimalista
├── module-crud.scss        # Estilos específicos (mínimos)
└── module-crud.spec.ts     # Tests unitarios
```

---

## 📐 Layout Specification

### Estructura HTML Estándar
```html
<!-- Contenedor principal único -->
<div class="ios-card">
  <div class="p-6">
    <!-- Header: Título + Botón principal -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center">
        <!-- Icono del módulo -->
        <div class="w-10 h-10 mr-4 rounded-xl bg-gradient-to-br from-[color]-400 to-[color]-500 flex items-center justify-center shadow-lg">
          <i class="fas fa-[icon] text-white text-lg"></i>
        </div>
        <!-- Título y descripción -->
        <div>
          <h1 class="text-2xl font-bold text-gray-900">[Título del Módulo]</h1>
          <p class="text-gray-600 text-sm">[Descripción breve]</p>
        </div>
      </div>
      
      <!-- Acción principal -->
      <button class="bg-gradient-to-r from-[color]-500 to-[color]-600 text-white px-6 py-3 rounded-xl hover:from-[color]-600 hover:to-[color]-700 transition-all duration-200 flex items-center gap-3 shadow-lg hover:shadow-xl transform hover:scale-105">
        <div class="w-5 h-5 rounded-full bg-white/20 flex items-center justify-center">
          <i class="fas fa-plus text-xs"></i>
        </div>
        <span class="font-medium">Nueva [Entidad]</span>
      </button>
    </div>
    
    <!-- Grid de datos -->
    <app-pageable-grid
      [adapter]="dataAdapter"
      [columns]="gridColumns"
      [config]="gridConfig"
      (rowClick)="onViewDetails($event)">
    </app-pageable-grid>
  </div>
</div>
```

---

## 🎯 Configuración del PageableGrid

### Grid Config Estándar
```typescript
gridConfig: PageableGridConfig = {
  pageSize: 15,                    // Tamaño fijo óptimo
  pageSizeOptions: [15],           // Sin opciones múltiples
  showPageSizeSelector: false,     // Simplicidad total
  loadingText: 'Cargando [entidades]...',
  emptyText: 'No se encontraron [entidades]'
};
```

### Paginación Minimalista
- **4 botones únicamente**: Primera, Anterior, Siguiente, Última
- **Indicador central**: `"1 / 7"` (página actual / total)
- **Sin números individuales**: Eliminación del exceso visual
- **Controles glassmorphism**: `bg-white/20 border border-white/30`

### Definición de Columnas
```typescript
gridColumns: GridColumn[] = [
  { key: 'description', label: 'Descripción', sortable: false, width: '35%' },
  { key: 'amount', label: 'Monto', sortable: false, type: 'currency', align: 'right', width: '20%' },
  { key: 'category', label: 'Categoría', sortable: false, width: '25%' },
  { key: 'date', label: 'Fecha', sortable: false, type: 'date', width: '20%' }
  // NO incluir columna 'actions' a menos que tenga contenido real
];
```

---

## 🎨 Sistema de Colores

### Paleta Verde-Esmeralda (Finanzas)
- **Primary**: `from-green-500 to-emerald-600`
- **Header Grid**: `from-emerald-500 to-green-600`
- **Hover States**: `hover:from-green-600 hover:to-emerald-700`

### Formato Semántico de Datos
```typescript
// Montos con indicadores visuales
const formatCurrency = (value: number) => {
  const colorClass = value >= 0 ? 'text-emerald-600' : 'text-red-600';
  const icon = value >= 0 ? '↗' : '↘';
  return `<span class="${colorClass} font-semibold">
    <span class="text-xs opacity-70">${icon}</span> ${formatted}
  </span>`;
};
```

### Estados del Grid
- **Loading**: Gradiente `from-blue-50 to-indigo-50`
- **Empty**: Gradiente `from-gray-50 to-slate-100`  
- **Error**: Gradiente `from-red-50 to-pink-50`

---

## 🔄 Gestión de Estado

### Signals para Reactividad
```typescript
// Solo el estado esencial
export class ModuleCrudComponent {
  dataAdapter = inject(ModulePageableAdapter);
  
  gridConfig: PageableGridConfig = { /* config */ };
  gridColumns: GridColumn[] = [ /* columns */ ];
  
  async ngOnInit() {
    // Inicialización mínima - el adapter maneja la carga
  }
  
  onViewDetails(item: EntityType) {
    console.log('Ver detalles:', item);
  }
}
```

### Modal Management
```typescript
// En el componente padre (routing component)
showModal = signal<boolean>(false);

onRouteActivated(component: any) {
  if (component?.showTransactionForm) {
    component.showTransactionForm.subscribe(() => {
      this.showModal.set(true);
    });
  }
}
```

---

## 📱 Responsive Design

### Breakpoints Estándar
- **Mobile First**: Diseño optimizado desde 320px
- **Tablet**: `md:grid-cols-2` para layouts flexibles
- **Desktop**: `lg:flex-row` para controles horizontales

### Grid Responsivo
- **Columnas fluidas**: Anchos en porcentajes
- **Texto adaptativo**: `text-sm` en móvil, escalando según device
- **Botones adaptables**: `px-4 py-2` en mobile, `px-6 py-3` en desktop

---

## ⚙️ Mejores Prácticas

### 1. OpenAPI Compliance
- **Adherencia estricta** a la especificación API
- **Interfaces TypeScript** que reflejen exactamente los schemas
- **Sin agregar campos** no especificados en OpenAPI

### 2. Performance
- **Lazy Loading** para módulos grandes
- **OnPush Change Detection** en componentes de datos
- **Paginación obligatoria** para listas extensas
- **Signals** para estado reactivo eficiente

### 3. Accesibilidad
- **Labels ARIA** en todos los controles
- **Navegación por teclado** funcional
- **Contraste suficiente** en todos los textos
- **Focus indicators** visibles

### 4. Testing
- **Unit tests** para lógica de componentes
- **Integration tests** para adapters
- **E2E tests** para flujos críticos

---

## ⚠️ Consideraciones y Lecciones Aprendidas

### ❌ Errores a Evitar

#### 1. **Sobre-ingeniería Inicial**
- **Error**: Crear múltiples componentes complejos sin validar necesidades
- **Solución**: Comenzar con MVP y iterar según feedback del usuario
- **Ejemplo**: Tarjetas separadas para header, filtros, acciones innecesarias

#### 2. **Inconsistencia Visual**
- **Error**: Usar colores azules mezclados con tema verde-esmeralda
- **Solución**: Definir palette de colores estricta desde el inicio
- **Ejemplo**: Botones azules de paginación contrastando con tema verde

#### 3. **Complejidad de Filtros Prematura**
- **Error**: Implementar filtros complejos sin validar utilidad real
- **Solución**: Para demos y MVPs, mantener interfaz minimalista
- **Ejemplo**: Tarjeta completa de filtros para casos de uso simples

#### 4. **Columnas Vacías sin Propósito**
- **Error**: Agregar columna "Acciones" sin implementar funcionalidad
- **Solución**: Solo mostrar columnas con contenido real y útil
- **Ejemplo**: Columna "Acciones" mostrando espacios vacíos

#### 5. **Exceso de Controles de Navegación**
- **Error**: Mostrar todos los números de página (1,2,3,4,5...)
- **Solución**: Navegación minimalista con solo controles esenciales
- **Ejemplo**: Paginación con 8+ botones para 5 páginas

#### 6. **Configuraciones Innecesarias**
- **Error**: Selectores de "elementos por página" sin justificación
- **Solución**: Tamaño de página fijo y optimizado para el contexto
- **Ejemplo**: Dropdown con opciones 10,25,50,100 sin uso real

### ✅ **Principios de Éxito**

#### 1. **Diseño Progresivo**
- Comenzar con funcionalidad mínima viable
- Iterar basado en feedback real del usuario
- Agregar complejidad solo cuando sea necesaria

#### 2. **Consistencia como Prioridad**
- Definir sistema de diseño desde el inicio
- Aplicar patrones consistentemente en toda la aplicación
- Revisar regularmente para detectar inconsistencias

#### 3. **Minimalismo Funcional**
- Cada elemento debe tener propósito claro
- Eliminar opciones que no aportan valor real
- Priorizar claridad sobre funcionalidad exhaustiva

#### 4. **Validación Constante**
- Testear cada cambio en contexto real
- Obtener feedback temprano y frecuente
- Estar dispuesto a revertir decisiones erróneas

---

## 🚀 Implementación

### Checklist de Validación

- [ ] **Diseño minimalista** - Una sola tarjeta principal
- [ ] **Colores consistentes** - Palette definida sin excepciones
- [ ] **Grid funcional** - PageableGrid con paginación simple
- [ ] **Modal a nivel app** - Overlay correcto con z-index apropiado
- [ ] **Responsive** - Funciona en móvil, tablet y desktop
- [ ] **Performance** - Carga rápida y navegación fluida
- [ ] **OpenAPI compliant** - Interfaces exactas sin desviaciones
- [ ] **Accesible** - Navegación por teclado y ARIA labels

### Próximos Pasos

1. **Replicar en otros módulos** usando esta especificación
2. **Crear templates** reutilizables para agilizar desarrollo
3. **Documentar deviaciones** cuando sea necesario apartarse del estándar
4. **Mantener actualizada** esta especificación según evolución del diseño

---

## 📚 Referencias

- **Implementación base**: `personal-finance-crud` component
- **Design System**: TailwindCSS 4 + Glassmorphism patterns
- **Framework**: Angular 20 + Standalone components
- **Grid Component**: Custom PageableGrid implementation

---

*Este documento debe ser revisado y actualizado con cada iteración significativa del sistema de diseño CRUD.*