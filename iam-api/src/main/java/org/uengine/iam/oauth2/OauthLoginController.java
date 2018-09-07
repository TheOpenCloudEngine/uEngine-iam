package org.uengine.iam.oauth2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthclient.OauthClientService;
import org.uengine.iam.oauthscope.OauthScope;
import org.uengine.iam.oauthuser.OauthUser;
import org.uengine.iam.oauthuser.OauthUserRepository;
import org.uengine.iam.util.HttpUtils;
import org.uengine.iam.util.JsonFormatterUtils;
import org.uengine.iam.util.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oauth")
public class OauthLoginController {

    @Autowired
    private Environment environment;

    @Autowired
    private OauthService oauthService;

    @Autowired
    private OauthUserRepository userRepository;

    /**
     * Authorization Code,Implicit Grant 플로우의 3th 파티 사용자가 로그인 페이지에서 인증절차를 하면서 POST 이동하는 페이지
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public void login(HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
        try {
            new SecurityContextLogoutHandler().logout(request, null, null);

            String userName = request.getParameter("userName");
            String userPassword = request.getParameter("userPassword");
            Map authorizeResponseMap = JsonUtils.unmarshal(request.getParameter("authorizeResponse"));

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AuthorizeResponse authorizeResponse = objectMapper.convertValue(authorizeResponseMap, AuthorizeResponse.class);

            OauthUser oauthUser = userRepository.findByUserNameAndUserPassword(userName, userPassword);
            if (oauthUser == null) {
                String uiHost = environment.getProperty("ui-host");
                String encodeResult = URLEncoder.encode(JsonUtils.marshal(authorizeResponse), "UTF-8");
                response.sendRedirect(uiHost + "#/auth/login?authorizeResponse=" + encodeResult + "&status=fail");
            }

            //로그인 창을 통해서는 ok. 패스워드 그런트에도 이 로직을 추가할 것.
            else {
                List<OauthScope> missingScopes = oauthService.validateUserScopes(authorizeResponse.getOauthClient(), oauthUser, authorizeResponse.getOauthScopes());
                if (missingScopes == null) {
                    authorizeResponse.setOauthUser(oauthUser);
                    oauthService.processAuthorize(authorizeResponse, response);
                } else {
                    String uiHost = environment.getProperty("ui-host");
                    String encodeResult = URLEncoder.encode(JsonUtils.marshal(authorizeResponse), "UTF-8");
                    String missingScopesResult = URLEncoder.encode(JsonUtils.marshal(missingScopes), "UTF-8");

                    response.sendRedirect(uiHost + "#/auth/login?authorizeResponse=" + encodeResult +
                            "&status=fail&missingScopes=" + missingScopesResult +
                            "&userScopeCheckAll=" + authorizeResponse.getOauthClient().getUserScopeCheckAll());
                }
            }
        } catch (Exception ex) {
            String uiHost = environment.getProperty("ui-host");
            response.sendRedirect(uiHost + "#/auth/error");
        }
    }

    @RequestMapping(value = "/login/facebook/success", method = RequestMethod.GET, produces = "application/json")
    public void socialLoginSuccess(HttpServletRequest request, HttpServletResponse response
    ) throws Exception {

    }

    /**
     * Authorization Code,Implicit Grant 플로우의 3th 파티 앱이 최초 요청하는 로그인 페이지.
     *
     * @param session
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/authorize", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void authorize(HttpSession session, HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
        // 기존 로그인 정보를 제거한다.
        new SecurityContextLogoutHandler().logout(request, null, null);
        AuthorizeResponse authorizeResponse = oauthService.validateAuthorize(request);
        if (authorizeResponse.getError() != null) {
            //응답에 바로 에러를 보내준다.
            Map map = new HashMap();
            map.put("error", authorizeResponse.getError());
            map.put("error_description", authorizeResponse.getError_description());
            map.put("state", authorizeResponse.getState());
            String marshal = JsonUtils.marshal(map);
            String prettyPrint = JsonFormatterUtils.prettyPrint(marshal);
            response.setStatus(400);
            response.getWriter().write(prettyPrint);

            //TODO 아래의 리다이렉트 URL 로 에러를 보내주는 로직은, 추후 큐에 의해 스케쥴링되도록 한다.
            //리다이렉트 URL 에도 에러를 보내준다.
            //리다이렉트 URL 이 없는 경우는 실행하지 않는다.
            if (!StringUtils.isEmpty(authorizeResponse.getRedirectUri())) {
                try {
                    //application/x-www-form-urlencoded 을 이용해 리다이렉트 할 것.
                    Map<String, String> headers = new HashMap();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");

                    String getQueryString = HttpUtils.createGETQueryString(map);
                    String url = authorizeResponse.getRedirectUri();
                    new HttpUtils().makeRequest("GET", url + getQueryString, null, headers);
                } catch (Exception ex) {
                    //리다이렉트 전달 과정 중 실패가 일어나더라도 프로세스에는 영향을 끼지지 않는다.
                }
            }
        } else {
            String uiHost = environment.getProperty("ui-host");
            String encodeResult = URLEncoder.encode(JsonUtils.marshal(authorizeResponse), "UTF-8");
            response.sendRedirect(uiHost + "#/auth/login?authorizeResponse=" + encodeResult);
        }
    }
}
