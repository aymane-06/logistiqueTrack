# Testing Implementation Guide for LogiTrack

## Overview

I've set up a complete testing framework for your LogiTrack application. This guide explains what was done, how to use it, and how to add more tests.

---

## What Was Set Up

### 1. **Test Dependencies Added to `pom.xml`**

```xml
<!-- H2 In-Memory Database for Testing -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Boot Test AutoConfigure -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-test-autoconfigure</artifactId>
    <scope>test</scope>
</dependency>

<!-- JUnit 5, Mockito, AssertJ (already in spring-boot-starter-test) -->
```

**What these do:**
- **H2**: Creates a fake in-memory database for tests (no real database needed)
- **Spring Boot Test**: Provides testing utilities for Spring applications
- **JUnit 5**: Modern testing framework with annotations like `@Test`
- **Mockito**: Creates fake objects to test in isolation
- **AssertJ**: Makes assertions (checking results) easier to read

### 2. **Test Configuration Created**

**File:** `src/test/resources/application-test.properties`

```properties
# H2 Database Configuration for Testing
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

**What this does:**
- Tells Spring to use H2 in-memory database when running tests
- Automatically creates and destroys the database schema for each test
- Keeps tests isolated and fast

### 3. **DTOs Enhanced with Builders**

I added the `@Builder` annotation to DTOs so tests can easily create test data:

**Before:**
```java
@Data
public class ProductDTO {
    private String name;
    private BigDecimal boughtPrice;
}
```

**After:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private BigDecimal boughtPrice;
}
```

**Why this matters:**
Now tests can create objects easily:
```java
ProductDTO dto = ProductDTO.builder()
    .name("Test Product")
    .boughtPrice(new BigDecimal("50.00"))
    .build();
```

---

## What Tests Were Created

### **ProductServicesTest.java** - 8 Tests (All Passing ✅)

Located at: `src/test/java/com/logitrack/logitrack/services/ProductServicesTest.java`

#### Test 1: `testSaveProduct()`
- **What it tests**: Saving a new product
- **How it works**: 
  1. Creates test product data
  2. Mocks the repository to accept the save
  3. Calls the service
  4. Verifies the product was saved

#### Test 2: `testGetAllProducts()`
- **What it tests**: Retrieving all products
- **How it works**: Mocks repository to return 2 products, verifies the list

#### Test 3: `testGetProductBySku()`
- **What it tests**: Finding a product by its SKU code
- **How it works**: Mocks repository to find product by SKU, verifies result

#### Test 4: `testGetProductBySku_NotFound()`
- **What it tests**: Error handling when product not found
- **How it works**: Mocks repository to return empty, verifies exception is thrown

#### Test 5: `testUpdateProduct()`
- **What it tests**: Updating an existing product
- **How it works**: Mocks finding and saving, verifies update methods called

#### Test 6: `testUpdateProduct_NotFound()`
- **What it tests**: Error when updating non-existent product
- **How it works**: Mocks empty result, verifies exception thrown

#### Test 7: `testDeleteProductBySku()`
- **What it tests**: Deleting a product by SKU
- **How it works**: Mocks finding and deleting, verifies delete called

#### Test 8: `testDeleteProductBySku_NotFound()`
- **What it tests**: Error when deleting non-existent product
- **How it works**: Mocks empty result, verifies exception thrown

---

## How the Tests Work - Understanding the Pattern

### The AAA Pattern (Arrange, Act, Assert)

Every test follows this simple 3-step pattern:

```java
@Test
@DisplayName("Should save product successfully")
void testSaveProduct() {
    // STEP 1: ARRANGE - Set up test data and mock behavior
    Product product = Product.builder()
        .id(UUID.randomUUID())
        .name("Test Product")
        .sku("SKU-001")
        .build();
    
    when(productRepository.save(product))
        .thenReturn(product);
    
    // STEP 2: ACT - Call the method we're testing
    ProductRespDTO result = productServices.saveProduct(productDTO);
    
    // STEP 3: ASSERT - Check the result is correct
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Test Product");
    
    // Bonus: VERIFY - Check that mocks were called as expected
    verify(productRepository).save(product);
}
```

