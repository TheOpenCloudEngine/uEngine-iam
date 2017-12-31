package org.uengine.iam.mail;


import org.uengine.iam.notification.NotificationType;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthuser.OauthUser;

public interface MailService {

    void notificationMail(OauthClient oauthClient, OauthUser oauthUser, NotificationType notification_type, String redirect_url, String token) throws Exception;
}