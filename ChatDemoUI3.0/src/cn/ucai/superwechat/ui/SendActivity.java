package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.UserAvatar;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.parse.LogInCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Resultbean;
import cn.ucai.superwechat.net.Dao;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.OkHttpUtils;

public class SendActivity extends AppCompatActivity {
    String TAG = "send";
    @InjectView(R.id.send_back)
    ImageView sendBack;
    @InjectView(R.id.send_yan)
    EditText sendYan;
    @InjectView(R.id.send_notification)
    EaseSwitchButton sendNotification;
    String verification;
    private ProgressDialog progressDialog;
    UserAvatar user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        ButterKnife.inject(this);
        user = (UserAvatar) getIntent().getSerializableExtra("send");
        verification = sendYan.getText().toString().trim();
    }


    @OnClick({R.id.send_tv, R.id.send_notification,R.id.send_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_back:
                finish();
                break;
            case R.id.send_tv:
                sendContactRequest();
                break;
            case R.id.send_notification:
                break;
        }
    }

    public void sendContactRequest(){

        if(SuperWeChatHelper.getInstance().getContactList().containsKey(EMClient.getInstance().getCurrentUser())){
            //let the user know the contact already in your contact list
            if(EMClient.getInstance().contactManager().getBlackListUsernames().contains(user)){
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {
                try {
                    //demo use a hardcode reason here, you need let user to input if you like
                    String s = getResources().getString(R.string.Add_a_friend);
                    EMClient.getInstance().contactManager().addContact(user.getMUserName(), s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                            //addContact();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void addContact() {
        Dao.addContact(this, SuperWeChatHelper.getInstance().getCurrentUsernName(), user.getMUserName(), new OkHttpUtils.OnCompleteListener<Resultbean>() {
            @Override
            public void onSuccess(Resultbean result) {
                if (result.isRetMsg()) {
                    String json = result.getRetData().toString().trim();
                    Gson gson = new Gson();
                    UserAvatar userAvatar = gson.fromJson(json, UserAvatar.class);
                    SuperWeChatHelper.getInstance().saveAppContact(userAvatar);
                    Log.i(TAG, "onSuccess: success");
                }else {
                    Log.i(TAG, "onSuccess: "+result);
                }
            }
            @Override
            public void onError(String error) {
                Log.i("send", "onError: "+error);
                CommonUtils.showLongToast(error);
            }
        });
    }

}
