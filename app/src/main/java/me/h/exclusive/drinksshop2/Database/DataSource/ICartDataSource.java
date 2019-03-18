package me.h.exclusive.drinksshop2.Database.DataSource;

import java.util.List;

import io.reactivex.Flowable;
import me.h.exclusive.drinksshop2.Database.ModelDB.Cart;

public interface ICartDataSource {
    Flowable<List<Cart>> getCartItems();
    Flowable<List<Cart>> getCartItemById(int cartItemId);
    int countCartItems();
    float sumPrice();
    void emptyCart();
    void insertToCart(Cart...carts);
    void updateCart(Cart...carts);
    void deleteCartItem(Cart carts);
}
