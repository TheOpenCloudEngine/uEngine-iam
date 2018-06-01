## Online Jwt Decoder

[http://jwt.calebb.net/](http://jwt.calebb.net/)

## How to extract Jwt token in java application?


Add maven dependency.

```
<dependency>
    <groupId>com.nimbusds</groupId>
    <artifactId>nimbus-jose-jwt</artifactId>
    <version>4.16.1</version>
</dependency>
```


### If you want to obtain simple claim information from a Jwt token

**Do not pass your api server before validate a Jwt token. There is a risk of modulation.**

```
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
  "iss": "lts-iam",
  "context": {
    "clientKey": "my-client-key",
    "scopes": [
      "lts"
    ],
    "type": "user",
    "userName": "superadmin",
    "user": {
      "userName": "superadmin",
      "metaData": {
        "comp_no": null,
        "name": "관리자",
        "wno": 14,
        "scopes": [
          "lts"
        ],
        "admit_master": "Y",
        "part_code": null,
        "part_master": "N",
        "part_level_job": null
      },
      "regDate": 0,
      "updDate": 0
    },
    "refreshToken": "e60e04cb-9feb-4a75-9de5-8c9b0eb38a25"
  },
  "claim": {},
  "exp": 1527761031,
  "iat": 1527753831
}
```


### Validate Jwt token with Signature modulation verification

**Your Api gateway or Resource Server should validate Jwt token before response data.** 

```
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
        
        //Signature validation without expiration time check
        boolean validateToken = validateToken(jwt, sharedSecret);
        
        
        //Signature validation with expiration time check including jwt token
        boolean validateToken1 = validateToken(jwt, sharedSecret, null);
        
        
        //Signature validation with expiration time check with custom expireTime
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





