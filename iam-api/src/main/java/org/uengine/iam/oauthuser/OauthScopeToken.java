package org.uengine.iam.oauthuser;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by uengine on 2016. 4. 19..
 */
@Data
@ToString
public class OauthScopeToken implements Serializable {
    private String token;
    private boolean validated;
    private String userName;
    private String clientKey;
    private String scopes;
}
