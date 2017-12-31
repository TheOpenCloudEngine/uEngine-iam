package org.uengine.iam.oauthscope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
@ConfigurationProperties
public class OauthScopeServiceImpl implements OauthScopeService {
    @Autowired
    private Environment environment;

    private List<OauthScope> scopes;

    public List<OauthScope> getScopes() {
        return scopes;
    }

    public void setScopes(List<OauthScope> scopes) {
        this.scopes = scopes;
    }

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
}
