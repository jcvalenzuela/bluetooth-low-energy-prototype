package com.example.myapplication;

public class LiveLoginModel {
    private LoginState loginState;
    private String apiMessage;

    public LiveLoginModel(LoginState loginState, String apiMessage) {
        this.loginState = loginState;
        this.apiMessage = apiMessage;
    }

    public LoginState getLoginState() {
        return loginState;
    }

    public void setLoginState(LoginState loginState) {
        this.loginState = loginState;
    }

    public String getApiMessage() {
        return apiMessage;
    }

    public void setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
    }
}

