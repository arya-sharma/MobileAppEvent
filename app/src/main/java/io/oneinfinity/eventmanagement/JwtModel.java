package io.oneinfinity.eventmanagement;

/**
 * Created by ancha on 12/16/2017.
 */

public class JwtModel {

    public static String jwtToken;
    public static String password;

    JwtModel(String token, String password) {
        JwtModel.jwtToken = token;
        JwtModel.password = password;
    }

}
