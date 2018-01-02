package org.uengine.iam.oauth2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthclient.OauthClientService;
import org.uengine.iam.oauthscope.OauthScope;
import org.uengine.iam.oauthscope.OauthScopeService;
import org.uengine.iam.oauthtoken.*;
import org.uengine.iam.oauthuser.OauthUser;
import org.uengine.iam.oauthuser.OauthUserRepository;
import org.uengine.iam.util.ExceptionUtils;
import org.uengine.iam.util.JsonFormatterUtils;
import org.uengine.iam.util.JsonUtils;
import org.uengine.iam.util.JwtUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@ConfigurationProperties(prefix = "iam.jwt")
public class OauthGrantServiceImpl implements OauthGrantService {

    private String key;
    private String issuer;
    private long oldRefreshTokenTimeout;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public long getOldRefreshTokenTimeout() {
        return oldRefreshTokenTimeout;
    }

    public void setOldRefreshTokenTimeout(long oldRefreshTokenTimeout) {
        this.oldRefreshTokenTimeout = oldRefreshTokenTimeout;
    }

    @Autowired
    private OauthClientService clientService;

    @Autowired
    private OauthTokenService tokenService;

    @Autowired
    private OauthUserRepository userRepository;

    @Autowired
    private OauthScopeService scopeService;

    @Autowired
    private OauthTokenRepository tokenRepository;

    @Autowired
    private OauthCodeRepository codeRepository;

    @Override
    public void processTokenInfo(AccessTokenResponse accessTokenResponse) throws Exception {

        //수행해야 할 것. UUID 인지 아닌지 확인.
        //JWT 밸리데이트 과정 추가.
        Map map = new HashMap();

        //필요한 값을 검증한다.
        String[] params = new String[]{"access_token"};
        if (!this.checkParameters(params, accessTokenResponse)) {
            return;
        }

        String token = accessTokenResponse.getAccessToken();
        boolean matches = token.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");

        //Bearer 토큰일경우
        if (matches) {
            OauthAccessToken accessToken = tokenRepository.findByToken(accessTokenResponse.getAccessToken());
            if (accessToken == null) {
                accessTokenResponse.setError(OauthConstant.INVALID_TOKEN);
                accessTokenResponse.setError_description("Requested access_token is not exist.");
                this.responseToken(accessTokenResponse);
                return;
            }
            OauthClient oauthClient = clientService.selectByClientKey(accessToken.getClientKey());
            OauthUser oauthUser = userRepository.findByUserName(accessToken.getUserName());

            //코드의 발급시간을 확인한다.
            Long regDate = accessToken.getRegDate();
            Date currentTime = new Date();
            Date expirationTime = new Date(regDate + oauthClient.getAccessTokenLifetime() * 1000);
            long diff = (long) Math.floor((expirationTime.getTime() - currentTime.getTime()) / 1000);

            if (diff <= 0) {
                accessTokenResponse.setError(OauthConstant.INVALID_TOKEN);
                accessTokenResponse.setError_description("requested access_token has expired.");
                this.responseToken(accessTokenResponse);
                return;
            } else {
                map.put("expires_in", diff);
            }


            map.put("client", oauthClient.getClientKey());
            if (oauthUser != null) {
                map.put("username", oauthUser.getUserName());
            }
            if (!StringUtils.isEmpty(accessToken.getRefreshToken())) {
                map.put("refreshToken", accessToken.getRefreshToken());
            }
            map.put("type", accessToken.getType());
            map.put("scope", accessToken.getScopes());

            String marshal = JsonUtils.marshal(map);
            String prettyPrint = JsonFormatterUtils.prettyPrint(marshal);

            //TODO preTokenInfo 수행

            HttpServletResponse response = accessTokenResponse.getResponse();
            response.setStatus(200);
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.getWriter().write(prettyPrint);
        }

        //JWT 토큰일경우
        else {

            JWTClaimsSet jwtClaimsSet = JwtUtils.parseToken(token);

            //이슈어 확인
            String jwtIssuer = jwtClaimsSet.getIssuer();
            if (!issuer.equals(jwtIssuer)) {
                accessTokenResponse.setError(OauthConstant.INVALID_TOKEN);
                accessTokenResponse.setError_description("Invalid issuer.");
                this.responseToken(accessTokenResponse);
                return;
            }

            boolean validated = JwtUtils.validateToken(token, key);
            if (!validated) {
                accessTokenResponse.setError(OauthConstant.INVALID_TOKEN);
                accessTokenResponse.setError_description("Invalid token secret.");
                this.responseToken(accessTokenResponse);
                return;
            }

            //코드의 발급시간을 확인한다.
            Date currentTime = new Date();
            Date expirationTime = jwtClaimsSet.getExpirationTime();
            long diff = (long) Math.floor((expirationTime.getTime() - currentTime.getTime()) / 1000);

            if (diff <= 0) {
                accessTokenResponse.setError(OauthConstant.INVALID_TOKEN);
                accessTokenResponse.setError_description("requested access_token has expired.");
                this.responseToken(accessTokenResponse);
                return;
            } else {
                map.put("expires_in", diff);
            }

            Map<String, Object> claims = jwtClaimsSet.getClaims();
            map.putAll(claims);

            String marshal = JsonUtils.marshal(map);
            String prettyPrint = JsonFormatterUtils.prettyPrint(marshal);

            //커스텀 토큰 스크립트를 수행한다.
            Map context = (Map) claims.get("context");
            String clientKey = context.get("clientKey") != null ? (String) context.get("clientKey") : null;
            String type = context.get("type") != null ? (String) context.get("type") : null;
            List<String> scopes = context.get("scopes") != null ? (List<String>) context.get("scopes") : null;
            String userName = context.get("userName") != null ? (String) context.get("userName") : null;
            OauthUser user = null;
            if (context.get("user") != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                user = objectMapper.convertValue(context.get("user"), OauthUser.class);
            }
            Map claim = (Map) claims.get("claim");

            //TODO preTokenInfo 수행

            HttpServletResponse response = accessTokenResponse.getResponse();
            response.setStatus(200);
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.getWriter().write(prettyPrint);
        }
    }

