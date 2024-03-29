package com.example.applicationscalp_care;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.applicationscalp_care.databinding.ActivityLoginBinding;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    private RequestQueue queue;

    String loginURL =  "http://192.168.219.51:8089/join";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue= Volley.newRequestQueue(this);

        Log.d("LoginActivity","진입 성공");

        // Kakao KeyHash 확인
        String keyHash = Utility.INSTANCE.getKeyHash(this);
        Log.d("KeyHash: ", keyHash);

        // 카카오톡이 설치되어 있는지 확인하는 메서드 , 카카오에서 제공함. 콜백 객체를 이용합.
        Function2<OAuthToken,Throwable, Unit> callback =new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            // 콜백 메서드 ,
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                Log.e(TAG,"CallBack Method " + oAuthToken);

                //oAuthToken != null 이라면 로그인 성공
                if(oAuthToken!=null){
                    // 토큰이 전달된다면 로그인이 성공한 것이고 토큰이 전달되지 않으면 로그인 실패한다.
                    saveuser();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("moveFl","home");
                    startActivity(intent);
                    finish();
                }else {
                    //로그인 실패
                    Log.e(TAG, "invoke: login fail" );
                }
                return null;
            }
        };

        // 카카오 버튼 클릭 시 팝업창
        binding.btnLoginkakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(LoginActivity.this).setTitle("'Kakao'을(를) 사용하여 로그인하려고 합니다.").setMessage("사용자에 관한 정보를 앱 및 웹 사이트가 공유하게 됩니다.")
                        .setPositiveButton("계속", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 해당 기기에 카카오톡이 설치되어 있는 확인
                                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)){
                                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                                }else{
                                    // 카카오톡이 설치되어 있지 않다면
                                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                                }
                                saveuser();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(LoginActivity.this.getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                saveuser();

            }
        });

        // 네이버 로그인
        binding.btnLoginNaver.setOnClickListener(v -> {
            Toast.makeText(this, "준비중입니다.", Toast.LENGTH_SHORT).show();
        });

        // 구글 로그인
        binding.btnLoginGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "준비중입니다.", Toast.LENGTH_SHORT).show();
        });

        // 비회원 로그인
        binding.btnLoginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = "guest";
                String name = "guest";
                String email = "guest@guest.com";
                String classes = "guest";
                String img = "guest";

                insertLogin(uid,name,classes,email);

                SharedPreferences autoLogin = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLoginEdit = autoLogin.edit();
                autoLoginEdit.putString("uid",uid);
                autoLoginEdit.putString("email",email);
                autoLoginEdit.putString("name",name);
                autoLoginEdit.putString("img",img);
                autoLoginEdit.commit();

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }

    private void insertLogin(String uid, String name, String classes, String email){
        Log.d("LoginActivity","DB저장 함수 실행");
        Log.d("qwer",uid);
        Log.d("qwer",name);
        Log.d("qwer",classes);
        Log.d("qwer",email);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                loginURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responseCheck",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("responseCheck","이미 등록된 회원입니다.");

                    }
                }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("m_uid",uid);
                params.put("m_name",name);
                params.put("m_class",classes);
                params.put("m_email",email);

                return params;

            }
        };
        queue.add(request);
    }

    private void saveuser() {
        // 유저 정보 어플에 저장 및 자동 로그인
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {

                if (user != null) {
                    // 유저의 아이디
                    String uid = user.getId().toString();
                    // 유저의 이메일
                    String email = user.getKakaoAccount().getEmail().toString();
                    // 유저의 이름(닉네임)
                    String name = user.getKakaoAccount().getProfile().getNickname().toString();

                    // 로그인 분류
                    String classes = "kakao";

                    // 유저 이미지
                    String img = user.getKakaoAccount().getProfile().getThumbnailImageUrl().toString();

                    // 유저 정보 DB 저장
                    insertLogin(uid,name,classes,email);

                    // 유저 정보 세션 저장
                    SharedPreferences autoLogin = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor autoLoginEdit = autoLogin.edit();
                    autoLoginEdit.putString("uid",uid);
                    autoLoginEdit.putString("email",email);
                    autoLoginEdit.putString("name",name);
                    autoLoginEdit.putString("img",img);
                    autoLoginEdit.commit();

                } else {

                }

                return null;
            }
        });

    }

}