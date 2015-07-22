package com.jfc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                SharedPreferences sp = getApplication().getSharedPreferences("MainSharedPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor spe = sp.edit();
                spe.putBoolean("AppLoggedIn",true);
                spe.putString("Username", username);
                spe.putString("Password", password);
                spe.apply();
                final Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}