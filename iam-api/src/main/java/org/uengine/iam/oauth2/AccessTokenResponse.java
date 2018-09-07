package org.uengine.iam.oauth2;

import lombok.Data;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthscope.OauthScope;
import org.uengine.iam.oauthuser.OauthUser;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;

/**
 * Created by uengine on 2016. 4. 15..
 */
@Data
public class AccessTokenResponse implements Serializable {
    private String clientId;
    private String clientSecret;
    private String grant_type;
    private String redirectUri;
    private String code;
    private String scope;
    private String state;
    private String username;
    private String password;
    private String accessToken;
    private String tokenType;
    private String claim;
    private String assertion;
    private Long expiresIn;
    private String refreshToken;
    private OauthClient oauthClient;
    private List<OauthScope> oauthScopes;
    private OauthUser oauthUser;
    private HttpServletResponse response;
    private boolean saveWithOldRefreshToken = false;

    private String error;
    private String error_description;
}
