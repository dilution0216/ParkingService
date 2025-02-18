# README #

## 개요 ##
1주차 프로젝트에서 구현한 서비스에 회원가입 및 인증 backend 서비스를 추가로 구현

* 1주차 on-boarding 프로젝트 : https://bitbucket.org/dhicc/sp1-week1/src/master/

---
## 요구사항(필수, 4개 항목, 50점) ##

### 1. 회원가입 API 구현(10점) ###
서비스 회원을 등록할 수 있는 API 구현

* 일반 사용자/관리자 회원가입 API
* 사용자는 자신의 회원정보를 변경 할 수 있어야 함
* 회원 정보의 삭제는 관리자 권한만 가능
* 일반 사용자는 1개의 정기권 차량 정보를 소유할 수 있음(중복 등록 불가)
* 그 외 회원 정보의 세부 데이터에 대해서는 제약을 두지 않음(핸드폰 번호, 주소 등 자유롭게 설계)

### 2. JWT 기반 인증 구현(20점) ###
일반 사용자/관리자가 서비스 로그인 시 JWT 토큰 발급
모든 API 요청 시 유효한 JWT가 필요함

* Spring Security에서 UsernamePasswordAuthenticationFilter 앞에 JWT 필터 적용
* 로그인 성공 시 JWT 생성 및 반환
* 매 요청마다 Authorization 헤더의 JWT를 검증하여 사용자 인증 수행

### 2-1. 사용자 역할(Role) 및 권한 설정 ###
ROLE_USER: 일반 사용자 (입/출차 등록, 주차요금 결제, 정기권 등록 가능)
ROLE_ADMIN: 관리자 (요금 정책 등록/변경, 전체 결제 내역 조회 가능)

### 2-3. 사용자 패스워드 암호화 (BCrypt) ###
사용자 가입 시 패스워드는 BCrypt 로 암호화하여(일방향 해시함수 사용) 저장

* BCryptPasswordEncoder 를 사용하여 비밀번호 저장
* 로그인 시 입력된 비밀번호와 DB의 해시된 비밀번호를 비교
* 패스워드는 최소 8자 이상, 대문자/소문자/숫자/특수문자 포함해야 함

### 3. 정기권 기능 고도화(10점) ###

* 정기권 차량 조회는 로그인한 사용자 본인의 차량만 조회 가능
* 정기권 등록 API는 ROLE_USER(일반 사용자)만 접근 가능, 변경 및 삭제는 ROLE_ADMIN(관리자)만 가능

### 4. 스케줄링 기능 구현(10점) ###
아래 각 상황에 맞는 스케줄러를 구현

* 하루 1회, 사용기간이 만료된 정기권 데이터 자동 삭제 처리
* 7일 이상이 지난 주차요금 데이터(정산 내역) 자동 삭제 처리

---
## 추가 도전 과제 (선택, 추가 점수) ##
### 5. API 요청 로깅(AOP) ###

* Spring AOP를 활용하여 모든(실패, 오류 건 포함) API 호출 로그 기록
* 로그를 Elasticsearch에 저장하여 Kibana 대시보드 시각화(ELK Stack 참고)

---
## 기술 요구사항 ##
* 언어: Java 또는 Kotlin
* 프레임워크: Spring Boot
* DB: PostgreSQL (또는 H2 사용 가능)
* ORM: JPA + Hibernate
* 비동기 이벤트: Spring Event 또는 Kafka (Mock 구현 가능)
* API 문서화: Swagger 또는 Spring REST Docs
* 테스트: JUnit + Mockito (단위 테스트 코드 필수 작성)
* CI/CD: Jenkins Pipeline을 활용한 CI/CD 구축 (추가 점수)

---
## 온보딩 프로젝트 제출 방법 ##
1. Bitbucket 저장소에 최종 코드 업로드 후 링크 제출
2. README.md 파일에 설치 및 실행 방법, 설계 설명 작성
3. Swagger 또는 Postman을 활용하여 API 테스트 가능하도록 문서 제공