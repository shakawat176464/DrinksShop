package me.h.exclusive.drinksshop2.Database.Local;


import java.util.List;

import io.reactivex.Flowable;
import me.h.exclusive.drinksshop2.Database.DataSource.ICartDataSource;
import me.h.exclusive.drinksshop2.Database.ModelDB.Cart;

public class CartDataSource implements ICartDataSource {

    private CartDAO cartDAO;
    private static CartDataSource instance;


    public CartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }


    public static CartDataSource getInstance(CartDAO cartDAO)
    {
        if (instance==null)
            instance = new CartDataSource(cartDAO);
        return instance;
    }


    @Override
    public Flowable<List<Cart>> getCartItems() {
        return cartDAO.getCartItems();
    }


    @Override
    public Flowable<List<Cart>> getCartItemById(int cartItemId) {
        return cartDAO.getCartItemById(cartItemId);
    }


    @Override
    public int countCartItems() {
        return cartDAO.countCartItems();
    }


    @Override
    public float sumPrice() {
        return cartDAO.sumPrice();
    }


    @Override
    public void emptyCart() {
        cartDAO.emptyCart();
    }


    @Override
    public void insertToCart(Cart... carts) {
        cartDAO.insertToCart(carts);
    }

    @Override
    public void updateCart(Cart... carts) {
        cartDAO.updateCart(carts);

    }

    @Override
    public void deleteCartItem(Cart carts) {
        cartDAO.deleteCartItem(carts);

    }
}

