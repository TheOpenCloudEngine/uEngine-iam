package org.uengine.iam.oauthregist;

import org.uengine.iam.notification.NotificationType;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthuser.OauthUser;

public interface OauthRegistService {

    OauthRegist singUp(OauthClient oauthClient, OauthUser oauthUser, String redirect_url) throws Exception;

    OauthRegist forgotPassword(OauthClient oauthClient, OauthUser oauthUser, String redirect_url) throws Exception;

    OauthUser verification(OauthClient oauthClient, String token, NotificationType notification_type) throws Exception;

    OauthUser acceptSingUp(OauthClient oauthClient, String token) throws Exception;

    OauthUser acceptPassword(OauthClient oauthClient, String token, String password) throws Exception;

    OauthUser rePassword(OauthClient oauthClient, OauthUser oauthUser, String beforePassword, String afterPassword) throws Exception;
}
