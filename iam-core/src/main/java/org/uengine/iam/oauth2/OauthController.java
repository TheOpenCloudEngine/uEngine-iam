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
    private OauthService oauthService;


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
