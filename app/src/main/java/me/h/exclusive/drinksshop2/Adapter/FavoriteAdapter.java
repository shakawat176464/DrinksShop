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

import com.squareup.picasso.Picasso;

import java.util.List;

import me.h.exclusive.drinksshop2.Database.ModelDB.Favorite;
import me.h.exclusive.drinksshop2.R;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>{

    Context context;
    List<Favorite> favoriteList;

    public FavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.fav_item_layout,parent,false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {

        Picasso.with(context)
                .load(favoriteList.get(position).link).into(holder.img_product);
        holder.txt_price.setText(new StringBuilder("TK ").append(favoriteList.get(position).price).toString());
        holder.txt_product_name.setText(favoriteList.get(position).name);

    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder
    {

        ImageView img_product;
        TextView txt_product_name,txt_price;

        public RelativeLayout view_background;
        public LinearLayout view_forground;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            img_product = itemView.findViewById(R.id.fav_img_product);
            txt_product_name = itemView.findViewById(R.id.fav_txt_product_name);
            txt_price = itemView.findViewById(R.id.fav_txt_price);

            view_background = itemView.findViewById(R.id.view_background);
            view_forground = itemView.findViewById(R.id.view_forground);

        }
    }


    public void removeItem(int position)
    {
        favoriteList.remove(position);
        notifyItemRemoved(position);
    }


    public void restoreItem(Favorite item, int position)
    {
        favoriteList.add(position,item);
        notifyItemInserted(position);
    }
}


