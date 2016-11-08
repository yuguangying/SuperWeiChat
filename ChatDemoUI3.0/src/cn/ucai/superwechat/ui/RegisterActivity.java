/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Resultbean;
import cn.ucai.superwechat.net.Dao;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * register screen
 */
public class RegisterActivity extends BaseActivity {
    @InjectView(R.id.iv_username)
    ImageView ivUsername;
    @InjectView(R.id.username)
    EditText musername;
    @InjectView(R.id.iv_usernick)
    ImageView ivUsernick;
    @InjectView(R.id.usernick)
    EditText musernick;
    @InjectView(R.id.iv_password)
    ImageView ivPassword;
    @InjectView(R.id.password)
    EditText mpassword;
    @InjectView(R.id.iv_password2)
    ImageView ivPassword2;
    @InjectView(R.id.confirm_password)
    EditText mconfirmPassword;
    @InjectView(R.id.btn_register)
    Button btnRegister;

    String username;
    String usernick;
    String pwd;
    ProgressDialog pd = null;
    RegisterActivity context;
    @InjectView(R.id.title_back)
    ImageView titleBack;
//    @InjectView(R.id.title_rl)
//    RelativeLayout titleRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_register);
        ButterKnife.inject(this);
        context = this;
//        titleRl.setVisibility(View.VISIBLE);
        titleBack.setVisibility(View.VISIBLE);

    }

    public void register() {
        username = musername.getText().toString().trim();
        usernick = musernick.getText().toString().trim();
        pwd = mpassword.getText().toString().trim();
        String confirm_pwd = mconfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            musername.requestFocus();
            return;
        } else if (username.matches("^[a-zA-Z]\\w{5,15}]$")) {
            Toast.makeText(RegisterActivity.this, "用户名格式不对，首字母，6-16位", Toast.LENGTH_SHORT).show();
            musername.requestFocus();
            return;
        } else if (TextUtils.isEmpty(usernick)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            mpassword.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            mpassword.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            mconfirmPassword.requestFocus();
            return;
        } else if (!pwd.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return;
        }


        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            pd = new ProgressDialog(this);
            pd.setMessage(getResources().getString(R.string.Is_the_registered));
            pd.show();
            registerAppService();


        }
    }

    private void registeEMService() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(username, pwd);
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            if (!RegisterActivity.this.isFinishing())
//                                pd.dismiss();
                            // save current user
                            SuperWeChatHelper.getInstance().setCurrentUserName(username);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
//                            pd.dismiss();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    pd.dismiss();
                    unregisterAppService();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }

    private void registerAppService() {
        Dao.register(context, username, usernick, pwd, new OkHttpUtils.OnCompleteListener<Resultbean>() {
            @Override
            public void onSuccess(Resultbean result) {
                if (result.isRetMsg()) {
                    registeEMService();
                } else if (result.getRetCode() == I.MSG_REGISTER_USERNAME_EXISTS) {
                    CommonUtils.showLongToast("账号已经存在");
                    L.i("账号已经存在");
                    pd.dismiss();
                } else if (result.getRetCode() == I.MSG_REGISTER_FAIL) {
                    CommonUtils.showLongToast("注册失败");
                    L.i("注册失败");
                    pd.dismiss();
                }
            }

            @Override
            public void onError(String error) {
                unregisterAppService();
                CommonUtils.showLongToast(error);
                L.i(error);
                pd.dismiss();
            }
        });
    }

    private void unregisterAppService() {
        Dao.unregister(context, username, new OkHttpUtils.OnCompleteListener<Resultbean>() {
            @Override
            public void onSuccess(Resultbean result) {
                if (result.isRetMsg()) {
                    L.i("删除数据成功");
                } else if (result.getRetCode() == I.MSG_UNREGISTER_FAIL) {
                    L.i("解除注册失败");
                } else {
                    L.i("数据异常");
                }
                pd.dismiss();
            }

            @Override
            public void onError(String error) {
                CommonUtils.showLongToast(error);
                L.i(error);
                pd.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick({R.id.btn_register,R.id.title_back})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:
                register();
                break;
            case R.id.title_back:
                finish();
                break;
        }
    }


}
