package com.junhee.android.firebasepracticingfcm.domain;

/**
 * Created by JunHee on 2017. 7. 5..
 */

public class Uid {

    public String deviceUid;
    public String name;
    public String token;

    public Uid(){

    }

    public Uid(String deviceUid, String name, String token) {
        this.deviceUid = deviceUid;
        this.name = name;
        this.token = token;
    }
}
