package org.uengine.iam.oauthtoken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthclient.OauthClientService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/rest/v1")
public class AccessToeknRestController {
    @Autowired
    private OauthTokenRepository tokenRepository;

    @Autowired
    private OauthClientService clientService;

    @Autowired
    private OauthTokenService tokenService;


    @RequestMapping(value = "/token", method = RequestMethod.GET, produces = "application/json")
    public Map listToken(HttpServletRequest request, HttpServletResponse response) {
        return tokenService.tokenStatus();
    }

    @RequestMapping(value = "/token/{token}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<OauthAccessToken> getTokenByToken(HttpServletRequest request, HttpServletResponse response,
                                                            @PathVariable("token") String token) {

        OauthAccessToken accessToken = tokenRepository.findByToken(token);
        if (accessToken == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @RequestMapping(value = "/token/{token}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteTokenByToken(HttpServletRequest request, HttpServletResponse response,
                                                   @PathVariable("token") String token) {

        OauthAccessToken accessToken = tokenRepository.findByToken(token);
        if (accessToken == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        tokenRepository.delete(accessToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
