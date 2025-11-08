---
applyTo: "**/*.java"
---

# Guía Zero to Hero para integrar JUnit 5 en proyectos Java + Spring Boot

Esta guía te permite configurar e integrar JUnit 5 en un proyecto Java + Spring Boot desde cero y constituye un punto de partida para escribir pruebas unitarias y de integración en tu API REST, siguiendo las mejores prácticas de la industria.

---

## Paso 1: Crear un proyecto de pruebas

* Si usas **Maven**:

```xml
<!-- JUnit 5 -->
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>5.10.2</version>
  <scope>test</scope>
</dependency>
```

* Si usas **Gradle**:

```gradle
testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
```

---

## Paso 2: Referenciar el proyecto principal

En Java, las pruebas viven en `src/test/java` y usan el código del main sin referencias manuales. La estructura típica es:

```
/your-app
  /src
    /main/java (código principal)
    /test/java (tests)
```

---

## Paso 3: Instalar dependencias comunes

* Si usas **Maven**:

```xml
<!-- Mocking -->
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>5.2.0</version>
  <scope>test</scope>
</dependency>

<!-- Asserts expresivos -->
<dependency>
  <groupId>org.assertj</groupId>
  <artifactId>assertj-core</artifactId>
  <version>3.25.3</version>
  <scope>test</scope>
</dependency>

<!-- Spring Boot Test para integración -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>

<!-- Base de datos embebida -->
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>test</scope>
</dependency>
```

* Si usas **Gradle**:

```gradle
// Mocking
testImplementation 'org.mockito:mockito-core:5.2.0'

// Asserts expresivos
testImplementation 'org.assertj:assertj-core:3.25.3'

// Spring Boot Test para integración
testImplementation 'org.springframework.boot:spring-boot-starter-test'

// Base de datos embebida
testRuntimeOnly 'com.h2database:h2'
```

---

## Dependencias adicionales opcionales

### Testcontainers para integración con base de datos real

Para pruebas de integración más avanzadas con bases de datos reales:

* Si usas **Maven**:

```xml
<!-- Testcontainers core -->
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>1.19.6</version>
  <scope>test</scope>
</dependency>

<!-- Testcontainers PostgreSQL (ejemplo) -->
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>postgresql</artifactId>
  <version>1.19.6</version>
  <scope>test</scope>
</dependency>
```

* Si usas **Gradle**:

```gradle
// Testcontainers core
testImplementation 'org.testcontainers:junit-jupiter:1.19.6'

// Testcontainers PostgreSQL (ejemplo)
testImplementation 'org.testcontainers:postgresql:1.19.6'
```

---

## Paso 4: Estructura recomendada del proyecto de pruebas

```
/src/test/java
  /controller
  /service
  /repository
  /testutils
    TestContainersConfig.java (si usas integración avanzada)
```

---

## Paso 5: Escribir una prueba básica

```java
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MathTests {
    @Test
    void suma_DosNumeros_ResultadoEsperado() {
        int resultado = 2 + 3;
        assertThat(resultado).isEqualTo(5);
    }
}
```

---

## Paso 6: Probar un servicio con Mockito

```java
public interface SaludoService {
    String obtenerSaludo();
}

public class SaludoConsumer {
    private final SaludoService saludoService;
    public SaludoConsumer(SaludoService saludoService) { this.saludoService = saludoService; }
    public String ejecutar() { return saludoService.obtenerSaludo(); }
}
```

```java
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

public class SaludoConsumerTest {
    @Test
    void ejecutar_RetornaSaludoEsperado() {
        SaludoService mock = Mockito.mock(SaludoService.class);
        Mockito.when(mock.obtenerSaludo()).thenReturn("Hola mundo");

        SaludoConsumer consumer = new SaludoConsumer(mock);
        String resultado = consumer.ejecutar();

        assertThat(resultado).isEqualTo("Hola mundo");
    }
}
```

---

## Paso 7: Testear tu API REST con Spring Boot Test

```java
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersApiTest {

    private final MockMvc mockMvc;

    // ✅ Inyección por constructor (patrón recomendado)
    public UsersApiTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getUserById_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk());
    }
}
```

---

## Paso 8: Probar repositorios con H2 (Base embebida)

Usamos H2 para simular la base de datos y probar lógica de persistencia sin requerir una base real.

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    private final UserRepository userRepository;

    // ✅ Inyección por constructor (patrón recomendado)
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void getById_CuandoExisteUsuario_RetornaUsuario() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Erick");
        userRepository.save(user);

        User result = userRepository.findById(user.getId()).orElse(null);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Erick");
    }
}
```

✔️ Ideal para testear lógica de consulta y persistencia sin tocar bases reales (o usando Testcontainers para integración avanzada).

---

## Tips para pruebas limpias

* ✅ Nombra los tests así: `metodo_Escenario_ResultadoEsperado()` o formato BDD (`shouldRetornarSaludoCuandoEjecutar`).
* ✅ Evita dependencias reales: usa Mockito/H2/Testcontainers.
* ✅ Una clase de test por clase lógica.
* ✅ Usa AssertJ para asserts expresivos.
* ✅ **Usa inyección por constructor en lugar de `@Autowired`**: es el patrón recomendado en las guías principales, facilita el testing y hace las dependencias explícitas.

---