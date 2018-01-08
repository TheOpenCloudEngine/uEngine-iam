package org.uengine.iam.oauthuser;

import java.util.List;

/**
 * Created by uengine on 2017. 12. 27..
 */
public class OauthUserPage {
    private Long total;
    private List<OauthUser> oauthUserList;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<OauthUser> getOauthUserList() {
        return oauthUserList;
    }

    public void setOauthUserList(List<OauthUser> oauthUserList) {
        this.oauthUserList = oauthUserList;
    }
}
