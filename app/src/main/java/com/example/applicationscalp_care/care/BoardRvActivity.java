package com.example.applicationscalp_care.care;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.applicationscalp_care.R;
import com.example.applicationscalp_care.databinding.ActivityBoardInsideBinding;
import com.example.applicationscalp_care.databinding.ActivityBoardRvBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class BoardRvActivity extends AppCompatActivity {

    private ActivityBoardRvBinding binding;
    String boradviewURL = "http://192.168.219.52:8089/Boardview";
    private ArrayList<BoardVO> dataset = null;
    private ArrayList<String> keyset = null;
    private BoardAdapter adapter = null;
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardRvBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 초기화 작업
        dataset = new ArrayList<>();
        keyset = new ArrayList<>();
        adapter = new BoardAdapter( dataset, keyset );

        if (queue == null) {
            queue= Volley.newRequestQueue(this);
        }


        getBoardData();

        // RecyclerView를 초기화하고, 레이아웃 매니저를 설정하고, 어댑터를 연결하여 화면에 데이터를 표시하는 기능
        binding.BoardRvCompare.setLayoutManager(new LinearLayoutManager(this));
        binding.BoardRvCompare.setAdapter(adapter);

    }


    // 게시판 출력
    public void getBoardData() {
        Log.d("ScalpCompareActivity","데이터 가져올래요!");
        StringRequest request = new StringRequest(
                Request.Method.POST,
                boradviewURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ScalpCompareActivity",response);
                        try {
                            // JsonArray(List<String>)
                            JSONArray jsonArray = new JSONArray(response);
                            Log.d("ScalpCompareActivity", jsonArray.toString());

                            // dataset.clear();

                            // 파싱한 데이터를 데이터셋에 추가
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Log.d("ScalpCompareActivity","여기까진 오는건가?");

                                String JsonItemString = jsonArray.getString(i);
                                // Json(String) → 객체화
                                JSONObject jsonObject = new JSONObject(JsonItemString);

                                Log.d("ScalpCompareActivity", jsonObject.toString());

                                // 각 필요한 데이터를 추출
                                String indate = BoardRvActivity.this.formatIndate(jsonObject.getString("indate"));
                                String content = jsonObject.getString("content");
                                int uc_num = jsonObject.getInt("ucNum");

                                // 데이터셋에 추가
                                dataset.add(new BoardVO(uc_num, indate, content));

                            }

                            // 어댑터에 데이터셋 변경을 알림
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ScalpCompareActivity","여기 문제있어요2222!");
            }
        }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences autoLogin = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
                String ucUid = autoLogin.getString("uid","null");
                Log.d("ScalpCompareActivity",ucUid);

                Map<String, String> params = new HashMap<>();
                params.put("ucUid",ucUid);
                return params;

            }
        };
        queue.add(request);
    }

    // 어댑터에서 사용할 날짜 형식 메서드
    private String formatIndate(String indateString) {
        try {
            long indateValue = Long.parseLong(indateString);
            Date indateDate = new Date(indateValue);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            return sdf.format(indateDate);
        } catch (NumberFormatException e) {
            Log.e("CareActivity", "날짜 파싱 오류: " + e.getMessage());
            return indateString; // 변환이 실패하면 원본 문자열 반환
        }
    }

}