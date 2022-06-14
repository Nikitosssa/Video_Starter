package com.example.videostarter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout checkToAdminLayout;
    private final String accessKey = "1111";
    private Boolean isAdmin = false;
    private EditText adminPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkToAdminLayout = findViewById(R.id.check_to_admin_layout);
        adminPasswordField = findViewById(R.id.admin_password_field);
    }

    public void onShowAuthClick(View view){
        checkToAdminLayout.setVisibility(View.VISIBLE);
    }

    public void onToClientActivityClick(View view){
        startClientActivity();
    }

    public void onCheckInputKeyClick(View view) {

        if (adminPasswordField.getText().toString().equals(accessKey)) {
            isAdmin = true;
        }
        if (isAdmin) {
            startAdminActivity();
        } else {
            Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAdminActivity(){
        Intent toAdminActivity = new Intent(this, AdminActivity.class);
        startActivity(toAdminActivity);
    }

    private void startClientActivity(){
        Intent toClientActivity = new Intent(this, ClientActivity.class);
        startActivity(toClientActivity);
    }

}