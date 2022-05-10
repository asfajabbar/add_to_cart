package com.example.add_to_cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.NotNull;

import org.w3c.dom.Text;

public class Confirm_order extends AppCompatActivity {


    EditText address, city, postal;
    Button confirm;
    Animation topAnim;
    ImageView lock_img;
    TextView confirm_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        //getActionBar().hide();
        lock_img= findViewById(R.id.lock);
        confirm_txt= findViewById(R.id.con_txt);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        postal = findViewById(R.id.postal);
        confirm = findViewById(R.id.conbtn);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        lock_img.setAnimation(topAnim);
        confirm_txt.setAnimation(topAnim);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Caddress = address.getText().toString().trim();

                if (TextUtils.isEmpty(Caddress)) {
                    address.setError("Enter Email");
                    return;
                }

                String Ccity = city.getText().toString().trim();

                if (TextUtils.isEmpty(Caddress)) {
                    city.setError("Enter Email");
                    return;
                }
                String Cpostal = postal.getText().toString().trim();

                if (TextUtils.isEmpty(Cpostal)) {
                    postal.setError("Enter Email");
                    return;
                }
                if(!(TextUtils.isEmpty(Caddress)) && !(TextUtils.isEmpty(Caddress)) && !(TextUtils.isEmpty(Cpostal)) )
                {

                    Toast.makeText(Confirm_order.this, "Order Placed!.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}