    @Override
    public void processRefreshToken(AccessTokenResponse accessTokenResponse) throws Exception {
        //필요한 값을 검증한다.
        String[] params = new String[]{"client_id", "client_secret", "grant_type", "refresh_token"};
        if (!this.checkParameters(params, accessTokenResponse)) {
            return;
        }

        //클레임을 검증한다.
        if (!this.checkClaim(accessTokenResponse)) {
            return;
        }

        //클라이언트를 검증한다.
        OauthClient oauthClient = clientService.selectByClientKey(accessTokenResponse.getClientId());
        if (!this.checkClient(oauthClient, accessTokenResponse)) {
            return;
        }
        accessTokenResponse.setOauthClient(oauthClient);

        //클라이언트가 리프레쉬 토큰을 허용하는지 알아본다.
        if (!oauthClient.getRefreshTokenValidity()) {
            accessTokenResponse.setError(OauthConstant.UNSUPPORTED_GRANT_TYPE);
            accessTokenResponse.setError_description("Requested client does not support grant_type refresh_token");
            this.responseToken(accessTokenResponse);
            return;
        }

        //올드 리프레쉬 토큰을 찾는다.
        //올드 리프레쉬 토큰이 있고, 1분 이내에 발급되었다면, 그대로 내보낸다.
        OauthAccessToken oldAccessToken = tokenRepository.findByOldRefreshToken(accessTokenResponse.getRefreshToken());
        if (oldAccessToken != null) {
            Long regDate = oldAccessToken.getRegDate();
            long diff = (long) Math.floor((new Date().getTime() - regDate) / 1000);
            if (diff < oldRefreshTokenTimeout) {
                accessTokenResponse.setAccessToken(oldAccessToken.getToken());
                boolean matches = oldAccessToken.getToken().matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
                //JWT 토큰일경우
                if (!matches) {
                    accessTokenResponse.setTokenType("JWT");
                    accessTokenResponse.setExpiresIn(accessTokenResponse.getOauthClient().getAccessTokenLifetime() - (int) diff);
                } else {
                    accessTokenResponse.setTokenType("Bearer");
                    accessTokenResponse.setExpiresIn(accessTokenResponse.getOauthClient().getAccessTokenLifetime() - (int) diff);
                }
                accessTokenResponse.setRefreshToken(oldAccessToken.getRefreshToken());
                this.responseToken(accessTokenResponse);
                return;
            }
        }

        //어세스 토큰을 찾는다.
        OauthAccessToken accessToken = tokenRepository.findByRefreshToken(accessTokenResponse.getRefreshToken());
        if (accessToken == null) {
            accessTokenResponse.setError(OauthConstant.INVALID_TOKEN);
            accessTokenResponse.setError_description("Requested refresh_token is not exist.");
            this.responseToken(accessTokenResponse);
            return;
        }

        //어세스 토큰의 발급자 확인
        if (!accessToken.getClientKey().equals(oauthClient.getClientKey())) {
            accessTokenResponse.setError(OauthConstant.ACCESS_DENIED);
            accessTokenResponse.setError_description("client does not have authority to requested refresh_token.");
            this.responseToken(accessTokenResponse);
            return;
        }

        //어세스 토큰의 발급시간을 확인한다.
        Long regDate = accessToken.getRegDate();
        Long refreshTokenLifetime = oauthClient.getRefreshTokenLifetime();
        Date expirationTime = new Date(regDate + (refreshTokenLifetime * 1000));
        int compareTo = new Date().compareTo(expirationTime);
        if (compareTo > 0) {
            accessTokenResponse.setError(OauthConstant.ACCESS_DENIED);
            accessTokenResponse.setError_description("requested refresh_token has expired.");
            this.responseToken(accessTokenResponse);
            return;
        }

        //스코프 설정
        accessTokenResponse.setScope(Joiner.on(",").join(accessToken.getScopes()));

        //유저 설정,토큰 타입 설정
        String type;
        if (accessToken.getType().equals("user")) {
            type = "user";
            OauthUser oauthUser = userRepository.findByUserName(accessToken.getUserName());
            accessTokenResponse.setOauthUser(oauthUser);
        } else {
            type = "client";
        }

        //토큰 요청이 JWT 이고, 요청에서 claim 이 넘어오지 않았다면, claim 을 덮어쓴다.
        if ("JWT".equals(accessTokenResponse.getTokenType()) && StringUtils.isEmpty(accessTokenResponse.getClaim())) {
            String token = accessToken.getToken();
            boolean matches = token.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
            //JWT 토큰일경우
            if (!matches) {
                JWTClaimsSet jwtClaimsSet = JwtUtils.parseToken(token);
                Map<String, Object> claims = jwtClaimsSet.getClaims();
                String claim = JsonUtils.marshal((Map) claims.get("claim"));
                accessTokenResponse.setClaim(claim);
            }
        }

        //TODO preRefreshToken 수행

        //어세스 토큰을 만들고 저장한다.
        //기존 리프레쉬 토큰을 어세스 토큰에 같이 저장한다.
        accessTokenResponse.setSaveWithOldRefreshToken(true);
        AccessTokenResponse tokenResponse = this.insertAccessToken(accessTokenResponse, type);

        //기존 토큰은 삭제한다.
        tokenRepository.delete(accessToken);

        //리스폰스를 수행한다.
        this.responseToken(tokenResponse);
    }

