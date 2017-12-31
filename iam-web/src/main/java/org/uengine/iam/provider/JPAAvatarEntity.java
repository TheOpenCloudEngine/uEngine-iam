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
@Table(name = "my_avatar_table")
public class JPAAvatarEntity {

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Lob
    @Column(length = 100000)
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    private String contentType;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
