package com.miracle.rajdhani.signUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.miracle.rajdhani.R;
import com.miracle.rajdhani.main.MainActivity;

public class SignUpActivity extends AppCompatActivity {
//    TextView tvTitle;
    TextView tvSignUp;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        init();
        componentEvents();
    }

    private void init() {
//        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSignUp = (TextView) findViewById(R.id.tv_signUp);
        tvLogin = (TextView) findViewById(R.id.tv_login);

//        Typeface custom_fonts = Typeface.createFromAsset(getAssets(), "fonts/ArgonPERSONAL-Regular.otf");
//        tvTitle.setTypeface(custom_fonts);
    }

    private void componentEvents() {
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(it);
                finish();
            }
        });
    }
}
