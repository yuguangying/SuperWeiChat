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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.UserAvatar;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Resultbean;
import cn.ucai.superwechat.db.SuperWeChatDBManager;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.net.Dao;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * Login screen
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    public static final int REQUEST_CODE_SETNICK = 1;
    @InjectView(R.id.login_username)
    EditText loginUsername;
    @InjectView(R.id.login_password)
    EditText loginPassword;
    @InjectView(R.id.btn_login)
    Button btnLogin;
    @InjectView(R.id.title_back)
    ImageView titleBack;
    //    @InjectView(R.id.title_rl)
//    RelativeLayout titleRl;
    Context context;

    private boolean progressShow;
    private boolean autoLogin = false;
    ProgressDialog pd = null;
    String currentUsername;
    String currentPassword;
    UserAvatar userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enter the main activity if already logged in
        if (SuperWeChatHelper.getInstance().isLoggedIn()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

            return;
        }
        setContentView(R.layout.em_activity_login);
        context = this;
        ButterKnife.inject(this);

//        titleRl.setVisibility(View.VISIBLE);
        titleBack.setVisibility(View.VISIBLE);
        // if user changed, clear the password
        loginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginPassword.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (SuperWeChatHelper.getInstance().getCurrentUsernName() != null) {
            loginUsername.setText(SuperWeChatHelper.getInstance().getCurrentUsernName());
        }
    }

    /**
     * login
     *
     * @param //view
     */
    public void login() {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        currentUsername = loginUsername.getText().toString().trim();
        currentPassword = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        progressShow = true;
        pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d(TAG, "EMClient.getInstance().onCancel");
                progressShow = false;
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();

        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        SuperWeChatDBManager.getInstance().closeDB();

        // reset current user name before login
        SuperWeChatHelper.getInstance().setCurrentUserName(currentUsername);
        //本地服务登陆
        Dao.login(context, currentUsername, currentPassword, new OkHttpUtils.OnCompleteListener<Resultbean>() {
            @Override
            public void onSuccess(Resultbean result) {
                if (result.isRetMsg()) {
                    String json = result.getRetData().toString();
                    Gson gson = new Gson();
                    userAvatar = gson.fromJson(json, UserAvatar.class);
                    L.i(TAG, "userAvatar" + userAvatar.toString());
                    //环信注册
                    loginEM(currentUsername, currentPassword, pd);

                } else if (result.getRetCode() == I.MSG_LOGIN_UNKNOW_USER) {
                    CommonUtils.showLongToast("账户不存在");
                    L.i("账户不存在");
                    pd.dismiss();
                } else if (result.getRetCode() == I.MSG_LOGIN_ERROR_PASSWORD) {
                    CommonUtils.showLongToast("账户密码错误");
                    L.i("账户密码错误");
                    pd.dismiss();
                } else {
                    CommonUtils.showLongToast("登录失败");
                    L.i("登陆失败");
                    pd.dismiss();
                }
            }

            @Override
            public void onError(String error) {
                CommonUtils.showLongToast(error);
                L.i(error);
                pd.dismiss();
            }
        });

    }

    private void loginEM(String currentUsername, String currentPassword, final ProgressDialog pd) {
        final long start = System.currentTimeMillis();
        // call login method

        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");
                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                        SuperWeChatApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }
                // get user's info (this should be get from App's server or 3rd party service)
                SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                //CommonUtils.showLongToast("登陆成功");
                //将用户信息保存到内存
                SuperWeChatHelper.getInstance().setUserAvatar(userAvatar);
                //将用户信息保存到手机数据库
                UserDao ud = new UserDao(context);
                ud.saveUser(userAvatar);


                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pd != null)
            pd.dismiss();
    }

    /**
     * register
     *
     * @param //view
     */
    public void register() {
        startActivityForResult(new Intent(this, RegisterActivity.class), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
        if (SuperWeChatHelper.getInstance().getCurrentUsernName() != null) {
            loginUsername.setText(SuperWeChatHelper.getInstance().getCurrentUsernName());
        }
    }

    @OnClick({R.id.title_back, R.id.btn_login, R.id.login_register})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.login_register:
                register();
                break;
        }
    }
}
