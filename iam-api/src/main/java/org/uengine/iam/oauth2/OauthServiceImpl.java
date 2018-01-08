package org.uengine.iam.oauth2;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthclient.OauthClientService;
import org.uengine.iam.oauthscope.OauthScope;
import org.uengine.iam.oauthscope.OauthScopeService;
import org.uengine.iam.oauthtoken.*;
import org.uengine.iam.oauthuser.OauthUser;
import org.uengine.iam.util.HttpUtils;
import org.uengine.iam.util.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
public class OauthServiceImpl implements OauthService {

    @Autowired
    private OauthClientService clientService;

    @Autowired
    private OauthScopeService scopeService;

    @Autowired
    private OauthTokenService tokenService;

    @Autowired
    private OauthGrantService grantService;

    @Autowired
    private OauthCodeRepository codeRepository;

    @Autowired
    private OauthTokenRepository tokenRepository;

    /**
     * Authorization Code,Implicit Grant 의 최초 요청을 검증하는 단계.
     *
     * @param request
     * @return
     */
    @Override
    public AuthorizeResponse validateAuthorize(HttpServletRequest request) {

        AuthorizeResponse authorizeResponse = new AuthorizeResponse();

        authorizeResponse.setClientId(request.getParameter("client_id"));
        if (StringUtils.isEmpty(authorizeResponse.getClientId())) {
            authorizeResponse.setClientId(request.getHeader("client-key"));
        }

        authorizeResponse.setResponseType(request.getParameter("response_type"));
        authorizeResponse.setRedirectUri(request.getParameter("redirect_uri"));
        authorizeResponse.setScope(request.getParameter("scope"));
        authorizeResponse.setState(request.getParameter("state"));
        authorizeResponse.setTokenType(request.getParameter("token_type"));
        authorizeResponse.setClaim(request.getParameter("claim"));

        //필요한 값을 검증한다.
        if (StringUtils.isEmpty(authorizeResponse.getClientId())) {
            authorizeResponse.setError(OauthConstant.INVALID_REQUEST);
            authorizeResponse.setError_description("client_id is required.");
            return authorizeResponse;
        }
        if (StringUtils.isEmpty(authorizeResponse.getResponseType())) {
            authorizeResponse.setError(OauthConstant.INVALID_REQUEST);
            authorizeResponse.setError_description("response_type is required.");
            return authorizeResponse;
        }
        if (StringUtils.isEmpty(authorizeResponse.getScope())) {
            authorizeResponse.setError(OauthConstant.INVALID_REQUEST);
            authorizeResponse.setError_description("scope is required.");
            return authorizeResponse;
        }

        //클라이언트를 얻는다
        OauthClient oauthClient = clientService.selectByClientKey(authorizeResponse.getClientId());
        if (oauthClient == null) {
            authorizeResponse.setError(OauthConstant.UNAUTHORIZED_CLIENT);
            authorizeResponse.setError_description("Requested client is not exist.");
            return authorizeResponse;
        }
        authorizeResponse.setOauthClient(oauthClient);

        //클라이언트의 리스폰트 타입 허용 범위를 체크한다.
        String responseType = authorizeResponse.getResponseType();
        List grantTypes = Arrays.asList(oauthClient.getAuthorizedGrantTypes());
        switch (responseType) {
            case "code":
                if (!grantTypes.contains("code")) {
                    authorizeResponse.setError(OauthConstant.UNSUPPORTED_RESPONSE_TYPE);
                    authorizeResponse.setError_description("Requested client does not support response_type code");
                    return authorizeResponse;
                }
                break;
            case "token":
                if (!grantTypes.contains("implicit")) {
                    authorizeResponse.setError(OauthConstant.UNSUPPORTED_RESPONSE_TYPE);
                    authorizeResponse.setError_description("Requested client does not support response_type token");
                    return authorizeResponse;
                }
                break;
            default:
                authorizeResponse.setError(OauthConstant.UNSUPPORTED_RESPONSE_TYPE);
                authorizeResponse.setError_description("response_type must be code or token");
                return authorizeResponse;
        }

        //클레임을 검증한다.
        String claim = authorizeResponse.getClaim();
        if (responseType.equals("token")) {
            if (!StringUtils.isEmpty(authorizeResponse.getTokenType())) {
                if ("JWT".equals(authorizeResponse.getTokenType())) {
                    if (!StringUtils.isEmpty(authorizeResponse.getClaim())) {
                        try {
                            JsonUtils.unmarshal(claim);
                        } catch (IOException ex) {
                            authorizeResponse.setError(OauthConstant.INVALID_REQUEST);
                            authorizeResponse.setError_description("claim for jwt must be a url encoded json object string format");
                            return authorizeResponse;
                        }
                    }
                }
            }
        }

        //리다이렉트 유알엘을 검증한다.
        if (StringUtils.isEmpty(authorizeResponse.getRedirectUri())) {
            authorizeResponse.setRedirectUri(oauthClient.getWebServerRedirectUri());
        }
        if (StringUtils.isEmpty(authorizeResponse.getRedirectUri())) {
            authorizeResponse.setError(OauthConstant.INVALID_REQUEST);
            authorizeResponse.setError_description("Requested client does not have default redirect_uri. You must set redirect_uri in your parameters.");
            return authorizeResponse;
        }

        //스코프를 검증한다.
        List<OauthScope> clientScopes = scopeService.selectClientScopes(oauthClient.getClientKey());
        List<OauthScope> requestScopes = new ArrayList<OauthScope>();
        List<String> enableScopesNames = new ArrayList<>();
        for (int i = 0; i < clientScopes.size(); i++) {
            enableScopesNames.add(clientScopes.get(i).getName());
        }

        List<String> requestScopesNames = Arrays.asList(authorizeResponse.getScope().split(","));
        for (int i = 0; i < requestScopesNames.size(); i++) {
            String scopeName = requestScopesNames.get(i);
            if (!enableScopesNames.contains(scopeName)) {
                authorizeResponse.setError(OauthConstant.INVALID_SCOPE);
                authorizeResponse.setError_description("Client dost not have requested scope");
                return authorizeResponse;
            } else {
                requestScopes.add(scopeService.selectByName(scopeName));
            }
        }
        authorizeResponse.setOauthScopes(requestScopes);
        return authorizeResponse;
    }

