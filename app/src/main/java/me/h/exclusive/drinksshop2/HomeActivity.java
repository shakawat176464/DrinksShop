package me.h.exclusive.drinksshop2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.accountkit.AccountKit;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.h.exclusive.drinksshop2.Adapter.CategoryAdapter;
import me.h.exclusive.drinksshop2.Database.DataSource.CartRepository;
import me.h.exclusive.drinksshop2.Database.DataSource.FavoriteRepository;
import me.h.exclusive.drinksshop2.Database.Local.CartDataSource;
import me.h.exclusive.drinksshop2.Database.Local.FavoriteDataSource;
import me.h.exclusive.drinksshop2.Database.Local.SakawatRoomDatabase;
import me.h.exclusive.drinksshop2.Model.Banner;
import me.h.exclusive.drinksshop2.Model.Category;
import me.h.exclusive.drinksshop2.Retrofit.IDrinkShopAPI;
import me.h.exclusive.drinksshop2.Utils.Common;
import me.h.exclusive.drinksshop2.Utils.ProgressRequestBody;
import me.h.exclusive.drinksshop2.Utils.UploadCallBack;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UploadCallBack {

    private static final int PICK_FILE_REQUEST = 1222;
    TextView txt_name,txt_phone;

    SliderLayout sliderLayout;
    IDrinkShopAPI mService;

    NotificationBadge badge;
    ImageView cart_icon;


    CircleImageView img_avatar;

    RecyclerView lst_menu;

    Uri selectFileUri;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sliderLayout = findViewById(R.id.slider);


        mService = Common.getAPI();


        lst_menu = findViewById(R.id.lst_menu);
        //lst_menu.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        lst_menu.setLayoutManager(new GridLayoutManager(this,2));
        lst_menu.setHasFixedSize(true);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txt_name = headerView.findViewById(R.id.txt_name);
        txt_phone = headerView.findViewById(R.id.txt_phone);
        img_avatar = headerView.findViewById(R.id.img_avatar);


        ///event
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        ///set info
        txt_name.setText(Common.currentUsers.getName());
        txt_phone.setText(Common.currentUsers.getPhone());



        ///set avatar
        if (!TextUtils.isEmpty(Common.currentUsers.getAvatarUrl()))
        {
            Picasso.with(this)
                    .load(new StringBuilder(Common.BASE_URL)
                    .append("user_avatar/")
                    .append(Common.currentUsers.getAvatarUrl()).toString())
                    .into(img_avatar);
        }



        ////get banner
        getBannerImage();

        ////getMenu
        getMenu();


        getToppingList();


        ////Init Database
        initDB();


    }

    private void chooseImage() {

        startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent(),"Select a file"),
                PICK_FILE_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode ==Activity.RESULT_OK)
        {
            if (data !=null)
            {
                selectFileUri = data.getData();
                if (selectFileUri != null && !selectFileUri.getPath().isEmpty())
                {
                    img_avatar.setImageURI(selectFileUri);
                    uploadFile();
                }
                else {
                    Toast.makeText(this, "Can not upload file to server", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void uploadFile() {

        if (selectFileUri != null)
        {
            File file = FileUtils.getFile(this,selectFileUri);

            String fileName = new StringBuffer(Common.currentUsers.getPhone())
                    .append(FileUtils.getExtension(file.toString()))
                    .toString();

            ProgressRequestBody requestFile = new ProgressRequestBody(file,this);
            final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file",fileName,requestFile);
            final MultipartBody.Part userPhone = MultipartBody.Part.createFormData("phone",Common.currentUsers.getPhone());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mService.uploadFile(userPhone,body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Toast.makeText(HomeActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();

        }

    }

    private void initDB() {
        Common.shakawatRoomDatabase = SakawatRoomDatabase.getInstance(this);
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.shakawatRoomDatabase.cartDAO()));
        Common.favoriteRepository = FavoriteRepository.getInstance(FavoriteDataSource.getInstance(Common.shakawatRoomDatabase.favoriteDAO()));
    }


    private void getToppingList() {

        compositeDisposable.add(mService.getMenu()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        displayMenu(categories);
                    }
                }));

    }


    private void getMenu() {

        compositeDisposable.add(mService.getMenu()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        displayMenu(categories);
                    }
                }));


    }


    private void displayMenu(List<Category> categories) {

        CategoryAdapter adapter = new CategoryAdapter(this,categories);
        lst_menu.setAdapter(adapter);

    }


    private void getBannerImage() {

        compositeDisposable.add(mService.getBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Banner>>() {
                    @Override
                    public void accept(List<Banner> banners) throws Exception {
                        displayImage(banners);
                    }
                }));

    }


    private void displayImage(List<Banner> banners) {

        HashMap<String,String> bannerMap = new HashMap<>();
        for (Banner item:banners)
            bannerMap.put(item.getName(),item.getLink());

        for (String name: bannerMap.keySet())
        {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            sliderLayout.addSlider(textSliderView);
        }

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);

    }


    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        sliderLayout.stopAutoCycle();
        super.onStop();
    }


    ///exit application when click BACK button
    boolean isBackButtonClicked=false;


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (isBackButtonClicked)
            {
                super.onBackPressed();
                return;
            }
            this.isBackButtonClicked=true;
            Toast.makeText(this, "Please Click BACK again to exit!!!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        View itemView = menu.findItem(R.id.cart_menu).getActionView();
        badge = (NotificationBadge) itemView.findViewById(R.id.badge);
        cart_icon = itemView.findViewById(R.id.cart_icon);
        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///go to cart activity
                startActivity(new Intent(getApplicationContext(),CartActivity.class));
            }
        });
        updateCartCount();
        return true;
    }



    private void updateCartCount() {
        if (badge==null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Common.cartRepository.countCartItems()==0)
                    badge.setVisibility(View.INVISIBLE);
                else {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(Common.cartRepository.countCartItems()));
                }
            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart_menu) {
            return true;
        }
        else if (id == R.id.search_menu) {
            startActivity(new Intent(getApplicationContext(),SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart_Id) {
            // Handle the camera action

            startActivity(new Intent(getApplicationContext(),CartActivity.class));

        }
        else if (id == R.id.nav_favorite_Id) {

            if (Common.currentUsers !=null)
            {
                startActivity(new Intent(getApplicationContext(),FavoriteListActivity.class));
            }
            else {
                Toast.makeText(this, "Please login to use this feature", Toast.LENGTH_SHORT).show();
            }

        }
        else if (id == R.id.nav_show_orders_Id) {

            if (Common.currentUsers !=null)
            {
                startActivity(new Intent(getApplicationContext(),ShowOrdersActivity.class));
            }
            else {
                Toast.makeText(this, "Please login to use this feature", Toast.LENGTH_SHORT).show();
            }

        }
        else if (id == R.id.nav_logout_Id) {

            // Create confirm exit App Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit Application");
            builder.setMessage("Do you want to exit this application..??");

            builder.setNegativeButton("ON", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    AccountKit.logOut();
                    ///clear all activity
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
        isBackButtonClicked=false;
    }


    @Override
    public void onProgressUpdate(int pertantage) {

    }
}
