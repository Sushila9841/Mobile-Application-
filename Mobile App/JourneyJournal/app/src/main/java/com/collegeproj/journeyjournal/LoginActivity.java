package com.collegeproj.journeyjournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;

    DbHelper dbHelper;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("userinfo", 0);


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        dbHelper = new DbHelper(this);
        if(sharedPreferences.getBoolean("loggedIn",false)){
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isEmailFieldValid(LoginActivity.this, email)
                        && Utility.isEmptyFieldValid(LoginActivity.this, password, "Enter password")) {
                    if (dbHelper.isLoginSuccess(email.getText().toString(), password.getText().toString())) {
                        sharedPreferences.edit().putString("userid", dbHelper.getUserid(email.getText().toString(), password.getText().toString())).commit();
                        sharedPreferences.edit().putString("fullname", dbHelper.getUsername(email.getText().toString(), password.getText().toString())).commit();
                        sharedPreferences.edit().putBoolean("loggedIn",true).commit();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Login credential", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });

    }
}