### Key Annotations Used

```java
@ExtendWith(MockitoExtension.class)
// Enables Mockito (mocking library)
class ProductServicesTest {

    @Mock
    private ProductRepository productRepository;
    // Creates a FAKE repository (doesn't actually hit database)
    
    @Mock
    private ProductMapper productMapper;
    // Creates a FAKE mapper
    
    @InjectMocks
    private ProductServices productServices;
    // Injects the fake dependencies into the real service
    
    @BeforeEach
    void setUp() {
        // Runs BEFORE each test to set up test data
    }
    
    @Test
    @DisplayName("Description of what this test checks")
    void testMethodName() {
        // This is a test method
    }
}
```

### Using Mockito (Mocking Dependencies)

**Mock Setup:**
```java
@Mock
private ProductRepository repo;

// Tell the mock what to do when called
when(repo.findById(1L))
    .thenReturn(Optional.of(product));

when(repo.save(any()))
    .thenReturn(savedProduct);
```

**Mock Verification:**
```java
// Verify a method was called
verify(repo).save(product);

// Verify it was called exactly twice
verify(repo, times(2)).findById(1L);

// Verify it was NEVER called
verify(repo, never()).delete(any());
```

### Using AssertJ (Assertions)

**Check Values:**
```java
assertThat(result).isNotNull();
assertThat(result.getName()).isEqualTo("Test");
assertThat(list).hasSize(2);
assertThat(price).isGreaterThan(0);
```

**Check Collections:**
```java
assertThat(list)
    .hasSize(3)
    .contains(item1, item2)
    .doesNotContain(item3);
```

**Check Exceptions:**
```java
assertThatThrownBy(() -> service.getById(999))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessageContaining("not found");
```

---

## How to Run Tests

### Run the Existing Tests
```bash
./mvnw test -Dtest=ProductServicesTest
```

