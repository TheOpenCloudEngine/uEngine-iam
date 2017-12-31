package org.opencloudengine.garuda.client.examples;

import org.opencloudengine.garuda.client.IamClient;
import org.opencloudengine.garuda.client.model.OauthClient;
import org.opencloudengine.garuda.client.model.OauthScope;
import org.opencloudengine.garuda.client.model.OauthUser;
import org.opencloudengine.garuda.client.util.JsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
                18080,
                "e74a9505-a811-407f-b4f6-129b7af1c703",
                "109cf590-ac67-4b8c-912a-913373ada046");
        iamClient.setRestBasePath("/oce/rest/v1");


        /**
         * 템플릿 테스트
         */

        /**
         * 회원가입 테스트
         */

        /**
         * 비밀번호 분실 테스트
         */

        /**
         * 비밀번호 변경 테스트
         */


//        OauthUser oauthUser = iamClient.getUserByName("jyjang@uengine.org");
//
//        iamClient.createUserAvatar(new File("/Users/uengine/Downloads/5iELGDdoK8.svg"),"image/svg+xml",oauthUser.get_id(), null);
        //iamClient.deleteUserAvatar(oauthUser.get_id());


//        /**
//         * create user
//         */
//        OauthUser user = new OauthUser();
//        user.setUserName("sample111222");
//        user.setUserPassword("gosu23546");
//
//        ArrayList<String> list = new ArrayList<>();
//        list.add("KKK");
//        user.put("pp", list);
//        OauthUser createdUser = iamClient.createUser(user);
//        System.out.println(JsonUtils.marshal(createdUser));
//
//        /**
//         * update User
//         */
//        createdUser.setUserName("sample11123");
//
//        OauthUser updateUser = iamClient.updateUser(createdUser);
//        System.out.println(JsonUtils.marshal(updateUser));
//
//
//        /**
//         * delete user
//         */
//        iamClient.deleteUser(updateUser.get_id());

//        /**
//         * create client
//         */
//        OauthClient client = new OauthClient();
//        client.setName("sample111222");
//        client.setClientTrust("3th_party"); //trust
//        client.setClientType("confidential"); //public
//        client.setActiveClient("Y");
//        client.setAuthorizedGrantTypes("code,implicit,password,credentials");
//        client.setWebServerRedirectUri("http://localhost:8080/oauth/authorize_redirect");
//        client.setRefreshTokenValidity("Y");
//        client.setCodeLifetime(3600);
//        client.setRefreshTokenLifetime(3600);
//        client.setAccessTokenLifetime(3600);
//        client.setJwtTokenLifetime(3600);
//
//        OauthClient createdClient = iamClient.createClient(client);
//        System.out.println(JsonUtils.marshal(createdClient));
//
//        /**
//         * update Client
//         */
//        createdClient.setName("sample11123");
//
//        OauthClient updateClient = iamClient.updateClient(createdClient);
//        System.out.println(JsonUtils.marshal(updateClient));
//
//
//        /**
//         * create scope
//         */
//        OauthScope scope = new OauthScope();
//        scope.setName("form:read");
//        scope.setDescription("form:read");
//
//        OauthScope createdScope = iamClient.createScope(scope);
//        System.out.println(JsonUtils.marshal(createdScope));
//
//        /**
//         * create Client scope
//         */
//        iamClient.createClientScope(createdClient.get_id(), createdScope.get_id());
//
//        /**
//         * list Client scope
//         */
//        iamClient.getAllClientScope(createdClient.get_id());
//
//        /**
//         * delete Client scope
//         */
//        iamClient.deleteClientScope(createdClient.get_id(), createdScope.get_id());
//
//
//        /**
//         * delete client
//         */
//        iamClient.deleteClient(updateClient.get_id());


    }
}
