package org.uengine.iam.oauthuser.billing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uengine.iam.oauthuser.OauthUser;

import java.util.Map;

@Service
public class BillingService {

    @Autowired
    BillingApi billingApi;

    @Autowired
    BillingConfig billingConfig;

    /**
     * IAM 과 빌링 어카운트의 싱크를 맞춘다.
     *
     * @param user
     * @return
     */
    public void syncUser(OauthUser user) {
        try {
            if (!billingConfig.isAccountSync()) {
                return;
            }
            Map account = billingApi.getAccountByExternalKey(user.getUserName());
            //if account not exist? create new billing account.
            if (account == null) {
                billingApi.createAccount(user.getUserName(), user.getMetaData());
            } else {
                billingApi.updateAccount(account.get("accountId").toString(), user.getUserName(), user.getMetaData());
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
