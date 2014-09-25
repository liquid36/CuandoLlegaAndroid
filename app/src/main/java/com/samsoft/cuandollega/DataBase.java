package com.samsoft.cuandollega;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class DataBase  {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String TAG = "SQLiteLocationDAO";
    public static final String NAME = "CuandoLLega";
    public static final String TCOLECTIVOS = "colectivos";
    public static final String TCALLES = "calles";
    public static final String TPARADAS = "paradas";
    private Context context;
    private SQLiteDatabase db;
    public DataBase(Context context) {
        this.context = context;
        db = openDatabase(NAME);
    }

    public void Close()
    {
        db.close();
    }

    private SQLiteDatabase openDatabase(String dbname)
    {
        File dbfile = this.context.getDatabasePath(dbname + ".db");
        //Log.i("DATABASE", "SD: " + Environment.getExternalStorageDirectory().getAbsolutePath());
        //dbfile.delete();
        if (!dbfile.exists()) {
            boolean b = dbfile.getParentFile().mkdirs();
        }
        SQLiteDatabase mydb = SQLiteDatabase.openOrCreateDatabase(dbfile.getAbsolutePath(), null);
        mydb.execSQL("CREATE TABLE IF NOT EXISTS colectivos (id INTEGER, name TEXT, bandera TEXT , linea TEXT)");
        mydb.execSQL("CREATE TABLE IF NOT EXISTS calles (id INTEGER, desc TEXT)");
        mydb.execSQL("CREATE TABLE IF NOT EXISTS paradas (idColectivo INTEGER, idCalle INTEGER,idInter INTEGER, parada INTEGER , desc TEXT)");
        return mydb;
    }


    public void insertLinea(JSONObject o)
    {
        //SQLiteDatabase db = this.openDatabase(NAME);
        db.beginTransaction();
        ContentValues values = getContentValuesLineas(o);
        if (values != null) db.insert(TCOLECTIVOS, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }

    public JSONArray getLineas() {
       //SQLiteDatabase db = null;
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            //db = this.openDatabase(NAME);
            c = db.rawQuery("SELECT * FROM " + TCOLECTIVOS  , new String[] {});
            while (c.moveToNext()) {
                JSONObject o = hydrateLinea(c);
                if (o != null) arr.put(o);
            }
        } finally {
            if (c != null) {
                c.close();
            }
            /*if (db != null) {
                db.close();
            }*/
        }
        return arr;
    }


    public void insertCalle(JSONObject o)
    {
        //SQLiteDatabase db = this.openDatabase(NAME);
        db.beginTransaction();
        ContentValues values = getContentValuesCalle(o);
        if (values != null) db.insert(TCALLES, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }

    public JSONArray getCalles(String Colectivo,String name) {
        //SQLiteDatabase db = null;
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            //db = this.openDatabase(NAME);
            if (Colectivo.trim().isEmpty())
                //c = db.rawQuery("SELECT * FROM " + TCALLES + " WHERE desc LIKE '%" + name + "%'  ORDER BY desc"  , new String[] {});
                c = db.rawQuery("SELECT calles.id AS id, calles.desc AS desc FROM paradas " +
                    "INNER JOIN calles  ON calles.id = idCalle " +
                    "WHERE calles.desc LIKE '%" + name + "%'" +
                    " GROUP BY calles.id  ORDER BY calles.desc" , new String[] {});
            else
                c = db.rawQuery("SELECT calles.id AS id, calles.desc AS desc FROM paradas " +
                        "INNER JOIN calles     ON calles.id = idCalle " +
                        "INNER JOIN colectivos ON colectivos.id = idColectivo " +
                        "WHERE colectivos.name = ? AND calles.desc LIKE '%" + name + "%'" +
                        " GROUP BY calles.id  ORDER BY calles.desc" , new String[] {Colectivo});


            while (c.moveToNext()) {
                JSONObject o = hydrateCalle(c);
                if (o != null) arr.put(o);
            }
        } finally { if (c != null) c.close(); }
        return arr;
    }


    public void insertParada(JSONObject o)
    {
        db.beginTransaction();
        ContentValues values = getContentValuesParada(o);
        if (values != null) db.insert(TPARADAS, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public JSONArray getParadas() {
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            c = db.rawQuery("SELECT * FROM " + TPARADAS  , new String[] {});
            while (c.moveToNext()) {
                JSONObject o = hydrateParada(c);
                if (o != null) arr.put(o);
            }
        } finally {if (c != null) c.close();}
        return arr;
    }

    public JSONArray Intersecciones(int idCalle,String Colectivo,String name) {
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            if (Colectivo.trim().isEmpty())
                c = db.rawQuery("SELECT calles.id, calles.desc AS desc FROM paradas " +
                        "INNER JOIN calles ON calles.id = idInter " +
                        "WHERE idCalle = ? AND calles.desc LIKE '%" + name + "%'" +
                        "GROUP BY calles.id ORDER BY calles. desc" , new String[] {Integer.toString(idCalle)});
            else
                c = db.rawQuery("SELECT calles.id, calles.desc AS desc FROM paradas " +
                            "INNER JOIN calles ON calles.id = idInter " +
                            "INNER JOIN colectivos ON colectivos.id = idColectivo " +
                            "WHERE colectivos.name = ? AND idCalle = ? AND calles.desc LIKE '%" + name + "%'" +
                            "GROUP BY calles.id ORDER BY calles.desc" , new String[] {Colectivo, Integer.toString(idCalle)});
            while (c.moveToNext()) {
                JSONObject o = hydrateCalle(c);
                if (o != null) arr.put(o);
            }
        } finally {
            if (c != null) c.close();
        }
        return arr;
    }

    public JSONArray getAllBuses() {
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            c = db.rawQuery("SELECT * FROM colectivos GROUP BY name ORDER BY name" , new String[]{});
            while (c.moveToNext()) {
                JSONObject o = hydrateLinea(c);
                if (o != null) arr.put(o);
            }
        }catch (Exception e) {
        } finally {
            if (c != null) c.close();
        }
        return arr;
    }

    public JSONArray busInStop(int idCalle,int idInter) {
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            c = db.rawQuery("SELECT colectivos.id AS id, colectivos.name AS name,colectivos.bandera AS bandera, colectivos.linea AS linea FROM paradas " +
                    "INNER JOIN colectivos ON paradas.idColectivo = colectivos.id " +
                    "WHERE paradas.idCalle = ? AND paradas.idInter = ? GROUP BY colectivos.name ORDER BY colectivos.name", new String[]{Integer.toString(idCalle), Integer.toString(idInter)});
            while (c.moveToNext()) {
                JSONObject o = hydrateLinea(c);
                if (o != null) arr.put(o);
            }
        }catch (Exception e) {
        } finally {
            if (c != null) c.close();
        }
        return arr;
    }

    public JSONArray getStops(String bus,int idCalle,int idInter) {
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            String [] variables = {Integer.toString(idCalle), Integer.toString(idInter)};
            String query = "";
            if (!bus.isEmpty()) {
                variables = new String[]{bus,Integer.toString(idCalle), Integer.toString(idInter)};
                query = " colectivos.name = ? AND ";
            }
            c = db.rawQuery("SELECT idColectivo, desc,parada,name,bandera FROM paradas " +
                    "INNER JOIN colectivos ON idColectivo = colectivos.id WHERE " +
                    query + " idCalle = ? AND idInter = ?  " +
                    "GROUP BY colectivos.name, paradas.parada ORDER BY colectivos.name " , variables);
            while (c.moveToNext()) {
                JSONObject o = new JSONObject();
                o.put("idColectivo", c.getInt(c.getColumnIndex("idColectivo")));
                o.put("parada", c.getInt(c.getColumnIndex("parada")));
                o.put("desc", c.getString(c.getColumnIndex("desc")));
                o.put("name", c.getString(c.getColumnIndex("name")));
                o.put("bandera", c.getString(c.getColumnIndex("bandera")));
                if (o != null) arr.put(o);
            }
        }catch (Exception e) {
        } finally {
            if (c != null) c.close();
        }
        return arr;
    }


    private JSONObject hydrateLinea(Cursor c) {
        try {
            JSONObject l = new JSONObject();
            l.put("id", c.getInt(c.getColumnIndex("id")));
            l.put("name", c.getString(c.getColumnIndex("name")));
            l.put("bandera", c.getString(c.getColumnIndex("bandera")));
            l.put("linea", c.getString(c.getColumnIndex("linea")));
            return l;
        } catch (Exception e) { e.printStackTrace(); return null;}
    }

    private ContentValues getContentValuesLineas(JSONObject o) {
        try {
            ContentValues values = new ContentValues();
            values.put("id", o.getInt("id"));
            values.put("name", o.getString("name"));
            values.put("bandera", o.getString("bandera"));
            values.put("linea", o.getString("linea"));
            return values;
        } catch (Exception e) { e.printStackTrace(); return null;}
    }

    private JSONObject hydrateCalle(Cursor c) {
        try {
            JSONObject l = new JSONObject();
            l.put("id", c.getInt(c.getColumnIndex("id")));
            l.put("desc", c.getString(c.getColumnIndex("desc")));
            return l;
        } catch (Exception e) { e.printStackTrace(); return null;}
    }

    private ContentValues getContentValuesCalle(JSONObject o) {
        try {
            ContentValues values = new ContentValues();
            values.put("id", o.getInt("id"));
            values.put("desc", o.getString("desc"));
            return values;
        } catch (Exception e) { e.printStackTrace(); return null;}
    }

    private JSONObject hydrateParada(Cursor c) {
        try {
            JSONObject l = new JSONObject();
            l.put("idColectivo", c.getInt(c.getColumnIndex("idColectivo")));
            l.put("idCalle", c.getInt(c.getColumnIndex("idCalle")));
            l.put("idInter", c.getInt(c.getColumnIndex("idInter")));
            l.put("parada", c.getInt(c.getColumnIndex("parada")));
            l.put("desc", c.getString(c.getColumnIndex("desc")));
            return l;
        } catch (Exception e) { e.printStackTrace(); return null;}
    }

    private ContentValues getContentValuesParada(JSONObject o) {
        try {
            ContentValues values = new ContentValues();
            values.put("idColectivo", o.getInt("idColectivo"));
            values.put("idCalle", o.getInt("idCalle"));
            values.put("idInter", o.getInt("idInter"));
            values.put("parada", o.getInt("parada"));
            values.put("desc", o.getString("desc"));
            return values;
        } catch (Exception e) { Log.i("JSONERROR" , o.toString()); e.printStackTrace(); return null;}
    }



    public Date stringToDate(String dateTime) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat(DATE_FORMAT);
        Date date = null;
        try {
            date = iso8601Format.parse(dateTime);

        } catch (ParseException e) {
            Log.e("DBUtil", "Parsing ISO8601 datetime ("+ dateTime +") failed", e);
        }

        return date;
    }

    public String dateToString(Date date) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat(DATE_FORMAT);
        return iso8601Format.format(date);
    }


    public void AttachDB(String name)
    {
        //db.setTransactionSuccessful();
        //db.endTransaction();
        db.execSQL("ATTACH DATABASE ? AS DB1",new String[]{name});
        db.beginTransaction();
        db.execSQL("DELETE FROM colectivos");
        db.execSQL("DELETE FROM paradas");
        db.execSQL("DELETE FROM calles");
        db.execSQL("INSERT INTO Colectivos SELECT * FROM DB1.Colectivos");
        db.execSQL("INSERT INTO paradas SELECT * FROM DB1.paradas");
        db.execSQL("INSERT INTO calles SELECT * FROM DB1.calles");
        db.setTransactionSuccessful();
        db.endTransaction();
    }

}

/*
db.setTransactionSuccessful();
        db.endTransaction();
        db.execSQL("ATTACH DATABASE ? AS names",new String[]{namesDb});
        db.beginTransaction();
  */