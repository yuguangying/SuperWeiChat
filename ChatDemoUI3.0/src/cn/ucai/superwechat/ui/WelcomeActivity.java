package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.ucai.superwechat.R;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener{
Button login,register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        login = (Button) findViewById(R.id.welcome_login);
        register = (Button) findViewById(R.id.welcome_register);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.welcome_login:
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.welcome_register:
                startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
                finish();
                break;
        }
    }
}