    /**
     * Authorization Code,Implicit Grant 에서 사용자 인증을 마친 후 진행하는 단계
     *
     * @param authorizeResponse
     * @param response
     * @throws IOException
     */
    @Override
    public void processAuthorize(AuthorizeResponse authorizeResponse, HttpServletResponse response) throws IOException {

        //리다이렉트 유알엘이 있을경우만 수행.
        if (StringUtils.isEmpty(authorizeResponse.getRedirectUri())) {
            return;
        }
        Map params = new HashMap();
        try {
            switch (authorizeResponse.getResponseType()) {
                //코드일경우
                //1. 코드를 만들고 저장.
                //2. 코드와 스테이트를 리턴
                case "code":
                    OauthCode oauthCode = new OauthCode();
                    oauthCode.setClientKey(authorizeResponse.getOauthClient().getClientKey());
                    oauthCode.setUserName(authorizeResponse.getOauthUser().getUserName());
                    oauthCode.setCode(UUID.randomUUID().toString());
                    oauthCode.setScopes(Arrays.asList(authorizeResponse.getScope().split(",")));
                    codeRepository.save(oauthCode);

                    params.put("code", oauthCode.getCode());
                    params.put("state", authorizeResponse.getState());
                    break;

                //토큰일경우
                //어세스토큰을 만든다.
                //리스레쉬토큰이 허용될 경우 리프레쉬 토큰도 만든다.
                //access_token,token_type,expires_in,scope,state 리턴
                //토큰 타입은 bearer 이다.
                //토큰 타입이 JWT 일 경우 jwt 토큰 제너레이션
                case "token":

                    OauthAccessToken accessToken = new OauthAccessToken();

                    accessToken.setType("user");
                    accessToken.setScopes(Arrays.asList(authorizeResponse.getScope().split(",")));
                    accessToken.setToken(UUID.randomUUID().toString());
                    accessToken.setUserName(authorizeResponse.getOauthUser().getUserName());
                    accessToken.setClientKey(authorizeResponse.getOauthClient().getClientKey());

                    if (authorizeResponse.getOauthClient().getRefreshTokenValidity()) {
                        accessToken.setRefreshToken(UUID.randomUUID().toString());
                    }

                    if ("JWT".equals(authorizeResponse.getTokenType())) {
                        String jwtToken = tokenService.generateJWTToken(
                                authorizeResponse.getOauthUser(),
                                authorizeResponse.getOauthClient(),
                                accessToken,
                                authorizeResponse.getClaim(),
                                authorizeResponse.getOauthClient().getAccessTokenLifetime(),
                                "user");
                        accessToken.setToken(jwtToken);

                        params.put("token_type", "JWT");
                        params.put("expires_in", authorizeResponse.getOauthClient().getAccessTokenLifetime());
                    } else {
                        params.put("token_type", "Bearer");
                        params.put("expires_in", authorizeResponse.getOauthClient().getAccessTokenLifetime());
                    }

                    OauthClient oauthClient = authorizeResponse.getOauthClient();
                    OauthUser oauthUser = authorizeResponse.getOauthUser();
                    String scope = authorizeResponse.getScope();

                    //TODO preAuthorize 수행

                    tokenRepository.save(accessToken);
                    params.put("access_token", accessToken.getToken());
                    params.put("scope", authorizeResponse.getScope());
                    params.put("state", authorizeResponse.getState());
                    break;

                default:
                    params.put("error", OauthConstant.SERVER_ERROR);
                    params.put("error_description", "Server can not process authorize");
                    params.put("state", authorizeResponse.getState());
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            params.put("error", OauthConstant.SERVER_ERROR);
            params.put("error_description", "Server can not process authorize");
            params.put("state", authorizeResponse.getState());
        }

        response.setHeader("Content-Type", "application/x-www-form-urlencoded");
        String getQueryString = HttpUtils.createGETQueryString(params);
        String url = authorizeResponse.getRedirectUri();
        response.sendRedirect(url + getQueryString);
    }

    @Override
    public void processAccessToken(HttpServletRequest request, HttpServletResponse response) {
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();

        accessTokenResponse.setClientId(request.getParameter("client_id"));
        if (StringUtils.isEmpty(accessTokenResponse.getClientId())) {
            accessTokenResponse.setClientId(request.getHeader("client-key"));
        }

        accessTokenResponse.setClientSecret(request.getParameter("client_secret"));
        if (StringUtils.isEmpty(accessTokenResponse.getClientSecret())) {
            accessTokenResponse.setClientSecret(request.getHeader("client-secret"));
        }

        accessTokenResponse.setGrant_type(request.getParameter("grant_type"));
        accessTokenResponse.setCode(request.getParameter("code"));
        accessTokenResponse.setRedirectUri(request.getParameter("redirect_uri"));
        accessTokenResponse.setScope(request.getParameter("scope"));
        accessTokenResponse.setUsername(request.getParameter("username"));
        accessTokenResponse.setPassword(request.getParameter("password"));
        accessTokenResponse.setAssertion(request.getParameter("assertion"));
        accessTokenResponse.setRefreshToken(request.getParameter("refresh_token"));
        accessTokenResponse.setTokenType(request.getParameter("token_type"));
        accessTokenResponse.setClaim(request.getParameter("claim"));
        accessTokenResponse.setResponse(response);

        //grant_type을 검증한다.
        if (StringUtils.isEmpty(accessTokenResponse.getGrant_type())) {
            accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
            accessTokenResponse.setError_description("grant_type is required.");
            grantService.responseToken(accessTokenResponse);
            return;
        }

        //gratn_type 별로 프로세스를 수행한다.
        try {
            switch (accessTokenResponse.getGrant_type()) {
                case "authorization_code":
                    grantService.processCodeGrant(accessTokenResponse);
                    break;

                case "password":
                    grantService.processPasswordGrant(accessTokenResponse);
                    break;

                case "client_credentials":
                    grantService.processClientCredentialsGrant(accessTokenResponse);
                    break;

                case "urn:ietf:params:oauth:grant-type:jwt-bearer":
                    grantService.processJWTGrant(accessTokenResponse);
                    break;

                case "refresh_token":
                    grantService.processRefreshToken(accessTokenResponse);
                    break;

                default:
                    accessTokenResponse.setError(OauthConstant.INVALID_REQUEST);
                    accessTokenResponse.setError_description("grant_type must be one of authorization_code,password,client_credentials,refresh_token,urn:ietf:params:oauth:grant-type:jwt-bearer");
                    grantService.responseToken(accessTokenResponse);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            accessTokenResponse.setError(OauthConstant.SERVER_ERROR);
            accessTokenResponse.setError_description("server error during access token processing");
            grantService.responseToken(accessTokenResponse);
        }
    }

    @Override
    public void processTokenInfo(HttpServletRequest request, HttpServletResponse response) {

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();

        accessTokenResponse.setAccessToken(request.getParameter("access_token"));
        accessTokenResponse.setResponse(response);

        try {
            grantService.processTokenInfo(accessTokenResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
            accessTokenResponse.setError(OauthConstant.SERVER_ERROR);
            accessTokenResponse.setError_description("server error during access token processing");
            grantService.responseToken(accessTokenResponse);
        }
    }
}
