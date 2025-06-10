package com.example.museline.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.museline.data.model.Track;
import com.example.museline.data.model.FavoriteTrack;
import com.example.museline.data.model.TrackHistory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "museline.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRACKS = "tracks";
    private static final String TABLE_FAVORITES = "favorites";
    private static final String TABLE_HISTORY = "track_history";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ARTIST = "artist";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String COLUMN_URL = "url";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_LISTENERS = "listeners";
    private static final String COLUMN_PLAYCOUNT = "playcount";

    private static final String CREATE_TABLE_TRACKS = "CREATE TABLE " + TABLE_TRACKS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_ARTIST + " TEXT,"
            + COLUMN_URL + " TEXT,"
            + COLUMN_IMAGE_URL + " TEXT,"
            + COLUMN_LISTENERS + " INTEGER,"
            + COLUMN_PLAYCOUNT + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE " + TABLE_FAVORITES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_ARTIST + " TEXT,"
            + "UNIQUE(" + COLUMN_NAME + ", " + COLUMN_ARTIST + ")"
            + ")";

    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE " + TABLE_HISTORY + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_ARTIST + " TEXT,"
            + COLUMN_TIMESTAMP + " INTEGER,"
            + "UNIQUE(" + COLUMN_NAME + ", " + COLUMN_ARTIST + ")"
            + ")";

    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRACKS);
        db.execSQL(CREATE_TABLE_FAVORITES);
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public void insertTracks(List<Track> tracks) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TRACKS, null, null);
            
            for (Track track : tracks) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME, track.getName());
                values.put(COLUMN_ARTIST, track.getArtist());
                values.put(COLUMN_URL, track.getUrl());
                values.put(COLUMN_IMAGE_URL, track.getImageUrl());
                values.put(COLUMN_LISTENERS, track.getListeners());
                values.put(COLUMN_PLAYCOUNT, track.getPlaycount());
                db.insert(TABLE_TRACKS, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Track> getAllTracks() {
        List<Track> tracks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_TRACKS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Track track = new Track(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LISTENERS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAYCOUNT))
                );
                track.setFavorite(isFavorite(track.getName(), track.getArtist()));
                tracks.add(track);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tracks;
    }

    public void insertFavorite(FavoriteTrack favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, favorite.getName());
        values.put(COLUMN_ARTIST, favorite.getArtist());
        db.insertWithOnConflict(TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteFavorite(String name, String artist) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, 
                 COLUMN_NAME + "=? AND " + COLUMN_ARTIST + "=?",
                 new String[]{name, artist});
    }

    public boolean isFavorite(String name, String artist) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, new String[]{COLUMN_ID},
                COLUMN_NAME + "=? AND " + COLUMN_ARTIST + "=?",
                new String[]{name, artist}, null, null, null);
        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        return isFavorite;
    }

    public List<FavoriteTrack> getAllFavorites() {
        List<FavoriteTrack> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_FAVORITES, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                favorites.add(new FavoriteTrack(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favorites;
    }

    public void insertHistory(TrackHistory history) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, history.getTrackName());
        values.put(COLUMN_ARTIST, history.getArtistName());
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        db.insertWithOnConflict(TABLE_HISTORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void updateHistoryTimestamp(String name, String artist, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, timestamp);
        db.update(TABLE_HISTORY,
                values,
                COLUMN_NAME + "=? AND " + COLUMN_ARTIST + "=?",
                new String[]{name, artist});
    }

    public boolean historyExists(String name, String artist) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, new String[]{COLUMN_ID},
                COLUMN_NAME + "=? AND " + COLUMN_ARTIST + "=?",
                new String[]{name, artist}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<TrackHistory> getAllHistory() {
        List<TrackHistory> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_HISTORY, null, null, null, null, null, 
                COLUMN_TIMESTAMP + " DESC");
        if (cursor.moveToFirst()) {
            do {
                TrackHistory item = new TrackHistory(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST))
                );
                history.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return history;
    }
} 