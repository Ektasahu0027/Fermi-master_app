package com.example.fermi.fermi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    TextView changepass,changesub;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.Toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });
        changepass=(TextView)findViewById(R.id.emailSignup);
        changesub=(TextView)findViewById(R.id.fileds);

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(SettingActivity.this,ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        changesub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(SettingActivity.this,UpdateSubjectsActivity.class);
                startActivity(intent);
            }
        });
    }
}