    @Override
    public void processCodeGrant(AccessTokenResponse accessTokenResponse) throws Exception {

        //필요한 값을 검증한다.
        String[] params = new String[]{"client_id", "client_secret", "grant_type", "code"};
        if (!this.checkParameters(params, accessTokenResponse)) {
            return;
        }

        //클레임을 검증한다.
        if (!this.checkClaim(accessTokenResponse)) {
            return;
        }

        //클라이언트를 검증한다.
        OauthClient oauthClient = clientService.selectByClientKey(accessTokenResponse.getClientId());
        if (!this.checkClient(oauthClient, accessTokenResponse)) {
            return;
        }
        accessTokenResponse.setOauthClient(oauthClient);

        //클라이언트의 그런트 타입 허용 범위를 체크한다.
        List grantTypes = Arrays.asList(oauthClient.getAuthorizedGrantTypes());
        if (!grantTypes.contains("code")) {
            accessTokenResponse.setError(OauthConstant.UNSUPPORTED_GRANT_TYPE);
            accessTokenResponse.setError_description("Requested client does not support grant_type authorization_code");
            this.responseToken(accessTokenResponse);
            return;
        }

        //리다이렉트 유알엘을 검증한다.
        //요청된 리다이렉트 유알엘 파라미터가 없으면 미리 등재된 유알엘을 등록한다.
        //미리 등재된 유알엘도 없다면 검증을 실패처리한다.
        if (StringUtils.isEmpty(accessTokenResponse.getRedirectUri())) {
            accessTokenResponse.setRedirectUri(oauthClient.getWebServerRedirectUri());
        }
        params = new String[]{"redirect_uri"};
        if (!this.checkParameters(params, accessTokenResponse)) {
            return;
        }
        //리다이렉트 유알엘이 미리 등재된 유알엘과 같은지 검증한다.
        //리다이렉트 유알엘을 사용자가 파라미터로 등록했을 경우만 해당된다.
        if (!accessTokenResponse.getRedirectUri().equals(oauthClient.getWebServerRedirectUri())) {
            accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
            accessTokenResponse.setError_description("redirect_uri is not match to previously stored redirect_uri.");
            this.responseToken(accessTokenResponse);
            return;
        }

        //코드를 찾는다.
        OauthCode oauthCode = codeRepository.findByCodeAndClientKey(accessTokenResponse.getCode(), oauthClient.getClientKey());
        if (oauthCode == null) {
            accessTokenResponse.setError(OauthConstant.ACCESS_DENIED);
            accessTokenResponse.setError_description("requested code is not exist.");
            this.responseToken(accessTokenResponse);
            return;
        }
        //코드의 발급시간을 확인한다.
        Long regDate = oauthCode.getRegDate();
        Date expirationTime = new Date(regDate + oauthClient.getCodeLifetime() * 1000);
        int compareTo = new Date().compareTo(expirationTime);
        if (compareTo > 0) {
            accessTokenResponse.setError(OauthConstant.ACCESS_DENIED);
            accessTokenResponse.setError_description("requested code has expired.");
            this.responseToken(accessTokenResponse);
            return;
        }

        //유저를 등록한다.
        OauthUser oauthUser = userRepository.findByUserName(oauthCode.getUserName());
        if (oauthUser == null) {
            accessTokenResponse.setError(OauthConstant.ACCESS_DENIED);
            accessTokenResponse.setError_description("requested user is not exist.");
            this.responseToken(accessTokenResponse);
            return;
        }
        accessTokenResponse.setOauthUser(oauthUser);

        accessTokenResponse.setScope(Joiner.on(",").join(oauthCode.getScopes()));

        //TODO preCodeGrant 수행

        //어세스 토큰을 만들고 저장한다.
        this.insertAccessToken(accessTokenResponse, "user");

        //리스폰스를 수행한다.
        this.responseToken(accessTokenResponse);
    }

