package com.example.add_to_cart.listener;

import com.example.add_to_cart.model.CakeModel;

import java.util.List;

public interface IEatLoadListener {
    void onEatLoadSuccess(List<CakeModel> cakeModelList);
    void onEatLoadFailed(String message);
}
