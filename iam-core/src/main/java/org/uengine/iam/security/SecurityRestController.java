package org.uengine.iam.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/rest/v1")
public class SecurityRestController {

    /**
     * 시큐리티 체크
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/security", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Void> securityIsOk(HttpServletRequest request,
                                             HttpServletResponse response) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
