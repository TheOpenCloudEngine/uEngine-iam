package org.uengine.iam.oauthclient;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("/rest/v1")
public class OauthClientRestController {

    @Autowired
    private OauthClientService oauthClientService;


    @RequestMapping(value = "/client", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<OauthClient>> listAllClients(HttpServletRequest request, HttpServletResponse response) {
        List<OauthClient> oauthClients = oauthClientService.selectAll();
        if (oauthClients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(oauthClients, HttpStatus.OK);
    }


    @RequestMapping(value = "/client/{clientKey}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<OauthClient> getClient(HttpServletRequest request, HttpServletResponse response,
                                                 @PathVariable("clientKey") String clientKey) {
        OauthClient oauthClient = oauthClientService.selectByClientKey(clientKey);
        if (oauthClient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(oauthClient, HttpStatus.OK);
    }
}
