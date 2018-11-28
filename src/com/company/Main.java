package com.company;

import com.company.model.Artist;
import com.company.model.Datasource;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Datasource datasource = new Datasource();
        if(!datasource.open()){
            System.out.println("Cant open datasource");
            return;
        }

        List<Artist> artists = datasource.queryArtists(Datasource.ORDER_BY_ASC);
        if(artists == null){
            System.out.println("No artists!");
            return;
        }

        for(Artist artist: artists){
            System.out.println("ID = " + artist.getId() + ", Name = " + artist.getName());
        }

        List<String> albumsForArtists = datasource.queryAlbumsForArtist("Iron Maiden", Datasource.ORDER_BY_ASC);
        for(String album: albumsForArtists){
            System.out.println(album);
        }

        datasource.close();
    }
}
