package com.example.add_to_cart.adapter;

import android.content.Context;
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
import com.example.add_to_cart.listener.ICartLoadListener;
import com.example.add_to_cart.listener.IRecyclerViewClickListener;
import com.example.add_to_cart.model.CakeModel;
import com.example.add_to_cart.model.CartModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

//import com.google.firebase.database.core.view.View;

public class MyEatAdapter extends RecyclerView.Adapter<MyEatAdapter.MyEatViewHolder> {

    private Context context;
    private List<CakeModel> cakeModelList;
    private ICartLoadListener iCartLoadListener;

    public MyEatAdapter(Context context, List<CakeModel> cakeModelList, ICartLoadListener iCartLoadListener) {
        this.context = context;
        this.cakeModelList = cakeModelList;
        this.iCartLoadListener = iCartLoadListener;
    }

    @NonNull
    @Override
    public MyEatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyEatViewHolder (LayoutInflater.from(context).
                inflate(com.example.add_to_cart.R.layout.cakes_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyEatViewHolder holder, int position) {
        Glide.with (context).load (cakeModelList.get(position).getImage()).into(holder.imageview);
        holder.txt_price.setText(new StringBuilder("RS "). append(cakeModelList.get(position).getPrice()));
        holder.txt_Name.setText(new StringBuilder(). append(cakeModelList.get(position).getName()));

        //for cart
        holder.setListener((view, adapterPosition) -> {

            addToCart(cakeModelList.get(position));
        });

    }

    private void addToCart(CakeModel cakeModel) {
        DatabaseReference userCart= FirebaseDatabase
                .getInstance()
                .getReference("Cart")
        .child("UNIQUE_USER_ID"); // user data aye ga
        userCart.child(cakeModel.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    CartModel cartModel = snapshot.getValue(CartModel.class);
                    cartModel.setQuantity(cartModel.getQuantity() + 1);
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("quantity", cartModel.getQuantity());
                    updateData.put("totalPrice", cartModel.getQuantity() * Float.parseFloat(cartModel.getPrice()));

                    userCart.child(cakeModel.getKey())
                            .updateChildren(updateData)
                            .addOnSuccessListener(avoid -> {
                                iCartLoadListener.onCartLoadFailed("added to cart");

                            })
                            .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                }
                else //if no item in cart
                {
                    CartModel cartModel = new CartModel();
                    //CartModel.setUserName(userModel).getName());
                    cartModel.setName(cakeModel.getName());
                    cartModel.setImage(cakeModel.getImage());
                    cartModel.setKey((cakeModel.getKey()));
                    cartModel.setPrice((cakeModel.getPrice()));
                    cartModel.setQuantity(1);
                    cartModel.setTotal_price(Float.parseFloat(cakeModel.getPrice()));
                    userCart.child(cakeModel.getKey())
                            .setValue(cartModel)
                            .addOnSuccessListener(avoid -> {
                        iCartLoadListener.onCartLoadFailed("added to cart");

                    })
                            .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                }

                EventBus.getDefault().postSticky(new MyUpdateCartEvent());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                iCartLoadListener.onCartLoadFailed(error.getMessage());

            }
        });
    }

    @Override
    public int getItemCount() {
        return cakeModelList.size();
    }

    public class MyEatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imageview)
        ImageView imageview;
        @BindView(R.id.txt_Name)
        TextView txt_Name;
        @BindView(R.id.txt_price)
        TextView txt_price;

        //to add badge in cart
        IRecyclerViewClickListener listener;

        public void setListener(IRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        private Unbinder unbinder;

        public MyEatViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onRecyclerClick(view,getAdapterPosition());
        }
    }
}
