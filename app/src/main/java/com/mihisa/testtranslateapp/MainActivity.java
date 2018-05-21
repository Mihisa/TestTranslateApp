package com.mihisa.testtranslateapp;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.mihisa.testtranslateapp.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout =  findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TranslateFragment(), "Translate");
        adapter.addFragment(new HistoryFragment(), "History");
        adapter.addFragment(new FavoriteFragment(), "Favorite");
        viewPager.setAdapter(adapter);
    }

}
