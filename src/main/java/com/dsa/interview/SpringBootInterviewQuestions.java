package com.dsa.interview;

import java.util.*;

/**
 * Spring Boot Interview Questions - Code Examples & Concepts
 * 
 * Covers: Dependency Injection, IOC, Bean Lifecycle, Annotations,
 * REST APIs, Spring Data JPA, Transaction Management, AOP,
 * Spring Security, Actuator, Profiles, Exception Handling,
 * Caching, Scheduling, Validation, Testing
 */
public class SpringBootInterviewQuestions {

    // =============================================
    // 1. DEPENDENCY INJECTION & IOC
    // =============================================

    /**
     * Q1: What is Dependency Injection? Types?
     * 
     * DI is a design pattern where objects receive their dependencies from
     * an external source (Spring Container) rather than creating them internally.
     * 
     * Types:
     * 1. Field Injection - @Autowired on field
     * 2. Setter Injection - @Autowired on setter
     * 3. Constructor Injection - @Autowired on constructor (RECOMMENDED)
     * 
     * Why Constructor Injection is preferred:
     * - Immutability (final fields)
     * - Required dependencies are guaranteed
     * - Easier testing (no reflection needed)
     * - Circular dependency detection at startup
     */

    // Field Injection (NOT recommended)
    /*
    @Service
    public class UserService {
        @Autowired
        private UserRepository userRepository; // Can't be final, hard to test
    }
    */

    // Constructor Injection (RECOMMENDED)
    /*
    @Service
    public class UserService {
        private final UserRepository userRepository;

        // @Autowired optional for single constructor (Spring 4.3+)
        public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }
    }
    */

    /**
     * Q2: @Component vs @Service vs @Repository vs @Controller
     * 
     * @Component - Generic stereotype for any Spring-managed bean
     * @Service - Indicates business service layer (adds service semantics)
     * @Repository - Indicates DAO/repository layer (adds persistence exception translation)
     * @Controller - Indicates web controller (adds request handling)
     * 
     * All are specializations of @Component, all enable component scanning.
     * The distinction is semantic and allows for aspect-oriented additions.
     */

    /**
     * Q3: Bean Scopes
     * 
     * 1. singleton (default) - One instance per Spring container
     * 2. prototype - New instance every time requested
     * 3. request - One instance per HTTP request (web-aware)
     * 4. session - One instance per HTTP session (web-aware)
     * 5. application - One instance per ServletContext (web-aware)
     * 6. websocket - One instance per WebSocket (web-aware)
     * 
     * @Scope("prototype")
     * @Component
     * public class PrototypeBean { }
     */

    /**
     * Q4: Bean Lifecycle Callbacks
     * 
     * Order: Constructor → @PostConstruct → afterPropertiesSet() → init-method
     *        → Bean is ready → @PreDestroy → destroy() → destroy-method
     * 
     * @PostConstruct - After dependencies injected, before bean is used
     * @PreDestroy - Before bean is destroyed
     * InitializingBean.afterPropertiesSet() - Alternative to @PostConstruct
     * DisposableBean.destroy() - Alternative to @PreDestroy
     */

    static class BeanLifecycleDemo {
        public BeanLifecycleDemo() {
            System.out.println("1. Constructor called");
        }

        // @PostConstruct
        public void init() {
            System.out.println("2. @PostConstruct / afterPropertiesSet()");
        }

        // @PreDestroy
        public void cleanup() {
            System.out.println("3. @PreDestroy / destroy()");
        }
    }

    // =============================================
    // 2. SPRING CONFIGURATION
    // =============================================

    /**
     * Q5: Java-based Configuration (@Configuration + @Bean)
     * 
     * @Configuration - Indicates class declares @Bean methods
     * @Bean - Declares a bean managed by Spring container
     */
    /*
    @Configuration
    public class AppConfig {
        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:3306/db")
                    .username("user")
                    .password("pass")
                    .build();
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }
    }
    */

