package com.segre.camup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SelectActivity extends AppCompatActivity {
    //@Override


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Log.d("PREF",
                PreferenceManager
                        .getDefaultSharedPreferences(this)
                        .getString("ftp_server", "")
        );

        Boolean gallery = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("local_gallery", true);
        if (gallery) {
            galeriaClick(findViewById(R.id.gallery_btn));
        }
    }

    public void upClick(View view) {
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }

    public void cfgClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contrassenya de administrador");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        builder.setView(input);
        final Context c = this;
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pwd = input.getText().toString();
                if (pwd.equals("prem.goras")) {
                    Intent intent = new Intent(c, SettingsActivity.class);
                    startActivity(intent);
                }
                dialog.cancel();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();
    }

    public void galeriaClick(View view) {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }
}
