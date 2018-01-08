package org.uengine.iam.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uengine.iam.oauthtoken.OauthTokenService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties
@RestController
@RequestMapping("/rest/v1")
public class SecurityRestController {

    private Map<String, Object> iam;

    public Map<String, Object> getIam() {
        return iam;
    }

    public void setIam(Map<String, Object> iam) {
        this.iam = iam;
    }

    @Autowired
    private Environment environment;

    @Autowired
    private OauthTokenService tokenService;

    /**
     * 시큐리티 체크
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/security", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map> securityIsOk(HttpServletRequest request,
                                            HttpServletResponse response) {
        return new ResponseEntity<>(new HashMap(), HttpStatus.OK);
    }

    /**
     * 시스템 콘피그
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/system", method = RequestMethod.GET, produces = "application/json")
    public Map system(HttpServletRequest request,
                      HttpServletResponse response) {
        this.iam.put("currentTime", new Date().toString());
        this.iam.put("token", tokenService.tokenStatus());
        return this.iam;
    }
}
