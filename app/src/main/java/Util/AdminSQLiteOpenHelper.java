package Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CorruptusinExtremis on 08/03/15.
 */
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE SEQUENCE client_id START WITH 1 INCREMENT BY 1" );
        db.execSQL( "INSERT INTO client ( name, latitude, longitude ) VALUES ( 'Jose Díaz Perez', 20.659264, -103.356113 )" );
        db.execSQL( "INSERT INTO client ( name, latitude, longitude ) VALUES ( 'Alma Lopez Anduisa',20.672103 , -103.368859 )" );
        db.execSQL( "INSERT INTO client ( name, latitude, longitude ) VALUES ( 'Carlos Linares Martinez',20.636864 , -103.396625 )" );
        db.execSQL( "INSERT INTO client ( name, latitude, longitude ) VALUES ( 'Andres Lopez Juarez',20.679372 , -103.380135 )" );

        db.execSQL( "CREATE TABLE client " +
                "( id integer DEFAULT nextval('client_id'::regclass) NOT NULL," +
                "name text NOT NULL, latitude double precision," +
                "longitude double precision )" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
        db.execSQL("drop table if exists client");
        db.execSQL( "CREATE TABLE client " +
                "( id integer DEFAULT nextval('client_id'::regclass) NOT NULL," +
                "name text NOT NULL, latitude double precision," +
                "longitude double precision )" );
    }
}