    @Override
    public void processPasswordGrant(AccessTokenResponse accessTokenResponse) throws Exception {
        //필요한 값을 검증한다.
        String[] params = new String[]{"client_id", "client_secret", "grant_type", "username", "password", "scope"};
        if (!this.checkParameters(params, accessTokenResponse)) {
            return;
        }

        //클레임을 검증한다.
        if (!this.checkClaim(accessTokenResponse)) {
            return;
        }

        //클라이언트를 검증한다.
        OauthClient oauthClient = clientService.selectByClientKey(accessTokenResponse.getClientId());
        if (!this.checkClient(oauthClient, accessTokenResponse)) {
            return;
        }
        accessTokenResponse.setOauthClient(oauthClient);

        //클라이언트의 그런트 타입 허용 범위를 체크한다.
        List grantTypes = Arrays.asList(oauthClient.getAuthorizedGrantTypes());
        if (!grantTypes.contains("password")) {
            accessTokenResponse.setError(OauthConstant.UNSUPPORTED_GRANT_TYPE);
            accessTokenResponse.setError_description("Requested client does not support grant_type password");
            this.responseToken(accessTokenResponse);
            return;
        }

        //스코프를 검증한다.
        List<OauthScope> clientScopes = scopeService.selectClientScopes(oauthClient.getClientKey());
        List<OauthScope> requestScopes = new ArrayList<OauthScope>();
        List<String> enabelScopesNames = new ArrayList<>();
        for (int i = 0; i < clientScopes.size(); i++) {
            enabelScopesNames.add(clientScopes.get(i).getName());
        }
        List<String> requestScopesNames = Arrays.asList(accessTokenResponse.getScope().split(","));
        for (int i = 0; i < requestScopesNames.size(); i++) {
            String scopeName = requestScopesNames.get(i);
            if (!enabelScopesNames.contains(scopeName)) {
                accessTokenResponse.setError(OauthConstant.INVALID_SCOPE);
                accessTokenResponse.setError_description("Client dost not have requested scope");
                this.responseToken(accessTokenResponse);
                return;
            } else {
                requestScopes.add(scopeService.selectByName(scopeName));
            }
        }
        accessTokenResponse.setOauthScopes(requestScopes);

        //유저를 찾는다.
        OauthUser oauthUser = userRepository.findByUserNameAndUserPassword(accessTokenResponse.getUsername(), accessTokenResponse.getPassword());
        if (oauthUser == null) {
            accessTokenResponse.setError(OauthConstant.ACCESS_DENIED);
            accessTokenResponse.setError_description("requested user is not exist.");
            this.responseToken(accessTokenResponse);
            return;
        }
        accessTokenResponse.setOauthUser(oauthUser);

        //TODO prePasswordGrant 수행

        //어세스 토큰을 만들고 저장한다.
        this.insertAccessToken(accessTokenResponse, "user");

        //리스폰스를 수행한다.
        this.responseToken(accessTokenResponse);
    }

