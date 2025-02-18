# Parking Service Onboarding

## 📌 프로젝트 개요

**Parking Service Onboarding**은 차량 주차 및 정기권 관리 시스템을 위한 API를 제공
차량의 입차/출차, 요금 계산, 정기권 등록/취소 등의 기능을 갖고 있음

---
## ERD





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
Parking Service API 명세서
## 🏷 **1. 주차 관리 API** (`/parking`)

🚗 **차량의 입차/출차 기록을 관리하는 API**

### ✅ **1-1. 입차 기록 등록**

- **URL**: `POST /parking/entry/{vehicleNumber}`
- **설명**: 차량이 주차장에 입차할 때 호출
- **응답 예시**:

```json

{
    "vehicleNumber": "TEST123",
    "entryTime": "2025-02-14T10:00:00"
}

```

---

### ✅ **1-2. 출차 기록 등록**

- **URL**: `POST /parking/exit/{vehicleNumber}`
- **설명**: 차량이 주차장에서 출차할 때 호출
- **응답 예시**:

```json

{
    "vehicleNumber": "TEST123",
    "entryTime": "2025-02-14T10:00:00",
    "exitTime": "2025-02-14T12:00:00",
    "fee": 5000
}

```

---

### ✅ **1-3. 주차 기록 조회**

- **URL**: `GET /parking/{vehicleNumber}`
- **설명**: 차량 번호로 입출차 기록을 조회
- **응답 예시**:

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

## 🏷 **2. 정기권 관리 API** (`/subscription`)

📜 **정기권 등록/취소를 관리하는 API**

### ✅ **2-1. 정기권 등록**

- **URL**: `POST /subscription/register`
- **설명**: 차량 번호와 기간을 입력해 정기권을 등록
- **요청 예시**:

```json

{
    "vehicleNumber": "TEST123",
    "startDate": "2025-02-01",
    "endDate": "2025-03-01"
}

```

- **응답 예시**:

```json

{
    "vehicleNumber": "TEST123",
    "startDate": "2025-02-01",
    "endDate": "2025-03-01"
}

```

---

### ✅ **2-2. 정기권 취소**

- **URL**: `DELETE /subscription/cancel/{vehicleNumber}`
- **설명**: 차량 번호를 이용해 정기권을 취소
- **응답 예시**:

```json

{
    "message": "정기권이 취소되었습니다."
}

```

---

## 🏷 **3. 결제 관리 API** (`/payment`)

💳 **출차 후 요금 결제 및 조회 기능을 제공하는 API**

### ✅ **3-1. 결제 요청**

- **URL**: `POST /payment/process/{vehicleNumber}`
- **설명**: 차량의 출차 기록을 바탕으로 결제를 진행
- **요청 예시**:

```json

{
    "couponCode": "DISCOUNT50"
}

```

- **응답 예시**:

```json

{
    "vehicleNumber": "TEST123",
    "amount": 2500,
    "discountDetails": "쿠폰 할인 적용: DISCOUNT50 (50%)",
    "timestamp": "2025-02-14T12:01:00"
}

```

---

### ✅ **3-2. 결제 내역 조회**

- **URL**: `GET /payment/{paymentId}`
- **설명**: 특정 결제 ID로 결제 내역을 조회
- **응답 예시**:

```json

{
    "vehicleNumber": "TEST123",
    "amount": 5000,
    "timestamp": "2025-02-14T12:01:00"
}

```

---

### ✅ **3-3. 모든 결제 내역 조회**

- **URL**: `GET /payment/all`
- **설명**: 전체 결제 내역을 조회
- **응답 예시**:

```json

{
    "payments": [
        {
            "vehicleNumber": "TEST123",
            "amount": 5000,
            "timestamp": "2025-02-14T12:01:00"
        },
        {
            "vehicleNumber": "TEST456",
            "amount": 3000,
            "timestamp": "2025-02-14T14:20:00"
        }
    ]
}

```

---

## 🏷 **4. 비동기 결제 정산 & 영수증 발송 API** (`/receipt`)

📧 **결제 완료 시 이벤트 기반으로 자동 실행**

### ✅ **4-1. 결제 완료 후 영수증 발송 이벤트 발생**

