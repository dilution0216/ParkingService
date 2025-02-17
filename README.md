# Parking Service Onboarding

## 📌 프로젝트 개요

**Parking Service Onboarding**은 차량 주차 및 정기권 관리 시스템을 위한 API를 제공
차량의 입차/출차, 요금 계산, 정기권 등록/취소 등의 기능을 갖고 있음

---

## 🚀 프로젝트 설정 및 실행 방법

### 1️⃣ **필수 환경**

- Java 17
- Maven
- PostgreSQL (운영 환경), H2 (테스트 환경)

### 2️⃣ **프로젝트 실행 방법**

```
# 프로젝트 빌드
mvn clean package

# 애플리케이션 실행
mvn spring-boot:run
```

### 3️⃣ **테스트 실행**

```
mvn test
```

---

## 🛠 API 명세 (Swagger 문서) - 로컬에서 사용

### Swagger UI 접근 URL:

```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON 문서:

```
http://localhost:8080/v3/api-docs
```

---

## 📌 주요 API 목록

### 1️⃣ **주차 관리 API** (`/parking`)

### 🚗 **입차 기록 등록**

- **URL**: `POST /parking/entry/{vehicleNumber}`
- **설명**: 차량이 주차장에 입차할 때 호출
- **응답 예시**:

```
{
    "vehicleNumber": "TEST123",
    "entryTime": "2025-02-14T10:00:00"
}
```

### 🚙 **출차 기록 등록**

- **URL**: `POST /parking/exit/{vehicleNumber}`
- **설명**: 차량이 주차장에서 출차할 때 호출
- **응답 예시**:

```
{
    "vehicleNumber": "TEST123",
    "entryTime": "2025-02-14T10:00:00",
    "exitTime": "2025-02-14T12:00:00",
    "fee": 5000
}
```

### 📝 **주차 기록 조회**

- **URL**: `GET /parking/{vehicleNumber}`
- **설명**: 차량 번호로 입출차 기록을 조회
- **응답 예시**:

```
{
    "data": [
        {
            "vehicleNumber": "TEST123",
            "entryTime": "2025-02-14T10:00:00",
            "exitTime": "2025-02-14T12:00:00",
            "fee": 5000
        }
    ]
}
```

---

### 2️⃣ **정기권 관리 API** (`/subscription`)

### 🏷 **정기권 등록**

- **URL**: `POST /subscription/register`
- **설명**: 차량 번호와 기간을 입력해 정기권을 등록
- **요청 예시**:

```
{
    "vehicleNumber": "TEST123",
    "startDate": "2025-02-01",
    "endDate": "2025-03-01"
}
```

- **응답 예시**:

```
{
    "vehicleNumber": "TEST123",
    "startDate": "2025-02-01",
    "endDate": "2025-03-01"
}
```

### ❌ **정기권 취소**

- **URL**: `DELETE /subscription/cancel/{vehicleNumber}`
- **설명**: 차량 번호를 이용해 정기권을 취소
- **응답 예시**:

```
{
    "message": "정기권이 취소되었습니다."
}
```
## 3️⃣ **결제 API** (`/payment`)

### 💳 **주차 요금 결제**

- **URL**: `POST /payment/process`
- **설명**: 차량의 주차 요금을 결제
- **요청 예시**:
    
    ```json
    
    {
        "vehicleNumber": "TEST123",
        "amount": 5000,
        "couponCode": "WELCOME10"
    }
    
    ```
    
- **응답 예시**:
    
    ```json
    
    {
        "vehicleNumber": "TEST123",
        "amount": 4500,
        "discountApplied": true,
        "timestamp": "2025-02-14T12:05:00"
    }
    
    ```
    

### 📜 **결제 내역 조회**

- **URL**: `GET /payment/history/{vehicleNumber}`
- **설명**: 특정 차량의 결제 내역을 조회
- **응답 예시**:
    
    ```json
    
    {
        "data": [
            {
                "vehicleNumber": "TEST123",
                "amount": 4500,
                "discountApplied": true,
                "timestamp": "2025-02-14T12:05:00"
            }
        ]
    }
    
    ```
---

## ✅ 테스트 정리

## ✅ **1. 주차 관련 테스트 (ParkingServiceTest)**

📌 **입차 및 출차 로직을 검증하는 테스트 코드**

### **📍입차 테스트 (`testVehicleEntry`)**

- 🚗 새로운 차량이 입차할 때 **정상적으로 기록되는지 검증**
- 동일 차량이 **이미 입차한 상태에서 중복 입차 요청 시 예외 발생하는지 확인**

```java

