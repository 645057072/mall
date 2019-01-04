package com.hmall.unit;

import java.security.MessageDigest;

public class MD5Uitl {
    private static String byteAraayToHexString(byte b[]){
        StringBuffer resultSb=new StringBuffer();
        for(int i=0;i<resultSb.length();i++)
            resultSb.append(byteToHexString(b[i]));
            return resultSb.toString();
    }

    private static String byteToHexString(byte b){
        int n=b;
        if(n<0)
            n+=256;
            int d1=n/16;
            int d2=n%16;
            return  hexDigits[d1]+hexDigits[d2];
    }

    private static String MD5Encode(String origin,String charsetname){
        String resultString=null;
        try {
            resultString = new String(origin);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteAraayToHexString(messageDigest.digest(resultString.getBytes()));
            else
                resultString = byteAraayToHexString(messageDigest.digest(resultString.getBytes(charsetname)));
        }catch (Exception e){}
        return resultString.toUpperCase();
        }
        public static String MD5EncodeUtf8(String origin){
        return MD5Encode(origin,"utf-8");
    }
    private static final String hexDigits[]={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g"};
}
