package org.opencloudengine.garuda.client;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by uengine on 2016. 12. 26..
 */
public class ClientCredentialsGrant extends AccessTokenRequest{
    String client_id;

    String client_secret;

    String grant_type;

    String scope;

    String token_type;

    String claim;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }
}
