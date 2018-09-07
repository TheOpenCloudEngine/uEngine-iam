package org.uengine.iam.oauthuser;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by uengine on 2015. 6. 3..
 */
@Data
public class OauthUser {

    private String userName;
    private String userPassword;
    private Map<String, Object> metaData;
    private long regDate;
    private long updDate;
    private String provider;
}
