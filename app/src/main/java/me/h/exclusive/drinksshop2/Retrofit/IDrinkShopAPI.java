package me.h.exclusive.drinksshop2.Retrofit;

import java.util.List;

import io.reactivex.Observable;
import me.h.exclusive.drinksshop2.Model.Banner;
import me.h.exclusive.drinksshop2.Model.Category;
import me.h.exclusive.drinksshop2.Model.CheckUserResponse;
import me.h.exclusive.drinksshop2.Model.Drink;
import me.h.exclusive.drinksshop2.Model.Order;
import me.h.exclusive.drinksshop2.Model.Users;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IDrinkShopAPI {

    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkUserExists(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("register.php")
    Call<Users> registerNewUser(@Field("phone") String phone,
                                @Field("name") String name,
                                @Field("address") String address,
                                @Field("birthdate") String birthdate);

    @FormUrlEncoded
    @POST("getUser.php")
    Call<Users> getUserInformation(@Field("phone") String phone);

    @GET("getbanner.php")
    Observable<List<Banner>> getBanner();

    @GET("getmenu.php")
    Observable<List<Category>> getMenu();


    ///for upload avatar
    @Multipart
    @POST("upload.php")
    Call<String> uploadFile(@Part MultipartBody.Part phone, @Part MultipartBody.Part file);



    @FormUrlEncoded
    @POST("getdrink.php")
    Observable<List<Drink>> getDrink(@Field("menuid") String menuID);

    @GET("getAlldrinks.php")
    Observable<List<Drink>> getAllDrinks();

    @FormUrlEncoded
    @POST("submitorder.php")
    Call<String> submitOrder(@Field("price") float orderPrice,
                             @Field("orderDetail") String orderDetail,
                             @Field("comment") String comment,
                             @Field("address") String address,
                             @Field("phone") String phone);


    @FormUrlEncoded
    @POST("getorder.php")
    Observable<List<Order>> getOrder(@Field("userPhone") String userPhone,
                                     @Field("status") String status);



    @FormUrlEncoded
    @POST("updatetoken.php")
    Call<String> updateToken(@Field("phone") String phone,
                             @Field("token") String token,
                             @Field("isServerToken") String isServerToken);


    @FormUrlEncoded
    @POST("cancelorder.php")
    Call<String> cancelOrder(@Field("orderId") String orderId,
                             @Field("userPhone") String userPhone);


}