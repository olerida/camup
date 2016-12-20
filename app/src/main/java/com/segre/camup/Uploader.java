package com.segre.camup;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by olerida on 20/12/16.
 */

public class Uploader extends Thread {
    private FTPClient ftpClient;
    private File file;

    Uploader(FTPClient ftpCli, File f) {
        this.ftpClient = ftpCli;
        this.file = f;
    }

    public void run() {
        try {
            InputStream is = new FileInputStream(file);
            ftpClient.storeFile(file.getName(), is);
            is.close();
        } catch (FileNotFoundException e) {
            Log.d("FILE", "File not found " + file.getName());
        } catch (IOException e) {
            Log.d("FILE", "Cant upload file " + file.getName());
        }
    }
}
