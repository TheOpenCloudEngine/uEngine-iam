package org.opencloudengine.garuda.client.model;


import org.opencloudengine.garuda.client.couchdb.CouchFlexibleDAO;

/**
 * Created by uengine on 2015. 6. 3..
 */
public class OauthUser extends CouchFlexibleDAO {

    private String managementId;
    private String userName;
    private String userPassword;
    private Integer level;
    private Long regDate;
    private Long updDate;

    public String getManagementId() {
        return this.asString("managementId");
    }

    public void setManagementId(String managementId) {
        this.put("managementId", managementId);
    }

    public String getUserName() {
        return this.asString("userName");
    }

    public void setUserName(String userName) {
        this.put("userName", userName);
    }

    public String getUserPassword() {
        return this.asString("userPassword");
    }

    public void setUserPassword(String userPassword) {
        this.put("userPassword", userPassword);
    }

    public Integer getLevel() {
        return this.asInteger("level");
    }

    public void setLevel(Integer level) {
        this.put("level", level);
    }

    public Long getRegDate() {
        return this.asLong("regDate");
    }

    public void setRegDate(Long regDate) {
        this.put("regDate", regDate);
    }

    public Long getUpdDate() {
        return this.asLong("updDate");
    }

    public void setUpdDate(Long updDate) {
        this.put("updDate", updDate);
    }
}
