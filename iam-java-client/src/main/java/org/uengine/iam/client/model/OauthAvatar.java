package org.uengine.iam.client.model;

/**
 * Created by uengine on 2015. 6. 3..
 */
public class OauthAvatar {

    private String userName;
    private String contentType;
    private byte[] data;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
