# Troubleshooting - Tổng hợp lỗi khi tách module Core

## Tổng quan
Document này tổng hợp tất cả các lỗi gặp phải khi tách module Core thành các module độc lập và cách giải quyết.

**Nguyên nhân chính:** Khi tách code thành module JAR dependency, Spring Boot **KHÔNG** tự động scan và load các `@Configuration`, `@Component` beans từ JAR như khi code ở cùng project.

**Giải pháp chung:** Sử dụng Spring Boot Auto-Configuration mechanism với file `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

---

## Lỗi 1: DataSource URL not specified

### Mô tả lỗi
```
Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.

Reason: Failed to determine a suitable driver class
```

### Nguyên nhân
- Module `core-database-sync` tự định nghĩa DataSource với properties `database.*` (thay vì `spring.datasource.*`)
- Spring Boot auto-configuration vẫn cố gắng tạo DataSource mặc định và yêu cầu `spring.datasource.url`
- `DataSourceConfig` có `@Configuration` nhưng không được load khi module được import

### Cách sửa
**Bước 1:** Tạo file auto-configuration imports
```
File: core-database-sync/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports

Nội dung:
com.core.syncDatabaseConfig.SyncDatabaseAutoConfiguration
```

**Bước 2:** Tạo `SyncDatabaseAutoConfiguration`
```java
@AutoConfiguration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@ConditionalOnClass(DataSource.class)
@Import(DataSourceConfig.class)
public class SyncDatabaseAutoConfiguration {
}
```

**Bước 3:** Cập nhật `DataSourceConfig`
```java
@Configuration
@Slf4j
@EnableConfigurationProperties(DatabaseProperties.class)
public class DataSourceConfig {
    
    @Bean
    @Primary  // Quan trọng: đánh dấu primary để override auto-configured DataSource
    public RoutingDataSource dataSource() {
        // ...
    }
}
```

**Bước 4:** Loại bỏ `@EnableAutoConfiguration(exclude = ...)` từ `DataSourceConfig` (không còn cần thiết)

---

## Lỗi 2: JWTTokenService bean not found

### Mô tả lỗi
```
Parameter 1 of constructor in tech.outsource.service.auth.AuthUseCaseService required a bean of type 'com.example.core.security.services.JWTTokenService' that could not be found.
```

### Nguyên nhân
- `JWTTokenService` có annotation `@Configuration` nhưng không được Spring Boot tự động load từ JAR
- Module `core-common` chưa có auto-configuration để đăng ký beans

### Cách sửa
**Bước 1:** Tạo file auto-configuration imports
```
File: core-common/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports

Nội dung:
com.example.core.security.SecurityAutoConfiguration
```

**Bước 2:** Tạo `SecurityAutoConfiguration`
```java
@AutoConfiguration
@ConditionalOnClass(JwtDecoder.class)
@EnableConfigurationProperties({JwtProperties.class, SecurityProperties.class})
@ComponentScan(basePackages = "com.example.core")
@Import({
    JWTTokenService.class,
    SecurityConfiguration.class
})
public class SecurityAutoConfiguration {
}
```

---

## Lỗi 3: IJwtCache bean not found

### Mô tả lỗi
```
Parameter 3 of constructor in tech.outsource.service.auth.AuthUseCaseService required a bean of type 'com.example.core.cache.IJwtCache' that could not be found.
```

### Nguyên nhân
- `InMemoryJwtCache` và `RedisJwtCache` có `@Component` nhưng không được scan
- `SecurityAutoConfiguration` chưa scan package `com.example.core.cache`

### Cách sửa
**Bước 1:** Mở rộng `@ComponentScan` trong `SecurityAutoConfiguration`
```java
@ComponentScan(basePackages = "com.example.core")  // Scan toàn bộ package thay vì từng package con
```

**Bước 2:** Thêm `@Primary` cho implementation mặc định
```java
@Primary
@Component("inMemoryJwtCache")
public class InMemoryJwtCache implements IJwtCache {
    // ...
}
```

**Lý do:** Có 2 implementations (`InMemoryJwtCache`, `RedisJwtCache`) nên cần chỉ định bean nào là mặc định.

---

## Lỗi 4: Duplicate JwtProperties bean

### Mô tả lỗi
```
Parameter 0 of constructor in JWTTokenService required a single bean, but 2 were found:
    - jwtProperties: defined in URL [jar:...]
    - jwt-com.example.core.security.models.JwtProperties: defined in unknown location
```

### Nguyên nhân
- `JwtProperties` có `@Component` annotation
- `SecurityAutoConfiguration` có `@EnableConfigurationProperties({JwtProperties.class})`
- Kết quả: 2 beans được tạo (một từ component scan, một từ EnableConfigurationProperties)

### Cách sửa
Loại bỏ `@Component` từ `JwtProperties`:
```java
// TRƯỚC
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties { }

// SAU
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties { }
```

**Nguyên tắc:** `@ConfigurationProperties` classes nên được đăng ký qua `@EnableConfigurationProperties`, KHÔNG dùng `@Component`.

---

## Lỗi 5: Duplicate CustomAuthenticationEntryPoint, CustomAccessDeniedHandler

### Mô tả lỗi
Tương tự lỗi 4 - có 2 beans cùng type được tạo.

### Nguyên nhân
- Classes có `@Component` annotation
- `SecurityAutoConfiguration` ban đầu tạo thêm các beans này bằng `@Bean` methods
- Kết quả: Duplicate beans

### Cách sửa
Loại bỏ các `@Bean` methods từ `SecurityAutoConfiguration` vì `@ComponentScan` đã tự động đăng ký:

```java
// TRƯỚC
@AutoConfiguration
@ComponentScan(basePackages = "com.example.core")
public class SecurityAutoConfiguration {
    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
    // ...
}

