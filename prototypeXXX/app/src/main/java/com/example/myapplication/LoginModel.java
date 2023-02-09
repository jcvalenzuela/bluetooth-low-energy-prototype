package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("response_code")
    private String responseCode;

    @SerializedName("server_message")
    private String serverMessage;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getServerMessage() {
        return serverMessage;
    }

    public void setServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }
}