    /**
     * Q6: @PropertySource and @Value
     * 
     * Externalize configuration to properties files
     */
    /*
    @Configuration
    @PropertySource("classpath:application.properties")
    public class AppConfig {
        @Value("${app.name:DefaultApp}")
        private String appName;

        @Value("${app.version:1.0.0}")
        private String appVersion;

        @Value("#{systemProperties['user.home']}")
        private String userHome;
    }
    */

    /**
     * Q7: @ConfigurationProperties (Type-safe configuration)
     * 
     * Binds external properties to structured Java objects
     */
    /*
    @ConfigurationProperties(prefix = "app.datasource")
    public class DataSourceProperties {
        private String url;
        private String username;
        private String password;
        private int maxPoolSize = 10;

        // getters and setters
    }

    // application.yml:
    // app:
    //   datasource:
    //     url: jdbc:mysql://localhost:3306/db
    //     username: user
    //     password: pass
    //     maxPoolSize: 20
    */

    // =============================================
    // 3. REST API
    // =============================================

    /**
     * Q8: REST Controller with CRUD operations
     */
    /*
    @RestController
    @RequestMapping("/api/users")
    public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
            this.userService = userService;
        }

        @GetMapping
        public ResponseEntity<List<User>> getAllUsers() {
            return ResponseEntity.ok(userService.findAll());
        }

        @GetMapping("/{id}")
        public ResponseEntity<User> getUserById(@PathVariable Long id) {
            return userService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
            User created = userService.save(user);
            return ResponseEntity.created(
                    URI.create("/api/users/" + created.getId())).body(created);
        }

        @PutMapping("/{id}")
        public ResponseEntity<User> updateUser(@PathVariable Long id,
                                                @Valid @RequestBody User user) {
            return ResponseEntity.ok(userService.update(id, user));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }
    */

    /**
     * Q9: HTTP Status Codes in REST
     * 
     * 200 OK - Successful GET, PUT, PATCH
     * 201 Created - Successful POST (resource created)
     * 204 No Content - Successful DELETE
     * 400 Bad Request - Validation errors
     * 401 Unauthorized - Authentication required
     * 403 Forbidden - Insufficient permissions
     * 404 Not Found - Resource doesn't exist
     * 409 Conflict - Duplicate resource, version conflict
     * 422 Unprocessable Entity - Business validation failure
     * 500 Internal Server Error - Unexpected server error
     */

    /**
     * Q10: @ExceptionHandler and @ControllerAdvice
     * 
     * Global exception handling
     */
    /*
    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
            return new ErrorResponse("NOT_FOUND", ex.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
            List<String> errors = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ErrorResponse("VALIDATION_ERROR", errors.toString());
        }

        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ErrorResponse handleGeneric(Exception ex) {
            return new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        }
    }
    */

    // =============================================
    // 4. SPRING DATA JPA
    // =============================================

    /**
     * Q11: JPA Entity Relationships
     * 
     * @OneToOne - One entity has one related entity
     * @OneToMany / @ManyToOne - One entity has many related entities
     * @ManyToMany - Many entities relate to many entities
     * 
     * Cascade types: ALL, PERSIST, MERGE, REMOVE, REFRESH, DETACH
     * Fetch types: LAZY (default for collections), EAGER (default for single)
     */
    /*
    @Entity
    @Table(name = "users")
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String email;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
                   fetch = FetchType.LAZY, orphanRemoval = true)
        private List<Order> orders = new ArrayList<>();

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "address_id")
        private Address address;
    }
    */

    /**
     * Q12: Spring Data JPA Repository Hierarchy
     * 
     * Repository<T, ID> (marker interface)
     *   └── CrudRepository<T, ID> (CRUD operations)
     *         └── PagingAndSortingRepository<T, ID> (pagination + sorting)
     *               └── JpaRepository<T, ID> (JPA-specific + flush, batch)
     * 
     * Query Methods:
     * - findBy{FieldName} - e.g., findByEmail(String email)
     * - findBy{FieldName}Containing - LIKE query
     * - findBy{FieldName}Between - range query
     * - findBy{FieldName}GreaterThan - comparison
     * - countBy{FieldName} - count results
     * - deleteBy{FieldName} - delete results
     * - existsBy{FieldName} - check existence
     * 
     * @Query - Custom JPQL or native SQL
     */
    /*
    public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);

        List<User> findByLastNameContainingIgnoreCase(String lastName);

        @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
        List<User> findByEmailDomain(@Param("domain") String domain);

        @Query(value = "SELECT * FROM users WHERE active = true",
               nativeQuery = true)
        List<User> findAllActiveUsers();

        @Modifying
        @Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")
        int deactivateInactiveUsers(@Param("date") LocalDate date);
    }
    */

