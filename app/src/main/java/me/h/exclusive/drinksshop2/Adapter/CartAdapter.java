package me.h.exclusive.drinksshop2.Adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.h.exclusive.drinksshop2.Database.ModelDB.Cart;
import me.h.exclusive.drinksshop2.R;
import me.h.exclusive.drinksshop2.Utils.Common;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    List<Cart> cartList;

    public CartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.cart_item_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {

        Picasso.with(context)
                .load(cartList.get(position).link)
                .into(holder.img_product);

        holder.txt_amount.setNumber(String.valueOf(cartList.get(position).amount));
        holder.txt_price.setText(new StringBuilder("TK ").append(cartList.get(position).price));
        holder.txt_product_name.setText(new StringBuilder(cartList.get(position).name)
                .append(" X")
                .append(cartList.get(position).amount)
                .append(cartList.get(position).size == 0 ? "Size M":"Size L"));

        holder.txt_sugar_ice.setText(new StringBuilder("Sugar: ")
                .append(cartList.get(position).suger).append("%").append("\n")
                .append("Ice: ").append(cartList.get(position).ice)
                .append("%").toString());

        ///get price of one with options
        final double priceOneCup=cartList.get(position).price / cartList.get(position).amount;


        ////auto save item when user change amount
        holder.txt_amount.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                Cart cart = cartList.get(position);
                cart.amount = newValue;
                cart.price= (int) Math.round(priceOneCup*newValue);

                Common.cartRepository.updateCart(cart);

                holder.txt_price.setText(new StringBuilder("TK ").append(cartList.get(position).price));

            }
        });



    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder
    {

        ImageView img_product;
        TextView txt_product_name,txt_sugar_ice,txt_price;
        ElegantNumberButton txt_amount;

        public RelativeLayout view_background;
        public LinearLayout view_forground;

        public CartViewHolder (View itemView){
            super(itemView);
            img_product = itemView.findViewById(R.id.img_products);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_sugar_ice = itemView.findViewById(R.id.txt_sugar_ice);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_amount = itemView.findViewById(R.id.txt_amount);

            view_background = itemView.findViewById(R.id.view_background);
            view_forground = itemView.findViewById(R.id.view_forground);

        }
    }


    public void removeItem(int position)
    {
        cartList.remove(position);
        notifyItemRemoved(position);
    }


    public void restoreItem(Cart item, int position)
    {
        cartList.add(position,item);
        notifyItemInserted(position);
    }

}

