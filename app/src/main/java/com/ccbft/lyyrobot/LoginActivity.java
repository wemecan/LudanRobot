package com.ccbft.lyyrobot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ccbft.lyyrobot.domain.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {
    private Button loginBtn;
    private EditText accountEt,passwordEt;
    private CheckBox checkBox;
    private TextView registerTv;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView forgetPasswordTv;
    private boolean flag;//是否保存了用户名和密码
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this,"23e6f080b022e006d4fb747bf1f28856");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        initData();
        initView();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=accountEt.getText().toString().trim();
                String password=passwordEt.getText().toString().trim();
                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                User user=new User();
                user.setUsername(username);
                user.setPassword(password);
                user.login(new SaveListener<User>() {

                    @Override
                    public void done(User user1, BmobException e) {
                        if(e==null){
                            if(checkBox.isChecked()){
                                editor.putString("username",username);
                                editor.putString("password",password);
                                editor.putBoolean("flag",true);
                                editor.apply();//将密码保存到本地
                            }else {//清除本地保存的用户名密码信息
                                editor.remove("username");
                                editor.remove("password");
                                editor.remove("flag");
                                editor.apply();
                            }
                            User currentUser = BmobUser.getCurrentUser(User.class);
                            Toast.makeText(LoginActivity.this, currentUser.getNickname()+"\n登录成功！", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        }
                        else {
                            String msg=e.getMessage();
                            Snackbar snackbar = Snackbar.make(v, "登录失败!\n" + msg, Snackbar.LENGTH_LONG);

                        }

                    }
                });

            }
        });

        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,0);
            }
        });

        forgetPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
            }
        });
    }

    private void initData() {
        sharedPreferences=getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    private void initView() {
        loginBtn=findViewById(R.id.loginBtn);
        accountEt=findViewById(R.id.accountEt);
        passwordEt=findViewById(R.id.passwordEt);
        registerTv=findViewById(R.id.registerTv);
        loginBtn=findViewById(R.id.loginBtn);
        checkBox=findViewById(R.id.checkBox);
        forgetPasswordTv=findViewById(R.id.forgetPasswordTv);
        accountEt.setText(sharedPreferences.getString("username",""));
        passwordEt.setText(sharedPreferences.getString("password",""));
        flag = sharedPreferences.getBoolean("flag", false);
        if (flag){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String u = data.getStringExtra("u");
            String p = data.getStringExtra("p");
            accountEt.setText(u);
            passwordEt.setText(p);
        }
    }

}
