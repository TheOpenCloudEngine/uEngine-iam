package org.uengine.iam.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

/**
 * Created by uengine on 2015. 5. 22..
 */
public class JwtTest {

    public static void main(String[] args) throws Exception {

        // RSA signatures require a public and private RSA key pair, the public key
// must be made known to the JWS recipient in order to verify the signatures
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(1024);

        KeyPair kp = keyGenerator.genKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey)kp.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey)kp.getPrivate();

        FileOutputStream fos = new FileOutputStream("/Users/uengine/IdeaProjects/oceIAM/iam-web/src/main/resources/jwt-keys/RS256.private");
        fos.write(privateKey.getEncoded());
        fos.close();

        FileOutputStream fos2 = new FileOutputStream("/Users/uengine/IdeaProjects/oceIAM/iam-web/src/main/resources/jwt-keys/RS256.pub");
        fos2.write(publicKey.getEncoded());
        fos2.close();

        privateKey = JwtUtils.getRSAPrivateKey(new File("/Users/uengine/IdeaProjects/oceIAM/iam-web/src/main/resources/jwt-keys/RS256.private"));
        publicKey = JwtUtils.getRSAPublicKey(new File("/Users/uengine/IdeaProjects/oceIAM/iam-web/src/main/resources/jwt-keys/RS256.pub"));


        System.out.println(publicKey.getFormat());
        System.out.println(privateKey.getFormat());
        System.out.println(new String(publicKey.getEncoded(),"utf-8"));
        System.out.println(new String(privateKey.getEncoded(),"utf-8"));


// Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(privateKey);

// Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("alice")
                .issuer("https://c2id.com")
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.RS256),
                claimsSet);

// Compute the RSA signature
        signedJWT.sign(signer);

// To serialize to compact form, produces something like
// eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
// mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
// maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
// -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
        String s = signedJWT.serialize();

// On the consumer side, parse the JWS and verify its RSA signature
        signedJWT = SignedJWT.parse(s);

        JWSVerifier verifier = new RSASSAVerifier(publicKey);

        boolean verify = signedJWT.verify(verifier);
        System.out.println(verify);


        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0NjIxMjQ3ODYsImNvbnRleHQiOnsic2NvcGVzIjoiZm9ybTpjcmVhdGUiLCJtYW5hZ2VtZW50SWQiOiI1ZTM0MzJhZDE3MjY0NGVhODA0MWZlNjdlYjhmNWNiZCIsInJlZnJlc2hUb2tlbiI6IjhjNTYzODViLTEwM2ItNDkyMS04ODA3LWYwYTgwZDFmZDI5ZiIsInR5cGUiOiJ1c2VyIiwib2F1dGhVc2VySWQiOiIxNTQzYzVhYzJjNTA0OWIxODA1ODY2MmRhMjM2ZjAxMSIsImNsaWVudElkIjoiOTdlZDhmMzBkYTAxNDgwMGI5MmEyNGQ4YmVkNmQ1YTUifSwiaXNzIjoib2NlLmlhbSIsImNsYWltIjp7ImFhYSI6ImJiYiJ9LCJpYXQiOjE0NjIxMjExODZ9.J8Edpn5fTtgx9-6cLXGiYV0NnRuPO2rbv28xkR55sl4";

//        //만료시간 체크 없이 시그네이쳐 밸리데이션
//        boolean validateToken = JwtUtils.validateToken(jwt);
//
//
//        //Jwt 토큰에 포함된 만료시간과 함께 시그네이쳐 밸리데이션
//        boolean validateToken1 = JwtUtils.validateToken(jwt, null);
//
//
//        //임의의 만료시간과 함께 시그네이쳐 밸리데이션
//        Date expireTime = new Date(140213...);
//        boolean validateToken2 = JwtUtils.validateToken(jwt, expireTime);
    }
}