    /**
     * Q13: N+1 Problem and Solutions
     * 
     * Problem: When fetching entities with LAZY relationships, each entity
     * triggers an additional query for its related entities.
     * 
     * Solutions:
     * 1. JOIN FETCH in JPQL
     * 2. @EntityGraph
     * 3. @BatchSize
     * 4. Criteria API with fetch joins
     */
    /*
    // Solution 1: JOIN FETCH
    @Query("SELECT u FROM User u JOIN FETCH u.orders WHERE u.id = :id")
    Optional<User> findByIdWithOrders(@Param("id") Long id);

    // Solution 2: @EntityGraph
    @EntityGraph(attributePaths = {"orders", "address"})
    @Query("SELECT u FROM User u")
    List<User> findAllWithRelations();
    */

    // =============================================
    // 5. TRANSACTION MANAGEMENT
    // =============================================

    /**
     * Q14: @Transactional - Propagation and Isolation
     * 
     * Propagation:
     * REQUIRED (default) - Join existing tx or create new
     * REQUIRES_NEW - Suspend existing tx, create new
     * NESTED - Execute within nested transaction
     * MANDATORY - Must have existing tx, throw if none
     * SUPPORTS - Join if exists, execute non-transactional if not
     * NOT_SUPPORTED - Suspend existing tx, execute non-transactional
     * NEVER - Throw if existing tx exists
     * 
     * Isolation:
     * READ_UNCOMMITTED - Dirty reads possible
     * READ_COMMITTED (default) - No dirty reads
     * REPEATABLE_READ - No dirty/non-repeatable reads
     * SERIALIZABLE - Highest isolation, lowest concurrency
     */
    /*
    @Service
    @Transactional
    public class OrderService {

        @Transactional(propagation = Propagation.REQUIRED,
                       isolation = Isolation.READ_COMMITTED,
                       rollbackFor = { InsufficientStockException.class },
                       noRollbackFor = { BusinessWarningException.class },
                       timeout = 30,
                       readOnly = false)
        public Order placeOrder(OrderRequest request) {
            // Business logic
            return order;
        }

        @Transactional(readOnly = true)
        public List<Order> getOrdersByUser(Long userId) {
            return orderRepository.findByUserId(userId);
        }
    }
    */

    // =============================================
    // 6. AOP (ASPECT ORIENTED PROGRAMMING)
    // =============================================

    /**
     * Q15: AOP Concepts and Pointcut Expressions
     * 
     * Concepts:
     * - Aspect: Cross-cutting concern (logging, security, tx)
     * - Join Point: Point in execution (method execution)
     * - Advice: Action taken at join point
     * - Pointcut: Expression matching join points
     * - Weaving: Linking aspects with application code
     * 
     * Advice Types:
     * @Before - Before method execution
     * @After - After method execution (finally)
     * @AfterReturning - After successful return
     * @AfterThrowing - After exception thrown
     * @Around - Before and after (most powerful)
     * 
     * Pointcut Expression Examples:
     * execution(* com.example.service.*.*(..)) - All methods in service package
     * within(com.example.controller..*) - All methods in controller subpackages
     * @annotation(org.springframework.web.bind.annotation.GetMapping) - Methods with @GetMapping
     * bean(*Service) - Beans ending with 'Service'
     */
    /*
    @Aspect
    @Component
    public class LoggingAspect {

        @Around("execution(* com.example.service.*.*(..))")
        public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            System.out.println(joinPoint.getSignature() + " executed in " + duration + "ms");
            return result;
        }

        @AfterThrowing(pointcut = "execution(* com.example..*.*(..))",
                       throwing = "ex")
        public void logException(JoinPoint joinPoint, Exception ex) {
            System.out.println("Exception in " + joinPoint.getSignature() + ": " + ex.getMessage());
        }
    }
    */

