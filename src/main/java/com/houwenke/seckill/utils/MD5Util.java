package com.houwenke.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }
    public static final String salt="1a2b3c4d";
    public static String inputPasswordToFromPass(String inputPass){
        String str=""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }
    public static String formPasswordToDBPass(String formPass,String salt){
        String str=""+salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }
    public static String inputPasswordToDBPass(String inputPass,String salt){
        String fromPass=inputPasswordToFromPass(inputPass);
        String dbPass=formPasswordToDBPass(fromPass,salt);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPasswordToFromPass("123456"));
        System.out.println(formPasswordToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
        System.out.println(inputPasswordToDBPass("123456","1a2b3c4d"));
    }
}