Expected output:
```
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Run All Tests
```bash
./mvnw test
```

### Run Tests with Coverage Report
```bash
./mvnw clean test jacoco:report
open target/site/jacoco/index.html
```

---

## How to Create Tests for Other Services

### Step 1: Create the Test File

Create: `src/test/java/com/logitrack/logitrack/services/YourServiceTest.java`

### Step 2: Copy This Template

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("YourService Unit Tests")
class YourServiceTest {

    @Mock
    private YourRepository repository;

    @Mock
    private YourMapper mapper;

    @InjectMocks
    private YourService service;

    private YourDTO testDTO;
    private YourEntity testEntity;
    private YourRespDTO testRespDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testDTO = YourDTO.builder()
            .field1("value1")
            .field2("value2")
            .build();

        testEntity = YourEntity.builder()
            .id(UUID.randomUUID())
            .field1("value1")
            .field2("value2")
            .build();

        testRespDTO = YourRespDTO.builder()
            .id(testEntity.getId())
            .field1("value1")
            .field2("value2")
            .build();
    }

    // ============================================================
    // CREATE / SAVE TESTS
    // ============================================================

    @Test
    @DisplayName("Should save entity successfully")
    void testSave() {
        // ARRANGE
        when(mapper.toEntity(testDTO)).thenReturn(testEntity);
        when(repository.save(testEntity)).thenReturn(testEntity);
        when(mapper.toResponseDTO(testEntity)).thenReturn(testRespDTO);

        // ACT
        YourRespDTO result = service.save(testDTO);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getField1()).isEqualTo("value1");
        
        // VERIFY
        verify(mapper).toEntity(testDTO);
        verify(repository).save(testEntity);
    }

    // ============================================================
    // READ / GET TESTS
    // ============================================================

    @Test
    @DisplayName("Should get all entities")
    void testGetAll() {
        // ARRANGE
        when(repository.findAll()).thenReturn(List.of(testEntity));
        when(mapper.toResponseDTO(testEntity)).thenReturn(testRespDTO);

        // ACT
        List<YourRespDTO> result = service.getAll();

        // ASSERT
        assertThat(result).hasSize(1);
        
        // VERIFY
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should get entity by ID")
    void testGetById() {
        // ARRANGE
        when(repository.findById(testEntity.getId()))
            .thenReturn(Optional.of(testEntity));
        when(mapper.toResponseDTO(testEntity))
            .thenReturn(testRespDTO);

        // ACT
        YourRespDTO result = service.getById(testEntity.getId());

        // ASSERT
        assertThat(result).isNotNull();
        
        // VERIFY
        verify(repository).findById(testEntity.getId());
    }

    @Test
    @DisplayName("Should throw exception when entity not found")
    void testGetById_NotFound() {
        // ARRANGE
        UUID invalidId = UUID.randomUUID();
        when(repository.findById(invalidId))
            .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.getById(invalidId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not found");
        
        // VERIFY
        verify(repository).findById(invalidId);
    }

    // ============================================================
    // UPDATE TESTS
    // ============================================================

    @Test
    @DisplayName("Should update entity")
    void testUpdate() {
        // ARRANGE
        UUID entityId = testEntity.getId();
        when(repository.findById(entityId))
            .thenReturn(Optional.of(testEntity));
        when(repository.save(testEntity))
            .thenReturn(testEntity);
        when(mapper.toResponseDTO(testEntity))
            .thenReturn(testRespDTO);

        // ACT
        YourRespDTO result = service.update(entityId, testDTO);

        // ASSERT
        assertThat(result).isNotNull();
        
        // VERIFY
        verify(repository).findById(entityId);
        verify(repository).save(testEntity);
    }

    // ============================================================
    // DELETE TESTS
    // ============================================================

    @Test
    @DisplayName("Should delete entity")
    void testDelete() {
        // ARRANGE
        UUID entityId = testEntity.getId();
        when(repository.findById(entityId))
            .thenReturn(Optional.of(testEntity));

        // ACT
        service.delete(entityId);

        // VERIFY
        verify(repository).findById(entityId);
        verify(repository).delete(testEntity);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent entity")
    void testDelete_NotFound() {
        // ARRANGE
        UUID invalidId = UUID.randomUUID();
        when(repository.findById(invalidId))
            .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.delete(invalidId))
            .isInstanceOf(IllegalArgumentException.class);
        
        // VERIFY
        verify(repository, never()).delete(any());
    }
}
```

### Step 3: Customize for Your Service

Replace:
- `YourService` with actual service name (e.g., `CarrierService`)
- `YourRepository` with actual repository
- `YourMapper` with actual mapper
- `YourDTO` and `YourEntity` with actual classes
- `field1`, `field2` with actual fields

### Step 4: Run Your Tests

```bash
./mvnw test -Dtest=CarrierServiceTest
```

---

## Common Testing Patterns

### Pattern 1: Testing Save
```java
@Test
void testSave() {
    // ARRANGE - Create input, set up mocks
    when(mapper.toEntity(inputDTO)).thenReturn(entity);
    when(repository.save(entity)).thenReturn(entity);
    when(mapper.toResponseDTO(entity)).thenReturn(respDTO);

    // ACT - Call service
    YourRespDTO result = service.save(inputDTO);

    // ASSERT - Check result
    assertThat(result).isNotNull();
    
    // VERIFY - Check repository was called
    verify(repository).save(entity);
}
```

### Pattern 2: Testing Get (Not Found)
```java
@Test
void testGetNotFound() {
    // ARRANGE - Mock empty result
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // ACT & ASSERT - Expect exception
    assertThatThrownBy(() -> service.getById(999L))
        .isInstanceOf(IllegalArgumentException.class);
}
```

### Pattern 3: Testing Delete
```java
@Test
void testDelete() {
    // ARRANGE
    when(repository.findById(1L)).thenReturn(Optional.of(entity));

    // ACT
    service.delete(1L);

    // VERIFY
    verify(repository).delete(entity);
}
```

