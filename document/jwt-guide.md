# OCE IAM

## Online Jwt Decoder

[http://jwt.calebb.net/](http://jwt.calebb.net/)

## Jwt Guide

프로젝트에 maven 디펜던시를 추가합니다.

```
<dependency>
    <groupId>com.nimbusds</groupId>
    <artifactId>nimbus-jose-jwt</artifactId>
    <version>4.16.1</version>
</dependency>
```


Jwt 토큰에서 단순한 Claim 정보를 얻어오고자 할 경우

```
package org.opencloudengine.garuda.util;

import com.nimbusds.jwt.JWTClaimsSet;

public class JwtTest {

    public static void main(String[] args) throws Exception {

        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0NjIxMjQ3ODYsImNvbnRleHQiOnsic2NvcGVzIjoiZm9ybTpjcmVhdGUiLCJtYW5hZ2VtZW50SWQiOiI1ZTM0MzJhZDE3MjY0NGVhODA0MWZlNjdlYjhmNWNiZCIsInJlZnJlc2hUb2tlbiI6IjhjNTYzODViLTEwM2ItNDkyMS04ODA3LWYwYTgwZDFmZDI5ZiIsInR5cGUiOiJ1c2VyIiwib2F1dGhVc2VySWQiOiIxNTQzYzVhYzJjNTA0OWIxODA1ODY2MmRhMjM2ZjAxMSIsImNsaWVudElkIjoiOTdlZDhmMzBkYTAxNDgwMGI5MmEyNGQ4YmVkNmQ1YTUifSwiaXNzIjoib2NlLmlhbSIsImNsYWltIjp7ImFhYSI6ImJiYiJ9LCJpYXQiOjE0NjIxMjExODZ9.J8Edpn5fTtgx9-6cLXGiYV0NnRuPO2rbv28xkR55sl4";
        JWTClaimsSet jwtClaimsSet = JwtUtils.parseToken(jwt);
        System.out.println(jwtClaimsSet);
    }
}

.
.
.
{
  "exp": 1462263268000,
  "context": {
    "scopes": "form:create",
    "clientKey": "fcf5afd7-be50-4dac-949f-d4ab768b485d",
    "userId": "1543c5ac2c5049b18058662da236f011",
    "userName": "user1",
    "managementId": "5e3432ad172644ea8041fe67eb8f5cbd",
    "type": "user",
    "refreshToken": "7d2378bd-1234-474a-9ecd-504d04c05e7c",
    "clientId": "97ed8f30da014800b92a24d8bed6d5a5"
  },
  "iss": "oce.iam",
  "expires_in": 3582,
  "claim": {
    "tenant": "a1,b1"
  },
  "iat": 1462259668000
}
```


Jwt 토큰 밸리데이션

```
package org.opencloudengine.garuda.util;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import net.minidev.json.JSONObject;

import java.util.Date;

public class JwtTest {

    public static void main(String[] args) throws Exception {


        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0NjIxMjQ3ODYsImNvbnRleHQiOnsic2NvcGVzIjoiZm9ybTpjcmVhdGUiLCJtYW5hZ2VtZW50SWQiOiI1ZTM0MzJhZDE3MjY0NGVhODA0MWZlNjdlYjhmNWNiZCIsInJlZnJlc2hUb2tlbiI6IjhjNTYzODViLTEwM2ItNDkyMS04ODA3LWYwYTgwZDFmZDI5ZiIsInR5cGUiOiJ1c2VyIiwib2F1dGhVc2VySWQiOiIxNTQzYzVhYzJjNTA0OWIxODA1ODY2MmRhMjM2ZjAxMSIsImNsaWVudElkIjoiOTdlZDhmMzBkYTAxNDgwMGI5MmEyNGQ4YmVkNmQ1YTUifSwiaXNzIjoib2NlLmlhbSIsImNsYWltIjp7ImFhYSI6ImJiYiJ9LCJpYXQiOjE0NjIxMjExODZ9.J8Edpn5fTtgx9-6cLXGiYV0NnRuPO2rbv28xkR55sl4";
        String sharedSecret = "fcf5afd7-be51-4dfc-949f-d4ab768b985d";
        
        //만료시간 체크 없이 시그네이쳐 밸리데이션
        boolean validateToken = validateToken(jwt, sharedSecret);
        
        
        //Jwt 토큰에 포함된 만료시간과 함께 시그네이쳐 밸리데이션
        boolean validateToken1 = validateToken(jwt, sharedSecret, null);
        
        
        //임의의 만료시간과 함께 시그네이쳐 밸리데이션
        Date expireTime = new Date();
        boolean validateToken2 = validateToken(jwt, sharedSecret, expireTime);
    }

    public static boolean validateToken(String jwtToken, String sharedSecret) throws Exception {
        JWSVerifier verifier = new MACVerifier(sharedSecret);
        JWSObject jwsObject = JWSObject.parse(jwtToken);

        if (!jwsObject.verify(verifier)) {
            return false;
        }
        return true;
    }

    public static boolean validateToken(String jwtToken, String sharedSecret, Date expirationTime) throws Exception {
        JWSVerifier verifier = new MACVerifier(sharedSecret);
        JWSObject jwsObject = JWSObject.parse(jwtToken);

        if (!jwsObject.verify(verifier)) {
            return false;
        }

        JSONObject jsonPayload = jwsObject.getPayload().toJSONObject();
        JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(jsonPayload);

        if (expirationTime == null) {
            expirationTime = jwtClaimsSet.getExpirationTime();
        }

        int compareTo = new Date().compareTo(expirationTime);
        if (compareTo > 0) {
            return false;
        }
        return true;
    }
}
```

예제 코드의 sharedSecret 은 사용자에게 제공되지 않습니다.

OCE-IAM 의 시스템 관리자만 알 수 있으며 sharedSecret 키의 설정은 소스코드의
iam-web/src/main/webapp/WEB-INF/config.properties 파일에서 security.jwt.secret 옵션을 수정하도록 합니다.

일반적으로 운영환경에서는 자체적인 어플리케이션에서 토큰 밸리데이션을 수행하지 않고 IAM 으로 호출하는 것을 추천합니다.

```
###########################################
## Security Configuration
###########################################

# Secret Key for Password Encryption
security.password.encoder.secret1=Bar12345Bar12345
security.password.encoder.secret2=ThisIsASecretKet

# Secret Key for AccessToken Jwt Signature
security.jwt.secret=fcf5afd7-be51-4dfc-949f-d4ab768b985d
security.jwt.issuer=oce.iam
```





