package org.uengine.iam.oauth2;

public interface OauthGrantService {

    void processCodeGrant(AccessTokenResponse accessTokenResponse) throws Exception;

    void processPasswordGrant(AccessTokenResponse accessTokenResponse) throws Exception;

    void processClientCredentialsGrant(AccessTokenResponse accessTokenResponse) throws Exception;

    void processJWTGrant(AccessTokenResponse accessTokenResponse) throws Exception;

    void responseToken(AccessTokenResponse accessTokenResponse);

    void processTokenInfo(AccessTokenResponse accessTokenResponse) throws Exception;

    void processRefreshToken(AccessTokenResponse accessTokenResponse) throws Exception;
}
