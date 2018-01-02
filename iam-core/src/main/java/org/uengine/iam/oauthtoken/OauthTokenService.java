package org.uengine.iam.oauthtoken;


import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthuser.OauthUser;

public interface OauthTokenService {
    String generateJWTToken(
            OauthUser oauthUser,
            OauthClient oauthClient,
            OauthAccessToken accessToken,
            String claimJson,
            long lifetime,
            String type) throws Exception;
}
