# OCE IAM

## Rest Api

IAM 구동 후,  /rest/console 에 접속하여 테스트합니다.

우측 상단에 Authorize 를 클릭하셔서 management-key, management-secret 을 입력하시면 테스트가 가능합니다.

![](images/swagger.png)

코드상에서 호출하실 경우 헤더에 management-key , management-secret 값을 주어야 합니다.

예)

```
curl -X GET \
--header 'Accept: application/json' \
--header 'management-key: bd0380d0-1220-4676-91dc-c6d6f444136c' \
--header 'management-secret: 6abc6003-b181-465f-9027-b5824c1a3ecd' \
'http://localhost:8080/rest/v1/user'
```




