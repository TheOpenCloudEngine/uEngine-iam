# OCE IAM

## CouchDB Schema And View

### User

Document

```
{
    "_id": "10e94d565d164bea841ca6840c54f7e8",
    "_rev": "5-dcbbbab83c454aa54d2cec32427e516d",
    "docType": "oauth_user",
    "managementId": "5e3432ad172644ea8041fe67eb8f5cbd"
    "userPassword": "asdasd",
    "userName": "user4",
    "level": 5,
    "regDate": 1462088181948,
    "updDate": 1462088612137
}
```

View

```
{
  "_id": "_design/oauth_user",
  "language": "javascript",
  "views": {
    "selectById": {
      "map": "function(doc) {if(doc.docType == \"oauth_user\"){emit([doc._id],doc); }}"
    },
    "selectByManagementId": {
      "map": "function(doc) {if(doc.docType == \"oauth_user\"){emit([doc.managementId],doc); }}"
    },
    "selectByManagementIdAndUserName": {
      "map": "function(doc) {if(doc.docType == \"oauth_user\"){emit([doc.managementId,doc.userName],doc); }}"
    },
    "selectByManagementIdAndCredential": {
      "map": "function(doc) {if(doc.docType == \"oauth_user\"){emit([doc.managementId,doc.userName,doc.userPassword],doc); }}"
    },
    "selectByManagementIdAndId": {
      "map": "function(doc) {if(doc.docType == \"oauth_user\"){emit([doc.managementId,doc._id],doc); }}"
    }
  }
}
```

### Scope

Document

```
{
    "_id": "621c711a8a4b4990b7e940c58ecbe108",
    "_rev": "1-96d9d3cd1245066384fb15f9c0fa1c78",
    "docType": "oauth_scope",
    "managementId": "5e3432ad172644ea8041fe67eb8f5cbd",
    "name": "form:delete",
    "description": "form:delete",
    "regDate": 1462088629198,
    "updDate": 1462088629198
}
```

View

```
{
  "_id": "_design/oauth_scope",
  "language": "javascript",
  "views": {
    "selectById": {
      "map": "function(doc) {if(doc.docType == \"oauth_scope\"){emit([doc._id],doc); }}"
    },
    "selectByManagementId": {
      "map": "function(doc) {if(doc.docType == \"oauth_scope\"){emit([doc.managementId],doc); }}"
    },
    "selectByManagementIdAndName": {
      "map": "function(doc) {if(doc.docType == \"oauth_scope\"){emit([doc.managementId,doc.name],doc); }}"
    },
    "selectByManagementIdAndId": {
      "map": "function(doc) {if(doc.docType == \"oauth_scope\"){emit([doc.managementId,doc._id],doc); }}"
    }
  }
}
```

### Client

Document

```
{
    "_id": "97ed8f30da014800b92a24d8bed6d5a5",
    "_rev": "3-2a2e7ac899eb79fe6b953b5c7899688b",
    "docType": "oauth_client",
    "managementId": "5e3432ad172644ea8041fe67eb8f5cbd",
    "name": "eform",
    "description": "eform",
    "clientKey": "fcf5afd7-be50-4dac-949f-d4ab768b485d",
    "clientSecret": "ac1603fa-f38a-481d-a336-aa06064c5eeb",
    "clientJwtSecret": "f6e7eca0-58fc-491f-950f-9e956d3b47b2",
    "clientTrust": "trust",
    "clientType": "confidential",
    "activeClient": "Y",
    "authorizedGrantTypes": "code,implicit,password,credentials",
    "webServerRedirectUri": "http://localhost:8080/oauth/authorize_redirect",
    "refreshTokenValidity": "Y",
    "codeLifetime": 3600,
    "refreshTokenLifetime": 3600,
    "accessTokenLifetime": 3600,
    "jwtTokenLifetime": 3600,
    "regDate": 1462088798255,
    "updDate": 1462088815929
}
```

View

