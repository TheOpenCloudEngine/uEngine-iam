package org.uengine.iam.oauth2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OauthController {

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
     * @param params
     * @throws IOException
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public void login(HttpServletRequest request, HttpServletResponse response,
                      @RequestBody Map params
    ) throws Exception {
        String userName = (String) params.get("userName");
        String userPassword = (String) params.get("userPassword");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AuthorizeResponse authorizeResponse = objectMapper.convertValue(params.get("authorizeResponse"), AuthorizeResponse.class);

        OauthUser oauthUser = userRepository.findByUserNameAndUserPassword(userName, userPassword);
        if (oauthUser == null) {
            throw new Exception("Invalid login");
        }
        authorizeResponse.setOauthUser(oauthUser);
        oauthService.processAuthorize(authorizeResponse, response);
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
            response.sendRedirect(uiHost + "#/oauth-login?authorizeResponse=" + encodeResult);
        }
    }

    @RequestMapping(value = "/access_token", method = RequestMethod.POST, produces = "application/json")
    public void accessToken(HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
        oauthService.processAccessToken(request, response);
    }

    @RequestMapping(value = "/token_info", method = RequestMethod.GET, produces = "application/json")
    public void tokenInfo(HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
        oauthService.processTokenInfo(request, response);
    }
}
