package com.example.applicationscalp_care;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.applicationscalp_care.care.BoardAdapter;
import com.example.applicationscalp_care.care.BoardVO;
import com.example.applicationscalp_care.care.BoardWriteActivity;
import com.example.applicationscalp_care.databinding.FragmentCareBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CareFragment extends Fragment {

    // 변수 선언
    private FragmentCareBinding binding = null;

    private ArrayList<BoardVO> dataset = null;
    private ArrayList<String> keyset = null;
    private BoardAdapter adapter = null;

    private RequestQueue queue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 초기화 작업
        binding = FragmentCareBinding.inflate(inflater, container, false);
        dataset = new ArrayList<>();
        keyset = new ArrayList<>();
        adapter = new BoardAdapter( dataset, keyset );
        if (queue == null) {
            queue = Volley.newRequestQueue(requireContext());
        }

        getBoardData();

        // RecyclerView를 초기화하고, 레이아웃 매니저를 설정하고, 어댑터를 연결하여 화면에 데이터를 표시하는 기능
        binding.BoardRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.BoardRv.setAdapter(adapter);

        // 작동 버튼 클릭시 BoardWriteActivity로 이동하는 기능 구현
        binding.btnAddBoard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BoardWriteActivity.class);
            startActivity(intent);
        });
        // root 리턴
        return binding.getRoot();




    }

    // 정보 가져와야함
    public void getBoardData() {
        Log.d("CareActivity","데이터 가져올래요!");
        StringRequest request = new StringRequest(
                Request.Method.POST,
                "http://192.168.219.50:8089/Boardview",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CareActivity","데이터 가져오나 확인 : " + response);
                        try {
                            if(response == null){
                                binding.errorImg.setVisibility(View.VISIBLE);
                            }else{
                                binding.errorImg.setVisibility(View.GONE);
                            }
                            // JsonArray(List<String>)
                            JSONArray jsonArray = new JSONArray(response);
                            Log.d("qwer1", jsonArray.toString());

                            // 파싱한 데이터를 데이터셋에 추가
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Log.d("CareActivity","여기까진 오는건가?");

                                String JsonItemString = jsonArray.getString(i);
                                // Json(String) → 객체화
                                JSONObject jsonObject = new JSONObject(JsonItemString);

                                Log.d("qwer2", jsonObject.toString());

                                // 각 필요한 데이터를 추출
                                String indate = jsonObject.getString("indate");
                                String content = jsonObject.getString("content");
                                String img = jsonObject.getString("img");

                                // 데이터셋에 추가
                                dataset.add(new BoardVO(indate, content, img));



                            }

                            // 어댑터에 데이터셋 변경을 알림
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.d("CareActivity","여기 문제있어요1111!");
                            Log.d("CareActivity",e.toString());
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CareActivity","여기 문제있어요2222!");
            }
        }
        );
        queue.add(request);

    }

}