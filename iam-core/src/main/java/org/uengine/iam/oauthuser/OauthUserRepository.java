package org.uengine.iam.oauthuser;

import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;

public interface OauthUserRepository {
    OauthUser insert(OauthUser oauthUser);

    OauthUser update(OauthUser oauthUser);

    OauthUser findByUserName(String userName);

    OauthUserPage findLikeUserName(String searchKey, Pageable pageable);

    OauthUser findByUserNameAndUserPassword(String userName, String userPassword);

    void deleteByUserName(String userName);

    OauthAvatar getAvatar(String userName);

    OauthAvatar insertAvatar(OauthAvatar oauthAvatar);

    void deleteAvatar(String userName);
}
