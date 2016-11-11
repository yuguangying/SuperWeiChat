package cn.ucai.superwechat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.UserAvatar;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Resultbean;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.net.Dao;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {

	private static final int sleepTime = 2000;

	UserAvatar user;
	Context context;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.em_activity_splash);
		super.onCreate(arg0);
		context = this;
		RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				if (SuperWeChatHelper.getInstance().isLoggedIn()) {
					// auto login mode, make sure all group and conversation is loaed before enter the main screen
					long start = System.currentTimeMillis();
					EMClient.getInstance().groupManager().loadAllGroups();
					EMClient.getInstance().chatManager().loadAllConversations();

					//再次将用户信息保存到内存
					UserDao ud = new UserDao(context);
					user = ud.getUser(EMClient.getInstance().getCurrentUser());
					SuperWeChatHelper.getInstance().setUserAvatar(user);
					ContactAllList();
					long costTime = System.currentTimeMillis() - start;

					//wait
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//enter main screen
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
					finish();
				}
			}
		}).start();
	}

	private void ContactAllList() {
		Dao.downloadContactAllList(context, new OkHttpUtils.OnCompleteListener<Resultbean>() {
            @Override
            public void onSuccess(Resultbean result) {
                if (result.isRetMsg()) {
                    String json = result.getRetData().toString().trim();
                    Gson gson = new Gson();
                    UserAvatar[] users = gson.fromJson(json, UserAvatar[].class);
                    Map<String, UserAvatar> userList = new HashMap<String, UserAvatar>();
                    for (UserAvatar user : users) {
                        EaseCommonUtils.setAppUserInitialLetter(user);
                        userList.put(user.getMUserName(), user);
                    }
                    SuperWeChatHelper.getInstance().setAppContactList(userList);
                    // save the contact list to database
                    UserDao dao = new UserDao(context);
                    ArrayList<UserAvatar> list = new ArrayList<UserAvatar>(userList.values());
                    L.e("list","SuperHelper : "+list);
                    dao.saveAppContactList(list);
                    SuperWeChatHelper.getInstance().notifyContactsSyncListener(true);
                }
            }

            @Override
            public void onError(String error) {
                CommonUtils.showLongToast(error);
                Log.i("list", "onError: splash : " + error);
            }
        });
	}

	/**
	 * get sdk version
	 */
	private String getVersion() {
	    return EMClient.getInstance().getChatConfig().getVersion();
	}
}
