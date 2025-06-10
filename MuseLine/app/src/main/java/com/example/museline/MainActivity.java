package com.example.museline;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.museline.utils.ThemeUtils;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private SwitchCompat themeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        findViewById(R.id.nav_home).setOnClickListener(v ->
                navController.navigate(R.id.homeFragment));

        findViewById(R.id.nav_search).setOnClickListener(v ->
                navController.navigate(R.id.searchFragment));

        findViewById(R.id.nav_favorite).setOnClickListener(v ->
                navController.navigate(R.id.favoriteFragment));

        setupThemeSwitch();
    }

    private void setupThemeSwitch() {
        themeSwitch = findViewById(R.id.theme_switch);

        boolean isDarkMode = ThemeUtils.isDarkMode(this);
        themeSwitch.setChecked(isDarkMode);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeUtils.setDarkMode(this, isChecked);
        });
    }
}

