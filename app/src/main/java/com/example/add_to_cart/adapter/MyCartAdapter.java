package com.example.add_to_cart.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.add_to_cart.R;
import com.example.add_to_cart.eventbus.MyUpdateCartEvent;
import com.example.add_to_cart.model.CartModel;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCartAdapter extends RecyclerView.Adapter <MyCartAdapter.MyCartViewHolder>{


    private Context context;
    private List<CartModel> cartModelList;

    public MyCartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyCartViewHolder(LayoutInflater.from(context).
                inflate(R.layout.cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {

        //binding
        Glide.with (context).load (cartModelList.get(position).getImage()).into(holder.imageView);
        holder.txt_Price.setText(new StringBuilder("RS "). append(cartModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder(). append(cartModelList.get(position).getName()));
        holder.txt_Quantity.setText(new StringBuilder(). append(cartModelList.get(position).getQuantity()));


        //Events
        holder.btnMinus.setOnClickListener(v->
        {
            minusCartItem(holder,cartModelList.get(position));

        });
        holder.btnPlus.setOnClickListener(v->
        {
            plusCartItem(holder,cartModelList.get(position));

        });
        holder.btnDel.setOnClickListener(v->
        {
            AlertDialog dialogue = new AlertDialog.Builder(context)
                    .setTitle("Delete icon")
                    .setMessage("do you really want to delete")
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("OK", (dialog12, which) -> {
                                //TEMP REMOVE
                                notifyItemRemoved(position);
                                deleteFromFirebase(cartModelList.get(position));
                                dialog12.dismiss();
                            }).create();
            dialogue.show();

        });

    }

    private void deleteFromFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart").child("UNIQUE_USER_ID").
                child(cartModel.getKey())
                .removeValue()
                .addOnSuccessListener(avoid-> EventBus.getDefault().
                        postSticky(new MyUpdateCartEvent()));
    }

    private void plusCartItem(MyCartViewHolder holder, CartModel cartModel) {

            cartModel.setQuantity(cartModel.getQuantity()+1);
            cartModel.setTotal_price(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

            //update quantity
            holder.txt_Quantity.setText((new StringBuilder().append(cartModel.getQuantity())));
            updateFirebase(cartModel);

    }

    private void minusCartItem(MyCartViewHolder holder, CartModel cartModel) {
        if(cartModel.getQuantity()>1)
        {
            cartModel.setQuantity(cartModel.getQuantity()-1);
            cartModel.setTotal_price(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

            //update quantity
            holder.txt_Quantity.setText((new StringBuilder().append(cartModel.getQuantity())));
            updateFirebase(cartModel);
        }
    }

    private void updateFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance().getReference("Cart").child("UNIQUE_USER_ID").
                child(cartModel.getKey()).setValue(cartModel)
                .addOnSuccessListener(avoid-> EventBus.getDefault().
                        postSticky(new MyUpdateCartEvent()));
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }






    public class MyCartViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.btn_minus)
        ImageView btnMinus;
        @BindView(R.id.btn_plus)
        ImageView btnPlus;
        @BindView(R.id.btnDel)
        ImageView btnDel;
        @BindView(R.id.imageview)
        ImageView imageView;
        @BindView(R.id.txt_Name)
        TextView txtName;
        @BindView(R.id.txt_price)
        TextView txt_Price;
        @BindView(R.id.txt_quantity)
        TextView txt_Quantity;
        Unbinder unbinder;

        public MyCartViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this, itemView);
        }
    }
}
