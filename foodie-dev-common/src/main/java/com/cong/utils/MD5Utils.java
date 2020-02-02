package com.cong.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

public class MD5Utils {

    public static String getMD5Str(String strValue) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String md5Str = Base64.encodeBase64String(md5.digest(strValue.getBytes()));

        return md5Str;
    }

    public static void main(String[] args) {
        try {
            String md5Str = getMD5Str("cong");
            System.out.println(md5Str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
