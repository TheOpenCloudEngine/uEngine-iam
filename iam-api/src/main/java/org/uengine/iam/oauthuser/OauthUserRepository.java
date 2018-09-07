package org.uengine.iam.oauthuser;

import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;

public interface OauthUserRepository {
    OauthUser insert(OauthUser oauthUser);

    OauthUser update(OauthUser oauthUser);

    //필수
    OauthUser findByUserName(String userName);

    OauthUserPage findLikeUserName(String searchKey, Pageable pageable);

    //필수
    OauthUser findByUserNameAndUserPassword(String userName, String userPassword);
    OauthUser findByUserNameAndUserPasswordAndProvider(String userName, String userPassword, String provider);

    OauthUser findByUserNameAndProvider(String userName, String provider);

    void deleteByUserName(String userName);

    OauthAvatar getAvatar(String userName);

    OauthAvatar insertAvatar(OauthAvatar oauthAvatar);

    void deleteAvatar(String userName);
}
