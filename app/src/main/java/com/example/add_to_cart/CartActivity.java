package com.example.add_to_cart;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.add_to_cart.adapter.MyCartAdapter;
import com.example.add_to_cart.listener.ICartLoadListener;
import com.example.add_to_cart.model.CartModel;

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

    ICartLoadListener cartLoadListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();
    }

    private void init()
    {
        ButterKnife.bind(this);
        cartLoadListener =this;

        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclercart.setLayoutManager(layoutManager);
        recyclercart.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        double sum =0;
        for(CartModel cartModel : cartModelList)
        {
            sum+=cartModel.getTotal_price();
        }
        txtcart.setText(new StringBuilder("RS").append(sum));
        MyCartAdapter myCartAdapter = new MyCartAdapter(this, cartModelList);
        recyclercart.setAdapter(adapter);
    }

    @Override
    public void onCartLoadFailed(String message) {

    }
}