package org.uengine.iam.oauthuser;

import lombok.Data;

import java.util.List;

/**
 * Created by uengine on 2017. 12. 27..
 */
@Data
public class OauthUserPage {
    private Long total;
    private List<OauthUser> oauthUserList;
}
