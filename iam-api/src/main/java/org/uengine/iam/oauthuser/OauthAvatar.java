package org.uengine.iam.oauthuser;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * Created by uengine on 2015. 6. 3..
 */
@Data
public class OauthAvatar {

    private String userName;
    private String contentType;
    private byte[] data;
}
