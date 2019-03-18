package me.h.exclusive.drinksshop2.Database.DataSource;

import java.util.List;

import io.reactivex.Flowable;
import me.h.exclusive.drinksshop2.Database.ModelDB.Favorite;

public interface IFavoriteDataSource {

    Flowable<List<Favorite>> getFavItems();

    int isFavorite(int itemId);

    void insertFav(Favorite...favorites);

    void delete(Favorite favorite);

}
