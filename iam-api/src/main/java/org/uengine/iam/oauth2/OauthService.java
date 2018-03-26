package org.uengine.iam.oauth2;

import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthscope.OauthScope;
import org.uengine.iam.oauthuser.OauthUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface OauthService {

    List<OauthScope> validateUserScopes(OauthClient oauthClient, OauthUser oauthUser, List<OauthScope> requestedScopes);

    AuthorizeResponse validateAuthorize(HttpServletRequest request);

    void processAuthorize(AuthorizeResponse authorizeResponse, HttpServletResponse response) throws IOException;

    void processAccessToken(HttpServletRequest request, HttpServletResponse response);

    void processTokenInfo(HttpServletRequest request, HttpServletResponse response);
}
