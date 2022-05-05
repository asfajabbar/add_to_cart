package com.example.add_to_cart.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyCartAdapter extends RecyclerView.Adapter <MyCartAdapter>.MyCartVieHolder {

    public class MyCartViewHolder extends RecyclerView.ViewHolder
    {

        public MyCartViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