@Test
void testVehicleEntry() {
    ParkingRecord record = parkingService.processEntry("TEST123");

    assertNotNull(record);
    assertEquals("TEST123", record.getVehicleNumber());
    assertNotNull(record.getEntryTime());
}

```

---

### **📍출차 테스트 (`testVehicleExit`)**

- 🚙 차량이 출차할 때 **정상적으로 출차 시간이 기록되는지 확인**
- 정기권 사용자의 경우 **요금이 0원으로 계산되는지 검증**
- **출차 기록 없이 출차 요청 시 예외 발생하는지 확인**

```java

@Test
void testVehicleExit() {
    parkingService.processEntry("TEST123");

    ParkingRecord record = parkingService.processExit("TEST123");

    assertNotNull(record.getExitTime());
    assertTrue(record.getFee() >= 0);
}

```

---

### **📍요금 계산 테스트 (`testFeeCalculation`)**

- 기본 요금 정책(30분 무료, 이후 10분당 1,000원) 검증
- 정기권 소지 차량의 요금 면제 여부 검증
- 할인 쿠폰 적용 테스트

```java

@Test
void testFeeCalculation() {
    LocalDateTime entry = LocalDateTime.of(2025, 2, 14, 10, 0);
    LocalDateTime exit = LocalDateTime.of(2025, 2, 14, 12, 0);

    int fee = parkingFeeService.calculateFee(entry, exit, false, null);

    assertEquals(9000, fee); // 1시간 30분 -> 9,000원
}

```

---

## ✅ **2. 정기권 관련 테스트 (SubscriptionServiceTest)**

📌 **정기권 등록 및 취소를 검증하는 테스트 코드**

### **📍정기권 등록 테스트 (`testRegisterSubscription`)**

- 🚀 차량 번호와 기간을 입력하면 **정상적으로 등록되는지 확인**
- **이미 등록된 차량이 다시 정기권 등록을 시도할 경우 예외 발생하는지 확인**

```java

@Test
void testRegisterSubscription() {
    SubscriptionDTO dto = new SubscriptionDTO("TEST123", LocalDate.now(), LocalDate.now().plusMonths(1));

    SubscriptionDTO result = subscriptionService.registerSubscription(dto);

    assertNotNull(result);
    assertEquals("TEST123", result.getVehicleNumber());
}

```

---

### **📍정기권 취소 테스트 (`testCancelSubscription`)**

- ❌ 차량 번호로 정기권을 취소할 때 **정상적으로 삭제되는지 검증**
- **존재하지 않는 정기권을 취소하려고 하면 예외 발생하는지 확인**

```java

@Test
void testCancelSubscription() {
    subscriptionService.registerSubscription(new SubscriptionDTO("TEST123", LocalDate.now(), LocalDate.now().plusMonths(1)));

    boolean result = subscriptionService.cancelSubscription("TEST123");

    assertTrue(result);
}

```

---

## ✅ **3. 예외 처리 테스트 (ExceptionHandlingTest)**

📌 **비정상적인 요청에 대한 예외 처리를 검증하는 코드**

### **📍존재하지 않는 차량 출차 요청 (`testExitWithoutEntry`)**

```java

@Test
void testExitWithoutEntry() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        parkingService.processExit("NON_EXISTENT_CAR");
    });

    assertEquals("입차 기록 없음", exception.getMessage());
}

```

---

### **📍정기권이 없는 차량 무료 출차 요청 (`testExitWithoutSubscription`)**

```java

@Test
void testExitWithoutSubscription() {
    parkingService.processEntry("TEST123");

    ParkingRecord record = parkingService.processExit("TEST123");

    assertTrue(record.getFee() > 0); // 정기권이 없으므로 요금이 부과됨
}

```

---

## ✅ **4. Swagger 문서 검증 테스트 (SwaggerApiDocumentationTest)**

📌 **Swagger 문서가 정상적으로 생성되는지 검증**

### **📍Swagger UI 정상 접근 테스트 (`testSwaggerUiLoads`)**

```java

