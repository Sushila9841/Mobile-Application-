package com.collegeproj.journeyjournal;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText email, fullname, password, address, mobile;

    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DbHelper(this);
        email = findViewById(R.id.email);
        fullname = findViewById(R.id.fullname);
        password = findViewById(R.id.password);
        address = findViewById(R.id.address);
        mobile = findViewById(R.id.mobile);

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isEmptyFieldValid(RegisterActivity.this, fullname, "Enter Full name")
                        && Utility.isEmptyFieldValid(RegisterActivity.this, mobile, "Enter Mobile number")
                        && Utility.isEmptyFieldValid(RegisterActivity.this, address, "Enter address")
                        && Utility.isEmailFieldValid(RegisterActivity.this, email)
                        && Utility.isEmptyFieldValid(RegisterActivity.this, password, "Enter password")) {


                    ContentValues contentValues = new ContentValues();
                    contentValues.put("email", email.getText().toString());
                    contentValues.put("address", address.getText().toString());
                    contentValues.put("fullname", fullname.getText().toString());
                    contentValues.put("phone", mobile.getText().toString());
                    contentValues.put("password", password.getText().toString());

                    if (dbHelper.insertUser(contentValues)) {
                        Toast.makeText(RegisterActivity.this, "User registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "email already registered.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
}