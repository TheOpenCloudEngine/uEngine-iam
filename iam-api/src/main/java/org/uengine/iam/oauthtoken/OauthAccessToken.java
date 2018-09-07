package org.uengine.iam.oauthtoken;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import lombok.Data;
import org.uengine.iam.util.JsonUtils;

import javax.persistence.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by uengine on 2015. 6. 3..
 */
@Data
@Entity
@Table(name = "oauth_token")
public class OauthAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String type;

    @JsonIgnore
    private String scopesString;

    @Column(columnDefinition = "TEXT")
    private String token;
    private String userName;
    private String clientKey;
    private String refreshToken;
    private String oldRefreshToken;

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

    public List<String> getScopes() {
        try {
            return Arrays.asList(this.scopesString.split(","));
        } catch (Exception ex) {
            return new ArrayList<String>();
        }
    }

    public void setScopes(List<String> scopes) {
        try {
            this.scopesString = Joiner.on(",").join(scopes);
        } catch (Exception ex) {
            this.scopesString = "";
        }
    }
}
