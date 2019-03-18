package me.h.exclusive.drinksshop2.Utils;

import java.util.ArrayList;
import java.util.List;

import me.h.exclusive.drinksshop2.Database.DataSource.CartRepository;
import me.h.exclusive.drinksshop2.Database.DataSource.FavoriteRepository;
import me.h.exclusive.drinksshop2.Database.Local.SakawatRoomDatabase;
import me.h.exclusive.drinksshop2.Model.Category;
import me.h.exclusive.drinksshop2.Model.Drink;
import me.h.exclusive.drinksshop2.Model.Order;
import me.h.exclusive.drinksshop2.Model.Users;
import me.h.exclusive.drinksshop2.Retrofit.IDrinkShopAPI;
import me.h.exclusive.drinksshop2.Retrofit.RetrofitClient;

public class Common {

    public static final String BASE_URL = "http://10.0.2.2/drinkshop2/";


    public static Users currentUsers = null;
    public static Category currentCategory = null;
    public static Order currentOrder = null;


    public static final String TOPPING_MENU_ID = "7";

    public static List<Drink> toppingList = new ArrayList<>();

    public static double toppingPrice = 0.0;
    public static List<String> toppingAdded = new ArrayList<>();


    ////hold field
    public static int sizeOfCup = -1;////-1 : no chose (error) , 0:M, 1:L
    public static int suger = -1;////-1 : on choose error
    public static int ice = -1;

    ///database
    public static SakawatRoomDatabase shakawatRoomDatabase;
    public static CartRepository cartRepository;
    //public static CartDatabase cartDatabase;
    public static FavoriteRepository favoriteRepository;





    public static IDrinkShopAPI getAPI()
    {
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static String convertCodeToStatus(int orderStatus) {

        switch (orderStatus)
        {
            case 0:
                return "Placed";
            case 1:
                return "Progressing";
            case 2:
                return "Shipping";
            case 3:
                return "Shipped";
            case -1:
                return "Cancelled";
            default:
                return "Order Error";
        }

    }
}
