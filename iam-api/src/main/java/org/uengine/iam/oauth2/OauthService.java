package org.uengine.iam.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface OauthService {

    AuthorizeResponse validateAuthorize(HttpServletRequest request);

    void processAuthorize(AuthorizeResponse authorizeResponse, HttpServletResponse response) throws IOException;

    void processAccessToken(HttpServletRequest request, HttpServletResponse response);

    void processTokenInfo(HttpServletRequest request, HttpServletResponse response);
}
