package org.uengine.iam.oauthscope;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthclient.OauthClientService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Data
@Service
@ConfigurationProperties
public class OauthScopeServiceImpl implements OauthScopeService {
    @Autowired
    private Environment environment;

    @Autowired
    private OauthClientService clientService;

    private List<OauthScope> scopes;

    @Override
    public OauthScope selectByName(String name) {
        OauthScope selected = null;
        for (OauthScope scope : this.scopes) {
            if (scope.getName().equals(name)) {
                selected = scope;
            }
        }
        return selected;
    }

    @Override
    public List<OauthScope> selectAll() {
        return this.scopes;
    }

    @Override
    public List<OauthScope> selectClientScopes(String clientKey) {
        OauthClient oauthClient = clientService.selectByClientKey(clientKey);

        List<String> enableScopes = Arrays.asList(oauthClient.getEnableScopes());
        List<OauthScope> scopes = new ArrayList<>();

        for (int i = 0; i < enableScopes.size(); i++) {
            OauthScope scope = this.selectByName(enableScopes.get(i));
            scopes.add(scope);
        }
        return scopes;
    }
}
