# Core Library - Hướng dẫn sử dụng

## Giới thiệu

Core Library là tập hợp các modules tái sử dụng cho các dự án Java Spring Boot, bao gồm:
- **core-common**: Security, JWT, Cache, Async, Utilities
- **core-database-sync**: Routing DataSource (Read/Write splitting)
- **core-database-async**: Async database operations

## Cấu trúc Project

```
core/
├── core-common/              # Common utilities, security, async
├── core-database-sync/       # Synchronous database with routing
├── core-database-async/      # Asynchronous database operations
├── README.md                 # File này
└── TROUBLESHOOTING.md        # Tổng hợp lỗi và cách sửa
```

---

## 1. Module: core-common

### Tính năng
- ✅ JWT Authentication & Authorization
- ✅ Spring Security Configuration
- ✅ JWT Token Cache (In-Memory & Redis)
- ✅ Async Task Executor với monitoring
- ✅ Custom Password Encoder
- ✅ Common utilities (JsonHelper, StringHelper, SearchFieldUtils)

### Cài đặt

**Gradle:**
```gradle
dependencies {
    implementation 'com.example:core-common:1.0.0'
}
```

**Maven:**
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>core-common</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Cấu hình

#### 1.1 JWT Configuration

**application.yml:**
```yaml
jwt:
  # RSA Keys (PKCS#8 format cho private key, X.509 format cho public key)
  privateKey: |
    -----BEGIN PRIVATE KEY-----
    MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...
    -----END PRIVATE KEY-----
  publicKey: |
    -----BEGIN PUBLIC KEY-----
    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvZ...
    -----END PUBLIC KEY-----
  
  # Token expiration (seconds)
  jwtAccessTokenExpirationS: 3600      # 1 hour
  jwtRefreshExpirationS: 2592000       # 30 days
  
  # Issuer
  issuer: "your-app-name"
  
  # Optional: JWK Set URI (nếu dùng external authorization server)
  # jwkSetUri: "https://auth-server.com/.well-known/jwks.json"
```

#### 1.2 Security Configuration

```yaml
security:
  # Danh sách endpoints không cần authentication
  permitAll:
    - /api/v1/auth/login
    - /api/v1/auth/register
    - /public/**
```

**Mặc định các endpoints này đã được permit:**
- `/swagger-ui/**`
- `/api-docs/**`, `/x/api-docs/**`
- `/actuator/**`
- `/error/**`
- `/favicon.ico`

#### 1.3 Async Configuration

```yaml
core:
  async:
    enable: true                    # Bật/tắt async (default: false)
    corePoolSize: 10                # Số thread core
    maxPoolSize: 50                 # Số thread tối đa
    queueCapacity: 100              # Queue capacity
    keepAliveSeconds: 60            # Keep alive time
    threadNamePrefix: "async-"      # Thread name prefix
```

**Lưu ý:** Async mặc định **TẮT**. Phải set `enable: true` để sử dụng.

### Sử dụng

#### 1.1 JWT Service

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JWTTokenService jwtTokenService;
    private final IJwtCache jwtCache;

    public JwtToken login(String username, String password) {
        // Authenticate user...
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        claims.put("email", user.getEmail());
        
        String accessToken = jwtTokenService.createAccessToken(username, claims);
        String refreshToken = jwtTokenService.createRefreshToken(username, claims);
        
        // Cache refresh token
        jwtCache.put(refreshToken, username, 2592000L);
        
        return new JwtToken(accessToken, refreshToken);
    }
    
    public void logout(String refreshToken) {
        jwtCache.remove(refreshToken);
    }
}
```

#### 1.2 Async Tasks

```java
@Service
public class EmailService {
    
    @Async("customTaskExecutor")  // Sử dụng custom executor
    public CompletableFuture<Boolean> sendEmail(String to, String subject, String body) {
        // Send email logic...
        return CompletableFuture.completedFuture(true);
    }
}
```

#### 1.3 Switching JWT Cache Implementation

**Default:** `InMemoryJwtCache` (sử dụng Caffeine)

**Dùng Redis:**
```java
@Service
@RequiredArgsConstructor
public class AuthService {
    @Qualifier("redisJwtCache")
    private final IJwtCache jwtCache;  // Inject Redis implementation
    
    // ...
}
```

---

## 2. Module: core-database-sync

### Tính năng
- ✅ Routing DataSource (Read/Write splitting)
- ✅ Tự động route dựa trên annotation `@ReadOnly` hoặc transaction
- ✅ Support custom database properties với prefix `database.*`
- ✅ Thread-safe routing với ThreadLocal

### Cài đặt

**Gradle:**
```gradle
dependencies {
    implementation 'com.example:core-database-sync:1.0.0'
}
```

### Cấu hình

**application.yml:**
```yaml
database:
  writer:
    url: jdbc:mysql://master-db:3306/mydb
    username: root
    password: secret
  reader:
    url: jdbc:mysql://slave-db:3306/mydb
    username: readonly
    password: secret

# HikariCP configuration (optional)
spring:
  datasource:
    hikari:
      connection-timeout: 30000
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 600000
      max-lifetime: 1800000
```

### Sử dụng

#### 2.1 Tự động routing

**Read operations:**
```java
@Service
public class UserService {
    
    @Transactional(readOnly = true)  // Tự động route đến READER
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
```

**Write operations:**
```java
@Service
public class UserService {
    
    @Transactional  // Tự động route đến WRITER
    public User create(User user) {
        return userRepository.save(user);
    }
}
```

#### 2.2 Manual routing

```java
@Service
public class UserService {
    
