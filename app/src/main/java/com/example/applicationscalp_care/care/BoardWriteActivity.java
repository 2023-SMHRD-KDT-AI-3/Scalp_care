package com.example.applicationscalp_care.care;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.applicationscalp_care.R;
import com.example.applicationscalp_care.databinding.ActivityBoardWriteBinding;

import java.io.ByteArrayOutputStream;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class BoardWriteActivity extends AppCompatActivity {

    private ActivityBoardWriteBinding binding;
    private RequestQueue queue;

    String result = null;

    String writeURL="http://192.168.219.51:8089/Boardsave";

    // 객체 생성
    AppCompatRadioButton yanghobtn, gyungjungbtn, joongdungbtn, joongjungbtn;

    // 앨범 런처
    private ActivityResultLauncher<Intent> albumLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        // 기본 갤러리에서 선택한 이미지를 Uri값으로 가져온 후 ImageView 초기화
                        Intent data = result.getData();
                        Uri imgUri = data.getData();
                        binding.imgContent.setImageURI(imgUri);
                    }
                }
            }
    );
    //카메라 런처
    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                // 캡쳐한 이미지 저장 공간을 접근 후 가져오기
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle bundle = data.getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        binding.imgContent.setImageBitmap(bitmap);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        Intent writeIntent = getIntent();
        if(writeIntent.getStringExtra("result") != null){
            result = writeIntent.getStringExtra("result");
        }
        String imgUri = writeIntent.getStringExtra("img");

        // 검사후 이미지를 가져온다면 이미지 세팅하기
        if(imgUri != null){
            Uri uri = Uri.parse(imgUri);
            binding.imgContent.setImageURI(uri);
        }


        // 게시글 추가페이지에 현재 일자 가져옴
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY년 MM월 dd일 HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        String currentTime = dateFormat.format(currentDate);
        binding.tvBoardTime.setText(currentTime);

        // 초기화 작업
        yanghobtn = findViewById(R.id.yanghobtn);
        gyungjungbtn = findViewById(R.id.gyungjungbtn);
        joongdungbtn = findViewById(R.id.joongdungbtn);
        joongjungbtn = findViewById(R.id.joongjungbtn);

        // 검사 후 온 결과에 따라 클릭해놓기
        if(result != null) {
            if (result.equals("양호")) {
                yanghobtn.performClick();
            } else if (result.equals("경증")) {
                gyungjungbtn.performClick();
            } else if (result.equals("중등도")) {
                joongdungbtn.performClick();
            } else if (result.equals("중증")) {
                joongjungbtn.performClick();
            }
        }


        // 뒤로가기
        binding.tvBack.setOnClickListener(v -> {
            finish();
        });
        binding.imgBack.setOnClickListener(v -> {
            finish();
        });

        queue= Volley.newRequestQueue(this);

        // 저장하기 버튼을 누를시 작동
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ClickEvent","클릭 확인됨");
                Log.d("Base64",Base64.class.getPackage().getImplementationVersion());

                SharedPreferences autoLogin = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
                String ucUid = autoLogin.getString("uid","null");

                // img → bitmap → base64(String)
                String content = binding.edtTvContent.getText().toString();

                BitmapDrawable drawable =(BitmapDrawable)binding.imgContent.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);  // PNG 형식으로 압축
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String base64_img = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.d("BoardWriteActivity",String.valueOf(base64_img.length()));

                // 1/2/3/4로 변환
                int conditionValue = getSelectedConditionValue();

                // 작성 내용 DB저장
                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        writeURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("responseCheck",response);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("responseCheck","errrrrrrrrrrrrrrrrrrror");

                            }
                        }
                ){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("content",content);
                        params.put("img",base64_img);
                        params.put("ucUid",ucUid);
                        params.put("indate",currentTime);
                        params.put("result",result);

                        return params;

                    }
                };
                queue.add(request);

            }

            // 양호/경증/중등도/중증 버튼 중에 클릭 시 1/2/3/4로 변환
            private int getSelectedConditionValue() {
                if (yanghobtn.isChecked()) {
                    result = "양호";
                    return 1;
                } else if (gyungjungbtn.isChecked()) {
                    result = "경증";
                    return 2;
                } else if (joongdungbtn.isChecked()) {
                    result = "중등도";
                    return 3;
                } else if (joongjungbtn.isChecked()) {
                    result = "중증";
                    return 4;
                } else {
                    return 0;
                }
            }
        });

        // activity_board_write.xml에 있는 플러스 이미지 클릭시, 앨범 또는 카메라를 띄우는 기능
        binding.imgContent.setOnClickListener(v -> {
            // 권한 물어보기
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO}, 1);
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("이미지 선택")
                        .setItems(new CharSequence[]{"카메라", "갤러리"},
                                (dialog, which) -> {
                                    switch (which) {
                                        case 0: // 카메라 선택
                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            cameraLauncher.launch(intent);
                                            break;
                                        case 1: // 갤러리 선택
                                            Intent cameraIntent = new Intent(Intent.ACTION_PICK);
                                            cameraIntent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                                            albumLauncher.launch(cameraIntent);
                                            break;
                                    }
                                });
                builder.create().show();

            }
        });


    }

    // 상태 : 양호/경증/중등도/중증 버튼 중에 클릭 시
    public void onRadioButtonClicked(View view) {
        boolean selected = ((AppCompatRadioButton) view).isChecked();

        if (view.getId() == R.id.yanghobtn && selected) {
            // '양호' 버튼을 클릭했을 때 특정 동작 수행
            yanghobtn.setTextColor(Color.WHITE);
            yanghobtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_on));
            gyungjungbtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            gyungjungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
            joongdungbtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            joongdungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
            joongjungbtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            joongjungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
        } else if (view.getId() == R.id.gyungjungbtn && selected) {
            // '경증' 버튼을 클릭했을 때 특정 동작 수행
            gyungjungbtn.setTextColor(Color.WHITE);
            gyungjungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_on));
            yanghobtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            yanghobtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
            joongdungbtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            joongdungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
            joongjungbtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            joongjungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
        } else if (view.getId() == R.id.joongdungbtn && selected) {
            // '중등도' 버튼을 클릭했을 때 특정 동작 수행
            joongdungbtn.setTextColor(Color.WHITE);
            joongdungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_on));
            gyungjungbtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            gyungjungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
            yanghobtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            yanghobtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
            joongjungbtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            joongjungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
        } else if (view.getId() == R.id.joongjungbtn && selected) {
            // '중증' 버튼을 클릭했을 때 특정 동작 수행
            joongjungbtn.setTextColor(Color.WHITE);
            joongjungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_on));
            gyungjungbtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            gyungjungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
            joongdungbtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            joongdungbtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
            yanghobtn.setTextColor(ContextCompat.getColor(this, R.color.lightgreen));
            yanghobtn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.sangtae_btn_off));
        }
    }

    public void checkPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

    }
}