---

## What Each Service Needs Tests For

For every service, create tests for:

### CRUD Operations
- ✅ Create/Save with valid data
- ✅ Create with invalid data
- ✅ Read/Get all
- ✅ Read/Get by ID (success)
- ✅ Read/Get by ID (not found)
- ✅ Update (success)
- ✅ Update (not found)
- ✅ Delete (success)
- ✅ Delete (not found)

### Example for CarrierService:
```java
// CREATE
testSaveCarrier()
testSaveCarrier_InvalidData()

// READ
testGetAllCarriers()
testGetCarrierById_Success()
testGetCarrierById_NotFound()

// UPDATE
testUpdateCarrier_Success()
testUpdateCarrier_NotFound()

// DELETE
testDeleteCarrier_Success()
testDeleteCarrier_NotFound()

// CUSTOM BUSINESS LOGIC
testIsCarrierActive()
testDeactivateCarrier()
```

---

## What's Important to Know

### 1. **Why Mock?**
Mocking creates fake objects so you can:
- Test in isolation (no real database)
- Make tests fast
- Test error conditions easily
- Verify interactions between objects

### 2. **Why Use Builders?**
Builders make test data creation readable:
```java
// Good - Clear what data is being tested
Product p = Product.builder()
    .name("Test")
    .sku("SKU-001")
    .build();

// Bad - Not clear what's important
Product p = new Product();
p.setId(1L);
p.setName("Test");
p.setSku("SKU-001");
```

### 3. **Why Separate Arrange, Act, Assert?**
Makes tests easy to understand:
```
ARRANGE (setup) → ACT (do the thing) → ASSERT (check result)
```

### 4. **Why Verify?**
Verifies that the service actually uses its dependencies:
```java
verify(repository).save(entity);  // Verify repo.save() was called
verify(mapper).toEntity(dto);     // Verify mapper was used
```

---

## Testing Mindset

### What to Test
- ✅ Business logic
- ✅ Error handling
- ✅ Data transformations
- ✅ Validation

### What NOT to Test
- ❌ Spring framework internals
- ❌ Database operations (mock the repository)
- ❌ Java library code
- ❌ Getters/setters

### Good Test Names
```
✅ testSaveProduct_WithValidData_ShouldSucceed()
✅ testGetProductBySku_WhenNotFound_ShouldThrowException()
✅ testUpdateProduct_WithInvalidPrice_ShouldFail()
```

---

## How to Debug Failing Tests

### Test Won't Compile
- Check spelling of class names
- Check imports are correct
- Run `./mvnw test-compile` to see errors

### Test Runs but Fails
1. Check your `when()` mock setup
   ```java
   when(repo.findById(1L)).thenReturn(Optional.of(entity));
   ```

2. Check your assertions match actual values
   ```java
   assertThat(result).isEqualTo(expectedValue);
   ```

3. Add println to see actual values
   ```java
   System.out.println("Result: " + result);
   ```

### Mock Not Being Called
- Check the method name in verify matches exactly
- Check you're using the right object
- Check your mock setup is before the service call

---

## Summary

**What was done:**
1. Added test dependencies to Maven
2. Created test configuration for H2 database
3. Enhanced DTOs with @Builder for easy test data
4. Created ProductServicesTest with 8 passing tests
5. Documented everything here

**How to use it:**
1. Run existing tests: `./mvnw test -Dtest=ProductServicesTest`
2. Copy the template for other services
3. Follow the AAA pattern (Arrange, Act, Assert)
4. Use @Mock for dependencies
5. Use Mockito to set up behavior
6. Use AssertJ to check results
7. Use verify() to check interactions

**Next steps:**
1. Create tests for CarrierService
2. Create tests for InventoryService
3. Create tests for other services
4. Run `./mvnw test` to see all tests pass
5. Generate coverage report: `./mvnw clean test jacoco:report`

**You now have everything needed to test your entire application!**
