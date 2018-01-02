package org.uengine.iam.oauthscope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/rest/v1")
public class OauthScopeRestController {
    @Autowired
    private OauthScopeService oauthScopeService;


    @RequestMapping(value = "/scope", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<OauthScope>> listAllScopes(HttpServletRequest request, HttpServletResponse response) {
        List<OauthScope> oauthScopes = oauthScopeService.selectAll();
        if (oauthScopes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(oauthScopes, HttpStatus.OK);
    }


    @RequestMapping(value = "/scope/{name}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<OauthScope> getScope(HttpServletRequest request, HttpServletResponse response,
                                               @PathVariable("name") String name) {
        OauthScope oauthScope = oauthScopeService.selectByName(name);
        if (oauthScope == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(oauthScope, HttpStatus.OK);
    }
}
