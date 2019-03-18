package me.h.exclusive.drinksshop2.Database.Local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import me.h.exclusive.drinksshop2.Database.ModelDB.Cart;
import me.h.exclusive.drinksshop2.Database.ModelDB.Favorite;

@Database(entities = {Cart.class, Favorite.class},version = 1)
public abstract class SakawatRoomDatabase extends RoomDatabase {

    public abstract CartDAO cartDAO();
    public abstract FavoriteDAO favoriteDAO();

    private static SakawatRoomDatabase instance;

    public static SakawatRoomDatabase getInstance(Context context)
    {
        if (instance==null)
            instance = Room.databaseBuilder(context,SakawatRoomDatabase.class,"ShakawatDB")
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }
}
