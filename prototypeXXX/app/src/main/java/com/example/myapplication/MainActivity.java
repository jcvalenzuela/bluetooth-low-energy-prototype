package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jakewharton.rxbinding3.view.RxView;
import com.ligl.android.widget.iosdialog.IOSDialog;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "xxx " + this.getClass().getSimpleName();
    private Context context;

    private Button btnLogin;
    private Button btnRegister;
    private EditText txtUsername;
    private EditText txtPassword;
    private ProgressBar progressBar;

    public MainActivity() {
        context = MainActivity.this;
    }

    private LoginViewModel loginViewModel;
    private CompositeDisposable compositeDisposableButton = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        txtUsername = findViewById(R.id.txt_username);
        txtPassword = findViewById(R.id.txt_password);
        progressBar = findViewById(R.id.progress_bar);

        intiVM();
        initTap();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposableButton != null && !compositeDisposableButton.isDisposed()) {
            Log.e(TAG, "Number of disposed items: " + compositeDisposableButton.size());
            compositeDisposableButton.dispose();
        }

        loginViewModel.dispose();
    }

    private void initTap() {
        compositeDisposableButton.add((RxView.clicks(btnLogin)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        if (TextUtils.isEmpty(txtUsername.getText()) || TextUtils.isEmpty(txtUsername.getText())) {
                            new IOSDialog.Builder(context)
                                    .setTitle("Cloud Service Prototype")
                                    .setMessage("Empty login credentials")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        } else {
                            loginViewModel.login(txtUsername.getText().toString(), txtPassword.getText().toString());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                })));

        compositeDisposableButton.add((RxView.clicks(btnRegister)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        if (TextUtils.isEmpty(txtUsername.getText()) || TextUtils.isEmpty(txtUsername.getText())) {
                            new IOSDialog.Builder(context)
                                    .setTitle("Cloud Service Prototype")
                                    .setMessage("Empty login credentials")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        } else {
                            loginViewModel.register(txtUsername.getText().toString(), txtPassword.getText().toString());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                })));
    }

    private void intiVM() {
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        // Login
        loginViewModel.getLoginLiveData().observe(this, new Observer<LiveLoginModel>() {
            @Override
            public void onChanged(LiveLoginModel liveLoginModel) {
                Log.e(TAG, "onChanged: " + liveLoginModel.getLoginState().toString());

                switch (liveLoginModel.getLoginState()) {
                    case LOGIN_SUCCESS:
                        new IOSDialog.Builder(context)
                                .setTitle("Cloud Service Prototype")
                                .setMessage(liveLoginModel.getApiMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //Intent intent = new Intent(MainActivity.this, BleActivity.class);
                                        //startActivity(intent);
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        break;
                    case INVALID_PASSWORD:
                        new IOSDialog.Builder(context)
                                .setTitle("Cloud Service Prototype")
                                .setMessage(liveLoginModel.getApiMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        break;
                    case LOGIN_FAILED:
                        new IOSDialog.Builder(context)
                                .setTitle("Cloud Service Prototype")
                                .setMessage(liveLoginModel.getApiMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        break;
                }
            }
        });

        // Register
        loginViewModel.getRegisterLiveData().observe(this, new Observer<LiveLoginModel>() {
            @Override
            public void onChanged(LiveLoginModel liveLoginModel) {
                Log.e(TAG, "onChanged: " + liveLoginModel.getLoginState().toString());

                switch (liveLoginModel.getLoginState()) {
                    case REGISTRATION_SUCCESS:
                        new IOSDialog.Builder(context)
                                .setTitle("Cloud Service Prototype")
                                .setMessage(liveLoginModel.getApiMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        break;
                    case EXISTING_USERNAME:
                        new IOSDialog.Builder(context)
                                .setTitle("Cloud Service Prototype")
                                .setMessage(liveLoginModel.getApiMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        break;
                    case REGISTRATION_FAILED:
                        new IOSDialog.Builder(context)
                                .setTitle("Cloud Service Prototype")
                                .setMessage(liveLoginModel.getApiMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        break;
                    case REGISTRATION_PENDING:
                        new IOSDialog.Builder(context)
                                .setTitle("Cloud Service Prototype")
                                .setMessage(liveLoginModel.getApiMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        break;
                }
            }
        });

        // Progress dialog
        loginViewModel.getIsLoadingLiveData().observe(this, new Observer<LoginState>() {
            @Override
            public void onChanged(LoginState loginState) {
                Log.e(TAG, "onChanged: " + loginState.toString());
                switch (loginState) {
                    case START_LOADING:
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case STOP_LOADING:
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        progressBar.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    public void convert() throws Exception {
        compositeDisposableButton.add((RxView.clicks(btnLogin)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        if (TextUtils.isEmpty(txtUsername.getText()) || TextUtils.isEmpty(txtUsername.getText())) {
                            new IOSDialog.Builder(context)
                                    .setTitle("Cloud Service Prototype")
                                    .setMessage("Empty login credentials")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        } else {
                            loginViewModel.login(txtUsername.getText().toString(), txtPassword.getText().toString());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                })));
    }
}