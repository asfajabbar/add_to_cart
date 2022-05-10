package com.example.add_to_cart;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.add_to_cart.adapter.MyEatAdapter;
import com.example.add_to_cart.eventbus.MyUpdateCartEvent;
import com.example.add_to_cart.listener.ICartLoadListener;
import com.example.add_to_cart.listener.IEatLoadListener;
import com.example.add_to_cart.model.CakeModel;
import com.example.add_to_cart.model.CartModel;
import com.example.add_to_cart.utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements IEatLoadListener, ICartLoadListener {

    @BindView(R.id.food_recycler)
    RecyclerView recyclerCake;
    @BindView(R.id.mainlayout)
    RelativeLayout mainlayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btn_cart)
    FrameLayout btn_cart;


    IEatLoadListener eatLoadListener;
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
        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        loadCakeFromFirebase();
        countCartItem();
        //getActionBar().hide();
    }

    private void loadCakeFromFirebase() {

        List<CakeModel> cakeModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Baskin Robins")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot eatSnapshot : snapshot.getChildren()) {
                                CakeModel cakeModel = eatSnapshot.getValue(CakeModel.class);
                                cakeModel.setKey(eatSnapshot.getKey());
                                cakeModels.add(cakeModel);
                            }
                            eatLoadListener.onEatLoadSuccess(cakeModels);
                        } else
                            eatLoadListener.onEatLoadFailed("CANT FIND");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        eatLoadListener.onEatLoadFailed(error.getMessage());
                    }
                });
    }

    private void init() {
        ButterKnife.bind(this);
        eatLoadListener = this;
        cartLoadListener = this;


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerCake.setLayoutManager(linearLayoutManager);
        recyclerCake.addItemDecoration(new SpaceItemDecoration());

        btn_cart.setOnClickListener(v-> {
            startActivity(new Intent(this, CartActivity.class));
        });
    }

    @Override
    public void onEatLoadSuccess(List<CakeModel> cakeModelList) {
        MyEatAdapter adapter = new MyEatAdapter(this, cakeModelList, cartLoadListener);
        recyclerCake.setAdapter(adapter);
    }

    @Override
    public void onEatLoadFailed(String message) {
        Snackbar.make(mainlayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int cartSum = 0;
        for (CartModel cartModel : cartModelList)
            cartSum += cartModel.getQuantity();
        badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainlayout, message, Snackbar.LENGTH_LONG).show();

    }


    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase
                .getInstance().getReference("cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                               CartModel cartModel= cartSnapshot.getValue(CartModel.class);
                               cartModel.setKey(cartSnapshot.getKey());
                               cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
}

