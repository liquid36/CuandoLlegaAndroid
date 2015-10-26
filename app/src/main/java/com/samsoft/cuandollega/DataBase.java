package com.samsoft.cuandollega;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
        ArmarBaseDeDatos();
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
        return mydb;
    }

    private void ArmarBaseDeDatos() {
        db.execSQL("CREATE TABLE IF NOT EXISTS colectivos (id INTEGER, name TEXT, bandera TEXT , linea TEXT, cl Boolean)");
        db.execSQL("CREATE TABLE IF NOT EXISTS calles (id INTEGER, desc TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS paradas (idColectivo INTEGER, idCalle INTEGER,idInter INTEGER, parada INTEGER , desc TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS favlist (idFav INTEGER, linea TEXT, parada INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS favoritos (fav INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS geostreetD (idCalle INTEGER, idInter INTEGER,lat DOUBLE,lng DOUBLE, sin_lat DOUBLE , cos_lat DOUBLE , sin_lng DOUBLE, cos_lng DOUBLE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS calleFreq (id INTEGER PRIMARY KEY, frecuencia INTEGER)");
        db.execSQL("CREATE VIEW  IF NOT EXISTS callesF AS " +
                "SELECT calles.id AS id, desc, ifnull(frecuencia,0) AS frecuencia FROM calles LEFT OUTER JOIN calleFreq ON calles.id = calleFreq.id");

    }
    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************

    public Integer addFavorito(String name)
    {
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("name",name);
        long id = db.insert("favoritos",null,values);
        //db.execSQL("INSERT INTO favoritos (name) VALUES (?)", new Object[]{name});
        db.setTransactionSuccessful();
        db.endTransaction();
        return (int) id;
    }

    public void removeFavorito(Integer fav)
    {

        db.beginTransaction();
        db.execSQL("DELETE FROM favList   WHERE idfav = ? ", new String[]{fav.toString()});
        db.execSQL("DELETE FROM favoritos WHERE fav = ? ", new String[]{fav.toString()});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public JSONArray getFavoritos()
    {
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            c = db.rawQuery("SELECT * FROM FAVORITOS", new String[]{});
            while (c.moveToNext()) {
                JSONObject o = new JSONObject();
                o.put("id", c.getInt(0));
                o.put("name", c.getString(1));
                arr.put(o);
            }
        } catch (Exception ee) {ee.printStackTrace();
        } finally {
            if (c != null) c.close();
        }
        return arr;
    }

    public void insertFavList(Integer fav,String linea, Integer parada)
    {
        db.beginTransaction();
        db.execSQL("INSERT INTO favlist VALUES (?,?,?)", new Object[]{fav,linea, parada});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteFavorito(String linea, Integer parada)
    {

        db.beginTransaction();
        db.execSQL("DELETE FROM favList WHERE linea ='" + linea + "' AND parada = " + parada , new String[]{});
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    public boolean chekcFavorito(String linea, Integer parada)
    {
        boolean res = false;
        Cursor c = db.rawQuery("SELECT * FROM favList WHERE linea ='" + linea + "' AND parada = " + parada  , new String[]{});
        if (c.moveToNext()) res = true;
        c.close();
        return res;
    }

    public boolean isFavCheck(Integer id,String linea, Integer parada)
    {
        boolean res = false;
        Cursor c = db.rawQuery("SELECT * FROM favList WHERE linea ='" + linea + "' AND parada = " + parada + " AND idFav = " + id , new String[]{});
        if (c.moveToNext()) res = true;
        c.close();
        return res;
    }

    public JSONArray getStopsFromFavorite(Integer fav) {
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {

            c = db.rawQuery("SELECT idColectivo, desc,paradas.parada AS parada ,name,colectivos.linea,bandera, idCalle, idInter FROM paradas " +
                    "INNER JOIN colectivos ON idColectivo = colectivos.id " +
                    "INNER JOIN favList ON favList.linea = colectivos.linea AND favList.parada = paradas.parada AND favList.idFav = " + fav +
                    " WHERE colectivos.cl = 1 " +
                    " GROUP BY colectivos.name, paradas.parada ORDER BY colectivos.name " , new String[]{});
            while (c.moveToNext()) {
                JSONObject o = new JSONObject();
                o.put("idColectivo", c.getInt(c.getColumnIndex("idColectivo")));
                o.put("parada", c.getInt(c.getColumnIndex("parada")));
                o.put("desc", c.getString(c.getColumnIndex("desc")));
                o.put("name", c.getString(c.getColumnIndex("name")));
                o.put("linea", c.getString(c.getColumnIndex("linea")));
                o.put("bandera", c.getString(c.getColumnIndex("bandera")));
                o.put("idCalle", c.getInt(c.getColumnIndex("idCalle")));
                o.put("idInter", c.getInt(c.getColumnIndex("idInter")));
                if (o != null) arr.put(o);
            }
        }catch (Exception e) {
        } finally {
            if (c != null) c.close();
        }
        return arr;
    }

    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************

    public void addFrequencia(Integer idCalle)
    {
        db.beginTransaction();
        db.execSQL("INSERT OR IGNORE INTO calleFreq VALUES (?,?)", new Object[]{idCalle,0});
        db.execSQL("UPDATE calleFreq SET frecuencia = frecuencia + 1 WHERE id = ? ", new Object[]{idCalle});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************
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

    public String getCalleName(Integer idCalle) {
        Cursor c = null;
        String res = "";
        try {
            c = db.rawQuery("SELECT desc FROM calles where id = ?"  , new String[] { idCalle.toString() });
            if (c.moveToNext()) res = c.getString(0);
        } finally {if (c != null) c.close();}
        return res;
    }

    public JSONArray getCalles(String Colectivo,String name) {
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            //db = this.openDatabase(NAME);
            if (Colectivo.trim().isEmpty())
                //c = db.rawQuery("SELECT * FROM " + TCALLES + " WHERE desc LIKE '%" + name + "%'  ORDER BY desc"  , new String[] {});
                c = db.rawQuery("SELECT callesF.id AS id, callesF.desc AS desc, frecuencia != 0 as frecuencia FROM paradas " +
                    "INNER JOIN callesF  ON callesF.id = idCalle " +
                    "WHERE callesF.desc LIKE '%" + name + "%'" +
                    " GROUP BY callesF.id  ORDER BY (callesF.frecuencia != 0) DESC,callesF.desc" , new String[] {});
            else
                c = db.rawQuery("SELECT callesF.id AS id, callesF.desc AS desc, frecuencia != 0 as frecuencia FROM paradas " +
                        "INNER JOIN callesF     ON callesF.id = idCalle " +
                        "INNER JOIN colectivos ON colectivos.id = idColectivo " +
                        "WHERE colectivos.linea = ? AND colectivos.cl = 1 AND callesF.desc LIKE '%" + name + "%'" +
                        " GROUP BY callesF.id  ORDER BY (callesF.frecuencia != 0) DESC,callesF.desc" , new String[] {Colectivo});


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
                c = db.rawQuery("SELECT callesF.id AS id, callesF.desc AS desc, frecuencia != 0 as frecuencia FROM paradas " +
                        "INNER JOIN callesF ON callesF.id = idInter " +
                        "WHERE idCalle = ? AND callesF.desc LIKE '%" + name + "%'" +
                        "GROUP BY callesF.id ORDER BY (callesF.frecuencia != 0) DESC,callesF. desc" , new String[] {Integer.toString(idCalle)});
            else
                c = db.rawQuery("SELECT callesF.id AS id, callesF.desc AS desc, frecuencia != 0 as frecuencia FROM paradas " +
                            "INNER JOIN callesF ON callesF.id = idInter " +
                            "INNER JOIN colectivos ON colectivos.id = idColectivo " +
                            "WHERE colectivos.linea = ? AND idCalle = ? AND colectivos.cl = 1 AND callesF.desc LIKE '%" + name + "%'" +
                            "GROUP BY callesF.id ORDER BY (callesF.frecuencia != 0) DESC,callesF.desc" , new String[] {Colectivo, Integer.toString(idCalle)});
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
        return getAllBuses(true);
    }

    public JSONArray getAllBuses(Boolean cl) {
        Cursor c = null;
        JSONArray arr = new JSONArray();
        String where_clasue = "";
        if (cl) where_clasue = " WHERE cl = 1 GROUP BY name";
        try {
            c = db.rawQuery("SELECT * FROM colectivos " + where_clasue + " ORDER BY name" , new String[]{});
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
                    "WHERE paradas.idCalle = ? AND paradas.idInter = ? AND colectivos.cl = 1  GROUP BY colectivos.name ORDER BY colectivos.name", new String[]{Integer.toString(idCalle), Integer.toString(idInter)});
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
                query = " colectivos.linea = ? AND ";
            }
            c = db.rawQuery("SELECT idColectivo, desc,parada,name,bandera,linea FROM paradas " +
                    "INNER JOIN colectivos ON idColectivo = colectivos.id WHERE colectivos.cl = 1 AND " +
                    query + " idCalle = ? AND idInter = ?  " +
                    "GROUP BY colectivos.name, paradas.parada ORDER BY colectivos.name " , variables);
            while (c.moveToNext()) {
                JSONObject o = new JSONObject();
                o.put("idColectivo", c.getInt(c.getColumnIndex("idColectivo")));
                o.put("parada", c.getInt(c.getColumnIndex("parada")));
                o.put("desc", c.getString(c.getColumnIndex("desc")));
                o.put("name", c.getString(c.getColumnIndex("name")));
                o.put("bandera", c.getString(c.getColumnIndex("bandera")));
                o.put("linea", c.getString(c.getColumnIndex("linea")));
                if (o != null) arr.put(o);
            }
        }catch (Exception e) {
        } finally {
            if (c != null) c.close();
        }
        return arr;
    }

    //**********************************************************************************************
    //**********      GEOLOCALIZACION                                                    ***********
    //**********************************************************************************************

    public JSONArray getClosePoint(String lat,String lng,Integer distance)
    {
        double deg2radMultiplier = Math.PI / 180;
        double latd = Double.parseDouble(lat) * deg2radMultiplier;
        double lngd = Double.parseDouble(lng) * deg2radMultiplier;

        double sin_lat = Math.sin(latd);
        double cos_lat = Math.cos(latd);
        double sin_lng = Math.sin(lngd);
        double cos_lng = Math.cos(lngd);
        double dist = Math.cos( ( ((double)distance) /1000.0) / 6371.0);

        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            String query =  "SELECT geostreetD.idCalle,geostreetD.IdInter,lat,lng, sin_lat * " + sin_lat + " + cos_lat * " + cos_lat +
                    " *  (cos_lng * " + cos_lng + " + sin_lng * "  + sin_lng
                    + ") AS distance, c1.desc , c2.desc FROM geostreetD "
                    + " INNER JOIN paradas      ON geostreetD.idCalle = paradas.idCalle AND geostreetD.idInter = paradas.idInter "
                    + " INNER JOIN calles AS c1 ON  geostreetD.idCalle = c1.id"
                    + " INNER JOIN calles AS c2 ON  geostreetD.idInter = c2.id"
                    + " GROUP BY geostreetD.idCalle, geostreetD.idInter HAVING distance > " + dist + " ORDER BY DISTANCE DESC";
            c = db.rawQuery(query, new String[]{});

            while (c.moveToNext()) {
                JSONObject o = new JSONObject();
                o.put("idCalle", c.getInt(0));
                o.put("idInter", c.getInt(1));
                o.put("lat", c.getDouble(2));
                o.put("lng", c.getDouble(3));
                o.put("distancia", Math.acos(c.getDouble(4)) *  6371  * 1000);
                o.put("name1",c.getString(5));
                o.put("name2",c.getString(6));
                arr.put(o);
            }
        }catch (Exception e) {
        } finally {
            if (c != null) c.close();
        }
        return arr;
    }


    public String colectivosEnEsquina(Integer idCalle, Integer idInter)
    {
        Cursor c = null;
        JSONArray arr = new JSONArray();
        try {
            //String query =  "SELECT idColectivo, name FROM paradas INNER JOIN colectivos ON id = idColectivo "
            //               + " WHERE idCalle = ? AND idInter= ? GROUP BY name";
            String query =  "select group_concat(linea,' ') FROM " +
                    "(SELECT DISTINCT linea FROM paradas INNER JOIN  colectivos ON id = idColectivo " +
                    " WHERE idCalle = ? AND idInter = ? GROUP BY linea);";
            c = db.rawQuery(query, new String[]{idCalle.toString(),idInter.toString()});

            int i = 0;

            String res;
            if (c.moveToNext()) {
                res = c.getString(0);
            } else res =  "";

            /*String [] res = new String[c.getCount()];
            while (c.moveToNext()) {
                res[i++] = c.getString(1);
            }*/
            if (c != null) c.close();
            return res;
        }catch (Exception e) {
            e.printStackTrace();
            if (c != null) c.close();
            return "";   //new String[0];
        }
    }


    //**********************************************************************************************
    //********               HYDRATE CURSOR Y JSONS                                        *********
    //**********************************************************************************************

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
            l.put("frecuencia", c.getInt(c.getColumnIndex("frecuencia")));
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

        try {
            db.execSQL("ALTER TABLE colectivos ADD COLUMN cl Boolean;");
        }catch (Exception e) {Log.d("DATABASE","CAMBIOS YA APLICADOS");}


        db.execSQL("DELETE FROM paradas");
        db.execSQL("DELETE FROM calles");
        db.execSQL("DELETE FROM geostreetD");

        db.execSQL("DROP TABLE colectivos");
        db.execSQL("DROP TABLE paradas");
        db.execSQL("DROP TABLE calles");
        db.execSQL("DROP TABLE geostreetD");

        ArmarBaseDeDatos();

        db.execSQL("INSERT INTO Colectivos SELECT * FROM DB1.Colectivos");
        db.execSQL("INSERT INTO paradas SELECT * FROM DB1.paradas");
        db.execSQL("INSERT INTO calles SELECT * FROM DB1.calles");
        db.execSQL("INSERT INTO geostreetD SELECT * FROM DB1.geostreetD");

        db.setTransactionSuccessful();
        db.endTransaction();

        Log.d("DATABASE","CAMBIOS APLICADOS");
    }

}

/*
db.setTransactionSuccessful();
        db.endTransaction();
        db.execSQL("ATTACH DATABASE ? AS names",new String[]{namesDb});
        db.beginTransaction();
  */