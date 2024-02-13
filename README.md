# 프로젝트 명
overseas remittance

# 운영 기업
모인(MOIN)

# 기술 스택
- 개발 언어: java 17
- 프레임워크: springboot 3.2.2
- DB: H2
- 유틸: JPA
- 단위 테스트: junit
- 빌드 환경: gradle

# 플랫폼 컨셉
### 주요 서비스
- 해외 송금 서비스 제공(미국, 일본)
### 이용 조건
- 회원 가입한 클라이언트만 송금 가능
### 세부 조건
- 개인 유저: 1일 송금 한도 $1000
- 법인 유저: 1일 송금 한도 $5000

# 백엔드 API
### *제약 사항!*
> - 인증 토큰은 JWT 사용: 로그인시 JWT로 인증 토큰 발급
> - Content-Type: application/json
> - 인증 토큰은 헤더에 담아서 요청
> - 민감 정보는 모두 암호화 처리 후 저장: 회원 가입시 비밀번호, 주민등록번호 or 사업자번호 암호화 -> bcrypt 처리
> - 소수점 연산 최대 자리수 12자리로 처리 -> java util 이용하여 처리
- [회원가입](#회원가입 API)
- [로그인](#로그인 API) => 인증 토큰 여부 o
- [송금 견적서 요청](#송금 견적서 요청 API) => 인증 토큰 여부 o
- [송금 접수 요청](#송금 접수 요청 API) => 인증 토큰 여부 o
- [회원 거래 이력 요청](#회원 거래 이력 요청 API) => 인증 토큰 여부 o
### 1. 회원가입 API
- #### EndPoint: /api/v1/user/signup
- #### Method: POST
- #### *Request Body*
  - 유저 아이디
  - 비밀 번호 -> bcrypt 처리
  - 유저 이름
  - 회원 타입 -> bcrypt 처리
```JSON
{
    "userId" : "String",
    "password" : "String",
    "name" : "String",
    "idType" : "String"
}
```
- #### *Response Body*
> 2xx
>> 200 회원가입 성공
>> ```JSON
>> {
>>    "result": {
>>    "code": 200,
>>    "message": "회원 가입 성공"
>>   }
>> }
>> ```
> 4xx
>> 400 요청 바디에 타입이 잘못된 경우
>>```JSON
>>{
>>  "result": {
>>    "code": 400,
>>    "message": "잘못된 파라미터 입니다."
>>  }
>>}
>>```

### 2. 로그인 API
- #### EndPoint: /api/v1/user/login
- #### Method: POST
- #### *Request Body*
  - 유저 아이디
  - 비밀 번호
```JSON
{
    "userId" : "String",
    "password" : "String"
}
```
- #### Response Body
>  2xx
>> 200 로그인 성공: 인증 토큰 발급
>> ```JSON
>> {
>>    "result": {
>>      "code": 200,
>>      "message": "로그인 성공"
>>    },
>>    "token": "String"
>> }
>> ```
> 4xx
>> 400 회원의 아이디와 비밀번호 일치하지 않는 경우
>>```JSON
>>{
>>  "result": {
>>    "code": 400,
>>    "message": "잘못된 파라미터 입니다."
>>  }
>>}
>>```

### 3. 송금 견적서 요청 API
- #### EndPoint: /api/v1/transfer/quote
- #### Method: GET
- #### *Parameters*
```
token(String) 인증 토큰 *required
targetCurrency(String) 통화 *required
amount(Number) 원화 금액 *required
```

### 4. 송금 접수 요청 API
- #### EndPoint: /api/v1/transfer/request
- #### Method: POST
- #### *Request Body*
    - 인증 토큰
    - 채번한 견적서의 id
```JSON
{
    "token": "String",
    "quoteId": "String"
}
```
- #### *Response Body*
>  2xx
>> 200 송금 접수 성공
>> ```JSON
>> {
>>    "result": {
>>      "code": 200,
>>      "message": "송금 접수 성공"
>>    }
>> }
>> ```
> 4xx
>> 400 견적서 만료
>>```JSON
>>{
>>  "result": {
>>    "code": 400,
>>    "message": "견적서가 만료 되었습니다."
>>  }
>>}
>>```
>> 400 1일 송금 한도 초과
>>```JSON
>>{
>>  "result": {
>>    "code": 400,
>>    "message": "오늘 송금 한도 초과 입니다."
>>  }
>>}
>>```
>> 401 유효하지 않은 인증 토큰
>>```JSON
>>{
>>  "result": {
>>    "code": 401,
>>    "message": "유효하지 않은 인증 토큰 입니다."
>>  }
>>}
>>```
> 5xx
>> 500 서버 에러
>>```JSON
>>{
>>  "result": {
>>    "code": 500,
>>    "message": "서버 에러"
>>  }
>>}
>>```

### 5. 회원 거래 이력 요청 API
- #### EndPoint: /api/v1/transfer/list
- #### Method: GET
- #### *Parameters*
```dtd
token(String) 인증 토큰 *required
```
- #### *Response Body*
>  2xx
>> 200 회원 거래 이력 응답 성공
>> - (Object) http 응답 결과
>> - (Object) 회원 거래 이력 데이터
>> ```JSON
>> {
>>  "result": {
>>      "code": 200,
>>      "message": "회원님의 송금 거래 이력입니다."
>>  },
>>  "log": {
>>      "userId": "moin@themonin.com",
>>      "name": "모인",
>>      "todayTransferCount": 2,
>>      "todayTransferUsdAmount": 278.33,
>>        "history": [
>>            {
>>                "sourceAmount": 20000,
>>                "fee": 5000,
>>                "usdExchangeRate": 1337.5,
>>                "usdAmount": 11.214953271028037,
>>                "targetCurrency": "USD",
>>                "exchangeRate": 1337.5,
>>                "targetAmount": 11.21,
>>                "userId": "test2@test.com",
>>                "requestedDate": "2024-01-26T22:41:08.994888+09:00"
>>             },
>>             {
>>                "sourceAmount": 400000,
>>                "fee": 43000,
>>                "usdExchangeRate": 1336.5,
>>                "usdAmount": 267.1156004489338,
>>                "targetCurrency": "USD",
>>                "exchangeRate": 1336.5,
>>                "targetAmount": 267.11,
>>                "userId": "test2@test.com",
>>                "requestedDate": "2024-01-26T22:43:02.401372+09:00"
>>              }
>>           ]
>>        }
>>     }
>>```
> 4xx
>> 401 유효하지 않은 인증 토큰
>>```JSON
>>{
>>  "result": {
>>    "code": 401,
>>    "message": "유효하지 않은 인증 토큰 입니다."
>>  }
>>}
>>```
# DB 테이블
### Table
> - member: 회원
> - remittance_quote: 송금 견적서
> - remittance_log : 송금 접수 요청 로그

### 설계 히스토리
> ### 세부적인 송금 견적서를 remittance_quote 테이블에 모두 저장하고 
> ### remittance_log 테이블에 견적서 아이디만 저장 해서 조회 하는 방법
>> - 이 경우 remittance_quote 테이블에 거래가 된 견적서인지 판단하는 컬럼 하나 추가 할 수 있음
>> - remittance_quote 테이블을 중심으로 송금 관련된 정보를 관리 할 수 있어서 편함
>> - 예상되는 문제점이 있다면 만료된 견적서 완료된 견적서가 뒤섞여서 테이블 하나에 데이터가 포화될 수 있음

> ### remittance_quote 테이블 remittance_log테이블 데이터를 나눠서 관리하는 방법
>> - 송금 견적서 세부정보를 송금 요청 할때 거래 이력 테이블에 한꺼번에 저장함 그 후 견적서에서 레코드 삭제
>> - 말하자면 송금 요청해서 완료된 견적서는 삭제하고 그 데이터가 그대로 거래 이력으로 이동시키는 것

# 구현 현황
> 계획한 단위 기능이 모두 체크된 것은 기능 완성률 100%
### Http Response
- ## 2xx
- [x] success 2xx 상태코드, 메세지를 담는 HttpResponseVO 생성: enum -> 상태 정의
- [x] Response body DTO(HttpResponseDTO) 생성 -> 응답 할 객체
- ## 4xx Error 
> - Type: 비즈니스 로직 에러, db 에러, valid 에러, 외부API 에러, invalid token 에러 => Exception처리
- [x] Error 4xx, 5xx 상태코드, 메세지를 담는 HttpResponseVO 생성: enum -> 상태 정의
- [x] ExceptionResolver: exception -> exceptionHandler
- [x] Exception ErrorResponse DTO

### common: 로그인 기능 제외한 모든 기능은 공통으로 jwt검증
- [ ] JWT 검증: JWT payload - 회원 타입, 회원 아이디

### 1. 회원가입
#### - 비즈니스 로직
- [x] DB MEMBER 테이블에 회원 저장
- [x] 하나의 유저는 개인 회원과 기업 회원으로 분류되어야 함
- [x] 개인 회원은 주민등록 번호, 법인 회원은 사업자 등록 번호로 정규식표현 패턴 valid check 로직 추가
- [ ] 비밀번호 주민등록번호 사업자등록번호 암호화 처리
#### - Exception 에러 처리: false인 경우
  - [x] validation 사용해서 request body로 들어오는 필드 MethodArgumentNotValidException 처리
  - [x] 개인 회원은 주민등록 번호, 법인 회원은 사업자 등록 번호로 정규식표현 패턴 적용 -> false -> InValidPatternTypeException

### 2. 로그인
#### - 비즈니스 로직
- [x] DB MEMBER 테이블에서 아이디와 비밀번호 일치하는 회원 조회
- [ ] JWT 인증 토큰 발급 -> 인증 만료시간 = 토큰 생성시간 + 30분

### 3. 송금 견적서 요청
#### - 비즈니스 로직
- [x] 환율 정보 제공하는 외부 API로 환율정보 요청 응답
- [x] 환율 정보 제공하는 외부 API로부터 받은 응답 데이터로 수수료, 받는 금액 연산 로직 추가
  - [x] 수수료 첫째 자리 반올림 연산
  - [x] 받는 금액 defaultFractionDigits값 번째 소수점 처리 연산
  - [x] 만료 시간 연산: 만료시간 = 송금 견적서 생성시간 + 10분
- [x] 가공된 송금 견적서 데이터를 DB remittance_quote 테이블에 저장
#### - Exception 에러 처리: false인 경우
  - [x] 보내는 금액이 양의 정수 인가? true : false
  - [x] 받는 금액이 양의 정수 인가? true : false => 이 경우는 보내는 금액이 수수료보다 작으면 invalid
  - [ ] 외부 API 호출 도중 에러 발생 => Server error throw
  - [x] 외부 API 응답 데이터가 배열 json으로 응답 -> 배열길이가 0 이상인가? true : false

### 4. 송금 접수 요청
#### - 비즈니스 로직
- [x] 채번한 견적서의 id로 이전에 발행한 견적서 조회 -> DB remittance_quote 테이블에서 조회
- [x] 견적서(quote dto) + 유저아이디 + 요청 날짜 -> 이 형태로 송금 접수 내용 저장(remittance_log 테이블에 저장)
- [ ] 저장 성공시 remittance_quote 테이블에서 해당 레코드 삭제: 이 경우는 테이블 관리를 위해 삭제하는게 맞다고 주관적으로 판단 => 현재 삭제 로직을 넣진 않았습니다.  
#### - Exception 에러 처리: false인 경우
  - [x] 견적서 만료 시간 안지났는가? true : false 
  - [x] 1일 송금 금액 초과 안했는가? true : false

### 5. 회원 거래 이력 요청
#### - 비즈니스 로직
- [x] DB remittance_log 테이블에서 user_id와 일치하는 레코드들 조회
- [x] 유저의 이름 조회

# 구현 방법
> ### 레이어드 아키텍처
> - 계층간에 역할 분담
> - DB 테이블과 일치하는 데이터 형태는 Entity 
> - 계층간 전송 객체 형태는 DTO로 전달
> - 변하지 말아야 하는 형태는 VO로 정의

<img src="./Images/레이어드 아키텍처.png" style="width: 100%"><br><br>
<img src="./Images/레이어드 아키텍처 세부.png" style="width: 100%">

> ### Exception 처리
> - 비즈니스 로직 에러, db 에러, valid 에러, 외부API 에러, invalid token 에러 
> - => 상황에 따른 에외 상황 Throw
> - => 예외 상황 처리
> - => 에러 객체를 ResponseEntiy body에 담아서 리턴
> 
<img src="./Images/에러처리과정.png" style="width: 100%">


# 개발 방향
최대한 과제에 제공된 제약사항을 최대한 유지하면서 개발에 임했습니다.
좋은 코드가 나오기위해선 백엔드 아키텍쳐 설계와 DB설계가 중요하다 생각하였고 최대한 레이어드 아키텍처 방식을 지키면서 구현했습니다.
또한 원활한 데이터 처리, 관리를 위해 Entity DTO VO 개념을 계속해서 리마인드하며 개발했습니다.
기술적인 에러는 제가 보유하고있는 서적이나 기술 블로그를 참고하였고 데이터 흐름이나 테이블 세팅은 현재도 코드 리팩토리 중인
본인이 개발한 somat 플랫폼을 보면서 몽구스의 스키마와 JPA의 엔티티 개념을 서로 비교해 가면서 과제 진행을 하였습니다. 
최대한 나올 수 있는 Exception들을 Exception resolver 개념을 토대로 구축하였습니다.
그리고 최대한 정확한 응답 데이터를 위해 enum 클래스로 최대한 많은 코드들을 나열했습니다.
컨트롤러는 최대한 요청 응답을 받는것에 포커스를 맞췄고 서비스단에서 모든 비즈니스 로직을 처리하였고 
jparepository 클래스를 상속 받아 엔티티의 속성과 연관된 repository의 interface를 만들었고 메소드들을 재정의 해가며 데이터의 CRUD 로직을 구축하였습니다.

# 개발 이슈 사항
### 2024-01-22
Parameter 0 of constructor in com.moin.remittance.domain.service.impl.MemberServiceImpl required a bean of type 'com.moin.remittance.domain.dao.MemberDAO' that could not be found.
Consider defining a bean of type 'com.moin.remittance.domain.dao.MemberDAO' in your configuration.
Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException: Table "MEMBER" not found (this database is empty); SQL statement:

엔티티를 생성하였지만 엔티티 대로 자동 테이블 생성이 안되어 수동으로 테이블 넣어줘야합니다.
아직까지 해결하지 못해서 수동으로 테이블을 넣어줘야합니다.
샘플 테이블을 Resouce디렉토리에 table.sql 파일에 쿼리문을 저장 해놓았습니다.

### 2024-01-23
org.junit.jupiter.api.extension.ParameterResolutionException: No ParameterResolver registered for parameter [com.moin.remittance.domain.repository.MemberRepositoryTest memberRepositoryTest] in constructor [public com.moin.remittance.domain.dao.impl.MemberDaoImplTest(com.moin.remittance.domain.repository.MemberRepositoryTest)]
test에서 엔티티를 불러오지 못하는 이슈가 있어 테스트 진행이 안되어 테스트 코드는 작성 하지 못했습니다 -> 해결하려고 시간 많이 썼는데 결국 해결 못함
### 2024-01-24
환 
### 2024-01-25
modelmapper가 계속 매핑을 하지 못하는 이슈
### 2024-01-26
modelmapper가 dto-> entity로 변환하는 작업에서 오류가 발생
근데 entity -> dto로 변환하는건 성공

데이터 타입 이슈와 
### 2024-01-27

### 2024-01-28
JWT 라이브러리 주입이 안되어 시간을 많이 할애했지만 버전 호환이 안맞아서 그런것 같습니다.

# 회고
이번 과제를 진행하면서 java와 springboot 개념들을 계속 리마인드 하였습니다. 
이번 과제를 통해서 Junit을 통해서 왜 테스트 코드를 작성 하는지 그 중요성을 많이 깨달았습니다.
짧은 시간내에 테스트 코드 작성하면서 과제를 진행하고싶었지만 호환 문제로 테스트 코드를 작성하지 못해서
너무 아쉽습니다. 또 송금 견적서 조회 송금 접수요청 등 송금관련 API는 완성하였지만 jwt와 bcrypt를 
적용하지 못해서 너무 아쉽습니다. 이틀만 더 시간이 있었으면 하는 마음이지만 짧은 시간내에 API를 구축하는것도
결국은 백엔드 개발자의 역량이라 생각하여 많이 부족 하다는걸 느꼈습니다.
하지만 송금 관련 API는 거의 마무리를 하여 그 부분은 만족합니다. 그래도 아쉬움이 많이 남습니다.
오늘부로 과제는 끝났지만 아직 미구현된 API를 추가 할 계획입니다. 과제를 통해서 성장해 나갈 수 있도록
한번 더 이런 좋은 내용의 과제를 내주셔서 감사합니다.



