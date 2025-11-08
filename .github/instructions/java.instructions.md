---
applyTo: "**/*.java"
---

# 📘 Buenas Prácticas para APIs REST con Java 17 + Spring Boot 3.x + JPA/Hibernate

Esta guía consolida las mejores prácticas reconocidas en la industria para construir APIs REST modernas con Java 17, Spring Boot 3.x y JPA/Hibernate, enfocándose en arquitectura, seguridad, validaciones y pruebas.

## Índice

1. [Stack Tecnológico](#stack-tecnológico)
2. [Arquitectura General](#arquitectura-general)
3. [Estructura de Carpetas Sugerida](#estructura-de-carpetas-sugerida)
4. [Routing y Convenciones REST](#routing-y-convenciones-rest)
5. [JPA (Hibernate)](#jpa-hibernate)
6. [DTOs y MapStruct](#dtos-y-mapstruct)
7. [Seguridad](#seguridad)
8. [Validaciones](#validaciones)
9. [Testing](#testing)
10. [Performance & Buenas Prácticas Adicionales](#performance--buenas-prácticas-adicionales)
11. [🧠 Buenas Prácticas de Desarrollo en General](#-buenas-prácticas-de-desarrollo-en-general)
12. [🛡️ Buenas Prácticas de Seguridad](#️-buenas-prácticas-de-seguridad)
13. [🧾 Prácticas de Logging](#-prácticas-de-logging)
14. [🧭 Principios de Diseño y Desarrollo](#-principios-de-diseño-y-desarrollo)
15. [Anti-Patrones Comunes](#anti-patrones-comunes)

---

## Stack Tecnológico

### Lenguaje y Framework

- **Java 17** - Lenguaje de programación con características modernas (records, pattern matching, text blocks)
- **Spring Boot 3.x** - Framework para desarrollo de aplicaciones empresariales
- **Spring Web** - Para creación de APIs REST
- **Spring Data JPA** - Abstracción de acceso a datos

### Persistencia

- **JPA (Jakarta Persistence API)** - Especificación estándar para ORM
- **Hibernate** - Implementación de JPA
- **PostgreSQL/MySQL/Oracle** - Bases de datos relacionales soportadas
- **Flyway/Liquibase** - Gestión de migraciones de base de datos

### Mapeo y Validación

- **MapStruct 1.5.5** - Generación automática de mappers entre entidades y DTOs
- **Bean Validation (Hibernate Validator)** - Validación de datos con anotaciones
- **hibernate-types** - Soporte para tipos especiales (JSON, arrays)

### Seguridad

- **Spring Security** - Framework de autenticación y autorización
- **BCrypt** - Algoritmo de cifrado para contraseñas
- **JWT (JSON Web Tokens)** - Tokens de autenticación stateless

### Testing

- **JUnit 5** - Framework de testing unitario
- **Mockito** - Framework de mocking
- **Spring Boot Test** - Utilidades de testing para Spring Boot
- **TestContainers** - Contenedores Docker para testing de integración

### Logging y Observabilidad

- **SLF4J** - API de logging
- **Logback** - Implementación de logging
- **Spring Boot Actuator** - Métricas y health checks
- **Micrometer** - Métricas para observabilidad

### Build y Gestión de Dependencias

- **Maven** o **Gradle** - Gestión de dependencias y build
- **Spring Boot Maven/Gradle Plugin** - Plugins específicos de Spring Boot

### Infraestructura y Entorno

- **Docker** - Contenerización de aplicaciones
- **Docker Compose** - Orquestación de servicios en desarrollo

---

## Arquitectura General

* Usar arquitectura por capas: `Controllers`, `Services`, `Repositories`, `DTOs`.
* Evitar lógica de negocio en los controllers.
* Usar DTOs para entrada/salida, no exponer directamente las entidades JPA.
* Organizar la clase principal (`Application.java`) y la configuración en clases separadas (`@Configuration`).
* Aplicar separación de responsabilidades (Separation of Concerns).
* Implementar inyección de dependencias por constructor (preferido sobre `@Autowired` en campos).

### Flujo de una Request

```
Request → Controller → Service → Repository → Database
                ↓
            Validation
                ↓
            DTO Mapping
                ↓
            Response
```

---

## Estructura de Carpetas Sugerida

```
src/main/java/com/example/project/
├── controller/          # Endpoints HTTP
├── service/            # Lógica de negocio
├── repository/         # Acceso a datos (JPA)
├── model/              # Entidades JPA
├── dto/                # Data Transfer Objects
├── mapper/             # Mappers (MapStruct)
├── config/             # Configuraciones de Spring
├── exception/          # Excepciones personalizadas
├── middleware/         # Interceptors, HandlerAdvice
└── filter/             # Filtros de Servlet/Spring
```

### 📁 `/controller`

Contienen los endpoints HTTP. Reciben requests, validan parámetros y delegan a los servicios.

**Responsabilidades:**
- Definir endpoints y rutas
- Validar parámetros de entrada
- Delegar a servicios
- Retornar respuestas HTTP apropiadas

**Ejemplo:**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) { 
        this.userService = userService; 
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id) {
        UserDto user = userService.getById(id);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserDto dto) {
        UserDto created = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

---

### 📁 `/service`

Contienen la lógica de negocio de la aplicación.

**Responsabilidades:**
- Implementar reglas de negocio
- Coordinar operaciones entre repositorios
- Manejar transacciones
- Aplicar validaciones de negocio

**Ejemplo:**

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public UserService(UserRepository repo, UserMapper mapper) {
        this.userRepository = repo;
        this.userMapper = mapper;
    }
    
    @Transactional(readOnly = true)
    public UserDto getById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDto create(CreateUserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateException("Email already exists");
        }
        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
```

---

### 📁 `/repository`

Manejan acceso a datos usando JPA (Hibernate). Extienden `JpaRepository` o `CrudRepository`.

**Responsabilidades:**
- Operaciones CRUD básicas
- Consultas personalizadas con @Query
- Specifications para búsquedas complejas

**Ejemplo:**

```java
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findRecentUsers(@Param("date") LocalDateTime date);
}
```

---

### 📁 `/model`

Representan las entidades persistentes (tablas de la base de datos).

**Responsabilidades:**
- Definir estructura de datos
- Mapear relaciones entre entidades
- Configurar constraints y validaciones a nivel de BD

**Ejemplo:**

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // getters, setters, equals, hashCode
}
```

---

### 📁 `/dto`

Data Transfer Objects para entrada/salida.

**Responsabilidades:**
- Exponer solo datos necesarios
- Aplicar validaciones de entrada
- Separar modelo de persistencia de API

**Ejemplo:**

```java
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    // getters, setters
}

public class CreateUserDto {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8)
    private String password;
    // getters, setters
}
```

---

### 📁 `/mapper`

Interfaces de MapStruct para mapear entre entidades y DTOs.

**Responsabilidades:**
- Conversión automática entre entidades y DTOs
- Mapeo de campos con diferentes nombres
- Conversiones de tipos personalizadas

**Ejemplo:**

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
    
    @Mapping(target = "password", ignore = true)
    User toEntity(CreateUserDto dto);
    
    List<UserDto> toDtoList(List<User> users);
}
```

---

### 📁 `/config`

Configuración de Spring Boot, JPA, MapStruct, CORS, etc.

**Ejemplo:**

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true);
    }
}
```

---

### 📁 `/exception`

Manejo de excepciones personalizadas y control global con `@ControllerAdvice`.

**Ejemplo:**

```java
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
    }
}
```

---

### `/middleware` y `/filter`

* Interceptores, filtros de servlet o filtros de Spring para cross-cutting concerns (logging, auth, etc).

**Ejemplo de Interceptor:**

```java
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("Request: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }
}
```

---

## Routing y Convenciones REST

* Usar `@RestController` y `@RequestMapping`.
* Usar los verbos HTTP correctos: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`.
* Preferir rutas limpias: `/api/users/{id}`.
* Usar nombres de recursos en plural: `/api/users`, `/api/products`.
* Aplicar versionado de API cuando sea necesario: `/api/v1/users`.

### Convenciones de Endpoints

| Operación | Método HTTP | Ruta | Descripción |
|-----------|-------------|------|-------------|
| Listar todos | GET | `/api/users` | Obtener lista de usuarios |
| Obtener uno | GET | `/api/users/{id}` | Obtener usuario específico |
| Crear | POST | `/api/users` | Crear nuevo usuario |
| Actualizar completo | PUT | `/api/users/{id}` | Actualizar usuario completo |
| Actualizar parcial | PATCH | `/api/users/{id}` | Actualizar campos específicos |
| Eliminar | DELETE | `/api/users/{id}` | Eliminar usuario |

### Códigos de Estado HTTP

- **200 OK** - Respuesta exitosa para GET, PUT, PATCH
- **201 Created** - Recurso creado exitosamente (POST)
- **204 No Content** - Operación exitosa sin contenido (DELETE)
- **400 Bad Request** - Datos de entrada inválidos
- **401 Unauthorized** - No autenticado
- **403 Forbidden** - No autorizado
- **404 Not Found** - Recurso no encontrado
- **409 Conflict** - Conflicto (duplicados, estado inválido)
- **500 Internal Server Error** - Error del servidor

---

## JPA (Hibernate)

* Usar `@Repository` y extender de `JpaRepository`.
* Usar `.findAll()`, `.findById()`, `.save()`, etc.
* Para consultas de solo lectura, preferir `@Transactional(readOnly = true)`.
* Separar acceso a datos en la capa repository.
* Aplicar migraciones con Flyway o Liquibase.

### Dependencias necesarias:

#### Maven

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- Para base de datos específica (ejemplo PostgreSQL) -->
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>
```

#### Gradle

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
// Para base de datos específica (ejemplo PostgreSQL)
runtimeOnly 'org.postgresql:postgresql'
```

### 1. **Mapeo de Relaciones**

#### 1.1. One to One

```java
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;
    // ...
}

@Entity
public class Profile {
    @Id @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    // ...
}
```

**Tip:** Usa `fetch = FetchType.LAZY` siempre que puedas, salvo que el acceso sea obligatorio.

---

#### 1.2. One to Many

```java
@Entity
public class Author {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();
    
    // Métodos helper para mantener sincronización bidireccional
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }
    
    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}

@Entity
public class Book {
    @Id @GeneratedValue
    private Long id;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;
    // ...
}
```

**Tip:** Prefiere listas (`List<>`), no sets. Usa `orphanRemoval = true` para sincronizar hijos.

---

#### 1.3. Many to Many

```java
@Entity
public class Student {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses = new ArrayList<>();
}

@Entity
public class Course {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();
}
```

**Tip:** Evita ManyToMany para relaciones complejas; crea entidad intermedia (ej: Enrollment) si necesitas atributos adicionales.

---

#### 1.4. Relaciones con Composite Key

```java
@Entity
@IdClass(EnrollmentId.class)
public class Enrollment {
    @Id
    private Long studentId;
    @Id
    private Long courseId;
    private LocalDate enrollmentDate;
    
    @ManyToOne
    @JoinColumn(name = "studentId", insertable = false, updatable = false)
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "courseId", insertable = false, updatable = false)
    private Course course;
}

public class EnrollmentId implements Serializable {
    private Long studentId;
    private Long courseId;
    
    // Constructor vacío
    public EnrollmentId() {}
    
    // Constructor con parámetros
    public EnrollmentId(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }
    
    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentId that = (EnrollmentId) o;
        return Objects.equals(studentId, that.studentId) &&
               Objects.equals(courseId, that.courseId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }
}
```

**Tip:** Siempre implementa correctamente `equals()` y `hashCode()` en la clave compuesta.

---

### 2. **Mapeo de Tipos de Datos Especiales**

#### 2.1. Enums

```java
public enum BookStatus {
    AVAILABLE, BORROWED, LOST
}

@Entity
public class Book {
    @Id @GeneratedValue
    private Long id;
    private String title;
    
    @Enumerated(EnumType.STRING)
    private BookStatus status;
}
```

**Tip:** Usa `EnumType.STRING` siempre para mayor seguridad ante cambios de orden.

---

#### 2.2. JSON (usando hibernate-types de Vlad Mihalcea)

```java
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@TypeDef(name = "json", typeClass = JsonType.class)
@Entity
public class Book {
    @Id @GeneratedValue
    private Long id;
    private String title;
    
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private BookMetadata metadata;
}

public class BookMetadata {
    private String isbn;
    private int pages;
    // getters/setters
}
```

**Dependencia necesaria:**

```xml
<dependency>
    <groupId>com.vladmihalcea</groupId>
    <artifactId>hibernate-types-60</artifactId>
    <version>2.21.1</version>
</dependency>
```

**Tip:** Usa librería [hibernate-types](https://github.com/vladmihalcea/hibernate-types) para mapear JSON en PostgreSQL/MySQL.

---

#### 2.3. Listas de JSON

```java
@Type(type = "json")
@Column(columnDefinition = "json")
private List<Review> reviews;

public class Review {
    private String reviewer;
    private String comment;
    private int rating;
    // getters/setters
}
```

**Tip:** Siempre que uses listas/objetos embebidos, asegúrate que sean serializables.

---

### 3. **Búsquedas usando Predicate y Specification**

#### 3.1. Búsquedas de rango de fechas

```java
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {}

public class BookSpecs {
    public static Specification<Book> publishedBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> cb.between(root.get("publicationDate"), from, to);
    }
    
    public static Specification<Book> publishedAfter(LocalDate date) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("publicationDate"), date);
    }
}

// Uso:
List<Book> libros = bookRepository.findAll(BookSpecs.publishedBetween(from, to));
```

---

#### 3.2. Búsqueda de rangos numéricos

```java
public static Specification<Book> priceBetween(double min, double max) {
    return (root, query, cb) -> cb.between(root.get("price"), min, max);
}

public static Specification<Book> priceGreaterThan(double min) {
    return (root, query, cb) -> cb.greaterThan(root.get("price"), min);
}

// Uso:
List<Book> libros = bookRepository.findAll(priceBetween(10, 50));
```

---

#### 3.3. Búsquedas de texto (LIKE, case-insensitive)

```java
public static Specification<Book> titleContains(String text) {
    return (root, query, cb) -> cb.like(
        cb.lower(root.get("title")), 
        "%" + text.toLowerCase() + "%"
    );
}

public static Specification<Book> authorNameEquals(String name) {
    return (root, query, cb) -> cb.equal(
        cb.lower(root.get("author").get("name")), 
        name.toLowerCase()
    );
}

// Uso combinado:
List<Book> libros = bookRepository.findAll(
    Specification.where(titleContains("java"))
        .and(priceBetween(10, 50))
        .and(publishedAfter(LocalDate.of(2020, 1, 1)))
);
```

**Tip:** Siempre usa búsquedas insensibles a mayúsculas/minúsculas para mejorar UX.

---

### 4. **Optimización de Consultas**

#### 4.1. Uso de FetchType.LAZY y FetchType.EAGER

```java
@OneToMany(fetch = FetchType.LAZY)
private List<Book> books; // Mejor práctica: LAZY

@OneToOne(fetch = FetchType.EAGER)
private Profile profile; // EAGER solo si siempre lo necesitas
```

**Tip:** Prefiere LAZY, usa EAGER solo en relaciones obligatorias y muy "pequeñas".

---

#### 4.2. Uso de JOIN FETCH

```java
@Query("SELECT a FROM Author a JOIN FETCH a.books WHERE a.id = :id")
Author findAuthorWithBooks(@Param("id") Long id);

@Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books WHERE a.name LIKE %:name%")
List<Author> findAuthorsWithBooksByName(@Param("name") String name);
```

**Tip:** Elimina el n+1 problem usando `JOIN FETCH` en queries. Usa `DISTINCT` cuando sea necesario para evitar duplicados.

---

#### 4.3. Uso de Proyecciones

```java
public interface BookView {
    String getTitle();
    String getAuthor();
    Double getPrice();
}

public interface BookRepository extends JpaRepository<Book, Long> {
    List<BookView> findByStatus(BookStatus status);
    
    @Query("SELECT b.title as title, b.author as author, b.price as price FROM Book b WHERE b.price > :minPrice")
    List<BookView> findExpensiveBooks(@Param("minPrice") Double minPrice);
}
```

**Tip:** Las proyecciones evitan cargar entidades completas, ahorrando recursos.

---

#### 4.4. Uso de EntityGraphs en JPA Repositories

```java
@EntityGraph(attributePaths = {"books", "books.publisher"})
@Query("SELECT a FROM Author a WHERE a.id = :id")
Author findAuthorWithBooksAndPublisher(@Param("id") Long id);

@EntityGraph(attributePaths = {"books"})
List<Author> findByCountry(String country);
```

**Tip:** `@EntityGraph` permite cargar relaciones específicas, evitando cargar datos innecesarios y el problema n+1.

---

## DTOs y MapStruct

* Usar DTOs para exponer/controlar los datos.
* Usar **MapStruct** para mapear entre entidades y DTOs.
* Separar DTOs de entrada (CreateXDto, UpdateXDto) de DTOs de salida (XDto).

### 1. Agregar dependencia MapStruct:

#### Maven

```xml
<dependency>
  <groupId>org.mapstruct</groupId>
  <artifactId>mapstruct</artifactId>
  <version>1.5.5.Final</version>
</dependency>
<dependency>
  <groupId>org.mapstruct</groupId>
  <artifactId>mapstruct-processor</artifactId>
  <version>1.5.5.Final</version>
  <scope>provided</scope>
</dependency>
```

#### Gradle

```gradle
implementation 'org.mapstruct:mapstruct:1.5.5.Final'
annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'
```

### 2. Crear el mapper:

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserDto dto);
    
    List<UserDto> toDtoList(List<User> users);
    
    @Mapping(source = "profile.firstName", target = "firstName")
    @Mapping(source = "profile.lastName", target = "lastName")
    UserDetailDto toDetailDto(User user);
}
```

### 3. Inyectar y usar:

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public UserService(UserRepository repo, UserMapper mapper) {
        this.userRepository = repo;
        this.userMapper = mapper;
    }
    
    public UserDto create(CreateUserDto dto) {
        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
```

### Alternativa manual:

```java
public class UserManualMapper {
    public static UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
    
    public static User toEntity(UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        return user;
    }
}
```

**Tip:** MapStruct es preferible porque genera código en tiempo de compilación, es más rápido y reduce errores.

---

## Seguridad

* Configurar CORS con `@CrossOrigin` o a nivel global.
* Validar inputs con anotaciones (`@NotNull`, `@Size`, etc.) o usando Bean Validation (Hibernate Validator).
* Nunca loggear datos sensibles.
* Cifrar contraseñas con BCrypt.
* Implementar autenticación con JWT o OAuth2.

### Dependencias para seguridad:

#### Maven

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

#### Gradle

```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
```

### Configuración de CORS

```java
@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### Cifrado de Contraseñas

```java
@Configuration
public class PasswordEncoderConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    public User createUser(CreateUserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }
    
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

---

## Validaciones

* Usar Bean Validation (`@Valid`, `@NotNull`, etc.) en DTOs y entidades.
* Manejar errores con `@ControllerAdvice` y excepciones personalizadas.

### Dependencias necesarias:

#### Maven

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### Gradle

```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

### 1. **Validación de Atributos**

#### 1.1. No nulo

```java
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class UserDto {
    @NotNull(message = "El ID no puede ser nulo")
    private Long id;
    
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    
    @NotEmpty(message = "La lista de roles no puede estar vacía")
    private List<String> roles;
}
```

**Diferencias:**
- `@NotNull` - No acepta null
- `@NotBlank` - No acepta null, string vacío o solo espacios
- `@NotEmpty` - No acepta null o colecciones/strings vacíos

---

#### 1.2. Expresiones Regulares

```java
import jakarta.validation.constraints.Pattern;

public class UserDto {
    @Pattern(regexp = "^[A-Za-z0-9]{6,12}$", 
             message = "El username debe tener entre 6 y 12 caracteres alfanuméricos")
    private String username;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$",
             message = "Número de teléfono inválido")
    private String phoneNumber;
}
```

---

#### 1.3. Formato de Correo

```java
import jakarta.validation.constraints.Email;

public class UserDto {
    @Email(message = "Correo inválido")
    @NotBlank
    private String email;
}
```

---

#### 1.4. Números en positivo

```java
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class ProductDto {
    @Positive(message = "El precio debe ser positivo")
    private Double price;
    
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
    
    @Min(value = 0, message = "El descuento mínimo es 0")
    @Max(value = 100, message = "El descuento máximo es 100")
    private Integer discountPercentage;
}
```

---

### 2. **Validación de Body en Controllers**

* Usa `@Valid` o `@Validated` en los parámetros de métodos del controller.
* El objeto debe ser un bean con anotaciones de validación.

```java
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserDto userDto) {
        // Si la validación falla, Spring retorna 400 automáticamente
        UserDto created = userService.create(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateUserDto userDto
    ) {
        UserDto updated = userService.update(id, userDto);
        return ResponseEntity.ok(updated);
    }
}
```

**Notas:**

* Spring valida automáticamente al recibir el request.
* Para colecciones, usar `@Valid` en la lista:

```java
public class OrderDto {
    @Valid
    @NotEmpty(message = "La orden debe tener al menos un producto")
    private List<ProductDto> products;
}
```

---

### 3. **Gestión de Query Parameters Complejos**

* Para endpoints con múltiples parámetros, agrúpalos en un DTO y valida sus atributos.

```java
public class ProductSearchQuery {
    @NotNull(message = "El precio mínimo es requerido")
    @Positive
    private Integer minPrice;
    
    @NotNull(message = "El precio máximo es requerido")
    @Positive
    private Integer maxPrice;
    
    @Pattern(regexp = "^[A-Za-z]+$", message = "La categoría solo puede contener letras")
    private String category;
    
    @Min(value = 0, message = "La página debe ser mayor o igual a 0")
    private Integer page = 0;
    
    @Min(value = 1, message = "El tamaño debe ser al menos 1")
    @Max(value = 100, message = "El tamaño máximo es 100")
    private Integer size = 20;
}

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> search(@Valid ProductSearchQuery query) {
        Page<ProductDto> products = productService.search(query);
        return ResponseEntity.ok(products);
    }
}
```

**Notas:**

* Los query params se mapean automáticamente a los atributos del DTO si los nombres coinciden.
* Puedes combinar validaciones en cada campo según tu necesidad.
* Para activar validación en query DTO, usa `@Valid` en el parámetro del método.

---

### 4. **Validaciones Personalizadas**

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "Email already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final UserRepository userRepository;
    
    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) return true;
        return !userRepository.existsByEmail(email);
    }
}

// Uso:
public class CreateUserDto {
    @Email
    @UniqueEmail
    private String email;
}
```

---

### **Tips adicionales**

* Puedes personalizar los mensajes de error en cada anotación.
* Para validaciones más avanzadas, implementa tus propios constraints.
* Combina validaciones a nivel de atributo y DTO para lógica cruzada (ej: `@AssertTrue`).
* Usa `@Validated` a nivel de clase para grupos de validación.
* Implementa un `@ControllerAdvice` global para capturar y formatear las respuestas de error.
* Usa mensajes de validación en archivos de propiedades para internacionalización.

### Manejo de Errores de Validación

```java
@RestControllerAdvice
public class ValidationExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ValidationErrorResponse response = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            errors,
            LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException ex
    ) {
        String message = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
            
        ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            message,
            LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
}
```

---

**Referencias**

* [Jakarta Bean Validation](https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html)
* [Spring Validation Docs](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html)

---

## Testing

Para mantener una guía de testing completa y especializada, se recomienda seguir las instrucciones detalladas en:

**[Sigue las instrucciones de testing en Java](./java-testing.instructions.md)**

### Resumen de Buenas Prácticas de Testing

* Usar JUnit 5 para pruebas unitarias
* Usar Mockito para mocking de dependencias
* Usar `@WebMvcTest` para testing de controllers
* Usar `@DataJpaTest` para testing de repositories
* Usar `@SpringBootTest` para pruebas de integración
* Usar TestContainers para pruebas con bases de datos reales
* Mantener cobertura de código > 80%
* Escribir tests legibles con nombres descriptivos
* Aplicar patrón Given-When-Then

---

## Performance & Buenas Prácticas Adicionales

* Habilitar compresión de respuestas con Spring Boot Actuator/config.
* No bloquear hilos: evitar `.get()` en `CompletableFuture` sin necesidad.
* Aplicar paginación en listas grandes (`Pageable`).
* Evitar uso de sesión HTTP en APIs REST.
* Usar cache cuando sea apropiado (`@Cacheable`, Redis, etc.).
* Implementar rate limiting para proteger endpoints.
* Usar indices apropiados en la base de datos.
* Optimizar queries con EXPLAIN ANALYZE.
* Implementar health checks y métricas con Actuator.

### Paginación

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        ));
        
        Page<UserDto> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }
}
```

### Caching

```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users", "products");
    }
}

@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public UserDto getById(UUID id) {
        // Esta operación se cachea
        return userMapper.toDto(userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found")));
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
    
    @CachePut(value = "users", key = "#result.id")
    public UserDto update(UUID id, UpdateUserDto dto) {
        // Actualiza el cache con el nuevo valor
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
        // ... actualizar user
        return userMapper.toDto(userRepository.save(user));
    }
}
```

---

## 🧠 Buenas Prácticas de Desarrollo en General

- **Aplica el principio SOLID**: cada clase debe tener una única responsabilidad clara.
- **Evita la lógica duplicada**: usa servicios reutilizables o métodos utilitarios.
- **Sigue el principio DRY (Don't Repeat Yourself)** para evitar mantenimiento excesivo.
- **Nombra las clases, métodos y variables de forma semántica** (ej: `createUser`, `findByEmail`).
- **Evita "magia" en el código** (valores hardcodeados, conversiones implícitas confusas).
- **Usa Lombok solo cuando aporte claridad**, no para ocultar comportamientos complejos.
- **Agrega JavaDoc o comentarios solo cuando sean realmente útiles** (por qué, no qué).
- **Aplica patrones de diseño comunes** según contexto: Factory, Builder, Strategy, etc.
- **Utiliza records de Java 17** para DTOs o respuestas inmutables.
- **Asegura la compatibilidad con UTF-8** y configuraciones regionales apropiadas.
- **Usa inyección de dependencias por constructor**, no por campo.
- **Prefiere composición sobre herencia** cuando sea apropiado.
- **Implementa equals() y hashCode() correctamente** en entidades y DTOs.
- **Usa Optional<T> para valores que pueden ser null**, especialmente en retornos de métodos.

### Ejemplo de Record para DTO

```java
public record UserDto(
    UUID id,
    String username,
    String email,
    LocalDateTime createdAt
) {}

public record CreateUserDto(
    @NotBlank String username,
    @Email String email,
    @NotBlank @Size(min = 8) String password
) {}
```

---

## 🛡️ Buenas Prácticas de Seguridad

- **Nunca exponer datos sensibles** (contraseñas, tokens, IDs internos) en las respuestas.
- **Valida y sanitiza toda entrada de usuario**, incluso la proveniente del frontend.
- **Usa `@Valid` y Bean Validation** en los DTOs para evitar inyecciones y datos inválidos.
- **Implementa CORS adecuadamente**: restringe orígenes, métodos y cabeceras permitidas.
- **Maneja autenticación con JWT** o mecanismos de OAuth2 cuando sea apropiado.
- **Cifra contraseñas** con BCrypt (no MD5 ni SHA-1).
- **Evita imprimir datos confidenciales** en logs o excepciones.
- **Usa HTTPS siempre**, especialmente en producción.
- **Define roles y permisos claros** (Spring Security: `@PreAuthorize` o `@Secured`).
- **Desactiva endpoints por defecto** de actuator o documentación pública sin seguridad.
- **Valida los encabezados HTTP y los tipos MIME esperados**.
- **Implementa rate limiting** para prevenir ataques de fuerza bruta.
- **Usa tokens CSRF** cuando sea necesario para prevenir ataques cross-site.
- **Sanitiza SQL para prevenir inyección SQL** (JPA lo hace automáticamente).
- **No uses `@RequestParam` para datos sensibles**, usa POST con body.
- **Implementa logout apropiado** invalidando tokens/sesiones.

### Ejemplo de Seguridad con JWT

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## 🧾 Prácticas de Logging

- Usa **SLF4J con Logback** o **Log4j2**, nunca `System.out`.
- Define **niveles de log adecuados**:
  - `TRACE`: Diagnóstico muy detallado.
  - `DEBUG`: Información útil en desarrollo.
  - `INFO`: Eventos importantes del flujo normal.
  - `WARN`: Situaciones no críticas.
  - `ERROR`: Fallos que requieren acción.
- Evita logs redundantes o demasiado verbosos.
- No loguees información sensible (contraseñas, tokens, PII).
- Usa `log.debug("Procesando usuario con ID: {}", userId);` para formato parametrizado.
- Configura rotación y retención de logs (log rotation).
- Centraliza logs con herramientas como ELK Stack o Azure Monitor.
- Incluye trazabilidad (correlationId, requestId) en los logs para depuración distribuida.
- Implementa un **Handler global** (`@ControllerAdvice`) para capturar excepciones y loguearlas consistentemente.

### Configuración de Logging

```java
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    
    @Around("execution(* com.example.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.debug("Ejecutando {}.{}", className, methodName);
        
        try {
            Object result = joinPoint.proceed();
            log.debug("Completado {}.{} exitosamente", className, methodName);
            return result;
        } catch (Exception e) {
            log.error("Error en {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
```

### application.yml para Logging

```yaml
logging:
  level:
    root: INFO
    com.example: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 30
```

---

## 🧭 Principios de Diseño y Desarrollo

- **SOLID Principles**:
  - **S** - Single Responsibility Principle
  - **O** - Open/Closed Principle
  - **L** - Liskov Substitution Principle
  - **I** - Interface Segregation Principle
  - **D** - Dependency Inversion Principle

- **DRY (Don't Repeat Yourself)** → evita duplicar lógica o estructuras.
- **KISS (Keep It Simple, Stupid)** → busca claridad sobre complejidad innecesaria.
- **YAGNI (You Aren't Gonna Need It)** → no implementes lo que aún no se requiere.
- **SoC (Separation of Concerns)** → separa claramente responsabilidades.
- **CQS (Command Query Separation)** → diferencia métodos que modifican estado de los que consultan.
- **Open/Closed Principle** → el código debe poder extenderse sin modificarse.
- **Dependency Injection** → evita acoplamientos fuertes entre clases.
- **Immutabilidad donde sea posible** → reduce errores de concurrencia.
- **Fail Fast** → detecta y reporta errores lo antes posible.
- **Convention over Configuration** → sigue convenciones de Spring Boot.

### Ejemplo de Aplicación de Principios

```java
// Single Responsibility - Cada clase tiene una responsabilidad
@Service
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final EmailService emailService;
    
    // Dependency Injection por constructor
    public UserService(UserRepository repository, UserMapper mapper, EmailService emailService) {
        this.repository = repository;
        this.mapper = mapper;
        this.emailService = emailService;
    }
    
    // Open/Closed - Extensible sin modificación
    public UserDto create(CreateUserDto dto) {
        validateUser(dto);
        User user = mapper.toEntity(dto);
        User saved = repository.save(user);
        notifyUserCreated(saved);
        return mapper.toDto(saved);
    }
    
    // Métodos privados para SRP
    private void validateUser(CreateUserDto dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new DuplicateException("Email already exists");
        }
    }
    
    private void notifyUserCreated(User user) {
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
    }
}
```

---

## Anti-Patrones Comunes

| Antipatrón | Mejor práctica | Ejemplo |
|------------|----------------|---------|
| Usar entidades en la respuesta | Usar DTOs y mappers (MapStruct) | `return userMapper.toDto(user);` |
| Lógica en controllers | Delegar a servicios | `userService.create(dto);` |
| `EntityManager` directo en controller | Usar inyección + capa de servicio/repositorio | `@Autowired UserRepository repo;` |
| Repetir mapeos manualmente | Usar MapStruct o helper de mapeo | `@Mapper interface UserMapper` |
| No validar entradas | Validar con Bean Validation | `@Valid @RequestBody CreateUserDto` |
| Colocar lógica de negocio dentro de los controladores | Separar en servicios | `@Service public class UserService` |
| Usar `@Autowired` sobre campos | Preferir inyección por constructor | `public UserService(UserRepository repo)` |
| Capturar excepciones genéricas (`Exception`) | Capturar excepciones específicas | `catch (NotFoundException ex)` |
| Crear métodos o clases "God Objects" | Aplicar Single Responsibility | Una clase, una responsabilidad |
| Usar queries nativas en exceso | Usar JPA cuando sea posible | `repository.findByUsername(name)` |
| Mezclar capas | Respetar arquitectura por capas | Controller → Service → Repository |
| Exponer IDs internos o secuenciales | Usar UUIDs o anonimización | `@GeneratedValue(strategy = UUID)` |
| Guardar secretos en código fuente | Usar variables de entorno | `${DB_PASSWORD}` en application.yml |
| Ignorar el manejo de errores HTTP | Usar códigos HTTP apropiados | `ResponseEntity.status(404)` |
| Dependencias circulares entre servicios | Reestructurar o usar eventos | Event-driven architecture |
| Usar System.out.println() | Usar logs estructurados | `log.info("Message")` |
| No usar transacciones | Anotar con `@Transactional` | `@Transactional public void save()` |
| Fetchear datos innecesarios | Usar proyecciones y LAZY loading | `@OneToMany(fetch = LAZY)` |
| No manejar el problema N+1 | Usar JOIN FETCH o EntityGraph | `@EntityGraph(attributePaths = {"books"})` |
| Hardcodear valores | Usar application.properties/yml | `@Value("${app.name}")` |

### Ejemplos de Anti-Patrones a Evitar

```java
// ❌ MAL - Lógica en controller
@RestController
public class UserController {
    @Autowired
    private UserRepository repository;
    
    @PostMapping("/users")
    public User create(@RequestBody User user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email exists");
        }
        return repository.save(user);
    }
}

// ✅ BIEN - Usar servicios y DTOs
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserDto dto) {
        UserDto created = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

---

## Notas Finales

* Para validación de existencia previa a acciones (como un filtro en .NET): en Spring se recomienda hacerlo en el servicio o con `@ControllerAdvice` lanzando una excepción personalizada (`NotFoundException`).

* Para middlewares/filtros globales, usar `HandlerInterceptor` o filtros de Servlet.

* Migraciones de base de datos: usa Flyway/Liquibase en vez de comandos de línea.

* Siempre documenta tu API con OpenAPI/Swagger.

* Implementa health checks y métricas con Spring Boot Actuator.

* Usa profiles de Spring (`dev`, `test`, `prod`) para diferentes configuraciones.

* Considera usar Docker para desarrollo y deployment consistente.

---

**Referencias Adicionales**

* [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
* [MapStruct Documentation](https://mapstruct.org/documentation/)
* [Jakarta Bean Validation](https://jakarta.ee/specifications/bean-validation/)
* [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/)
* [Baeldung - Spring Tutorials](https://www.baeldung.com/spring-tutorial)