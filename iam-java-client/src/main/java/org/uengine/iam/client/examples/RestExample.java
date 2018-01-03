package org.uengine.iam.client.examples;

import org.uengine.iam.client.IamClient;
import org.uengine.iam.client.model.OauthClient;
import org.uengine.iam.client.model.OauthScope;
import org.uengine.iam.client.model.OauthUser;
import org.uengine.iam.client.util.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by uengine on 2016. 9. 8..
 */
public class RestExample {
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
        OauthUser oauthUser = new OauthUser();
        oauthUser.setUserName("sample@uengine.io");
        oauthUser.setUserPassword("gosu23546");
        Map metaData = new HashMap();
        metaData.put("aaa", "bbb");
        metaData.put("bbb", "ccc");
        oauthUser.setMetaData(metaData);
        OauthUser created = iamClient.createUser(oauthUser);
        System.out.println(JsonUtils.marshal(oauthUser));

        //사용자 업데이트
        created.setUserPassword("gosu2354");
        OauthUser updated = iamClient.updateUser(created);
        System.out.println(JsonUtils.marshal(updated));


        //사용자 얻기
        OauthUser user = iamClient.getUser("sample@uengine.io");
        System.out.println(JsonUtils.marshal(user));

        //사용자 리스트
        List<OauthUser> allUser = iamClient.getAllUser(0, 2);
        System.out.println(JsonUtils.marshal(allUser));

        //사용자 삭제
        iamClient.deleteUser("sample@uengine.io");

        //클라이언트 리스트
        List<OauthClient> clients = iamClient.listAllClients();
        System.out.println(JsonUtils.marshal(clients));

        //스코프 리스트
        List<OauthScope> scopes = iamClient.listAllScopes();
        System.out.println(JsonUtils.marshal(scopes));
    }
}
