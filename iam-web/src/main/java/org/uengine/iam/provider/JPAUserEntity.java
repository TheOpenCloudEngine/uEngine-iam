package org.uengine.iam.provider;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "my_user_table")
public class JPAUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "userName", unique = true)
    private String userName;

    private String userPassword;

    @Column(name = "regDate", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date regDate;

    @Column(name = "updDate", nullable = false, updatable = true, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date updDate;

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }


    @JsonIgnore
    private String metaDataString;

    public String getMetaDataString() {
        return metaDataString;
    }

    public void setMetaDataString(String metaDataString) {
        this.metaDataString = metaDataString;
    }

    public Map getMetaData() {
        try {
            return JsonUtils.unmarshal(this.metaDataString);
        } catch (IOException ex) {
            return new HashMap();
        }
    }

    public void setMetaData(Map metaData) {
        try {
            this.metaDataString = JsonUtils.marshal(metaData);
        } catch (IOException ex) {
            this.metaDataString = "{}";
        }
    }
}
