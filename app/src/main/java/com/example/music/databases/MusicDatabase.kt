package com.example.music.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.music.Songs

class MusicDatabase: SQLiteOpenHelper{

    var _songList = ArrayList<Songs>()



    object Staticated{
        var DB_VERSION = 1
        val DB_NAME = "FavouriteDatabase"
        val TABLE_NAME = "FavouriteTable"
        val COLUMN_ID = "SongID"
        val COLUMN_TITLE = "SongTitle"
        val COLUMN_SONG_ARTIST = "SongArtist"
        val COLUMN_SONG_PATH = "SongPath"
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        sqLiteDatabase?.execSQL("CREATE TABLE " + Staticated.TABLE_NAME + "( " +Staticated.COLUMN_ID + " INTEGER,"
                + Staticated.COLUMN_SONG_ARTIST + " STRING," + Staticated.COLUMN_TITLE +" STRING,"+ Staticated.COLUMN_SONG_PATH + " STRING);")

            }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
    constructor(context: Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)

    fun storesAsFavourite(id: Int?,artist: String?, songTitle: String?,path: String?){
        val db = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put( Staticated.COLUMN_ID, id)
        contentValues.put( Staticated.COLUMN_SONG_ARTIST,artist)
        contentValues.put( Staticated.COLUMN_TITLE,songTitle)
        contentValues.put( Staticated.COLUMN_SONG_PATH,path)
        db.insert( Staticated.TABLE_NAME,null,contentValues)
        db.close()
    }

    fun queryDBList(): ArrayList<Songs>?{
        try {

            val db = this.readableDatabase
            val query_params = "SELECT * FROM "+ Staticated.TABLE_NAME
            var csor = db.rawQuery(query_params,null)
            if(csor.moveToFirst()){
                do {
                    var _id = csor.getInt(csor.getColumnIndexOrThrow( Staticated.COLUMN_ID))
                    var _artist=csor.getString(csor.getColumnIndexOrThrow( Staticated.COLUMN_SONG_ARTIST))
                    var _title = csor.getString(csor.getColumnIndexOrThrow( Staticated.COLUMN_TITLE))
                    var _path = csor.getString(csor.getColumnIndexOrThrow( Staticated.COLUMN_SONG_PATH))
                    _songList.add(Songs(_id.toLong(),_title,_artist,_path,0))

                }while (csor.moveToNext())
            }else{
                return null
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        return  _songList
    }

    fun checkifIDExists(_id: Int): Boolean{
        var storeId = -1090
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " +  Staticated.TABLE_NAME + " WHERE SongID = '$_id'"
        val csor = db.rawQuery(query_params,null)
        if(csor.moveToFirst()){
            do {
                storeId = csor.getInt(csor.getColumnIndexOrThrow( Staticated.COLUMN_ID))
            }while (csor.moveToNext())
        }else{
            return false
        }
        return storeId != -1090
    }

    fun deleteFavourite(_id: Int){
        val db = this.writableDatabase
        db.delete( Staticated.TABLE_NAME, Staticated.COLUMN_ID + "=" + _id, null)
        db.close()
    }

    fun checkSize(): Int{
        var counter = 0
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " +  Staticated.TABLE_NAME
        val csor = db.rawQuery(query_params,null)
        if(csor.moveToFirst()){
            do {
                counter = counter + 1
            }while (csor.moveToNext())
        }else{
            return 0
        }
        return counter
    }
}