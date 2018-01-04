package org.uengine.iam.oauthtoken;


import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthuser.OauthUser;

import java.util.Map;

public interface OauthTokenService {
    public String generateJWTToken(
            OauthUser oauthUser,
            OauthClient oauthClient,
            OauthAccessToken accessToken,
            String claimJson,
            long lifetime,
            String type) throws Exception;

    public Map tokenStatus();
}
