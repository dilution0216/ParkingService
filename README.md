# Parking Service Onboarding

## 📌 프로젝트 개요

**Parking Service Onboarding**은 차량 주차 및 정기권 관리 시스템을 위한 API를 제공
차량의 입차/출차, 요금 계산, 정기권 등록/취소 등의 기능이 있음음

---

## 🚀 프로젝트 설정 및 실행 방법

### 1️⃣ **필수 환경**

- Java 17 이상
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

## 🛠 API 명세 (Swagger 문서)

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

---

## ✅ 테스트 정리

### **📌 Swagger API 문서 검증 테스트**

1. **Swagger UI 정상 로드 확인** (`/swagger-ui/index.html`)
2. **OpenAPI JSON 문서 정상 반환 확인** (`/v3/api-docs`)
3. **Swagger 문서에 특정 API 엔드포인트 포함 확인** (`/subscription/register`, `/subscription/cancel/{vehicleNumber}`)

### **📌 주요 기능 테스트**

1. **주차 기록 테스트**
    - 입차/출차 등록 및 요금 계산 검증
    - 비정상적인 입출차 처리 확인
2. **정기권 관리 테스트**
    - 정기권 등록/취소 정상 동작 확인
    - 중복 등록 방지 및 미존재 차량 취소 검증
3. **예외 처리 테스트**
    - 존재하지 않는 차량 조회 및 처리
    - 잘못된 요청 처리

---

## 📌 프로젝트 구조

```
ParkingServiceOnboarding/
 ├── src/main/java/org/dhicc/parkingserviceonboarding/
 │   ├── controller/    # API 컨트롤러
 │   ├── service/       # 비즈니스 로직
 │   ├── repository/    # JPA Repository
 │   ├── model/        # 엔티티 클래스
 │   ├── config/       # 설정 파일 (Swagger 포함)
 │   └── dto/          # DTO 클래스
 │
 ├── src/test/java/org/dhicc/parkingserviceonboarding/
 │   ├── controller/    # 컨트롤러 테스트
 │   ├── service/       # 서비스 테스트
 │   ├── api/           # Swagger 문서 테스트
 │
 ├── pom.xml           # 프로젝트 의존성 관리
 ├── README.md         # 프로젝트 설명 파일
 └── ...
```

---