    @Override
    public void processClientCredentialsGrant(AccessTokenResponse accessTokenResponse) throws Exception {
        //필요한 값을 검증한다.
        String[] params = new String[]{"client_id", "client_secret", "grant_type", "scope"};
        if (!this.checkParameters(params, accessTokenResponse)) {
            return;
        }

        //클레임을 검증한다.
        if (!this.checkClaim(accessTokenResponse)) {
            return;
        }

        //클라이언트를 검증한다.
        OauthClient oauthClient = clientService.selectByClientKey(accessTokenResponse.getClientId());
        if (!this.checkClient(oauthClient, accessTokenResponse)) {
            return;
        }
        accessTokenResponse.setOauthClient(oauthClient);

        //클라이언트의 그런트 타입 허용 범위를 체크한다.
        List grantTypes = Arrays.asList(oauthClient.getAuthorizedGrantTypes());
        if (!grantTypes.contains("credentials")) {
            accessTokenResponse.setError(OauthConstant.UNSUPPORTED_GRANT_TYPE);
            accessTokenResponse.setError_description("Requested client does not support grant_type client_credentials");
            this.responseToken(accessTokenResponse);
            return;
        }

        //스코프를 검증한다.
        List<OauthScope> clientScopes = scopeService.selectClientScopes(oauthClient.getClientKey());
        List<OauthScope> requestScopes = new ArrayList<OauthScope>();
        List<String> enabelScopesNames = new ArrayList<>();
        for (int i = 0; i < clientScopes.size(); i++) {
            enabelScopesNames.add(clientScopes.get(i).getName());
        }
        List<String> requestScopesNames = Arrays.asList(accessTokenResponse.getScope().split(","));
        for (int i = 0; i < requestScopesNames.size(); i++) {
            String scopeName = requestScopesNames.get(i);
            if (!enabelScopesNames.contains(scopeName)) {
                accessTokenResponse.setError(OauthConstant.INVALID_SCOPE);
                accessTokenResponse.setError_description("Client dost not have requested scope");
                this.responseToken(accessTokenResponse);
                return;
            } else {
                requestScopes.add(scopeService.selectByName(scopeName));
            }
        }
        accessTokenResponse.setOauthScopes(requestScopes);

        //TODO preClientCredentialsGrantGrant 수행

        //어세스 토큰을 만들고 저장한다.
        this.insertAccessToken(accessTokenResponse, "client");

        //리스폰스를 수행한다.
        this.responseToken(accessTokenResponse);
    }

