package com.example.add_to_cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.add_to_cart.adapter.MyCartAdapter;
import com.example.add_to_cart.eventbus.MyUpdateCartEvent;
import com.example.add_to_cart.listener.ICartLoadListener;
import com.example.add_to_cart.model.CartModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements ICartLoadListener {

    @BindView(R.id.cart_recycler)
    RecyclerView recyclercart;
    @BindView(R.id.mainlayout)
    RelativeLayout mainlayout;
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.txt_cart)
    TextView txtcart;
    @BindView((R.id.txt_next))
     ImageView txt_next;
    ICartLoadListener cartLoadListener;
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {

        if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event)
    {
        loadCartFromFirebase();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //getActionBar().hide();

        init();
        loadCartFromFirebase();

    }

    private void loadCartFromFirebase() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Cart").child("UNIQUE_USER_ID").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for(DataSnapshot cartSnapshot: snapshot.getChildren())
                            {
                                CartModel cartModel= cartSnapshot.getValue(CartModel.class);
                                cartModel.setKey(cartSnapshot.getKey());
                                cartModels.add(cartModel);
                            }
                            cartLoadListener.onCartLoadSuccess(cartModels);
                        }
                        else
                        {
                            cartLoadListener.onCartLoadFailed("Cart Empty");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }



    private void init()
    {
        ButterKnife.bind(this);
        cartLoadListener =this;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclercart.setLayoutManager(layoutManager);
        recyclercart.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        btnBack.setOnClickListener(v -> finish());
        txt_next.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, Confirm_order.class));
                });

    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        double sum =0;
        for(CartModel cartModel : cartModelList)
        {
            sum+=cartModel.getTotal_price();
        }
        txtcart.setText(new StringBuilder("Rs: ").append(sum));
        MyCartAdapter myCartAdapter = new MyCartAdapter(this, cartModelList);
        recyclercart.setAdapter(myCartAdapter);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainlayout,message,Snackbar.LENGTH_LONG).show();

    }
}