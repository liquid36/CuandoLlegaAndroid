package com.samsoft.cuandollega.extra;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by sam on 3/09/15.
 */
public class updateDB
{
    private Context contex;
    private DataBase db;
    public updateDB(Context _c,DataBase _db)
    {
        contex = _c;
        db = _db;
    }

    public boolean update(boolean fromRaw) {
        try {
            File dbfile = contex.getDatabasePath("test.db");
            if (fromRaw) {
                InputStream in = contex.getResources().openRawResource(R.raw.test);
                OutputStream out = new FileOutputStream(dbfile.getAbsolutePath(), false);
                updateDB.CopyFile(in, out);
            }
            db.AttachDB(dbfile.getAbsolutePath());
            db.Close();
            dbfile.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void firstLoad(ProgressDialog progress)
    {
        progresDialog = progress;
        initTask.execute();
    }

    public String getOldMD5()
    {
        return readFileAsString(contex.getDatabasePath("db.md5").getAbsolutePath());
    }

    public String getNewMD5()
    {
        return readFileAsString(contex.getDatabasePath("temp.md5").getAbsolutePath());
    }

    private ProgressDialog progresDialog;
    private AsyncTask<String, Integer, Boolean> initTask = new AsyncTask<String, Integer, Boolean>() {
        protected Boolean doInBackground(String... urls)
        {
            try {
                File md5 = contex.getDatabasePath("db.md5");
                InputStream in = contex.getResources().openRawResource(R.raw.db);
                OutputStream out = new FileOutputStream(md5.getAbsoluteFile(), false);
                updateDB.CopyFile(in, out);
                update(true);
                return true;
            } catch (Exception e) {e.printStackTrace(); return false;}
        }

        protected void onProgressUpdate(Integer... progress) {
            return;
        }

        protected void onPostExecute(Boolean result) {
            if (progresDialog != null) progresDialog.dismiss();
            return;
        }
    };

    public static boolean downloadFile(String url,String path) {
        try {
            String name = url.substring(url.lastIndexOf("/") + 1);
            URL _url = new URL(url);
            URLConnection conexion = _url.openConnection();
            if (!name.equals("test.db")) conexion.setDoOutput(true);
            conexion.connect();

            int lenghtOfFile = conexion.getContentLength();

            InputStream input = new BufferedInputStream(_url.openStream());
            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[1024];
            long total = 0;
            int count = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            return true;
        } catch (Exception e) {e.printStackTrace();return false;}
    }

    public static boolean CopyFile(InputStream in,OutputStream out)
    {
        try {
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = in.read(buff)) > 0)
                out.write(buff, 0, read);
            in.close();
            out.close();
            return true;
        } catch (Exception e) {e.printStackTrace(); return  false;}
    }

    public static String readFileAsString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(fileName)));
            while ((line = in.readLine()) != null) stringBuilder.append(line);
        } catch (Exception e) {
            e.printStackTrace(); return "";
        }
        return stringBuilder.toString();
    }

}
