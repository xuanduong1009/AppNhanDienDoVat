package com.example.vovbacsi.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vovbacsi.R;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("https://yourwebsite.com/terms-and-conditions");

        TextView backButton = findViewById(R.id.login_link);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TermsActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
