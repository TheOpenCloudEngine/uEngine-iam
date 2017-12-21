package org.uengine.iam.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {
    public static String encrypt(String key1, String key2, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2.getBytes("UTF-8"));

            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string:" + Base64.encodeBase64String(encrypted));

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String key1, String key2, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2.getBytes("UTF-8"));

            System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println(key1);
            System.out.println(key2);

            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        String key1 = "ThisisServerIdPa"; // 128 bit key
        String key2 = "rserForUengine.";

        //서버 아이디 생성
        System.out.println(encrypt(key1, key2, "64:76:ba:b4:29:8c"));

        //서버 아이디에서 맥 어드레스 얻기
        System.out.println(decrypt(key1, key2, "fV7P8lPNtESx1rk7lOAK+Cv4H8li0BA6uUh88HTlKO0="));

    }
}