    @Override
    public void processJWTGrant(AccessTokenResponse accessTokenResponse) throws Exception {
        //필요한 값을 검증한다.
        String[] params = new String[]{"assertion", "scope"};
        if (!this.checkParameters(params, accessTokenResponse)) {
            return;
        }

        //토큰을 파싱한다.
        String jwtToken = accessTokenResponse.getAssertion();
        JWTClaimsSet jwtClaimsSet = null;
        try {
            jwtClaimsSet = JwtUtils.parseToken(jwtToken);
        } catch (Exception ex) {
            accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
            accessTokenResponse.setError_description("incorrect jwt token format.");
            this.responseToken(accessTokenResponse);
            return;
        }

        String issuer = jwtClaimsSet.getIssuer();

        //TODO 아래 두 값을 검증하는 방법을 찾아봐야 한다.
        Date issueTime = jwtClaimsSet.getIssueTime();
        Date expirationTime = jwtClaimsSet.getExpirationTime();


        //클라이언트를 찾는다.
        accessTokenResponse.setClientId(issuer);
        OauthClient oauthClient = clientService.selectByClientKey(accessTokenResponse.getClientId());
        if (oauthClient == null) {
            accessTokenResponse.setError(OauthConstant.UNAUTHORIZED_CLIENT);
            accessTokenResponse.setError_description("Requested client is not exist.");
            this.responseToken(accessTokenResponse);
            return;
        }

        //Jwt를 검증한다.
        boolean validatedToken = false;
        validatedToken = JwtUtils.validateToken(jwtToken, key);
        if (!validatedToken) {
            accessTokenResponse.setError(OauthConstant.ACCESS_DENIED);
            accessTokenResponse.setError_description("Invalid jwt signature.");
            this.responseToken(accessTokenResponse);
            return;
        }

        //Jwt 타임아웃 체크
        validatedToken = JwtUtils.validateToken(jwtToken, key, expirationTime);
        if (!validatedToken) {
            accessTokenResponse.setError(OauthConstant.ACCESS_DENIED);
            accessTokenResponse.setError_description("jwt token is expired.");
            this.responseToken(accessTokenResponse);
            return;
        }

        //클라이언트 액티브를 체크한다.
        if (!oauthClient.getActiveClient()) {
            accessTokenResponse.setError(OauthConstant.UNAUTHORIZED_CLIENT);
            accessTokenResponse.setError_description("Requested client is not active.");
            this.responseToken(accessTokenResponse);
            return;
        }
        accessTokenResponse.setOauthClient(oauthClient);


        //클라이언트의 그런트 타입 허용 범위를 체크한다.
        List grantTypes = Arrays.asList(oauthClient.getAuthorizedGrantTypes());
        if (!grantTypes.contains("credentials")) {
            accessTokenResponse.setError(OauthConstant.UNSUPPORTED_GRANT_TYPE);
            accessTokenResponse.setError_description("Requested client does not support grant_type client_credentials");
            this.responseToken(accessTokenResponse);
            return;
        }

        //스코프를 검증한다.
        List<OauthScope> clientScopes = scopeService.selectClientScopes(oauthClient.getClientKey());
        List<OauthScope> requestScopes = new ArrayList<OauthScope>();
        List<String> enabelScopesNames = new ArrayList<>();
        for (int i = 0; i < clientScopes.size(); i++) {
            enabelScopesNames.add(clientScopes.get(i).getName());
        }
        List<String> requestScopesNames = Arrays.asList(accessTokenResponse.getScope().split(","));
        for (int i = 0; i < requestScopesNames.size(); i++) {
            String scopeName = requestScopesNames.get(i);
            if (!enabelScopesNames.contains(scopeName)) {
                accessTokenResponse.setError(OauthConstant.INVALID_SCOPE);
                accessTokenResponse.setError_description("Client dost not have requested scope");
                this.responseToken(accessTokenResponse);
                return;
            } else {
                requestScopes.add(scopeService.selectByName(scopeName));
            }
        }
        accessTokenResponse.setOauthScopes(requestScopes);

        //TODO preJWTGrantGrant 수행

        //어세스토큰을 만들고 저장한다.
        OauthAccessToken accessToken = new OauthAccessToken();
        accessToken.setType("client");
        accessToken.setScopes(Arrays.asList(accessTokenResponse.getScope().split(",")));
        accessToken.setToken(UUID.randomUUID().toString());
        accessToken.setClientKey(oauthClient.getClientKey());
        if (oauthClient.getRefreshTokenValidity()) {
            accessToken.setRefreshToken(UUID.randomUUID().toString());
        }

        tokenRepository.save(accessToken);

        //리스폰스에 리턴값을 세팅한다.
        accessTokenResponse.setTokenType("Bearer");
        accessTokenResponse.setAccessToken(accessToken.getToken());
        accessTokenResponse.setExpiresIn(oauthClient.getAccessTokenLifetime());
        if (oauthClient.getRefreshTokenValidity()) {
            accessTokenResponse.setRefreshToken(accessToken.getRefreshToken());
        }

        //리스폰스를 수행한다.
        this.responseToken(accessTokenResponse);
    }

