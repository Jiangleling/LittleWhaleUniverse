package com.example.littlewhaleuniverse;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_diary) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DiaryFragment())
                        .commit();
                return true;
            } else if (id == R.id.nav_ai) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AiFragment())
                        .commit();
                return true;
            } else if (id == R.id.nav_universe) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new UniverseFragment())
                        .commit();
                return true;
            }
            return false;
        });

        bottomNav.setSelectedItemId(R.id.nav_diary);
    }
}
