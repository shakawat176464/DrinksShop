package me.h.exclusive.drinksshop2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.h.exclusive.drinksshop2.Adapter.CartAdapter;
import me.h.exclusive.drinksshop2.Database.ModelDB.Cart;
import me.h.exclusive.drinksshop2.Retrofit.IDrinkShopAPI;
import me.h.exclusive.drinksshop2.Utils.Common;
import me.h.exclusive.drinksshop2.Utils.RecyclerItemTouchHelper;
import me.h.exclusive.drinksshop2.Utils.RecyclerItemTouchHelperListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recycler_cart;
    Button btn_place_order;

    CompositeDisposable compositeDisposable;
    List<Cart> cartList = new ArrayList<>();

    CartAdapter cartAdapter;

    RelativeLayout rootLayout;

    IDrinkShopAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        compositeDisposable = new CompositeDisposable();

        mService = Common.getAPI();

        recycler_cart = findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_cart);


        btn_place_order = findViewById(R.id.btn_place_order);

        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });

        rootLayout = findViewById(R.id.rootLayout);

        loadCartItems();

    }

    private void placeOrder() {

        ///create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Submit Order");

        View submit_order_layout = LayoutInflater.from(this).inflate(R.layout.submit_order_layout,null);
        final EditText edt_comment=submit_order_layout.findViewById(R.id.edt_comment);
        final EditText edt_other_address=submit_order_layout.findViewById(R.id.edt_other_address);


        final RadioButton rdi_user_address= submit_order_layout.findViewById(R.id.rdi_user_address);
        final RadioButton rdi_others_address=submit_order_layout.findViewById(R.id.rdi_other_address);


        ///event
        rdi_user_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    edt_other_address.setEnabled(false);
            }
        });

        rdi_others_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    edt_other_address.setEnabled(true);
            }
        });

        builder.setView(submit_order_layout);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String orderComment = edt_comment.getText().toString();
                final String orderAddress;
                if (rdi_user_address.isChecked())
                {
                    orderAddress = Common.currentUsers.getAddress();////null=Common.currentUser.getAddress()
                    Toast.makeText(CartActivity.this, "User Address", Toast.LENGTH_SHORT).show();
                }
                else if (rdi_others_address.isChecked()) {
                    orderAddress = edt_other_address.getText().toString();
                }
                else
                    orderAddress="";

                ////submit order
                compositeDisposable.add(
                        Common.cartRepository.getCartItems()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<List<Cart>>() {
                                    @Override
                                    public void accept(List<Cart> carts) throws Exception {
                                        if (!TextUtils.isEmpty(orderAddress))
                                            sendOrderToServer(Common.cartRepository.sumPrice(),
                                                    carts,
                                                    orderComment,orderAddress);
                                        else
                                            Toast.makeText(CartActivity.this, "Others Address Can't Null", Toast.LENGTH_SHORT).show();
                                    }
                                })
                );

            }
        });
        builder.show();

    }

    private void sendOrderToServer(float sumPrice, List<Cart> carts, String orderComment, String orderAddress) {
        if (carts.size() > 0)
        {
            String orderDetails=new Gson().toJson(carts);

            /////null=Common.currentUser.getPhone()
            mService.submitOrder(sumPrice,orderDetails,orderComment,orderAddress,Common.currentUsers.getPhone())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Toast.makeText(CartActivity.this, "Order Submit", Toast.LENGTH_SHORT).show();
                            Common.cartRepository.emptyCart();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("ERROR",t.getMessage());
                        }
                    });

        }
    }


    private void loadCartItems() {

        compositeDisposable.add(
                Common.cartRepository.getCartItems()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<Cart>>() {
                            @Override
                            public void accept(List<Cart> carts) throws Exception {
                                displayCartItem(carts);
                            }
                        }));

    }

    private void displayCartItem(List<Cart> carts) {

        cartList = carts;

        cartAdapter = new CartAdapter(this,carts);
        recycler_cart.setAdapter(cartAdapter);

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartAdapter.CartViewHolder)
        {
            String name = cartList.get(viewHolder.getAdapterPosition()).name;
            final Cart deleteItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex=viewHolder.getAdapterPosition();

            cartAdapter.removeItem(deletedIndex);

            Common.cartRepository.deleteCartItem(deleteItem);

            Snackbar snackbar = Snackbar.make(rootLayout, new StringBuilder(name).append(" remove from Cart List").toString(),
                    Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartAdapter.restoreItem(deleteItem, deletedIndex);
                    Common.cartRepository.insertToCart(deleteItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
}

