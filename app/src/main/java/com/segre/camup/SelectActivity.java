package com.segre.camup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static android.provider.LiveFolders.INTENT;

public class SelectActivity extends AppCompatActivity {
    public final static String FILESLST = "com.segre.camup.FILESLST";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
    }

    public void upClick(View view) {
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }
}
