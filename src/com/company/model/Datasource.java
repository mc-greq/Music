package com.company.model;

import javax.sound.midi.Soundbank;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {

    public static final String DB_NAME = "music.db";

    public static final String CONNECTION_STRING = "jdbc:sqlite:G:\\java\\intellij\\Music\\" + DB_NAME;

    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;

    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;

    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    public static final String QUERY_ARTISTS_SORT =
            " ORDER BY " + COLUMN_ARTIST_NAME + " COLLATE NOCASE ";

    public static final String QUERY_ALBUMS_BY_ARTISTS_START =
            "SELECT " + TABLE_ALBUMS + '.' + COLUMN_ALBUM_NAME + " FROM " + TABLE_ALBUMS +
                    " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + '.' + COLUMN_ALBUM_ARTIST+
                    " = " + TABLE_ARTISTS + '.' + COLUMN_ARTIST_ID +
                    " WHERE " + TABLE_ARTISTS + '.' + COLUMN_ARTIST_NAME + " = \"";

    public static final String QUERY_ALBUM_BY_ARTISTS_SORT =
            " ORDER BY " + TABLE_ALBUMS + '.' + COLUMN_ALBUM_NAME + " COLLATE NOCASE ";

    private Connection connection;

    public boolean open(){
        try{
            connection = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        }catch (SQLException e){
            System.out.println("Couldnt connect to databse " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void close(){
        try{
            if(connection != null){
                connection.close();
            }
        }catch (SQLException e){
            System.out.println("Couldnt close connection " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Artist> queryArtists(int sortOrder){

        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TABLE_ARTISTS);
        selectOrder(sortOrder, sb, QUERY_ARTISTS_SORT);

        try(Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sb.toString())){

            List<Artist> artists = new ArrayList<>();
            while (results.next()){
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artists.add(artist);
            }

            return artists;

        }catch (SQLException e){
            System.out.println("Query failed " + e.getMessage());
            return null;
        }
    }

    public List<String> queryAlbumsForArtist(String artistName, int sortOrder){

        //select albums.name from albums
        //inner join artists on
        //albums.artist = artists._id
        //where artists.name = "Carole King"
        //order by albums.name
        //collate nocase asc
        StringBuilder sb = new StringBuilder(QUERY_ALBUMS_BY_ARTISTS_START);
        sb.append(artistName);
        sb.append("\"");
        selectOrder(sortOrder, sb, QUERY_ALBUM_BY_ARTISTS_SORT);

        System.out.println("SQL statement = " + sb.toString());

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sb.toString())){

            List<String> albums = new ArrayList<>();
            while (resultSet.next()){
                albums.add(resultSet.getString(1));
            }

            return albums;
        }catch (SQLException e){
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    private void selectOrder(int sortOrder, StringBuilder sb, String querySort) {
        if(sortOrder != ORDER_BY_NONE){
            sb.append(querySort);
            if(sortOrder == ORDER_BY_DESC){
                sb.append("DESC");
            }else{
                sb.append("ASC");
            }
        }
    }
}
