<img width="737" alt="image" src="https://github.com/fintech-base-1/fin-point/assets/104135990/996385c6-996c-4fb2-a9fd-c992f3a588bd">

## 개요


💵각자의 소비패턴에 맞게 일일 목표 지출금액을 설정

📱일일 지출 금액과 목표 금액을 비교하고

⏰그동안 아낀 금액을 알려줍니다!

👟아낀 금액으로 조각투자 상품을 구매해보세요


<br>
<br>

## 개발기간
2023.05.23 - 2023.06.14

<br>
<br>

## 팀원

<table>
  <tr>
     <td align="center"><img src="https://github.com/fintech-base-1/practice-git/assets/104135990/68ae5c44-ea7c-437a-a8cc-f5269a6d7b65" width="100px;" alt=""/><br /><sub><a href="https://github.com/hojncode"><b>@hojncode</b></sub></a><br /></td>
     <td align="center"><img src="https://github.com/fintech-base-1/practice-git/assets/104135990/1bee7fe5-7c11-4f58-9b5b-c3e24ebed757" width="100px;" alt=""/><br /><sub><a href="https://github.com/Damgom"><b>@Damgom</b></sub></a><br /></td>
    <td align="center"><img src="https://github.com/fintech-base-1/practice-git/assets/104135990/d763d5b1-dc62-4b01-b99a-5f3e3895ece4" width="100px;" alt=""/><br /><sub><a href="https://github.com/eastmeet"><b>@eastmeet</b></sub></a><br /></td>
    <td align="center"><img src="https://github.com/fintech-base-1/practice-git/assets/104135990/f9229e9c-40bb-49c8-b1a4-7e349e54b081" width="100px;" alt=""/><br /><sub><a href="https://github.com/jhyun9682"><b>@jhyun9682</b></sub></a><br /></td>
    <td align="center"><img src="https://github.com/fintech-base-1/practice-git/assets/104135990/bdedabe3-e075-4492-ad43-e9c1425c9b9c" width="100px;" alt=""/><br /><sub><a href="https://github.com/arxivj"><b>@arxivj</b></sub></a><br /></td>
</tr>
      <tr>
            <td align="center">Back-end</td>
            <td align="center">Back-end</td>
            <td align="center">Back-end</td>
            <td align="center">Back-end</td>
            <td align="center">Back-end</td>
  </tr>
      <tr>
      <td align="center">⭐️이호진</td>
      <td align="center">진종국</td>
      <td align="center">이동우</td>
      <td align="center">이정현</td>
      <td align="center">박진석</td>
      </tr>
    
</table>

<br>
<br>

<div><h1>📚 STACKS</h1></div>

<div> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
  
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> 
  <img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white"> 
  
  <img src="https://img.shields.io/badge/jwt-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white">

  <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
  <br>
  
  <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> 
  <img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white"> 
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"> 
  <img src="https://img.shields.io/badge/jquery-0769AD?style=for-the-badge&logo=jquery&logoColor=white">
    <img src="https://img.shields.io/badge/bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white">
  <br>
  

  <img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> 
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
  
</div>


<br>
<br>