    // =============================================
    // 7. SPRING SECURITY
    // =============================================

    /**
     * Q16: Spring Security Architecture
     * 
     * Security Filter Chain:
     * Request → SecurityContextPersistenceFilter → UsernamePasswordAuthenticationFilter
     * → AnonymousAuthenticationFilter → ExceptionTranslationFilter → FilterSecurityInterceptor
     * → Controller
     * 
     * Key Components:
     * - SecurityContextHolder: Stores current authentication
     * - Authentication: Principal + credentials + authorities
     * - UserDetailsService: Loads user from database
     * - PasswordEncoder: Encodes/verifies passwords
     * - AccessDecisionManager: Authorizes requests
     */
    /*
    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")
                    .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
            return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
    */

    /**
     * Q17: JWT Authentication Flow
     * 
     * 1. Client sends POST /api/auth/login with credentials
     * 2. Server validates credentials, generates JWT token
     * 3. Client includes JWT in Authorization: Bearer <token> header
     * 4. JwtAuthenticationFilter extracts and validates token
     * 5. SecurityContextHolder.setAuthentication(authenticated)
     * 6. Request proceeds to controller with authenticated user
     * 
     * JWT Structure: Header.Payload.Signature
     * Header: {"alg": "HS256", "typ": "JWT"}
     * Payload: {"sub": "user@example.com", "roles": ["USER"], "exp": 1700000000}
     * Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
     */

    // =============================================
    // 8. SPRING BOOT ACTUATOR
    // =============================================

    /**
     * Q18: Actuator Endpoints
     * 
     * /actuator/health - Application health (liveness + readiness)
     * /actuator/info - Custom application info
     * /actuator/metrics - Application metrics (JVM, CPU, memory)
     * /actuator/env - Environment properties
     * /actuator/beans - All Spring beans
     * /actuator/mappings - Request mappings
     * /actuator/loggers - Logger configuration
     * /actuator/threaddump - Thread dump
     * /actuator/heapdump - Heap dump (for memory analysis)
     * /actuator/prometheus - Prometheus metrics format
     * 
     * Custom Health Indicator:
     */
    /*
    @Component
    public class DatabaseHealthIndicator implements HealthIndicator {
        @Override
        public Health health() {
            try {
                // Check database connectivity
                return Health.up()
                        .withDetail("database", "MySQL")
                        .withDetail("status", "Connected")
                        .build();
            } catch (Exception e) {
                return Health.down(e)
                        .withDetail("database", "MySQL")
                        .build();
            }
        }
    }
    */

    // =============================================
    // 9. SPRING BOOT PROFILES
    // =============================================

    /**
     * Q19: Profiles - Environment-specific configuration
     * 
     * Application properties:
     * - application.properties (default, shared)
     * - application-dev.properties (development)
     * - application-qa.properties (testing)
     * - application-prod.properties (production)
     * 
     * Activate: spring.profiles.active=dev
     * Or: --spring.profiles.active=dev,swagger
     * 
     * @Profile on beans:
     */
    /*
    @Configuration
    @Profile("dev")
    public class DevConfig {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .build();
        }
    }

    @Configuration
    @Profile("prod")
    public class ProdConfig {
        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                    .url("jdbc:mysql://prod-server:3306/db")
                    .build();
        }
    }
    */

    // =============================================
    // 10. CACHING
    // =============================================

