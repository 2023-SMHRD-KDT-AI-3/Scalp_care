package com.example.applicationscalp_care.care;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.applicationscalp_care.CareFragment;
import com.example.applicationscalp_care.R;
import com.example.applicationscalp_care.databinding.ActivityBoardInsideBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class BoardInsideActivity extends AppCompatActivity {

    private ActivityBoardInsideBinding binding;

    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardInsideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        queue= Volley.newRequestQueue(this);

        Log.d("test","여기 안옴?");

        Intent data = getIntent();

        String indate = data.getStringExtra("indate");
        String content = data.getStringExtra("content");
        Long ucNum = data.getLongExtra("ucNum",0);

        Log.d("test11",String.valueOf(ucNum));

        StringRequest request = new StringRequest(
                Request.Method.POST,
                "http://192.168.219.50:8089/getImage",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("testBase64",String.valueOf(response.length()));
                        byte[] decodedBytes = Base64.decode(response, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        binding.imgContent.setImageBitmap(bitmap);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("test","통신 실패임?");
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ucNum", String.valueOf(ucNum));
                return params;
            }

        };
        queue.add(request);

        binding.tvInsideTime.setText(indate);
        binding.tvInsideContent.setText(content);

        // 뒤로가기
        binding.tvBack2.setOnClickListener(v ->{
            finish();
        });
        binding.imgBack2.setOnClickListener(v ->{
            finish();
        });
    }
}

