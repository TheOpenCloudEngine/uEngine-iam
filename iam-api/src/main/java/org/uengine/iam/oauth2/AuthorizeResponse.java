package org.uengine.iam.oauth2;

import lombok.Data;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthscope.OauthScope;
import org.uengine.iam.oauthuser.OauthUser;

import java.io.Serializable;
import java.util.List;

/**
 * Created by uengine on 2016. 4. 15..
 */
@Data
public class AuthorizeResponse implements Serializable {
    private String clientId;
    private String responseType;
    private String redirectUri;
    private String scope;
    private String state;
    private String code;
    private String accessToken;
    private String tokenType;
    private String claim;
    private Long expiresIn;
    private OauthClient oauthClient;
    private List<OauthScope> oauthScopes;
    private OauthUser oauthUser;

    private String error;
    private String error_description;
}
