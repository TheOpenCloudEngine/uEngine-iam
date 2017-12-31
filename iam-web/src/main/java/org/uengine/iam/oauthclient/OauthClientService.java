package org.uengine.iam.oauthclient;

import java.util.List;

public interface OauthClientService {

    OauthClient selectByClientKey(String clientKey);

    List<OauthClient> selectAll();
}
