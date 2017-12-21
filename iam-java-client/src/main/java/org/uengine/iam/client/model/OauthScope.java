package org.uengine.iam.client.model;

import org.uengine.iam.client.couchdb.CouchDAO;

/**
 * Created by uengine on 2015. 6. 3..
 */
public class OauthScope extends CouchDAO {

    private String managementId;
    private String name;
    private String description;
    private Long regDate;
    private Long updDate;

    public String getManagementId() {
        return managementId;
    }

    public void setManagementId(String managementId) {
        this.managementId = managementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRegDate() {
        return regDate;
    }

    public void setRegDate(Long regDate) {
        this.regDate = regDate;
    }

    public Long getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Long updDate) {
        this.updDate = updDate;
    }

    @Override
    public String toString() {
        return "OauthScope{" +
                "managementId='" + managementId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", regDate=" + regDate +
                ", updDate=" + updDate +
                '}';
    }
}
