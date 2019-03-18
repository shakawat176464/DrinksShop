package me.h.exclusive.drinksshop2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import me.h.exclusive.drinksshop2.Model.CheckUserResponse;
import me.h.exclusive.drinksshop2.Model.Users;
import me.h.exclusive.drinksshop2.Retrofit.IDrinkShopAPI;
import me.h.exclusive.drinksshop2.Utils.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE =1000 ;
    private static final int REQUEST_PERMISSION = 1001;
    Button btn_continue;

    IDrinkShopAPI mService;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case REQUEST_PERMISSION:
            {
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
                break;
                default:
                    break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSION
            );



        mService = Common.getAPI();


        btn_continue = findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage(LoginType.PHONE);
            }
        });



        ///check session
        if (AccountKit.getCurrentAccessToken() !=null)
        {

            final AlertDialog alertDialog = new SpotsDialog.Builder()
                    .setContext(MainActivity.this)
                    .build();
            alertDialog.show();
            alertDialog.setMessage("Please Waiting....");
            ///Auto Login
            ////get user and check exting user
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {


                    mService.checkUserExists(account.getPhoneNumber().toString())
                            .enqueue(new Callback<CheckUserResponse>() {
                                @Override
                                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                    CheckUserResponse userResponse = response.body();
                                    if (userResponse.isExists())
                                    {
                                        ////fetch information
                                        mService.getUserInformation(account.getPhoneNumber().toString())
                                                .enqueue(new Callback<Users>() {
                                                    @Override
                                                    public void onResponse(Call<Users> call, Response<Users> response) {
                                                        alertDialog.dismiss();

                                                        Common.currentUsers = response.body(); ///fixed here

                                                        ////update token
                                                        //updateTokenToFirebase();

                                                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Users> call, Throwable t) {
                                                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                        ////if user already exists, just start new Activity
                                        alertDialog.dismiss();
                                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                        finish();
                                    }
                                    else {
                                        ////else new register
                                        alertDialog.dismiss();

                                        showRegisterDialog(account.getPhoneNumber().toString());
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.d("ERROR",accountKitError.getErrorType().getMessage());
                }
            });
        }








    }

    private void startLoginPage(LoginType loginType) {

        Intent intent = new Intent(this,AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,builder.build());
        startActivityForResult(intent,REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_CODE)
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (result.getError() != null)
            {
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            }
            else if (result.wasCancelled())
            {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
            else {

                if (result.getAccessToken() !=null)
                {
                    final AlertDialog alertDialog = new SpotsDialog.Builder()
                            .setContext(MainActivity.this)
                            .build();
                    alertDialog.show();
                    alertDialog.setMessage("Please Waiting....");


                    ////get user and check exting user
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {


                            mService.checkUserExists(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<CheckUserResponse>() {
                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                                            CheckUserResponse userResponse = response.body();

                                            if (userResponse.isExists())
                                            {
                                                ////fetch information
                                                mService.getUserInformation(account.getPhoneNumber().toString())
                                                        .enqueue(new Callback<Users>() {
                                                            @Override
                                                            public void onResponse(Call<Users> call, Response<Users> response) {



                                                                Common.currentUsers = response.body();

                                                                ////update token
                                                                //updateTokenToFirebase();

                                                                ////if user already exists, just start new Activity
                                                                alertDialog.dismiss();
                                                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Users> call, Throwable t) {
                                                                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });



                                            }
                                            else {
                                                ////else new register
                                                alertDialog.dismiss();

                                                showRegisterDialog(account.getPhoneNumber().toString());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.d("ERROR",accountKitError.getErrorType().getMessage());
                        }
                    });
                }
            }

        }

    }


    private void showRegisterDialog(final String phone) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Register");


        LayoutInflater inflater = this.getLayoutInflater();
        View register_lauout = inflater.inflate(R.layout.register_layout,null);

        final MaterialEditText edt_name = register_lauout.findViewById(R.id.edt_name);
        final MaterialEditText edt_address = register_lauout.findViewById(R.id.edt_address);
        final MaterialEditText edt_birthdate = register_lauout.findViewById(R.id.edt_birthdate);

        Button btn_register = register_lauout.findViewById(R.id.btn_register);

        edt_birthdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        builder.setView(register_lauout);
        final AlertDialog dialog = builder.create();
        ///event
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*final AlertDialog waitingDialog = new SpotsDialog.Builder()
                        .setContext(MainActivity.this)
                        .build();
                waitingDialog.show();
                waitingDialog.setMessage("Please Waiting....");*/

                dialog.dismiss();

                if (TextUtils.isEmpty(edt_address.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Please enter your address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_birthdate.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Please enter your birthdate", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_name.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }


                final AlertDialog waitingDialog = new SpotsDialog.Builder()
                        .setContext(MainActivity.this)
                        .build();
                waitingDialog.show();
                waitingDialog.setMessage("Please Waiting....");

                mService.registerNewUser(phone,
                        edt_name.getText().toString(),
                        edt_address.getText().toString(),
                        edt_birthdate.getText().toString())
                        .enqueue(new Callback<Users>() {
                            @Override
                            public void onResponse(Call<Users> call, Response<Users> response) {

                                waitingDialog.dismiss();
                                Users users = response.body();
                                if (TextUtils.isEmpty(users.getError_msg()))
                                {
                                    Toast.makeText(MainActivity.this, "User register Successfully", Toast.LENGTH_SHORT).show();
                                    ///start new activity
                                    Common.currentUsers = response.body();

                                    ///update token
                                    //updateTokenToFirebase();

                                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Users> call, Throwable t) {
                                waitingDialog.dismiss();
                            }
                        });
            }
        });

        //dialog.setView(register_lauout);
        dialog.show();
    }


    private void printKeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("me.h.exclusive.drinksshop2",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH",Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
