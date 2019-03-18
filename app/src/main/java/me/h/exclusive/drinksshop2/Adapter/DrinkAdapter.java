package me.h.exclusive.drinksshop2.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.h.exclusive.drinksshop2.Database.ModelDB.Cart;
import me.h.exclusive.drinksshop2.Database.ModelDB.Favorite;
import me.h.exclusive.drinksshop2.Interface.ItemClickListener;
import me.h.exclusive.drinksshop2.Model.Drink;
import me.h.exclusive.drinksshop2.R;
import me.h.exclusive.drinksshop2.Utils.Common;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkViewHolder> {

    Context context;
    List<Drink> drinkList;

    public DrinkAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.drink_item_layout,null);
        return new DrinkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DrinkViewHolder holder, final int position) {

        holder.txt_price.setText(new StringBuilder("TK ").append(drinkList.get(position).Price).toString());

        holder.txt_drink_name.setText(drinkList.get(position).Name);

        holder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToCartDialog(position);
            }
        });


        Picasso.with(context)
                .load(drinkList.get(position).Link)
                .into(holder.img_product);


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        /////Favorite Syalem
        if (Common.favoriteRepository.isFavorite(Integer.parseInt(drinkList.get(position).ID))==1)
            holder.btn_favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
        else
            holder.btn_favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);

        holder.btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.favoriteRepository.isFavorite(Integer.parseInt(drinkList.get(position).ID)) !=1)
                {
                    addOrRemoveFavorite(drinkList.get(position),true);
                    holder.btn_favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                }
                else {
                    addOrRemoveFavorite(drinkList.get(position),false);
                    holder.btn_favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }
            }
        });



    }

    private void addOrRemoveFavorite(Drink drink, boolean isAdd) {

        Favorite favorite = new Favorite();
        favorite.id = drink.ID;
        favorite.link = drink.Link;
        favorite.name = drink.Name;
        favorite.price = drink.Price;
        favorite.menuId = drink.MenuId;

        if (isAdd)
            Common.favoriteRepository.insertFav(favorite);
        else
            Common.favoriteRepository.delete(favorite);

    }


    private void showAddToCartDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView =LayoutInflater.from(context).inflate(R.layout.add_to_cart_layout,null);

        ImageView img_product_dialog = itemView.findViewById(R.id.img_cart_product);

        final ElegantNumberButton txt_count = itemView.findViewById(R.id.txt_count);

        TextView txt_product_dialog = itemView.findViewById(R.id.txt_cart_product_name);

        EditText edt_comment = itemView.findViewById(R.id.edt_comment);

        RadioButton rdi_sizeM = itemView.findViewById(R.id.rdi_sizeM);
        RadioButton rdi_sizeL = itemView.findViewById(R.id.rdi_sizeL);


        rdi_sizeM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.sizeOfCup=0;
            }
        });

        rdi_sizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.sizeOfCup=1;
            }
        });


        RadioButton rdi_suger_100 = itemView.findViewById(R.id.rdi_suger_100);
        RadioButton rdi_suger_70 = itemView.findViewById(R.id.rdi_suger_70);
        RadioButton rdi_suger_50 = itemView.findViewById(R.id.rdi_suger_50);
        RadioButton rdi_suger_30 = itemView.findViewById(R.id.rdi_suger_30);
        RadioButton rdi_suger_free = itemView.findViewById(R.id.rdi_suger_free);

        rdi_suger_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.suger=30;
            }
        });
        rdi_suger_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.suger=50;
            }
        });
        rdi_suger_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.suger=70;
            }
        });
        rdi_suger_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.suger=100;
            }
        });

        rdi_suger_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.suger=0;
            }
        });

        RadioButton rdi_ice_100 = itemView.findViewById(R.id.rdi_ice_100);
        RadioButton rdi_ice_70 = itemView.findViewById(R.id.rdi_ice_70);
        RadioButton rdi_ice_50 = itemView.findViewById(R.id.rdi_ice_50);
        RadioButton rdi_ice_30 = itemView.findViewById(R.id.rdi_ice_30);
        RadioButton rdi_ice_free = itemView.findViewById(R.id.rdi_ice_free);


        rdi_ice_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.ice=30;
            }
        });
        rdi_ice_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.ice=50;
            }
        });
        rdi_ice_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.ice=70;
            }
        });
        rdi_ice_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.ice=100;
            }
        });
        rdi_ice_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.ice=0;
            }
        });

        RecyclerView recycler_tipping = itemView.findViewById(R.id.recycler_topping);
        recycler_tipping.setLayoutManager(new LinearLayoutManager(context));
        recycler_tipping.setHasFixedSize(true);

        MultiChoiceAdapter adapter = new MultiChoiceAdapter(context,Common.toppingList);
        recycler_tipping.setAdapter(adapter);


        Picasso.with(context)
                .load(drinkList.get(position).Link)
                .into(img_product_dialog);

        txt_product_dialog.setText(drinkList.get(position).Name);

        builder.setView(itemView);

        builder.setNegativeButton("ADD TO CART", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Common.sizeOfCup==-1)
                {
                    Toast.makeText(context, "Please choose size of cup!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Common.suger==-1)
                {
                    Toast.makeText(context, "Please choose suger!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Common.ice==-1)
                {
                    Toast.makeText(context, "Please choose ice!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                showConfirmDialog(position,txt_count.getNumber());
                dialog.dismiss();

            }
        });
        builder.show();


    }

    private void showConfirmDialog(final int position, final String number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView =LayoutInflater.from(context).inflate(R.layout.confirm_add_to_cart_layout,null);

        ImageView img_product_dialog = itemView.findViewById(R.id.img_product);
        final TextView txt_product_dialog = itemView.findViewById(R.id.txt_cart_product_name);
        final TextView txt_product_price = itemView.findViewById(R.id.txt_cart_product_price);
        TextView txt_sugar = itemView.findViewById(R.id.txt_sugar);
        TextView txt_ice = itemView.findViewById(R.id.txt_ice);
        final TextView txt_topping_extra = itemView.findViewById(R.id.txt_topping_extra);

        ///set data
        Picasso.with(context).load(drinkList.get(position).Link).into(img_product_dialog);
        txt_product_dialog.setText(new StringBuilder(drinkList.get(position).Name).append(" x")
                .append(Common.sizeOfCup==0 ? " Size M":"Size L")
                .append(number).toString());

        txt_ice.setText(new StringBuilder("Ice: ").append(Common.ice).append("%").toString());
        txt_sugar.setText(new StringBuilder("Sugar: ").append(Common.suger).append("%").toString());

        double price = (Double.parseDouble(drinkList.get(position).Price)*Double.parseDouble(number) + Common.toppingPrice);

        if (Common.sizeOfCup==1)
            price+=(3.0*Double.parseDouble(number));


        StringBuilder topping_final_comment = new StringBuilder("");
        for (String line:Common.toppingAdded)
            topping_final_comment.append(line).append("\n");

        txt_topping_extra.setText(topping_final_comment);


        final double finalPrice = Math.round(price);

        txt_product_price.setText(new StringBuilder("TK ").append(finalPrice));

        builder.setNegativeButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /////add to sqlite
                dialog.dismiss();

                try {
                    /////Add to SQLite
                    ///Create new Cart item
                    Cart cartItem = new Cart();
                    cartItem.name = drinkList.get(position).Name;
                    cartItem.amount = Integer.parseInt(number);
                    cartItem.ice = Common.ice;
                    cartItem.suger = Common.suger;
                    cartItem.price = (int) finalPrice;
                    cartItem.size=Common.sizeOfCup;
                    cartItem.toopingExtras = txt_topping_extra.getText().toString();
                    cartItem.link=drinkList.get(position).Link;

                    Common.cartRepository.insertToCart(cartItem);
                    Log.d("shakawat", new Gson().toJson(cartItem));

                    Toast.makeText(context, "Save item to Cart", Toast.LENGTH_SHORT).show();
                }
                catch (Exception ex){
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setView(itemView);
        builder.show();


    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }
}

