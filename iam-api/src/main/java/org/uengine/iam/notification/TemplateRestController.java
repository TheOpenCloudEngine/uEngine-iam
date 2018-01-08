package org.uengine.iam.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by uengine on 2017. 1. 11..
 */

@RestController
@RequestMapping("/rest/v1")
public class TemplateRestController {

    @Autowired
    private TemplateService templateService;


    @RequestMapping(value = "/client/{clientKey}/template", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Template> getAllTemplate(HttpServletRequest request,
                                                HttpServletResponse response,
                                                @PathVariable("clientKey") String clientKey) throws Exception {

        return templateService.selectByClientKey(clientKey);
    }

    @RequestMapping(value = "/client/{clientKey}/template/{notification_type}", method = RequestMethod.GET, produces = "application/json")
    public Template getTemplateByType(HttpServletRequest request,
                                      HttpServletResponse response,
                                      @PathVariable("clientKey") String clientKey,
                                      @PathVariable("notification_type") NotificationType notification_type) throws Exception {

        return templateService.selectByClientKeyAndType(clientKey, notification_type);
    }
}
