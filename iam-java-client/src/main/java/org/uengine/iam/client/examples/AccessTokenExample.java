package org.uengine.iam.client.examples;

import org.uengine.iam.client.IamClient;
import org.uengine.iam.client.ResourceOwnerPasswordCredentials;
import org.uengine.iam.client.TokenType;
import org.uengine.iam.client.model.OauthUser;
import org.uengine.iam.client.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by uengine on 2016. 9. 8..
 */
public class AccessTokenExample {
    public static void main(String[] args) throws Exception {

        /**
         * IamClient 세팅
         */
        IamClient iamClient = new IamClient(
                "localhost",
                8080,
                "my-client-key",
                "my-client-secret");
        iamClient.setRestBasePath("/rest/v1");


        //사용자 생성
        try {
            OauthUser oauthUser = new OauthUser();
            oauthUser.setUserName("sample2@uengine.io");
            oauthUser.setUserPassword("gosu23546");
            Map metaData = new HashMap();
            metaData.put("aaa", "bbb");
            metaData.put("bbb", "ccc");
            oauthUser.setMetaData(metaData);
            OauthUser created = iamClient.createUser(oauthUser);
            System.out.println(JsonUtils.marshal(oauthUser));
        } catch (Exception ex) {
            //이미 있다면 스킵
        }

        /**
         * ResourceOwnerPasswordCredentials 플로우
         */
        ResourceOwnerPasswordCredentials passwordCredentials = new ResourceOwnerPasswordCredentials();
        passwordCredentials.setUsername("sample2@uengine.io");
        passwordCredentials.setPassword("gosu23546");
        passwordCredentials.setScope("definition-service,process-service");
        passwordCredentials.setToken_type(TokenType.JWT);

        Map claim = new HashMap();
        claim.put("aa", "bb");
        passwordCredentials.setClaim(JsonUtils.marshal(claim));

        Map map = iamClient.accessToken(passwordCredentials);
        System.out.println(JsonUtils.marshal(map));

        Map tokenInfo = iamClient.tokenInfo(map.get("access_token").toString());
        System.out.println(JsonUtils.marshal(tokenInfo));

        //사용자 삭제
        iamClient.deleteUser("sample2@uengine.io");
    }
}
