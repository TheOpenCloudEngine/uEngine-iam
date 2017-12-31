package org.opencloudengine.garuda.client.examples;

import org.opencloudengine.garuda.client.IamClient;
import org.opencloudengine.garuda.client.ResourceOwnerPasswordCredentials;
import org.opencloudengine.garuda.client.TokenType;
import org.opencloudengine.garuda.client.util.JsonUtils;

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
        IamClient iamClient = new IamClient("iam.essencia.live", 8080, "713ec7bd-5855-4ef6-90bc-cc6a28d35529","59597da5-d9d5-4d2a-bb61-f7e513d4bf23");


        /**
         * ResourceOwnerPasswordCredentials 플로우
         */
        ResourceOwnerPasswordCredentials passwordCredentials = new ResourceOwnerPasswordCredentials();
        passwordCredentials.setUsername("user1");
        passwordCredentials.setPassword("12345");
        passwordCredentials.setScope("wrapper:pass");
        passwordCredentials.setToken_type(TokenType.JWT);

        Map claim = new HashMap();
        claim.put("aa", "bb");
        passwordCredentials.setClaim(JsonUtils.marshal(claim));

        Map map = iamClient.accessToken(passwordCredentials);
        System.out.println(JsonUtils.marshal(map));

        Map tokenInfo = iamClient.tokenInfo(map.get("access_token").toString());
        System.out.println(JsonUtils.marshal(tokenInfo));
    }
}
