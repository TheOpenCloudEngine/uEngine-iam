package org.uengine.iam.oauthscope;

import java.util.List;

public interface OauthScopeService {

    OauthScope selectByName(String name);

    List<OauthScope> selectAll();
}
