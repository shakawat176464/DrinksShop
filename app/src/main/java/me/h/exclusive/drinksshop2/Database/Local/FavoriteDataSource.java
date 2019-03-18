package me.h.exclusive.drinksshop2.Database.Local;


import java.util.List;

import io.reactivex.Flowable;
import me.h.exclusive.drinksshop2.Database.DataSource.IFavoriteDataSource;
import me.h.exclusive.drinksshop2.Database.ModelDB.Favorite;

public class FavoriteDataSource implements IFavoriteDataSource {

    private FavoriteDAO favoriteDAO;
    private static FavoriteDataSource instance;

    public FavoriteDataSource(FavoriteDAO favoriteDAO) {
        this.favoriteDAO = favoriteDAO;
    }


    public static FavoriteDataSource getInstance(FavoriteDAO favoriteDAO)
    {
        if (instance==null)
            instance = new FavoriteDataSource(favoriteDAO);
        return instance;
    }


    @Override
    public Flowable<List<Favorite>> getFavItems() {
        return favoriteDAO.getFavItems();
    }

    @Override
    public int isFavorite(int itemId) {
        return favoriteDAO.isFavorite(itemId);
    }

    @Override
    public void insertFav(Favorite... favorites) {
        favoriteDAO.insertFav(favorites);
    }

    @Override
    public void delete(Favorite favorite) {
        favoriteDAO.delete(favorite);
    }
}


