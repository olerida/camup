package com.segre.camup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UploadActivity extends AppCompatActivity {

    static final String PATH = "file:///storage/emulated/0/DCIM";
    static final String HOST = "ftp.segre.com";
    static final String USER = "foto";
    static final String PWD = "f.oto";
    static final String ALBUM = "proves_oscar";

    FTPClient ftp = new FTPClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            ftp.logout();
            ftp.disconnect();
            System.out.println("FTP connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Alert missage ok button close
    private void alert(String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(UploadActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                });
        alertDialog.show();
    }

    // Start
    public void start() {
        //test network
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Boolean connected = false;
            Log.d("NET", "Network connected");
            try {
                connected = new ftpConnectTask().execute(HOST, USER, PWD).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // check storage state
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                File imagesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), "/Camera");
                //File imagesDir = new File("/sdcard/DCIM/Camera");

                Log.d("Files", "Path: " + imagesDir.getPath());
                Log.d("Files", "Exist: " + imagesDir.exists());
                Log.d("Files", "IsDir: " + imagesDir.exists());
                Log.d("Files", "CanRead: " + imagesDir.canRead());
                File[] files = imagesDir.listFiles();
                Log.d("Files", "Images on dir: "+ files.length);

                if (!imagesDir.exists() || !imagesDir.canRead()) {
                    Log.d("FILES", "Cant access filesystem");
                    return;
                }



                // Check and go to working directory (album)
                try {
                    new ftpChangeDirTask().execute(ALBUM);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < files.length; i++)
                {
                    Log.d("Files", "FileName:" + files[i].getName());
                    // Call Upload files task
                    try {
                        Boolean uploaded = new ftpUploadFilesTask().execute(files[i]).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            alert("Sin conexión", "Comprueba que tienes conexion WIFI o de datos moviles");

        }
    }

    private class ftpChangeDirTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... dirs) {
            try {
                String dir = dirs[0];
                if (!ftp.changeWorkingDirectory(dir)) {
                    ftp.makeDirectory(dir);
                    ftp.changeWorkingDirectory(dir);
                }
                Log.d("FTP", ftp.getReplyString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ftpUploadFilesTask extends AsyncTask<File, Void, Boolean> {
        @Override
        protected Boolean doInBackground(File... files) {
            Log.d("Files", "Uploading: " + files[0].getName());
            InputStream input;
            File file = files[0];

            // Create stream and upload to ftp
            try {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                input = new FileInputStream(file);
                ftp.storeFile(file.getName(), input);
                input.close();
            } catch (FileNotFoundException e) {
                Log.w("FTP", "File not found: " + files[0].getName());
            } catch (IOException e) {
                Log.e("FTP", "Error on upload file: " + file.getName());
            }
            return true;
        }
    }

    // Connect and login on FTP servere task
    private class ftpConnectTask extends AsyncTask<String, Void, Boolean> {
        String host, user, pwd;

        @Override
        protected Boolean doInBackground(String... param) {

            host = param[0]; user = param[1]; pwd = param[2];

            try {
                ftp.connect(host);
                Log.d("FTP", "Connected to " + host + ".");
                ftp.enterRemotePassiveMode();
                Log.d("FTP", ftp.getReplyString());
                return ftp.login(user, pwd);
            } catch (Exception e) {
                Log.d("FTP", "Cant connect to " + host + ".");
                return false;
            }
        }

        protected void onPostExecute(Boolean login) {
        }

    }
}