    /**
     * Q20: Spring Cache Abstraction
     * 
     * @EnableCaching - Enable caching support
     * @Cacheable - Cache method result (checks cache first)
     * @CachePut - Always execute method, update cache
     * @CacheEvict - Remove entries from cache
     * @Caching - Group multiple cache annotations
     * @CacheConfig - Class-level cache settings
     * 
     * Cache Managers: ConcurrentMapCache, RedisCache, CaffeineCache, EhCache
     */
    /*
    @Service
    @CacheConfig(cacheNames = "users")
    public class UserService {

        @Cacheable(key = "#id", unless = "#result == null")
        public User findById(Long id) {
            return userRepository.findById(id).orElse(null);
        }

        @Cacheable(key = "#email")
        public User findByEmail(String email) {
            return userRepository.findByEmail(email).orElse(null);
        }

        @CachePut(key = "#user.id")
        public User update(User user) {
            return userRepository.save(user);
        }

        @CacheEvict(key = "#id")
        public void delete(Long id) {
            userRepository.deleteById(id);
        }

        @CacheEvict(allEntries = true)
        public void clearCache() {
            // Clears all entries in "users" cache
        }
    }
    */

    // =============================================
    // 11. SCHEDULING
    // =============================================

    /**
     * Q21: @Scheduled - Task Scheduling
     * 
     * @EnableScheduling - Enable scheduled task support
     * 
     * Cron expression: second minute hour day month weekday
     * @Scheduled(cron = "0 0 2 * * ?") - Every day at 2 AM
     * @Scheduled(fixedRate = 60000) - Every 60 seconds (doesn't wait for completion)
     * @Scheduled(fixedDelay = 60000) - 60 seconds after last completion
     * @Scheduled(initialDelay = 10000, fixedRate = 60000) - Initial delay then every 60s
     * 
     * Async execution with @Async:
     * @EnableAsync - Enable async method execution
     * @Async("taskExecutor") - Execute in separate thread
     */
    /*
    @Service
    public class ScheduledTasks {

        @Scheduled(cron = "0 0 2 * * ?")
        public void generateDailyReport() {
            // Runs every day at 2 AM
        }

        @Scheduled(fixedRate = 3600000)
        public void cleanupExpiredSessions() {
            // Runs every hour
        }

        @Async
        @Scheduled(fixedDelay = 5000)
        public void processEmailQueue() {
            // Runs asynchronously every 5 seconds after completion
        }
    }
    */

    // =============================================
    // 12. VALIDATION
    // =============================================

    /**
     * Q22: Bean Validation (@Valid)
     * 
     * Common annotations:
     * @NotNull - Value must not be null
     * @NotEmpty - String/Collection must not be empty
     * @NotBlank - String must not be blank (not null, trimmed length > 0)
     * @Size(min, max) - Size constraints
     * @Min / @Max - Numeric range
     * @Pattern(regexp) - Regex validation
     * @Email - Email format validation
     * @Past / @Future - Date validation
     * @Positive / @Negative - Sign validation
     * 
     * Custom Validator:
     */
    /*
    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = PhoneValidator.class)
    public @interface ValidPhone {
        String message() default "Invalid phone number";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
        @Override
        public boolean isValid(String phone, ConstraintValidatorContext context) {
            return phone != null && phone.matches("\\+?[1-9]\\d{9,14}");
        }
    }

    // Usage:
    public class UserRequest {
        @NotBlank
        private String name;

        @Email
        private String email;

        @ValidPhone
        private String phone;

        @Min(18) @Max(120)
        private int age;
    }
    */

    // =============================================
    // 13. TESTING
    // =============================================

    /**
     * Q23: Testing in Spring Boot
     * 
     * @SpringBootTest - Full application context
     * @WebMvcTest - Web layer only (controllers)
     * @DataJpaTest - JPA layer only (repositories)
     * @JsonTest - JSON serialization
     * @RestClientTest - REST client
     * 
     * Mockito with Spring:
     * @MockBean - Add mock to Spring context
     * @SpyBean - Add spy to Spring context
     * @Captor - Argument captor
     */
    /*
    @WebMvcTest(UserController.class)
    class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @Test
        void shouldReturnUser() throws Exception {
            User user = new User(1L, "John", "john@example.com");
            when(userService.findById(1L)).thenReturn(Optional.of(user));

            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("John"));
        }

        @Test
        void shouldReturn404WhenUserNotFound() throws Exception {
            when(userService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/users/99"))
                    .andExpect(status().isNotFound());
        }
    }
    */

