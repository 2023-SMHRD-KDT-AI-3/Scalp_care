package com.example.applicationscalp_care;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.applicationscalp_care.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private BottomNavigationView bnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bnv = this.findViewById(R.id.bnv);

        // 실행 할 때 바로 홈 화면 나올 수 있게
        getSupportFragmentManager().beginTransaction().replace(R.id.fl, new HomeFragment()).commit();

        bnv.setSelectedItemId(R.id.home);

        // bnv 바 아이콘 클릭 시
        binding.bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // 클릭한 item 정보(속성, id, title...)
                if(R.id.home == item.getItemId()) {
                    getSupportFragmentManager().beginTransaction().replace(
                            // 1) 어디에
                            R.id.fl,
                            // 2) 어떤 프래그먼트
                            new HomeFragment()
                    ).commit();
                }else if(R.id.gwanlee == item.getItemId()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl, new CareFragment()).commit();
                }else if(R.id.gumsa == item.getItemId()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl, new TestFragment()).commit();
                }else if(R.id.jungbo == item.getItemId()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl, new InformationFragment()).commit();
                }else if(R.id.duhbogi == item.getItemId()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl, new MoreSettingFragment()).commit();
                }
                return true;
            }
        });

        // Home Activity에서 원하는 Fragment로 이동할 때
        String moveFl = getIntent().getStringExtra("moveFl");
        if(moveFl!=null) {
            if (moveFl.equals("home")) {
                getSupportFragmentManager().beginTransaction().replace(
                        // 1) 어디에
                        R.id.fl,
                        // 2) 어떤 프래그먼트
                        new HomeFragment()
                ).commit();
                bnv.setSelectedItemId(R.id.home);
            } else if (moveFl.equals("care")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl, new CareFragment()).commit();
                bnv.setSelectedItemId(R.id.gwanlee);
            } else if (moveFl.equals("test")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl, new HomeFragment()).commit();
            } else if (moveFl.equals("info")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl, new HomeFragment()).commit();
            } else if (moveFl.equals("moreset")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl, new HomeFragment()).commit();
            }
        }


    }
}