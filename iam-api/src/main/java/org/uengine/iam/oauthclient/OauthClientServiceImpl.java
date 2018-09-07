package org.uengine.iam.oauthclient;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
@ConfigurationProperties
public class OauthClientServiceImpl implements OauthClientService {

    @Autowired
    private Environment environment;

    private List<OauthClient> clients;

    @Override
    public OauthClient selectByClientKey(String clientKey) {
        OauthClient selected = null;
        for (OauthClient client : this.clients) {
            if (client.getClientKey().equals(clientKey)) {
                selected = client;
            }
        }
        return selected;
    }

    @Override
    public List<OauthClient> selectAll() {
        return this.clients;
    }
}