    public void complexOperation() {
        // Force READER
        DatabaseRuntimeContext.setDatabaseType(DatabaseType.READER);
        List<User> users = userRepository.findAll();
        
        // Switch to WRITER
        DatabaseRuntimeContext.setDatabaseType(DatabaseType.WRITER);
        userRepository.saveAll(users);
        
        // Reset
        DatabaseRuntimeContext.clear();
    }
}
```

**Lưu ý:** Luôn gọi `DatabaseRuntimeContext.clear()` trong `finally` block để tránh memory leak.

---

## 3. Module: core-database-async

### Tính năng
- ✅ Async database operations
- ✅ Custom async configuration

### Cài đặt

**Gradle:**
```gradle
dependencies {
    implementation 'com.example:core-database-async:1.0.0'
}
```

*(Chi tiết về module này cần bổ sung thêm)*

---

## Build & Publish

### Build tất cả modules

```bash
./gradlew clean build
```

### Build module cụ thể

```bash
./gradlew :core-common:build
./gradlew :core-database-sync:build
./gradlew :core-database-async:build
```

### Publish to local Maven repository

```bash
./gradlew publishToMavenLocal
```

### Publish to remote repository

```bash
./gradlew publish
```

---

## Migration từ monolith sang modules

### Trước khi tách module

Khi tất cả code ở cùng project, Spring Boot tự động scan và load tất cả beans.

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Sau khi tách module

Modules sử dụng **Spring Boot Auto-Configuration** để tự động đăng ký beans khi được import.

**Không cần thêm gì vào application code:**
```java
@SpringBootApplication  // Không cần exclude, không cần ComponentScan thêm
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**Chỉ cần cấu hình properties:**
```yaml
# application.yml
jwt:
  privateKey: "..."
  publicKey: "..."
  # ...

database:
  writer:
    url: "..."
  reader:
    url: "..."
```

---

## Best Practices

### 1. Security

- ✅ Luôn dùng RSA keys mạnh (tối thiểu 2048 bits)
- ✅ Không commit private key vào Git (dùng environment variables hoặc secret management)
- ✅ Set JWT expiration phù hợp với use case
- ✅ Implement refresh token rotation

### 2. Database

- ✅ Luôn dùng connection pool (HikariCP)
- ✅ Set timeout phù hợp để tránh connection leak
- ✅ Monitor connection pool metrics
- ✅ Dùng `@Transactional(readOnly = true)` cho read operations để tận dụng read replica

### 3. Async

- ✅ Chỉ enable async khi thực sự cần (vì tốn resources)
- ✅ Set pool size phù hợp với load
- ✅ Monitor thread pool stats (auto log mỗi 10s khi enable)
- ✅ Handle exceptions trong async methods

### 4. Cache

- ✅ Dùng `InMemoryJwtCache` cho development/small apps
- ✅ Dùng `RedisJwtCache` cho production/distributed apps (cần implement)
- ✅ Set TTL phù hợp để tránh stale data

---

## Troubleshooting

Gặp lỗi khi sử dụng Core modules? Xem file [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) để biết các lỗi thường gặp và cách sửa.

**Lỗi phổ biến:**
- Bean not found → Check xem đã có cấu hình properties chưa
- Duplicate beans → Check xem có tạo beans thủ công không (không nên)
- DataSource error → Check database properties và connection

---

## Examples

### Ví dụ hoàn chỉnh: REST API with JWT Authentication

**build.gradle:**
```gradle
dependencies {
    implementation 'com.example:core-common:1.0.0'
    implementation 'com.example:core-database-sync:1.0.0'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
}
```

**application.yml:**
```yaml
spring:
  application:
    name: my-app

jwt:
  privateKey: ${JWT_PRIVATE_KEY}
  publicKey: ${JWT_PUBLIC_KEY}
  jwtAccessTokenExpirationS: 3600
  jwtRefreshExpirationS: 2592000
  issuer: "my-app"

database:
  writer:
    url: ${DB_WRITER_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  reader:
    url: ${DB_READER_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

security:
  permitAll:
    - /api/v1/auth/**
    - /api/v1/public/**
```

**Controller:**
```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.create(user));
    }
}
```

**Service:**
```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)  // Auto route to READER
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional  // Auto route to WRITER
    public User create(User user) {
        return userRepository.save(user);
    }
}
```

---

## FAQ

### Q: Tại sao phải dùng `database.*` thay vì `spring.datasource.*`?
**A:** Core module tự quản lý DataSource với read/write splitting, không dùng DataSource auto-configuration của Spring Boot.

### Q: Có thể dùng H2 embedded database không?
**A:** Có, nhưng không có lợi ích gì từ read/write splitting. Nên dùng cho testing only.

### Q: Async có enable mặc định không?
**A:** Không. Phải set `core.async.enable: true` để sử dụng.

### Q: Có thể customize SecurityFilterChain không?
**A:** Có. Tạo bean `SecurityFilterChain` trong project sẽ override configuration mặc định.

### Q: RedisJwtCache đã implement chưa?
**A:** Chưa. Hiện tại chỉ có skeleton. Cần implement logic connect Redis và CRUD operations.

---

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

[Specify your license here]

---

## Changelog

### Version 1.0.0 (2025-10-23)
- ✅ Initial release
- ✅ Core-common: JWT, Security, Async, Cache
- ✅ Core-database-sync: Routing DataSource
- ✅ Auto-configuration support

---

## Support

- 📧 Email: [your-email@example.com]
- 🐛 Issues: [GitHub Issues](https://github.com/your-repo/issues)
- 📖 Docs: [Wiki](https://github.com/your-repo/wiki)

---

**Happy Coding! 🚀**

