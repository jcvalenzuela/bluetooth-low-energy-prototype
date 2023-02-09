package com.example.myapplication;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.LoginState.START_LOADING;
import static com.example.myapplication.LoginState.STOP_LOADING;

public class LoginViewModel extends ViewModel {
    private final String TAG = "xxx " + this.getClass().getSimpleName();

    public Integer add(Integer input1, Integer input2) {
        return input1 + input2;
    }

    private Integer multiply(Integer input1, Integer input2) {
        return input1 * input2;
    }

    private ApiClient apiClient;
    private CompositeDisposable compositeDisposableViewModel = new CompositeDisposable();

    private MutableLiveData<LoginState> isLoadingLiveData = new MutableLiveData<>();

    public LiveData<LoginState> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    private MutableLiveData<LiveLoginModel> loginLiveData = new MutableLiveData<>();

    public LiveData<LiveLoginModel> getLoginLiveData() {
        return loginLiveData;
    }

    private MutableLiveData<LiveLoginModel> registerLiveData = new MutableLiveData<>();

    public LiveData<LiveLoginModel> getRegisterLiveData() {
        return registerLiveData;
    }

    private Observable<LoginModel> getLoginResponse(@NonNull String username, @NonNull String password) {
        return apiClient.getLogin(username, password);
    }

    private Observable<LoginModel> getRegisterResponse(@NonNull String username, @NonNull String password) {
        return apiClient.getRegister(username, password,"example");
    }


    public void dispose() {
        if (compositeDisposableViewModel != null && !compositeDisposableViewModel.isDisposed()) {
            Log.e(TAG, "Number of disposed items: " + compositeDisposableViewModel.size());
            compositeDisposableViewModel.dispose();
        }
    }

    // Method outside
    public void login(final String userName, final String passWord) {
        final Gson gson = new GsonBuilder().setLenient().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        try {
            Interceptor responseCodeInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    Log.e(TAG, "HTTP protocol: " + response.code());
                    return response;
                }
            };

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(responseCodeInterceptor)
                    .build();

            final Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.10.71.61/sample/")
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            apiClient = retrofit.create(ApiClient.class);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Retrofit error: " + e.getMessage());
            // LiveData feed
            loginLiveData.postValue(new LiveLoginModel(LoginState.LOGIN_FAILED, "Network problem"));
            return;
        }

        compositeDisposableViewModel.add(getLoginResponse(convertStringToUTF8(userName), convertStringToUTF8(passWord))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LoginModel>() {
                    @Override
                    protected void onStart() {
                        Log.e(TAG, "onStart");
                        // LiveData feed
                        isLoadingLiveData.postValue(START_LOADING);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                        // LiveData feed
                        isLoadingLiveData.postValue(STOP_LOADING);
                        compositeDisposableViewModel.clear();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        // LiveData feed
                        isLoadingLiveData.postValue(STOP_LOADING);
                        loginLiveData.postValue(new LiveLoginModel(LoginState.LOGIN_FAILED, e.getMessage()));
                        compositeDisposableViewModel.clear();
                    }

                    @Override
                    public void onNext(LoginModel loginModel) {
                        Log.e(TAG, "Response code: " + loginModel.getResponseCode());
                        Log.e(TAG, "Server message: " + loginModel.getServerMessage());

                        if (loginModel.getResponseCode().equals("0")) {
                            // LiveData feed
                            loginLiveData.postValue(new LiveLoginModel(LoginState.LOGIN_SUCCESS, loginModel.getServerMessage()));
                        } else if (loginModel.getResponseCode().equals("1")) {
                            // LiveData feed
                            loginLiveData.postValue(new LiveLoginModel(LoginState.INVALID_PASSWORD, loginModel.getServerMessage()));
                        } else if (loginModel.getResponseCode().equals("2")) {
                            // LiveData feed
                            loginLiveData.postValue(new LiveLoginModel(LoginState.LOGIN_FAILED, loginModel.getServerMessage()));
                        }
                    }
                }));
    }

    // Method outside
    public void register(final String userName, final String passWord) {
        final Gson gson = new GsonBuilder().setLenient().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        try {
            Interceptor responseCodeInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    Log.e(TAG, "HTTP protocol: " + response.code());
                    return response;
                }
            };

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(responseCodeInterceptor)
                    .build();

            final Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.10.71.61/sample/")
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            apiClient = retrofit.create(ApiClient.class);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Retrofit error: " + e.getMessage());
            // LiveData feed
            registerLiveData.postValue(new LiveLoginModel(LoginState.LOGIN_FAILED, "Network problem"));
            return;
        }

        compositeDisposableViewModel.add(getRegisterResponse(convertStringToUTF8(userName), convertStringToUTF8(passWord))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LoginModel>() {
                    @Override
                    protected void onStart() {
                        Log.e(TAG, "onStart");
                        // LiveData feed
                        isLoadingLiveData.postValue(START_LOADING);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                        // LiveData feed
                        isLoadingLiveData.postValue(STOP_LOADING);
                        compositeDisposableViewModel.clear();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        // LiveData feed
                        isLoadingLiveData.postValue(STOP_LOADING);
                        registerLiveData.postValue(new LiveLoginModel(LoginState.REGISTRATION_FAILED, e.getMessage()));
                        compositeDisposableViewModel.clear();
                    }

                    @Override
                    public void onNext(LoginModel loginModel) {
                        Log.e(TAG, "Response code: " + loginModel.getResponseCode());
                        Log.e(TAG, "Server message: " + loginModel.getServerMessage());

                        if (loginModel.getResponseCode().equals("0")) {
                            // LiveData feed
                            registerLiveData.postValue(new LiveLoginModel(LoginState.REGISTRATION_SUCCESS, loginModel.getServerMessage()));
                        } else if (loginModel.getResponseCode().equals("1")) {
                            // LiveData feed
                            registerLiveData.postValue(new LiveLoginModel(LoginState.EXISTING_USERNAME, loginModel.getServerMessage()));
                        }else if(loginModel.getResponseCode().equals("2")){
                            registerLiveData.postValue(new LiveLoginModel(LoginState.REGISTRATION_PENDING, loginModel.getServerMessage()));
                        }
                    }
                }));
    }

    public static String convertStringToUTF8(String input) {
        String output = input;
//        try {
//            output = URLEncoder.encode(output, "utf-8");
//        } catch (java.io.UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return output;
    }

//    // Encoding algorithm
//    private String encodeSha256(final String inputString) {
//        // Convert to hash value of SHA256
//        String sha256Result = "";
//
//        try {
//            MessageDigest md;
//            md = MessageDigest.getInstance("SHA-256");
//            md.update(inputString.getBytes());
//
//            byte[] bytes = md.digest();
//            StringBuffer result = new StringBuffer();
//            for (byte b : bytes) {
//                result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
//            }
//            sha256Result = result.toString();
//        } catch (NoSuchAlgorithmException e) {
//            Log.e(TAG, e.getMessage());
//        }
//
//        return sha256Result;
//    }
}