- **이벤트 트리거**: `PaymentCompletedEvent`
- **자동 실행 API**: `ReceiptService.sendReceiptEmail()`
- **요청 데이터 (이벤트 기반)**

```json

{
    "vehicleNumber": "TEST123",
    "amount": 2500,
    "timestamp": "2025-02-14T12:01:00"
}

```

- **Mock API 응답**

```json

{
    "message": "비동기 영수증 이메일 발송 완료 (Mock API 호출)"
}

```

---

## 🏷 **5. 요금 정책 관리 API** (`/pricing-policy`)

⚙ **관리자가 동적으로 요금 정책을 설정할 수 있는 API**

### ✅ **5-1. 요금 정책 조회**

- **URL**: `GET /pricing-policy`
- **설명**: 현재 적용된 요금 정책을 조회
- **응답 예시**:

```json

{
    "baseFee": 1000,
    "extraFeePer10Min": 500,
    "dailyMaxFee": 15000,
    "maxDaysCharged": 3,
    "nightDiscount": 0.2,
    "weekendDiscount": 0.1
}

```

---

### ✅ **5-2. 요금 정책 변경**

- **URL**: `PUT /pricing-policy`
- **설명**: 관리자가 요금 정책을 변경할 수 있음
- **요청 예시**:

```json

{
    "baseFee": 1200,
    "extraFeePer10Min": 600,
    "dailyMaxFee": 18000,
    "maxDaysCharged": 5,
    "nightDiscount": 0.25,
    "weekendDiscount": 0.15
}

```

- **응답 예시**:

```json

{
    "oldPolicy": {
        "baseFee": 1000,
        "extraFeePer10Min": 500,
        "dailyMaxFee": 15000,
        "nightDiscount": 0.2,
        "weekendDiscount": 0.1
    },
    "newPolicy": {
        "baseFee": 1200,
        "extraFeePer10Min": 600,
        "dailyMaxFee": 18000,
        "nightDiscount": 0.25,
        "weekendDiscount": 0.15
    }
}

```

---





🛠 테스트 API 명세서
---
## ✅ **1. 주차 관리 테스트** (`/parking`)

📌 **주차 입출차 및 기록 조회 테스트**

### 🔹 **1-1. 입차 테스트**

- **테스트명**: `testRegisterEntry()`
- **설명**: 차량이 정상적으로 입차되는지 테스트
- **입력값**:
    - `vehicleNumber`: `"TEST123"`
- **검증 내용**:
    - 입차 시간이 정상적으로 저장되는지 확인
    - 저장된 차량 번호가 입력값과 일치하는지 확인

---

### 🔹 **1-2. 출차 테스트**

- **테스트명**: `testRegisterExit()`
- **설명**: 차량이 정상적으로 출차되고 요금이 계산되는지 테스트
- **입력값**:
    - `vehicleNumber`: `"TEST123"`
- **검증 내용**:
    - 출차 시간이 정상적으로 저장되는지 확인
    - 요금이 0보다 큰 값으로 계산되는지 확인

---

### 🔹 **1-3. 주차 기록 조회 테스트**

- **테스트명**: `testGetParkingRecords()`
- **설명**: 특정 차량의 입출차 기록이 정상적으로 조회되는지 테스트
- **입력값**:
    - `vehicleNumber`: `"TEST123"`
- **검증 내용**:
    - 반환된 데이터 리스트가 비어있지 않은지 확인
    - 입차, 출차 시간 및 요금 정보가 올바르게 포함되어 있는지 확인

---

## ✅ **2. 정기권 관리 테스트** (`/subscription`)

📌 **정기권 등록 및 취소 테스트**

### 🔹 **2-1. 정기권 등록 테스트**

- **테스트명**: `testRegisterSubscription()`
- **설명**: 차량에 정기권이 정상적으로 등록되는지 테스트
- **입력값**:
    - `vehicleNumber`: `"TEST123"`
    - `startDate`: `"2025-02-01"`
    - `endDate`: `"2025-03-01"`
- **검증 내용**:
    - 정기권 정보가 올바르게 저장되었는지 확인
    - 시작일과 종료일이 요청값과 일치하는지 확인

---

### 🔹 **2-2. 정기권 취소 테스트**

