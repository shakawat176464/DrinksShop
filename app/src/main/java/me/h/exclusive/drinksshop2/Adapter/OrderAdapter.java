package me.h.exclusive.drinksshop2.Adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.h.exclusive.drinksshop2.Interface.ItemClickListener;
import me.h.exclusive.drinksshop2.Model.Order;
import me.h.exclusive.drinksshop2.OrderDetailsActivity;
import me.h.exclusive.drinksshop2.R;
import me.h.exclusive.drinksshop2.Utils.Common;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    Context context;
    List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.order_layout,viewGroup,false);

        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, final int i) {

        orderViewHolder.txt_order_id.setText(new StringBuilder("#").append(orderList.get(i).getOrderId()));
        orderViewHolder.txt_order_price.setText(new StringBuilder("$").append(orderList.get(i).getOrderPrice()));
        orderViewHolder.txt_order_address.setText(orderList.get(i).getOrderAddress());
        orderViewHolder.txt_order_comment.setText(orderList.get(i).getOrderComment());
        orderViewHolder.txt_order_status.setText(new StringBuilder("Order Status: ").append(Common.convertCodeToStatus(orderList.get(i).getOrderStatus())));


        orderViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentOrder = orderList.get(i);
                context.startActivity(new Intent(context, OrderDetailsActivity.class));
            }
        });



    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}

