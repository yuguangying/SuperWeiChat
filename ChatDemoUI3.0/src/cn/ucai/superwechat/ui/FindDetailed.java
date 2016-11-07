package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.UserAvatar;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.Serializable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.ucai.superwechat.R;

public class FindDetailed extends AppCompatActivity {

    @InjectView(R.id.find_head_avatar)
    ImageView findHeadAvatar;
    @InjectView(R.id.find_name)
    TextView findName;
    @InjectView(R.id.find_nick)
    TextView findNick;
    @InjectView(R.id.find_add)
    Button findAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_detailed);
        ButterKnife.inject(this);
        UserAvatar user = (UserAvatar) getIntent().getSerializableExtra("name");
        if (user != null) {
            findName.setText(user.getMUserName());
            findNick.setText(user.getMUserNick());
            EaseUserUtils.setAppUserAvatar(this,user.getMUserName(), findHeadAvatar);
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
                break;
            case R.id.find_send:
                break;
            case R.id.find_video:
                break;
        }
    }
}