- **테스트명**: `testCancelSubscription()`
- **설명**: 차량 정기권이 정상적으로 취소되는지 테스트
- **입력값**:
    - `vehicleNumber`: `"TEST123"`
- **검증 내용**:
    - 해당 차량의 정기권이 데이터베이스에서 삭제되었는지 확인

---

## ✅ **3. 결제 관리 테스트** (`/payment`)

📌 **출차 후 결제 및 내역 조회 테스트**

### 🔹 **3-1. 결제 요청 테스트**

- **테스트명**: `testProcessPayment()`
- **설명**: 차량 출차 후 요금이 정상적으로 결제되는지 테스트
- **입력값**:
    - `vehicleNumber`: `"TEST123"`
    - `couponCode`: `"DISCOUNT50"`
- **검증 내용**:
    - 결제 내역이 정상적으로 생성되었는지 확인
    - 할인 적용 후 최종 결제 금액이 올바르게 계산되었는지 확인

---

### 🔹 **3-2. 결제 내역 조회 테스트**

- **테스트명**: `testGetPaymentById()`
- **설명**: 특정 결제 ID로 결제 내역을 조회하는 테스트
- **입력값**:
    - `paymentId`: `"1"`
- **검증 내용**:
    - 반환된 결제 내역이 요청한 결제 ID와 일치하는지 확인
    - 결제 금액과 차량 번호가 올바르게 포함되었는지 확인

---

## ✅ **4. 비동기 결제 정산 & 영수증 발송 테스트** (`/receipt`)

📌 **결제 완료 후 이벤트 기반 비동기 영수증 발송 테스트**

### 🔹 **4-1. 영수증 발송 테스트**

- **테스트명**: `testSendReceiptEmail()`
- **설명**: 결제 완료 후 비동기 이벤트가 정상적으로 처리되는지 테스트
- **입력값**:
    - `vehicleNumber`: `"TEST123"`
    - `amount`: `2500`
    - `timestamp`: `"2025-02-14T12:01:00"`
- **검증 내용**:
    - Mock API를 활용해 이메일 발송이 정상적으로 수행되는지 확인
    - 로그 출력 결과를 확인하여 이메일 전송 완료 여부 체크

---

## ✅ **5. 요금 정책 관리 테스트** (`/pricing-policy`)

📌 **관리자가 요금 정책을 변경할 수 있는지 테스트**

### 🔹 **5-1. 요금 정책 조회 테스트**

- **테스트명**: `testGetPricingPolicy()`
- **설명**: 현재 적용된 요금 정책을 올바르게 조회하는지 테스트
- **검증 내용**:
    - 기본 요금, 추가 요금, 최대 요금 등 정책 값들이 정확한지 확인

---

### 🔹 **5-2. 요금 정책 변경 테스트**

- **테스트명**: `testUpdatePricingPolicy()`
- **설명**: 관리자가 요금 정책을 변경할 수 있는지 테스트
- **입력값**:

```json

{
    "baseFee": 1200,
    "extraFeePer10Min": 600,
    "dailyMaxFee": 18000,
    "maxDaysCharged": 5,
    "nightDiscount": 0.25,
    "weekendDiscount": 0.15
}

```

- **검증 내용**:
    - 정책 변경 후 `GET /pricing-policy` 호출 시 업데이트된 값이 반영되는지 확인
    - 이전 정책과 변경된 정책을 비교하여 정상적으로 변경되었는지 체크

---

## ✅ **6. End-to-End 테스트** (`E2E`)

📌 **모든 기능이 실제 프로세스에서 정상적으로 동작하는지 확인하는 통합 테스트**

### 🔹 **6-1. 전체 프로세스 테스트**

- **테스트명**: `testFullParkingProcess()`
- **설명**: 차량 입차 → 출차 → 결제 → 영수증 발송 → 정기권 등록까지 전체 프로세스를 검증
- **검증 내용**:
    - 입차 기록이 정상적으로 생성되는지 확인
    - 출차 시 요금이 계산되는지 확인
    - 결제가 정상적으로 처리되는지 확인
    - 이벤트 기반 영수증 발송이 정상적으로 수행되는지 확인
    - 정기권이 정상적으로 등록되는지 확인





---

## 📌 프로젝트 구조
