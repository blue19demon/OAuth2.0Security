package com.auth.utils;
public class AssertUtils {
    private static final String authTokenPrefix="Bearer";
    /**
     * @description 获取请求头的token
     * @param authToken
     * @return
     */
    public static String extracteToken(String authToken){
        if(authToken.indexOf(authTokenPrefix)!=-1){
            return authToken.substring(7);
        }else {
            return authToken;
        }
    }
    
    /**
     * @description 获取请求头的token
     * @param authToken
     * @return
     */
    public static String buildBearerToken(String token){
        return authTokenPrefix+" "+token;
    }
}