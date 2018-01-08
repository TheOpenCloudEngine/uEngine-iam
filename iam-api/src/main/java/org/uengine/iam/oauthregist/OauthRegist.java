package org.uengine.iam.oauthregist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.uengine.iam.notification.NotificationType;
import org.uengine.iam.oauthuser.OauthUser;
import org.uengine.iam.util.JsonUtils;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by uengine on 2015. 6. 3..
 */
@Entity
@Table(name = "oauth_regist")
public class OauthRegist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String clientKey;

    @Enumerated(EnumType.STRING)
    private NotificationType notification_type;

    private String token;

    private String redirect_url;

    @JsonIgnore
    private String oauthUserJson;

    @Column(columnDefinition = "TEXT")
    private String authorizeResponse;

    public String getAuthorizeResponse() {
        return authorizeResponse;
    }

    public void setAuthorizeResponse(String authorizeResponse) {
        this.authorizeResponse = authorizeResponse;
    }

    public OauthUser getOauthUser() {
        try {
            Map map = JsonUtils.unmarshal(this.oauthUserJson);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.convertValue(map, OauthUser.class);
        } catch (IOException ex) {
            return null;
        }
    }

    public void setOauthUser(OauthUser oauthUser) {
        try {
            this.oauthUserJson = JsonUtils.marshal(oauthUser);
        } catch (IOException ex) {
            this.oauthUserJson = "{}";
        }
    }

    @Column(name = "regDate", nullable = false, updatable = false, insertable = true)
    private long regDate;

    @Column(name = "updDate", nullable = false, updatable = true, insertable = true)
    private long updDate;

    @PrePersist
    void preInsert() {
        this.regDate = new Date().getTime();
        this.updDate = new Date().getTime();
    }

    @PreUpdate
    void preUpdate() {
        this.updDate = new Date().getTime();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public NotificationType getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(NotificationType notification_type) {
        this.notification_type = notification_type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public String getOauthUserJson() {
        return oauthUserJson;
    }

    public void setOauthUserJson(String oauthUserJson) {
        this.oauthUserJson = oauthUserJson;
    }

    public long getRegDate() {
        return regDate;
    }

    public void setRegDate(long regDate) {
        this.regDate = regDate;
    }

    public long getUpdDate() {
        return updDate;
    }

    public void setUpdDate(long updDate) {
        this.updDate = updDate;
    }
}
