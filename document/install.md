# OCE IAM

## Quick Start

**Build Front end**

```
$ cd iam-fron-end
$ npm install
$ npm run build
```

It will create static web resource in `iam-sample-app` directory.

*Tip : change build target directory*

Change `iam-front-end/config/index.js` as follows:

```
module.exports = {
  build: {
    env: require('./prod.env'),
    index: path.resolve(__dirname, '../../iam-sample-app/src/main/resources/static/index.html'),
    assetsRoot: path.resolve(__dirname, '../../iam-sample-app/src/main/resources/static'),
```

**Run**

```
$ mvn package -B
$ java -jar iam-sample-app/target/iam-sample-app-2.0.1-SNAPSHOT.jar
```

## Development
 

### Front-end Development

```
$ cd iam-frond-end
$ npm install
$ npm run dev
```

Front-end first login information: admin / admin

*It can chane by* `iam.admin.username` && `iam.admin.password` in `application.yml` file.

### Back-end Development

```
$ mvn package -B
$ java -jar iam-sample-app/target/iam-sample-app-2.0.1-SNAPSHOT.jar
```

### Configuration

Located at `iam-sample-app/src/main/resources/application.yml`

**Jpa rest api endpoint**

```
spring:
  security:
    enabled: false
  data:
    rest:
      basePath: /api/v1 => edit
```

**Port**

```
server:
  port: 8080 => edit
  servletPath: /
```

**Database**

**Some specific environment (ex, dynamic tenant routing database)** 
do not use these configuration. you should build your own database source

```
spring:
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        show_sql: true
  datasource:
    url: jdbc:mysql://localhost:3306/msa => edit
    username: root => edit
    password: => edit
```

**UI System login**

```
iam:
  admin:
    username: admin => edit
    password: admin => edit
```

**Scopes Example**

```
scopes:
  - name: cloud-server
    description: Enable cloud service
  - name: bpm
    description: Enalbe bpm service
  # If billing service required.  
  - name: billing
    description: Enalbe billing service
```


**Client critical settings example**

```
clients:
  - name: uEngine-cloud
    description: uEngine cloud Application
    clientKey: my-client-key
    clientSecret: my-client-secret

    # 토큰 발급시 요청할 수 있는 스코프 목록 (콤마 세퍼레이션)
    enable-scopes: cloud-server,bpm

    # 유저 스코프 체크. 유저 metadata 의 scopes 를 검사한다.
    # true: 요청받은 스코프를 모두 가지고 있어야 한다.
    # false: 요청받은 스코프 중 일부만 가지고 있으면 된다. (default)
    user-scope-check-all: false

    # client-key,client-secret 헤더로 Rest api 를 호출할 수 있는 권한.
    # 회원 가입,사용자 정보 수정등의 rest api 를 호출할 수 있다.
    # 서드파티 어플리케이션은 권한을 주면 안되고, 오피셜 어플리케이션은 권한을 가지는것을 추천.
    access-rest-enable: true

    # 클라이언트 활성화 여부
    activeClient: true

    # 지원하는 그런트 타입.
    authorizedGrantTypes: code,implicit,password,credentials

    # 리프레쉬 토큰 발급.
    refreshTokenValidity: true

    # 만기 토큰 자동 삭제.
    autoDeletionToken: true

    # JWT 토큰 콘텍스트에 포함될 정보 : clientKey,scopes,type,refreshToken,userName,user | ALL
    requiredContext: clientKey,scopes,type,refreshToken,userName,user

    # JWT 토큰 알고리즘: HS256 or RS256
    jwtAlgorithm: HS256

    # code 인증 방식의 유지시간.
    codeLifetime: 36000

    # 리프레쉬 토큰 유효시간
    refreshTokenLifetime: 2592000

    # 어세스 토큰 유효시간
    accessTokenLifetime: 3600

    # 사용자 알림 메일 발송 SIGN_UP,SIGNED_UP,FORGOT_PASSWORD,PASSWORD_CHANGED | ALL
    notification: ALL
``` 







