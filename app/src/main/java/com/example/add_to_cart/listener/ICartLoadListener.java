package com.example.add_to_cart.listener;

import com.example.add_to_cart.model.CakeModel;
import com.example.add_to_cart.model.CartModel;

import java.util.List;

public interface ICartLoadListener {
    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);
}