// SAU
@AutoConfiguration
@ComponentScan(basePackages = "com.example.core")
public class SecurityAutoConfiguration {
    // Beans tự động đăng ký qua @ComponentScan
}
```

**Nguyên tắc:** Không tạo `@Bean` cho classes đã có `@Component` khi dùng `@ComponentScan`.

---

## Lỗi 6: ThreadPoolMonitor - Multiple TaskExecutor beans

### Mô tả lỗi
```
Parameter 0 of constructor in ThreadPoolMonitor required a single bean, but 2 were found:
    - applicationTaskExecutor: defined by Spring Boot auto-configuration
    - customTaskExecutor: defined by AsyncAutoConfiguration
```

### Nguyên nhân
- Spring Boot tự động tạo `applicationTaskExecutor`
- `AsyncAutoConfiguration` tạo `customTaskExecutor`
- `ThreadPoolMonitor` constructor không biết inject bean nào

### Cách sửa ban đầu (SAI)
```java
// SAI - Lombok không copy @Qualifier vào constructor
@RequiredArgsConstructor
public class ThreadPoolMonitor {
    @Qualifier("customTaskExecutor")
    private final ThreadPoolTaskExecutor customTaskExecutor;
}
```

### Cách sửa đúng
Tạo constructor thủ công với `@Qualifier` trên parameter:
```java
@Slf4j
@Component
@ConditionalOnBean(name = "customTaskExecutor")
public class ThreadPoolMonitor {
    private final ThreadPoolTaskExecutor customTaskExecutor;

    public ThreadPoolMonitor(@Qualifier("customTaskExecutor") ThreadPoolTaskExecutor customTaskExecutor) {
        this.customTaskExecutor = customTaskExecutor;
    }
    
    // ...
}
```

**Nguyên tắc:** Khi cần `@Qualifier` với Lombok constructor injection, phải tạo constructor thủ công.

---

## Tổng kết kiến thức

### 1. Spring Boot Auto-Configuration cho module JAR

**File cần tạo:**
```
src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

**Nội dung:** Danh sách các `@AutoConfiguration` classes (mỗi class một dòng)
```
com.example.core.security.SecurityAutoConfiguration
com.example.core.database.DatabaseAutoConfiguration
```

### 2. Annotations quan trọng

| Annotation | Mục đích | Dùng khi nào |
|------------|----------|--------------|
| `@AutoConfiguration` | Đánh dấu class là auto-configuration | Class được khai báo trong AutoConfiguration.imports |
| `@EnableConfigurationProperties` | Đăng ký `@ConfigurationProperties` classes | Bind properties từ application.yml |
| `@ComponentScan` | Scan và đăng ký `@Component` beans | Cần scan package trong JAR module |
| `@Import` | Import thêm configuration classes | Import `@Configuration` classes không phải `@Component` |
| `@Primary` | Đánh dấu bean ưu tiên khi có nhiều candidates | Có nhiều beans cùng type |
| `@Qualifier` | Chỉ định bean cụ thể để inject | Cần inject bean theo tên |
| `@ConditionalOnClass` | Chỉ load khi class tồn tại trên classpath | Optional dependencies |
| `@ConditionalOnProperty` | Chỉ load khi property có giá trị cụ thể | Feature toggle |
| `@ConditionalOnBean` | Chỉ load khi bean khác tồn tại | Dependent beans |

### 3. Quy tắc vàng

✅ **NÊN:**
- Dùng `@EnableConfigurationProperties` cho `@ConfigurationProperties` classes
- Dùng `@ComponentScan` cho package chứa nhiều `@Component` beans
- Dùng `@Import` cho các `@Configuration` classes riêng lẻ
- Dùng `@Primary` khi có nhiều implementations cùng interface
- Tạo constructor thủ công khi cần `@Qualifier` với Lombok

❌ **KHÔNG NÊN:**
- Dùng `@Component` cho `@ConfigurationProperties` classes khi đã có `@EnableConfigurationProperties`
- Tạo `@Bean` cho classes đã có `@Component` trong package được scan
- Dùng `@Qualifier` trên field với `@RequiredArgsConstructor`

### 4. Pattern để exclude Spring Boot auto-configuration

**Cách 1:** `@AutoConfigureBefore` (khuyến nghị cho library)
```java
@AutoConfiguration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Import(CustomDataSourceConfig.class)
public class CustomAutoConfiguration {
}
```

**Cách 2:** Provide `@Primary` bean
```java
@Bean
@Primary
public DataSource customDataSource() {
    // Custom implementation
}
```

**Cách 3:** Exclude trong application (dùng bởi consumer project)
```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application { }
```

---

## Checklist khi tạo module Core

- [ ] Tạo file `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- [ ] Tạo `@AutoConfiguration` class chính
- [ ] Thêm `@ComponentScan` cho package beans
- [ ] Thêm `@EnableConfigurationProperties` cho properties classes
- [ ] Loại bỏ `@Component` từ `@ConfigurationProperties` classes
- [ ] Không tạo `@Bean` cho classes đã có `@Component`
- [ ] Thêm `@Primary` cho beans có nhiều implementations
- [ ] Thêm `@Qualifier` khi inject beans cụ thể
- [ ] Thêm `@Conditional*` annotations để control khi nào load beans
- [ ] Test với project thật để đảm bảo không thiếu beans

---

## Tài liệu tham khảo

- [Spring Boot Auto-Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration)
- [Spring Boot 3.x Auto-Configuration Format](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide#auto-configuration-files)
- [Creating Your Own Auto-configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration)

---

**Ngày tạo:** 23/10/2025  
**Version:** 1.0.0