    @Override
    public void responseToken(AccessTokenResponse accessTokenResponse) {
        try {
            if (accessTokenResponse.getError() != null) {
                Map map = new HashMap();
                map.put("error", accessTokenResponse.getError());
                map.put("error_description", accessTokenResponse.getError_description());

                String marshal = JsonUtils.marshal(map);
                String prettyPrint = JsonFormatterUtils.prettyPrint(marshal);
                System.out.println(prettyPrint);

                HttpServletResponse response = accessTokenResponse.getResponse();

                response.setStatus(400);
                response.setHeader("Content-Type", "application/json;charset=UTF-8");
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Pragma", "no-cache");
                response.getWriter().write(prettyPrint);
            } else {
                Map map = new HashMap();

                map.put("access_token", accessTokenResponse.getAccessToken());
                map.put("token_type", accessTokenResponse.getTokenType());
                map.put("expires_in", accessTokenResponse.getExpiresIn());
                if (!StringUtils.isEmpty(accessTokenResponse.getRefreshToken())) {
                    map.put("refresh_token", accessTokenResponse.getRefreshToken());
                }

                String marshal = JsonUtils.marshal(map);
                String prettyPrint = JsonFormatterUtils.prettyPrint(marshal);

                HttpServletResponse response = accessTokenResponse.getResponse();

                response.setStatus(200);
                response.setHeader("Content-Type", "application/json;charset=UTF-8");
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Pragma", "no-cache");
                response.getWriter().write(prettyPrint);
            }
        } catch (IOException ex) {
            //response 전달 과정 중 실패가 일어나더라도 프로세스에는 영향을 끼지지 않는다.
            ExceptionUtils.httpExceptionResponse(ex, accessTokenResponse.getResponse());
        }
    }