```
{
  "_id": "_design/oauth_client",
  "language": "javascript",
  "views": {
    "selectById": {
      "map": "function(doc) {if(doc.docType == \"oauth_client\"){emit([doc._id],doc); }}"
    },
    "selectByManagementId": {
      "map": "function(doc) {if(doc.docType == \"oauth_client\"){emit([doc.managementId],doc); }}"
    },
    "selectByManagementIdAndName": {
      "map": "function(doc) {if(doc.docType == \"oauth_client\"){emit([doc.managementId,doc.name],doc); }}"
    },
    "selectByManagementIdAndId": {
      "map": "function(doc) {if(doc.docType == \"oauth_client\"){emit([doc.managementId,doc._id],doc); }}"
    },
    "selectByClientKey": {
      "map": "function(doc) {if(doc.docType == \"oauth_client\"){emit([doc.clientKey],doc); }}"
    },
    "selectByClientKeyAndSecret": {
      "map": "function(doc) {if(doc.docType == \"oauth_client\"){emit([doc.clientKey,doc.clientSecret],doc); }}"
    }
  }
}
```

### Client Scopes

Document

```
{
   "_id": "0848940809214a81979e01fc240487e4",
   "_rev": "1-61168f9e89cff19f97d57c97b42673c1",
   "docType": "oauth_client_scopes",
   "clientId": "97ed8f30da014800b92a24d8bed6d5a5",
   "scopeId": "621c711a8a4b4990b7e940c58ecbe108",
   "regDate": 1462088816176,
   "updDate": 1462088816176
}
```

View

```
{
  "_id": "_design/oauth_client_scopes",
  "language": "javascript",
  "views": {
    "selectClientScopesByClientId": {
      "map": "function(doc) {if(doc.docType == \"oauth_client_scopes\"){emit([doc.clientId],doc); }}"
    },
    "selectClientScopesByClientIdAndScopeId": {
      "map": "function(doc) {if(doc.docType == \"oauth_client_scopes\"){emit([doc.clientId,doc.scopeId],doc); }}"
    }
  }
}
```

### Code

Document

```
{
   "_id": "7e6735a08a0145e3b64508ed38eba731",
   "_rev": "1-89b7759c58c8cbfbb219c1b4cac07242",
   "docType": "oauth_code",
   "managementId": "5e3432ad172644ea8041fe67eb8f5cbd",
   "clientId": "97ed8f30da014800b92a24d8bed6d5a5",
   "oauthUserId": "1543c5ac2c5049b18058662da236f011",
   "code": "376b9aaa-f9de-43b7-a5b2-d4e01ac6ef5a",
   "scopes": "form:create",
   "regDate": 1462089230963,
   "updDate": 1462089230963
}
```

View

```
{
  "_id": "_design/oauth_code",
  "language": "javascript",
  "views": {
    "selectCodeById": {
      "map": "function(doc) {if(doc.docType == \"oauth_code\"){emit([doc._id],doc); }}"
    },
    "selectCodeByCode": {
      "map": "function(doc) {if(doc.docType == \"oauth_code\"){emit([doc.code],doc); }}"
    },
    "selectCodeByCodeAndClientId": {
      "map": "function(doc) {if(doc.docType == \"oauth_code\"){emit([doc.code,doc.clientId],doc); }}"
    }
  }
}
```

### Token

Document

```
{
   "_id": "0398ec50c4a140868e10f9d1a63382bf",
   "_rev": "1-b799f3d748f4bfeda343357befb31fff",
   "docType": "oauth_access_token",
   "type": "user",
   "scopes": "form:create",
   "token": "2d5eb022-14fc-4796-bfec-8579f6888ace",
   "oauthUserId": "1543c5ac2c5049b18058662da236f011",
   "managementId": "5e3432ad172644ea8041fe67eb8f5cbd",
   "clientId": "97ed8f30da014800b92a24d8bed6d5a5",
   "refreshToken": "14bb353d-e881-4b57-bb14-6ebf6f4be538",
   "regDate": 1462117442112,
   "updDate": 1462117442112
}
```

View

