package org.uengine.iam.oauthregist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.uengine.iam.mail.MailService;
import org.uengine.iam.notification.NotificationType;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthuser.OauthUser;
import org.uengine.iam.oauthuser.OauthUserRepository;
import org.uengine.iam.util.DateUtils;

import java.util.Date;

@Service
public class OauthRegistServiceImpl implements OauthRegistService {

    @Autowired
    OauthRegistRepository registRepository;

    @Autowired
    MailService mailService;

    @Autowired
    OauthUserRepository userRepository;

    /**
     * 회원 가입 요청시 이메일을 발송하고 토큰을 저장한다.
     *
     * @param oauthClient
     * @param oauthUser
     * @param redirect_url
     * @return
     * @throws Exception
     */
    @Override
    public OauthRegist singUp(OauthClient oauthClient, OauthUser oauthUser, String redirect_url, String authorizeResponse) throws Exception {
        String token = new String(Base64.encode(String.valueOf(System.currentTimeMillis()).getBytes()));

        OauthRegist oauthRegist = new OauthRegist();
        oauthRegist.setRedirect_url(redirect_url);
        oauthRegist.setClientKey(oauthClient.getClientKey());
        oauthRegist.setNotification_type(NotificationType.SIGN_UP);
        oauthRegist.setOauthUser(oauthUser);
        oauthRegist.setToken(token);
        oauthRegist.setAuthorizeResponse(authorizeResponse);

        mailService.notificationMail(oauthClient, oauthUser, NotificationType.SIGN_UP, redirect_url, token);
        registRepository.save(oauthRegist);

        return oauthRegist;
    }

    @Override
    public OauthRegist forgotPassword(OauthClient oauthClient, OauthUser oauthUser, String redirect_url, String authorizeResponse) throws Exception {
        String token = new String(Base64.encode(String.valueOf(System.currentTimeMillis()).getBytes()));

        OauthRegist oauthRegist = new OauthRegist();
        oauthRegist.setRedirect_url(redirect_url);
        oauthRegist.setClientKey(oauthClient.getClientKey());
        oauthRegist.setNotification_type(NotificationType.FORGOT_PASSWORD);
        oauthRegist.setOauthUser(oauthUser);
        oauthRegist.setToken(token);
        oauthRegist.setAuthorizeResponse(authorizeResponse);

        mailService.notificationMail(oauthClient, oauthUser, NotificationType.FORGOT_PASSWORD, redirect_url, token);
        registRepository.save(oauthRegist);

        return oauthRegist;
    }

    /**
     * 사인업 또는 비밀번호 찾기 요청의 토큰을 검증한다.
     *
     * @param oauthClient
     * @param token
     * @param notification_type
     * @return
     * @throws Exception
     */
    @Override
    public OauthUser verification(OauthClient oauthClient, String token, NotificationType notification_type) throws Exception {

        //토큰 만료 시간이 지났을 경우.
        long tokenTimestamp = Long.parseLong(new String(org.apache.commons.codec.binary.Base64.decodeBase64(token)));
        if (DateUtils.getDiffDays(new Date(), new Date(tokenTimestamp)) > 1) {
            return null;
        }

        OauthRegist oauthRegist = registRepository.findByClientKeyAndTokenAndType(oauthClient.getClientKey(), token, notification_type);
        if (oauthRegist == null) {
            return null;
        }

        //비밀번호 요청인 경우 현재 사용자 정보를 리턴한다.
        if (NotificationType.FORGOT_PASSWORD.equals(notification_type)) {
            return userRepository.findByUserName(oauthRegist.getOauthUser().getUserName());
        }
        //가입 요청인 경우 저장된 사용자 정보를 리턴한다.
        else {
            return oauthRegist.getOauthUser();
        }
    }


    @Override
    public OauthUser acceptSingUp(OauthClient oauthClient, String token) throws Exception {

        //토큰 만료 시간이 지났을 경우.
        long tokenTimestamp = Long.parseLong(new String(org.apache.commons.codec.binary.Base64.decodeBase64(token)));
        if (DateUtils.getDiffDays(new Date(), new Date(tokenTimestamp)) > 1) {
            return null;
        }

        OauthRegist oauthRegist = registRepository.findByClientKeyAndTokenAndType(oauthClient.getClientKey(), token, NotificationType.SIGN_UP);
        if (oauthRegist == null) {
            return null;
        }

        //사용자를 등록한다.
        OauthUser oauthUser = oauthRegist.getOauthUser();
        oauthUser = userRepository.insert(oauthUser);

        //사용자 가입확인 메일 발송.
        mailService.notificationMail(oauthClient, oauthUser, NotificationType.SIGNED_UP, null, null);

        //레지스트 삭제
        registRepository.delete(oauthRegist);

        return oauthUser;
    }

    @Override
    public OauthUser acceptPassword(OauthClient oauthClient, String token, String password) throws Exception {
        //토큰 만료 시간이 지났을 경우.
        long tokenTimestamp = Long.parseLong(new String(org.apache.commons.codec.binary.Base64.decodeBase64(token)));
        if (DateUtils.getDiffDays(new Date(), new Date(tokenTimestamp)) > 1) {
            return null;
        }

        OauthRegist oauthRegist = registRepository.findByClientKeyAndTokenAndType(oauthClient.getClientKey(), token, NotificationType.FORGOT_PASSWORD);
        if (oauthRegist == null) {
            return null;
        }

        //사용자를 패스워드를 변경한다.
        OauthUser oauthUser = userRepository.findByUserName(oauthRegist.getOauthUser().getUserName());
        oauthUser.setUserPassword(password);
        oauthUser = userRepository.update(oauthUser);

        //사용자 패스워드 변경 확인 메일 발송.
        mailService.notificationMail(oauthClient, oauthUser, NotificationType.PASSWORD_CHANGED, null, null);

        //레지스트 삭제
        registRepository.delete(oauthRegist);

        return oauthUser;
    }

    @Override
    public OauthUser rePassword(OauthClient oauthClient, OauthUser oauthUser, String beforePassword, String afterPassword) throws Exception {

        //패스워드가 맞지 않다면 에러.
        if (!oauthUser.getUserPassword().equals(beforePassword)) {
            throw new SecurityException("beforePassword is not match to afterPassword for user " + oauthUser.getUserName());
        }

        //사용자를 패스워드를 변경한다.
        oauthUser.setUserPassword(afterPassword);
        oauthUser = userRepository.update(oauthUser);

        //사용자 패스워드 변경 확인 메일 발송.
        mailService.notificationMail(oauthClient, oauthUser, NotificationType.PASSWORD_CHANGED, null, null);

        return oauthUser;
    }
}
