package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.UserAvatar;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.MGFT;

public class FindDetailed extends AppCompatActivity {

    @InjectView(R.id.find_head_avatar)
    ImageView findHeadAvatar;
    @InjectView(R.id.find_name)
    TextView findName;
    @InjectView(R.id.find_nick)
    TextView findNick;
    @InjectView(R.id.find_add)
    Button findAdd;
    @InjectView(R.id.find_send)
    Button findSend;
    @InjectView(R.id.find_video)
    Button findVideo;
    UserAvatar user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_detailed);
        ButterKnife.inject(this);
        user = (UserAvatar) getIntent().getSerializableExtra("name");
        if (!SuperWeChatHelper.getInstance().getAppContactList().containsKey(user.getMUserName())) {
            findAdd.setVisibility(View.VISIBLE);
        } else {
            findSend.setVisibility(View.VISIBLE);
            findVideo.setVisibility(View.VISIBLE);
        }
        if (user != null) {
            findName.setText(user.getMUserName());
            findNick.setText(user.getMUserNick());
            EaseUserUtils.setAppUserAvatar(this, user.getMUserName(), findHeadAvatar);
        }
    }

    @OnClick({R.id.find_back, R.id.find_more, R.id.find_add, R.id.find_send, R.id.find_video})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.find_back:
                finish();
                break;
            case R.id.find_more:
                break;
            case R.id.find_add:
                MGFT.gotoSend(this, user);
                break;
            case R.id.find_send:
                MGFT.gotoChat(this, user.getMUserName());
                break;
            case R.id.find_video:
                break;
        }
    }
}