    private boolean checkParameters(String[] params, AccessTokenResponse accessTokenResponse) {
        List<String> list = Arrays.asList(params);
        for (int i = 0; i < list.size(); i++) {
            String paramName = list.get(i);
            switch (paramName) {
                case "client_id":
                    if (StringUtils.isEmpty(accessTokenResponse.getClientId())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("client_id is required.");
                    }
                    break;
                case "client_secret":
                    if (StringUtils.isEmpty(accessTokenResponse.getClientSecret())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("client_secret is required.");
                    }
                    break;
                case "grant_type":
                    if (StringUtils.isEmpty(accessTokenResponse.getGrant_type())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("grant_type is required.");
                    }
                    break;
                case "code":
                    if (StringUtils.isEmpty(accessTokenResponse.getCode())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("code is required.");
                    }
                    break;
                case "username":
                    if (StringUtils.isEmpty(accessTokenResponse.getUsername())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("username is required.");
                    }
                    break;
                case "password":
                    if (StringUtils.isEmpty(accessTokenResponse.getPassword())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("password is required.");
                    }
                    break;
                case "scope":
                    if (StringUtils.isEmpty(accessTokenResponse.getScope())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("scope is required.");
                    }
                    break;
                case "redirect_uri":
                    if (StringUtils.isEmpty(accessTokenResponse.getRedirectUri())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("Requested client does not have default redirect_uri. You must set redirect_uri in your parameters.");
                    }
                    break;
                case "assertion":
                    if (StringUtils.isEmpty(accessTokenResponse.getAssertion())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("assertion is required.");
                    }
                    break;
                case "access_token":
                    if (StringUtils.isEmpty(accessTokenResponse.getAccessToken())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("access_token is required.");
                    }
                    break;
                case "refresh_token":
                    if (StringUtils.isEmpty(accessTokenResponse.getRefreshToken())) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("refresh_token is required.");
                    }
                    break;
            }
            if (accessTokenResponse.getError() != null) {
                this.responseToken(accessTokenResponse);
                return false;
            }
        }
        return true;
    }

    private boolean checkClient(OauthClient oauthClient, AccessTokenResponse accessTokenResponse) {
        if (oauthClient == null) {
            accessTokenResponse.setError(OauthConstant.UNAUTHORIZED_CLIENT);
            accessTokenResponse.setError_description("Requested client is not exist.");
            this.responseToken(accessTokenResponse);
            return false;
        }

        //클라이언트 액티브를 체크한다.
        if (!oauthClient.getActiveClient()) {
            accessTokenResponse.setError(OauthConstant.UNAUTHORIZED_CLIENT);
            accessTokenResponse.setError_description("Requested client is not active.");
            this.responseToken(accessTokenResponse);
            return false;
        }

        //클라이언트 비밀번호를 체크한다.
        if (!oauthClient.getClientSecret().equals(accessTokenResponse.getClientSecret())) {
            accessTokenResponse.setError(OauthConstant.ACCESS_DENIED);
            accessTokenResponse.setError_description("client_secret is not match.");
            this.responseToken(accessTokenResponse);
            return false;
        }

        return true;
    }

    private boolean checkClaim(AccessTokenResponse accessTokenResponse) {
        String claim = accessTokenResponse.getClaim();
        if (!StringUtils.isEmpty(accessTokenResponse.getTokenType())) {
            if ("JWT".equals(accessTokenResponse.getTokenType())) {
                if (!StringUtils.isEmpty(accessTokenResponse.getClaim())) {
                    try {
                        JsonUtils.unmarshal(claim);
                    } catch (IOException ex) {
                        accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                        accessTokenResponse.setError_description("claim for jwt must be a json object string format");
                        this.responseToken(accessTokenResponse);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private AccessTokenResponse insertAccessToken(AccessTokenResponse accessTokenResponse, String type) throws Exception {
        //어세스토큰을 만들고 저장한다.
        OauthAccessToken accessToken = new OauthAccessToken();

        if (type.equals("user")) {
            accessToken.setType("user");
            accessToken.setUserName(accessTokenResponse.getOauthUser().getUserName());
        } else {
            accessToken.setType("client");
        }
        accessToken.setScopes(Arrays.asList(accessTokenResponse.getScope().split(",")));
        accessToken.setToken(UUID.randomUUID().toString());
        accessToken.setClientKey(accessTokenResponse.getOauthClient().getClientKey());

        if (accessTokenResponse.getOauthClient().getRefreshTokenValidity()) {
            accessToken.setRefreshToken(UUID.randomUUID().toString());
        }

        //이전 리프레쉬 토큰을 함께 저장해야 하는 경우(리프레쉬 토큰 로직인 경우)
        if (accessTokenResponse.getSaveWithOldRefreshToken()) {
            accessToken.setOldRefreshToken(accessTokenResponse.getRefreshToken());
        }

        //토큰 타입이 명시되지 않으면 배리어이다.
        if (accessTokenResponse.getTokenType() == null) {
            accessTokenResponse.setTokenType("Bearer");
        }

        if ("JWT".equals(accessTokenResponse.getTokenType())) {
            String jwtToken = tokenService.generateJWTToken(accessTokenResponse.getOauthUser(),
                    accessTokenResponse.getOauthClient(),
                    accessToken,
                    accessTokenResponse.getClaim(),
                    accessTokenResponse.getOauthClient().getAccessTokenLifetime(),
                    type);
            accessToken.setToken(jwtToken);

            //리스폰스에 리턴값을 세팅한다.
            accessTokenResponse.setTokenType("JWT");
            accessTokenResponse.setAccessToken(accessToken.getToken());
            if (accessTokenResponse.getOauthClient().getRefreshTokenValidity()) {
                accessTokenResponse.setRefreshToken(accessToken.getRefreshToken());
            }
            accessTokenResponse.setExpiresIn(accessTokenResponse.getOauthClient().getAccessTokenLifetime());
        } else {
            //리스폰스에 리턴값을 세팅한다.
            accessTokenResponse.setTokenType("Bearer");
            accessTokenResponse.setAccessToken(accessToken.getToken());
            if (accessTokenResponse.getOauthClient().getRefreshTokenValidity()) {
                accessTokenResponse.setRefreshToken(accessToken.getRefreshToken());
            }
            accessTokenResponse.setExpiresIn(accessTokenResponse.getOauthClient().getAccessTokenLifetime());
        }

        tokenRepository.save(accessToken);
        return accessTokenResponse;
    }
}