@Test
void testSwaggerUiLoads() {
    RestAssured.given()
            .baseUri("http://localhost:" + port)
            .when().get("/swagger-ui/index.html")
            .then()
            .statusCode(HttpStatus.OK.value());
}

```

---

### **📍OpenAPI JSON 문서 반환 테스트 (`testOpenApiJsonExists`)**

```java

@Test
void testOpenApiJsonExists() {
    RestAssured.given()
            .baseUri("http://localhost:" + port)
            .when().get("/v3/api-docs")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("openapi", equalTo("3.0.1"))
            .body("paths", not(empty()));
}

```

---

## 1️⃣ **할인 쿠폰 컨트롤러 테스트 (`DiscountControllerTest`)**

📌 **할인 쿠폰 적용 및 검증을 위한 테스트**

### **📍1. 할인 쿠폰 등록 테스트 (`testRegisterDiscountCoupon`)**

- 🎟 **쿠폰 코드와 할인율을 입력하면 정상적으로 등록되는지 검증**
- **같은 코드로 중복 등록 시 예외 발생하는지 확인**
- **입력값 예시**:
    
    ```json
    
    {
        "couponCode": "WELCOME10",
        "discountRate": 10
    }
    
    ```
    
- **예상 응답**:
    
    ```json
    
    {
        "couponCode": "WELCOME10",
        "discountRate": 10
    }
    
    ```
    

### **📍2. 할인 쿠폰 적용 테스트 (`testApplyDiscountCoupon`)**

- ✅ **유효한 쿠폰을 적용 시 할인된 금액이 반환되는지 확인**
- ❌ **만료되었거나 존재하지 않는 쿠폰 사용 시 예외 발생하는지 검증**
- **입력값 예시**:
    
    ```json
    
    {
        "couponCode": "WELCOME10",
        "originalAmount": 5000
    }
    
    ```
    
- **예상 응답**:
    
    ```json
    
    {
        "discountedAmount": 4500
    }
    
    ```
    

---

## 2️⃣ **주차 컨트롤러 테스트 (`ParkingControllerTest`)**

📌 **입차/출차 및 주차 기록을 검증하는 테스트**

### **📍1. 입차 테스트 (`testVehicleEntry`)**

- 🚗 **차량이 입차할 때 정상적으로 기록되는지 확인**
- **이미 입차한 차량이 다시 입차 요청 시 예외 발생하는지 검증**
- **입력값 예시**:
    
    ```
    
    POST /parking/entry/TEST123
    
    ```
    
- **예상 응답**:
    
    ```json
    
    {
        "vehicleNumber": "TEST123",
        "entryTime": "2025-02-14T10:00:00"
    }
    
    ```
    

### **📍2. 출차 테스트 (`testVehicleExit`)**

- 🚙 **출차 시 정상적으로 요금이 계산되고 기록되는지 검증**
- **출차 기록 없이 출차 요청할 경우 예외 발생 확인**
- **입력값 예시**:
    
    ```
    
    POST /parking/exit/TEST123
    
    ```
    
- **예상 응답**:
    
    ```json
    
    {
        "vehicleNumber": "TEST123",
        "entryTime": "2025-02-14T10:00:00",
        "exitTime": "2025-02-14T12:00:00",
        "fee": 5000
    }
    
    ```
    

### **📍3. 주차 기록 조회 테스트 (`testParkingHistory`)**

- 📝 **차량의 입출차 기록을 정상적으로 조회할 수 있는지 검증**
- **존재하지 않는 차량 조회 시 빈 배열 반환 확인**
- **입력값 예시**:
    
    ```
    
    GET /parking/TEST123
    
    ```
    
- **예상 응답**:
    
    ```json
    
    {
        "data": [
            {
                "vehicleNumber": "TEST123",
                "entryTime": "2025-02-14T10:00:00",
                "exitTime": "2025-02-14T12:00:00",
                "fee": 5000
            }
        ]
    }
    
    ```
    

---

## 3️⃣ **결제 컨트롤러 테스트 (`PaymentControllerTest`)**

📌 **주차 요금 결제 및 할인 적용 검증**

### **📍1. 결제 요청 테스트 (`testProcessPayment`)**

- 💳 **차량의 주차 요금을 결제할 때 정상적으로 처리되는지 검증**
- **유효하지 않은 차량번호로 결제 시 예외 발생 확인**
- **입력값 예시**:
    
    ```json
    
    {
        "vehicleNumber": "TEST123",
        "amount": 5000,
        "couponCode": "WELCOME10"
    }
    
    ```
    
- **예상 응답**:
    
    ```json
    
    {
        "vehicleNumber": "TEST123",
        "amount": 4500,
        "discountApplied": true,
        "timestamp": "2025-02-14T12:05:00"
    }
    
    ```
    

### **📍2. 결제 내역 조회 테스트 (`testGetPaymentHistory`)**

- 📜 **특정 차량의 결제 내역을 정상적으로 조회할 수 있는지 검증**
- **존재하지 않는 차량 조회 시 빈 배열 반환 확인**
- **입력값 예시**:
    
    ```
    
    GET /payment/history/TEST123
    
    ```
    
- **예상 응답**:
    
    ```json
    
    {
        "data": [
            {
                "vehicleNumber": "TEST123",
                "amount": 4500,
                "discountApplied": true,
                "timestamp": "2025-02-14T12:05:00"
            }
        ]
    }
    
    ```
    

---

## 4️⃣ **정기권 컨트롤러 테스트 (`SubscriptionControllerTest`)**

📌 **정기권 등록/취소 및 상태 확인을 검증**

### **📍1. 정기권 등록 테스트 (`testRegisterSubscription`)**

- 🏷 **차량 번호와 기간을 입력하면 정상적으로 정기권이 등록되는지 검증**
- **이미 정기권이 등록된 차량이 다시 요청할 경우 예외 발생 확인**
- **입력값 예시**:
    
    ```json
    
    {
        "vehicleNumber": "TEST123",
        "startDate": "2025-02-01",
        "endDate": "2025-03-01"
    }
    
    ```
    
- **예상 응답**:
    
    ```json
    
    {
        "vehicleNumber": "TEST123",
        "startDate": "2025-02-01",
        "endDate": "2025-03-01"
    }
    
    ```
    

### **📍2. 정기권 취소 테스트 (`testCancelSubscription`)**

- ❌ **차량 번호를 입력하면 정기권이 정상적으로 삭제되는지 확인**
- **존재하지 않는 차량의 정기권 취소 요청 시 예외 발생 확인**
- **입력값 예시**:
    
    ```
    pgsql
    DELETE /subscription/cancel/TEST123
    
    ```
    
- **예상 응답**:
    
    ```json
    
    {
        "message": "정기권이 취소되었습니다."
    }
    
    ```
    

### **📍3. 정기권 상태 조회 테스트 (`testCheckSubscriptionStatus`)**

- 📆 **차량 번호로 정기권 상태를 조회하면 정상적으로 반환되는지 확인**
- **만료된 정기권 차량이 조회될 경우 만료 상태로 반환 확인**
- **입력값 예시**:
    
    ```
    pgsql
    GET /subscription/status/TEST123
    
    ```
    
- **예상 응답 (정기권 보유 시)**:
    
    ```json
    
    {
        "vehicleNumber": "TEST123",
        "status": "ACTIVE",
        "startDate": "2025-02-01",
        "endDate": "2025-03-01"
    }
    
    ```
    
- **예상 응답 (정기권 없음)**:
    
    ```json
    
    {
        "message": "정기권이 없습니다."
    }
    
    ```
    

---

---

## 📌 프로젝트 구조

```
ParkingServiceOnboarding/
 ├── src/main/java/org/dhicc/parkingserviceonboarding/
 │   ├── controller/    # API 컨트롤러
 │   ├── service/       # 서비스(비즈니스 로직)
 │   ├── repository/    # JPA Repo
 │   ├── model/        # Entty
 │   ├── config/       # 설정 파일 (Swagger 포함)
 │   └── dto/          # DTO
 │
 ├── src/test/java/org/dhicc/parkingserviceonboarding/
 │   ├── controller/    # 컨트롤러 테스트
 │   ├── service/       # 서비스 테스트
 │   ├── api/           # Swagger 문서 테스트
 │
 ├── pom.xml           # 의존성 관리
 ├── README.md         # 프로젝트 설명
```

---
