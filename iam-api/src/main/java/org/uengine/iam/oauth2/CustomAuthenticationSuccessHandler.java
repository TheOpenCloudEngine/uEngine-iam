package org.uengine.iam.oauth2;

import com.google.common.base.Joiner;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthclient.OauthClientService;
import org.uengine.iam.oauthclient.SocialClientResources;
import org.uengine.iam.oauthregist.OauthRegistService;
import org.uengine.iam.oauthscope.OauthScope;
import org.uengine.iam.oauthuser.OauthUser;
import org.uengine.iam.oauthuser.OauthUserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

@Component
@Data
@NoArgsConstructor
public class CustomAuthenticationSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements AuthenticationSuccessHandler {

    private SocialClientResources socialClientResources;

    private Environment environment;
    private OauthService oauthService;
    private OauthClientService clientService;
    private OauthUserRepository userRepository;
    private OauthRegistService registService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            if (authentication.isAuthenticated()) {

                Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();

                System.out.println("name  = " + (String) details.get("name"));
                System.out.println("email  = " + (String) details.get("email"));

                String name = (String) details.get("name");
                String email = (String) details.get("email");
                String password = "user";

                AuthorizeResponse authorizeResponse = new AuthorizeResponse();
                OauthUser oauthUser = userRepository.findByUserNameAndProvider(name, socialClientResources.getClientName());

                if (oauthUser == null) {
                    oauthUser = new OauthUser();
                    // 회원가입
                    oauthUser.setUserName(name);
                    oauthUser.setUserPassword(password);
                    List<String> enableScopes = Arrays.asList(socialClientResources.getScopes());
                    oauthUser.setMetaData(new HashMap<>());
                    oauthUser.getMetaData().put("scopes", enableScopes);
                    oauthUser.getMetaData().put("name", name);
                    oauthUser.getMetaData().put("email", email);
                    oauthUser.setProvider(socialClientResources.getClientName());
                    registService.socialSingUp(oauthUser);
                }

                String scops = null;
                if (oauthUser.getMetaData().get("scopes") != null && oauthUser.getMetaData().get("scopes") instanceof List) {
                    scops = Joiner.on(",").join((List) oauthUser.getMetaData().get("scopes"));
                }

                authorizeResponse.setClientId(socialClientResources.getParentClientId());
                authorizeResponse.setResponseType(socialClientResources.getResponseType());
                authorizeResponse.setScope(scops);
                authorizeResponse.setTokenType(socialClientResources.getTokenType());
                authorizeResponse.setOauthUser(oauthUser);
                OauthClient oauthClient = clientService.selectByClientKey(authorizeResponse.getClientId());
                authorizeResponse.setOauthClient(oauthClient);
                authorizeResponse.setRedirectUri(socialClientResources.getRedirectUri());
                oauthService.processAuthorize(authorizeResponse, response);

            } else {
                String uiHost = environment.getProperty("ui-host");
                response.sendRedirect(uiHost + "#/auth/error");
            }
        } catch (Exception ex) {
            String uiHost = environment.getProperty("ui-host");
            response.sendRedirect(uiHost + "#/auth/error");
        }
    }
}