    // =============================================
    // 14. SPRING BOOT INTERNALS
    // =============================================

    /**
     * Q24: Spring Boot Auto-Configuration
     * 
     * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
     * 
     * Auto-configuration works via:
     * 1. spring.factories file in META-INF
     * 2. @Conditional annotations:
     *    - @ConditionalOnClass - Class exists on classpath
     *    - @ConditionalOnMissingBean - Bean not already defined
     *    - @ConditionalOnProperty - Property has specific value
     *    - @ConditionalOnExpression - SpEL expression
     *    - @ConditionalOnWebApplication - Web application context
     * 
     * Example: DataSourceAutoConfiguration
     * - Checks if DataSource.class is on classpath
     * - Checks if DataSource bean is not already defined
     * - Checks if spring.datasource.url property is set
     * - Creates DataSource bean with HikariCP
     */

    /**
     * Q25: Embedded Tomcat vs Reactive (WebFlux)
     * 
     * Spring MVC (Servlet-based):
     * - Thread-per-request model
     * - Blocking I/O
     * - Better for CPU-bound or traditional applications
     * - Uses Tomcat, Jetty, Undertow
     * 
     * Spring WebFlux (Reactive):
     * - Event-loop model
     * - Non-blocking I/O
     * - Better for I/O-bound, streaming, or high-concurrency apps
     * - Uses Netty
     * - Requires reactive libraries (Reactor, R2DBC, MongoDB Reactive)
     * 
     * When to use WebFlux:
     * - High concurrency with few threads
     * - Streaming data (Server-Sent Events)
     * - Long-lived connections (WebSocket)
     * - Microservices with non-blocking I/O
     */

    // =============================================
    // MAIN METHOD
    // =============================================

    public static void main(String[] args) {
        System.out.println("SPRING BOOT INTERVIEW QUESTIONS\n");
        System.out.println("This file contains conceptual code examples for Spring Boot interview questions.\n");
        System.out.println("Topics covered:");
        System.out.println("1. Dependency Injection & IoC (Constructor Injection, @Autowired)");
        System.out.println("2. Stereotypes (@Component, @Service, @Repository, @Controller)");
        System.out.println("3. Bean Scopes (singleton, prototype, request, session)");
        System.out.println("4. Bean Lifecycle (@PostConstruct, @PreDestroy)");
        System.out.println("5. Java Configuration (@Configuration, @Bean)");
        System.out.println("6. External Configuration (@Value, @ConfigurationProperties)");
        System.out.println("7. REST APIs (@RestController, HTTP status codes)");
        System.out.println("8. Exception Handling (@ControllerAdvice, @ExceptionHandler)");
        System.out.println("9. Spring Data JPA (relationships, repositories, N+1 problem)");
        System.out.println("10. Transaction Management (@Transactional propagation & isolation)");
        System.out.println("11. AOP (aspects, pointcuts, advice types)");
        System.out.println("12. Spring Security (filter chain, JWT, OAuth2)");
        System.out.println("13. Actuator (health, metrics, custom indicators)");
        System.out.println("14. Profiles (environment-specific configuration)");
        System.out.println("15. Caching (@Cacheable, @CacheEvict, @CachePut)");
        System.out.println("16. Scheduling (@Scheduled, @Async)");
        System.out.println("17. Validation (@Valid, custom validators)");
        System.out.println("18. Testing (@WebMvcTest, @DataJpaTest, MockMvc)");
        System.out.println("19. Auto-Configuration (@Conditional annotations)");
        System.out.println("20. Spring MVC vs WebFlux (blocking vs reactive)");

        System.out.println("\n================================================");
        System.out.println("Bean Lifecycle Demo");
        System.out.println("================================================\n");

        BeanLifecycleDemo demo = new BeanLifecycleDemo();
        demo.init();
        demo.cleanup();

        System.out.println("\n================================================");
        System.out.println("DEMONSTRATION COMPLETE");
        System.out.println("================================================");
    }
}
