package me.h.exclusive.drinksshop2;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.h.exclusive.drinksshop2.Adapter.OrderAdapter;
import me.h.exclusive.drinksshop2.Model.Order;
import me.h.exclusive.drinksshop2.Retrofit.IDrinkShopAPI;
import me.h.exclusive.drinksshop2.Utils.Common;


public class ShowOrdersActivity extends AppCompatActivity {

    IDrinkShopAPI mService;
    RecyclerView recycler_orders;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_orders);

        mService = Common.getAPI();

        recycler_orders = findViewById(R.id.recycler_orders);
        recycler_orders.setLayoutManager(new LinearLayoutManager(this));
        recycler_orders.setHasFixedSize(true);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.order_new)
                {
                    loadOrder("0");
                }
                else if (menuItem.getItemId() == R.id.order_cancel)
                {
                    loadOrder("-1");
                }
                else if (menuItem.getItemId() == R.id.order_progressing)
                {
                    loadOrder("1");
                }
                else if (menuItem.getItemId() == R.id.order_shipping)
                {
                    loadOrder("2");
                }
                else if (menuItem.getItemId() == R.id.order_shipped)
                {
                    loadOrder("3");
                }

                return true;
            }
        });





        loadOrder("0");

    }

    private void loadOrder(String statusCode) {

        if (Common.currentUsers != null)
        {
            compositeDisposable.add(mService.getOrder(Common.currentUsers.getPhone(),statusCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Order>>() {
                        @Override
                        public void accept(List<Order> orders) throws Exception {
                            displayOrder(orders);
                        }
                    }));
        }
        else {
            Toast.makeText(this, "Please Login Again...!!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void displayOrder(List<Order> orders) {

        OrderAdapter adapter = new OrderAdapter(this,orders);
        recycler_orders.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrder("0");
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
}

