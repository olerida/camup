package com.segre.camup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UploadActivity extends AppCompatActivity {

    //static final String PATH = "file:///storage/emulated/0/DCIM/Wi-Fi SD";
    static final String PATH = "Wi-Fi SD";
    static final String HOST = "ftp.segre.com";
    static final String USER = "foto";
    static final String PWD = "f.oto";
    static final String ALBUM = "proves_oscar";

    FTPClient ftp = new FTPClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (ftp.isConnected()) ftp.disconnect();
            Log.d("FTP", "FTP connection closed.");
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


    @Override
    public void onStart() {
        super.onStart();

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
                File imagesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), PATH);
                //File imagesDir = new File("/sdcard/DCIM/Camera");

                Log.d("Files", "Path: " + imagesDir.getPath());
                Log.d("Files", "Exist: " + imagesDir.exists());
                Log.d("Files", "IsDir: " + imagesDir.exists());
                Log.d("Files", "CanRead: " + imagesDir.canRead());
                final File[] files = imagesDir.listFiles();
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

                final Context c = this;
                final LinearLayout vl = (LinearLayout) findViewById(R.id.vLayout);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for ( int i = 0; i < files.length; i++) {
                                final File f =  files[i];
                                Log.d("Files", "FileName:" + files[i].getName());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        RelativeLayout rl = new RelativeLayout(c);
                                        ImageView img = new ImageView(c);
                                        BitmapFactory.Options bopt = new BitmapFactory.Options();
                                        bopt.inSampleSize = 2;
                                        Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath(), bopt);
                                        Drawable d = new BitmapDrawable(getResources(), b);
                                        img.setImageDrawable(d);
                                        img.setLayoutParams(new LinearLayoutCompat.LayoutParams(300, 300));
                                        img.setForegroundGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                                        rl.addView(img); // 0 imatge de la foto
                                        ProgressBar pg = new ProgressBar(c);
                                        pg.setForegroundGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                                        rl.addView(pg); // 1 progressbar
                                        b = BitmapFactory.decodeResource(getResources(), R.drawable.completed, bopt);
                                        img = new ImageView(c);
                                        img.setLayoutParams(new LinearLayoutCompat.LayoutParams(100, 100));
                                        d = new BitmapDrawable(getResources(), b);
                                        img.setImageDrawable(d);
                                        img.setVisibility(View.INVISIBLE);
                                        rl.addView(img); // 2 imatge del ok
                                        b = BitmapFactory.decodeResource(getResources(), R.drawable.error, bopt);
                                        img = new ImageView(c);
                                        img.setLayoutParams(new LinearLayoutCompat.LayoutParams(100, 100));
                                        d = new BitmapDrawable(getResources(), b);
                                        img.setImageDrawable(d);
                                        img.setVisibility(View.INVISIBLE);
                                        rl.addView(img); // 3 imatge del error

                                        LinearLayout hl = new LinearLayout(c); hl.setOrientation(LinearLayout.HORIZONTAL);
                                        hl.addView(rl);
                                        TextView tv = new TextView(c);
                                        tv.setText(f.getName());
                                        hl.addView(tv);

                                        vl.addView(hl);
                                        ScrollView sc = (ScrollView) findViewById(R.id.scrView);
                                        sc.fullScroll(View.FOCUS_DOWN);
                                    }
                                });

                                // Call Upload files task
                                try {
                                    Boolean uploaded = new ftpUploadFilesTask(c, i).execute(f).get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }).start();
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
        File file;
        Context mc;
        int p;

        public ftpUploadFilesTask(Context c, int position) {
            mc = c; p = position;
        }

        @Override
        protected Boolean doInBackground(File... files) {


            Log.d("Files", "Uploading: " + files[0].getName());
            InputStream input;
            file = files[0];

            // Create stream and upload to ftp
            try {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                input = new FileInputStream(file);
                ftp.storeFile(file.getName(), input);
                //Thread.sleep(5000);
                input.close();
            } catch (FileNotFoundException e) {
                Log.w("FTP", "File not found: " + files[0].getName());
                return false;
            } catch (IOException e) {
                Log.e("FTP", "Error on upload file: " + file.getName());
                return false;
/*            } catch (InterruptedException e) {
                e.printStackTrace();*/
            }
            return true;
        }

        protected void onPostExecute(Boolean uploaded) {
            if (uploaded) {
                //Toast.makeText(mc, file.getName() + " Uploaded.", Toast.LENGTH_SHORT).show();
                Log.d("FTP", file.getName() + " uploaded.");
                LinearLayout vl = (LinearLayout) findViewById(R.id.vLayout);
                LinearLayout hl = (LinearLayout) vl.getChildAt(p);
                RelativeLayout rl = (RelativeLayout) hl.getChildAt(0);
                ProgressBar pb = (ProgressBar) rl.getChildAt(1);
                pb.setVisibility(View.INVISIBLE);
                ImageView imgok = (ImageView) rl.getChildAt(2);
                imgok.setVisibility(View.VISIBLE);
            } else {
                //Toast.makeText(mc, file.getName() + " cant upload.", Toast.LENGTH_SHORT).show();
                Log.d("FTP", file.getName() + " cant upload.");
                LinearLayout vl = (LinearLayout) findViewById(R.id.vLayout);
                LinearLayout hl = (LinearLayout) vl.getChildAt(p);
                RelativeLayout rl = (RelativeLayout) hl.getChildAt(0);
                ProgressBar pb = (ProgressBar) rl.getChildAt(1);
                pb.setVisibility(View.INVISIBLE);
                ImageView imgok = (ImageView) rl.getChildAt(3);
                imgok.setVisibility(View.VISIBLE);
            }

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