```
{
  "_id": "_design/oauth_access_token",
  "language": "javascript",
  "views": {
    "selectTokenById": {
      "map": "function(doc) {if(doc.docType == \"oauth_access_token\"){emit([doc._id],doc); }}"
    },
    "selectTokenByToken": {
      "map": "function(doc) {if(doc.docType == \"oauth_access_token\"){emit([doc.token],doc); }}"
    },
    "selectTokenByRefreshToken": {
      "map": "function(doc) {if(doc.docType == \"oauth_access_token\"){emit([doc.refreshToken],doc); }}"
    },
    "selectTokenByManagementIdAndId": {
      "map": "function(doc) {if(doc.docType == \"oauth_access_token\"){emit([doc.managementId,doc._id],doc); }}"
    }
  }
}
```

### Management

Document

```
{
   "_id": "5e3432ad172644ea8041fe67eb8f5cbd",
   "_rev": "2-1a87b4a0046609b160956a2d08214be5",
   "docType": "management",
   "userId": "5e7f277ef2f34f62ad97b3665e2dbd9a",
   "managementName": "forcs",
   "managementKey": "bd0380d0-1220-4676-91dc-c6d6f444136c",
   "managementSecret": "6abc6003-b181-465f-9027-b5824c1a3ecd",
   "managementJwtSecret": "b970e761-6051-4ecf-86e0-6e4f642972f0",
   "sessionTokenLifetime": 0,
   "scopeCheckLifetime": 0,
   "description": "forcs",
   "regDate": 1462087977782,
   "updDate": 1462117586882
}
```

View

```
{
  "_id": "_design/management",
  "language": "javascript",
  "views": {
    "selectById": {
      "map": "function(doc) {if(doc.docType == \"management\"){emit([doc._id],doc); }}"
    },
    "selectByKey": {
      "map": "function(doc) {if(doc.docType == \"management\"){emit([doc.managementKey],doc); }}"
    },
    "selectByUserIdAndId": {
      "map": "function(doc) {if(doc.docType == \"management\"){emit([doc.userId,doc._id],doc); }}"
    },
    "selectByUserId": {
      "map": "function(doc) {if(doc.docType == \"management\"){emit([doc.userId],doc); }}"
    },
    "selectByCredential": {
      "map": "function(doc) {if(doc.docType == \"management\"){emit([doc.managementKey,doc.managementSecret],doc); }}"
    }
  }
}
```

### Management User

Document

```
{
   "_id": "4df80d3f8e5b4015bfd23b0e91faa7f5",
   "_rev": "2-7924ba5df13181c9b47a39ef19127d60",
   "docType": "management_user",
   "email": "darkgodarkgo@gmail.com",
   "password": "pdtMP3ZhsRtygBZmqA08sQ==",
   "authority": "ROLE_USER",
   "enabled": true,
   "regDate": 1462112923661,
   "updDate": 1462112941451,
   "level": "5",
   "country": "US",
   "admin": false
}
```

View

```
{
  "_id": "_design/management_user",
  "language": "javascript",
  "views": {
    "selectByUserId": {
      "map": "function(doc) {if(doc.docType == \"management_user\"){emit([doc._id],doc); }}"
    },
    "selectByUserEmail": {
      "map": "function(doc) {if(doc.docType == \"management_user\"){emit([doc.email],doc); }}"
    }
  }
}
```

### Regist

Document

```
{
   "_id": "2bc926c4e3c94bde8114cf1faa109e5c",
   "_rev": "1-4327c5f24565277b6c0d1d36f22a16a9",
   "docType": "registe",
   "userId": "4df80d3f8e5b4015bfd23b0e91faa7f5",
   "token": "MTQ2MjExMjkyMzc2MQ==",
   "regDate": 1462112923763,
   "updDate": 1462112923763
}
```

View

```
{
  "_id": "_design/registe",
  "language": "javascript",
  "views": {
    "selectByUserIdAndToken": {
      "map": "function(doc) {if(doc.docType == \"registe\"){emit([doc.userId,doc.token],doc); }}"
    }
  }
}
```