<details>
<summary> 실행하기 위한 yml</summary>
<div markdown="1">
<pre>spring:
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
#  h2:
#    console:
#      path: /h2
#      enabled: true
#  datasource:
#    url: jdbc:h2:mem:test
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/{DATABASE NAME}
    username: {DB.USERNAME}
    password: {DB.PASSWORD}
  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
    open-in-view: false
  mail:
    host: smtp.gmail.com
    port: 587
    username: {SMTP.USERNAME}
    password: {SMTP.PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
  redis:
    host: localhost
    port: 6379

login:
  mail:
    subject: "[finpoint] 회원가입 안내"
    imagePath: {STATIC IMAGE PATH}
    imageId: logo
    sender: {SMTP.SENDER.EMAILADDRESS}

jwt:
  salt: {RANDOM SALT VALUE}

oauth:
  naver:
    client_id: {OAUTH NAVER CLIENT ID}
    client_secret: {OAUTH NAVER CLIENT SECRET}
    callback: {OAUTH CALLBACK URI}
    state: {OAUTH RANDOM STATE}
  kakao:
    client_id: {OAUTH KAKAO CLIENT ID}
    client_secret: {OAUTH KAKAO CLIENT SECRET}
    callback: {OAUTH KAKAO CALLBACK URI}
  google:
    client_id: {OAUTH GOOGLE CLIENT ID}
    client_secret: {OAUTH GOOGLE CLIENT SECRET}
    callback: {OAUTH GOOGLE CALLBACK URI}

bank:
  client_id: {OPENBANK CLIENT ID}
  client_secret: {OPENBANK CLIENT SECRET}
  redirect_uri: {OPENBANK REDIRECT URI}
  grant_type: {OPENBANK GRANT TYPE}
  state: {OPENBANK RANDOM STATE}

external:
  url: 
  path:
  account:
    path: 
    detail: 

file:
  dir: {FILE PATH}
</details>

## 시연 영상 및 상세 내용

[상세내용](https://enshrined-scooter-6ed.notion.site/ecf5db232b3d4bb5825b25a08f488820?pvs=4)
  
## 고찰

### 사용자의 사용경험을 중시한 서버의 분리

---

운영서버에서 실시간으로 오픈API와 통신하는 경우 통신상태에 따라 사용자가 기다려야 하는 상황이 생길 수 있습니다. 그렇기 때문에 오픈 API를 이용해 데이터를 가공하는 서버를 스프링 스케줄러를 이용해 따로 구성했습니다. 이렇게 아키텍처를 구성하게되면 정해진 시각 오픈 API를 통해 데이터를 받아오고 그 데이터를 필요한 형태로 가공해 DB에 넣어둘 수 있기 때문에 운영서버에서는 가공된 데이터를 조회하기만 하면 됩니다. 이것은 사용자가 기다리는 시간을 줄일 수 있고, 서비스를 이용하는데 조금 더 편한 환경을 제공할 수 있을 것이라 생각했습니다.

### 동시성을 고려한 상품구매

---

 이번 프로젝트에서 조각투자의 상품을 구매하는 로직을 구현했습니다. 상품의 수량이 제한되어있고 동시성 문제가 발생할 수 있기 때문에 JPA에서 제공하는 낙관적 락을 사용해 동시성 이슈를 제어했습니다. 낙관적 락은 조회 시점의 version 컬럼을 커밋하는 시점에서 값을 비교하여 DB에 변동된 값을 반영합니다.

이번에 동시성을 고려하며 트랜잭션과 isolation level에 대해 조금 더 공부했습니다. 트랜잭션에서 isolation level이 높아질수록 데이터의 무결성과 정합성은 보장할 수 있지만 성능은 그것에 반비례함을 알게됐고, 서비스에 맞게 고려해야함을 알게됐습니다.

### 테이블 설계의 문제점

---

테이블을 설계하면서 테이블 정규화에 오픈API를 통해 받아오는 정보의 사양이 달라질 수 있고, 조각투자 상품의 사양 또한 달라질 여지가 있어 정규화에 힘썼습니다. 하지만 이런 정규화는 JPA 에서 쿼리의 복잡도를 늘리고 필요없는 쿼리를 날리게 된다는 것을 알게됐습니다. 그동안 당연히 해야하는줄 알았던 정규화였는데 관련 내용을 찾아보니 오히려 현업에서는 DB의 성능향상을 위해 데이터의 중복을 허용하고 조인을 줄이는 반정규화를 하는 경우도 있다는 것을 알게 됐습니다. 이후 관련 내용을 좀 더 학습하며 조금 더 고민하고 기록해 볼 예정입니다.

### OpenFeign을 이용한 외부 API 호출

---

기존 RestTemplate을 이용해 외부 API를 호출하는 방법은 알고 있었지만 이번 프로젝트를 통해 OpenFeign을 알게 됐고, 이것을 이용해 오픈뱅킹API를 이용해보고, OAuth를 구현해봤습니다. OpenFeign은 Spring에서 RestController를 작성하는 방식과 거의 비슷한 방식으로 사용하기 때문에 배우는 난이도가 RestTemplate보다 쉬웠고, 팀원들과 빠르게 학습할 수 있다는 장점이 있었습니다.

### 코드리뷰

---

이전 프로젝트에서 시니어 개발자분께 코드리뷰를 받았던 적이 있습니다. 그 때의 기억이 좋게 남아 이번 프로젝트에서 서로 아는 부분에 대해 코드리뷰를 진행해보자고 제안했습니다. 서로가 그동안 해보지 않았던 부분들을 구현했던터라 기존에 그 부분을 구현해봤던 사람들이 해주는 코드리뷰는 정말 좋았습니다. 다른사람의 코드를 보는데 익숙하지 않아 처음엔 조금 힘들었지만 각자 의견을 교환하고 서로의 습관들에 대해 토론하는 부분이 좋았습니다.

## Commit Message Convention
- INIT : 새로운 branch 를 만들고 작업을 시작
- FEAT : 새로운 기능의 추가
- REFACTOR : 코드 리팩토링
- FIX : 오류 수정, 버그 FIX
- CONFIG : 설정파일 추가, 수정
- TEST : 테스트 코트, 리팩토링 테스트 코드 추가
- CHORE : 빌드 업무 수정, 패키지 매니저 수정
- STYLE : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
- COMMENT :	필요한 주석 추가 및 변경
- DOCS : 문서를 수정한 경우
- RENAME : 파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우
- REMOVE : 파일을 삭제하는 작업만 수행한 경우

## 깃 플로우
- main
  - dev
    - feat

> feat 네이밍 규칙 예시 : feat-oauth-kakao

> feat에서 개발 브랜치 생성후 feat에서 PR(pull request) -> 기능 테스트후 dev 브랜치로 PR
