package me.h.exclusive.drinksshop2.Adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.h.exclusive.drinksshop2.Interface.ItemClickListener;
import me.h.exclusive.drinksshop2.R;

public class DrinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    ImageView img_product;
    TextView txt_drink_name,txt_price;

    ItemClickListener itemClickListener;

    ImageView btn_add_to_cart,btn_favorite;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DrinkViewHolder(@NonNull View itemView) {
        super(itemView);

        img_product = itemView.findViewById(R.id.image_product);
        txt_drink_name = itemView.findViewById(R.id.txt_drink_name);
        txt_price = itemView.findViewById(R.id.txt_price);

        btn_add_to_cart = itemView.findViewById(R.id.btn_add_cart);
        btn_favorite = itemView.findViewById(R.id.btn_favorite);



        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v);
    }
}

