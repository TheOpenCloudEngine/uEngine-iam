package org.uengine.iam.oauthregist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.uengine.iam.notification.NotificationType;
import org.uengine.iam.oauth2.AuthorizeResponse;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthclient.OauthClientService;
import org.uengine.iam.oauthuser.OauthUser;
import org.uengine.iam.oauthuser.OauthUserRepository;
import org.uengine.iam.oauthuser.OauthUserService;
import org.uengine.iam.util.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rest/v1")
public class OauthRegistRestController {

    @Autowired
    private OauthRegistService registService;

    @Autowired
    private OauthClientService clientService;

    @Autowired
    private OauthUserRepository userRepository;

    @Autowired
    private OauthRegistRepository registRepository;

    @RequestMapping(value = "/user/verification", method = RequestMethod.GET)
    public ResponseEntity<OauthRegist> signUpVerification(HttpServletRequest request, HttpServletResponse response,
                                                          @RequestParam("token") String token) throws Exception {
        OauthRegist regist = registRepository.findByToken(token);
        if (regist == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(regist, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/signup", method = RequestMethod.POST)
    public ResponseEntity<OauthRegist> signUpUser(HttpServletRequest request, HttpServletResponse response,
                                                  @RequestBody OauthRegist regist) throws Exception {
        AuthorizeResponse authorizeResponse = JsonUtils.convertValue(JsonUtils.marshal(regist.getAuthorizeResponse()), AuthorizeResponse.class);
        OauthClient oauthClient = clientService.selectByClientKey(authorizeResponse.getClientId());

        if (oauthClient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        OauthUser existUser = userRepository.findByUserName(regist.getOauthUser().getUserName());
        if (existUser != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        OauthRegist oauthRegist = registService.singUp(oauthClient, regist.getOauthUser(), regist.getRedirect_url(), regist.getAuthorizeResponse());

        return new ResponseEntity<>(oauthRegist, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/user/signup/accept", method = RequestMethod.POST)
    public ResponseEntity<OauthUser> signUpAccept(HttpServletRequest request, HttpServletResponse response,
                                                  @RequestBody Map params) throws Exception {

        String token = params.get("token").toString();
        OauthRegist regist = registRepository.findByToken(token);
        String clientKey = regist.getClientKey();
        OauthClient oauthClient = clientService.selectByClientKey(clientKey);
        if (oauthClient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        OauthUser oauthUser = registService.acceptSingUp(oauthClient, token);
        if (oauthUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(oauthUser, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/user/repassword", method = RequestMethod.PUT)
    public ResponseEntity<OauthUser> repassword(HttpServletRequest request, HttpServletResponse response,
                                                @RequestBody Map params) throws Exception {
        String clientKey = params.get("clientKey").toString();
        String userName = params.get("userName").toString();
        String beforePassword = params.get("beforePassword").toString();
        String afterPassword = params.get("afterPassword").toString();
        OauthClient oauthClient = clientService.selectByClientKey(clientKey);
        if (oauthClient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        OauthUser oauthUser = userRepository.findByUserName(userName);
        if (oauthUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        oauthUser = registService.rePassword(oauthClient, oauthUser, beforePassword, afterPassword);
        return new ResponseEntity<>(oauthUser, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/user/forgot", method = RequestMethod.POST)
    public ResponseEntity<OauthRegist> forgotPassword(HttpServletRequest request, HttpServletResponse response,
                                                      @RequestBody OauthRegist regist) throws Exception {
        AuthorizeResponse authorizeResponse = JsonUtils.convertValue(JsonUtils.marshal(regist.getAuthorizeResponse()), AuthorizeResponse.class);
        OauthClient oauthClient = clientService.selectByClientKey(authorizeResponse.getClientId());

        if (oauthClient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        OauthUser existUser = userRepository.findByUserName(regist.getOauthUser().getUserName());
        if (existUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        OauthRegist oauthRegist = registService.forgotPassword(oauthClient, existUser, regist.getRedirect_url(), regist.getAuthorizeResponse());

        return new ResponseEntity<>(oauthRegist, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/user/forgot/accept", method = RequestMethod.POST)
    public ResponseEntity<OauthUser> forgotPasswordAccept(HttpServletRequest request, HttpServletResponse response,
                                                          @RequestBody Map params) throws Exception {
        String token = params.get("token").toString();
        String password = params.get("password").toString();
        OauthRegist regist = registRepository.findByToken(token);
        String clientKey = regist.getClientKey();
        OauthClient oauthClient = clientService.selectByClientKey(clientKey);
        if (oauthClient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        OauthUser oauthUser = registService.acceptPassword(oauthClient, token, password);
        if (oauthUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(oauthUser, HttpStatus.CREATED);
    }
}
