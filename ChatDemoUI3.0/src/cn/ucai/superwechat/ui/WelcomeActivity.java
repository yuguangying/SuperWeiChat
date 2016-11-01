package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.L;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.welcome_login)
    Button welcomeLogin;
    @InjectView(R.id.welcome_register)
    Button welcomeRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.welcome_login, R.id.welcome_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.welcome_login:
                L.i("welcome_login");
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.welcome_register:
                L.i("welcome_register");
                startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
                finish();
                break;
        }
    }
}
