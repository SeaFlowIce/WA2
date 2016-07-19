package android.galgame.white_album_2.wa2;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by SeaFlow on 2016/7/16.
 */
public class Wa2Activity extends AppCompatActivity {

    private ViewPager mViewPager;
    private Integer position;
    private Integer[] imgData=new Integer[]{R.drawable.wa2_wa2,R.drawable.wa2_girls,R.drawable.wa2_first,R.drawable.wa2_sec,R.drawable.wa2_last};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wa2);

        position=getIntent().getIntExtra("position",0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("白色相簿2");

        ImageView ivImage = (ImageView)findViewById(R.id.ivImage);
        ivImage.setImageResource(imgData[position]);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager,position);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        if (position == 0) {
            Log.e("tabLayout","addTab"+position);
            tabLayout.addTab(tabLayout.newTab().setText("脱宅神器"));
            tabLayout.addTab(tabLayout.newTab().setText("丸户老贼"));
            tabLayout.addTab(tabLayout.newTab().setText("白学"));
        }else{
            //TODO setSelected和setCurrentItem缺一不可
            switch (position){
                case 2:
                    Log.e("tabLayout","addTab"+position);
                    tabLayout.addTab(tabLayout.newTab().setText("序章"));
                    tabLayout.addTab(tabLayout.newTab().setText("终章"));
                    tabLayout.addTab(tabLayout.newTab().setText("最终章"));
                    break;
                case 3:
                    Log.e("tabLayout","addTab"+position);
                    tabLayout.addTab(tabLayout.newTab().setText("序章"),false);
                    tabLayout.addTab(tabLayout.newTab().setText("终章"),true);
                    tabLayout.addTab(tabLayout.newTab().setText("最终章"),false);
                    break;
                case 4:
                    Log.e("tabLayout","addTab"+position);
                    tabLayout.addTab(tabLayout.newTab().setText("序章"),false);
                    tabLayout.addTab(tabLayout.newTab().setText("终章"),false);
                    tabLayout.addTab(tabLayout.newTab().setText("最终章"),true);
                    break;
            }

        }

        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(position-2);
    }

    private void setupViewPager(ViewPager mViewPager,Integer position) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        if (position == 0){
            adapter.addFragment(DetailFragment.newInstance(getAsset("book_content.txt")), "脱宅神器");
            adapter.addFragment(DetailFragment.newInstance(getAsset("book_author.txt")), "丸户老贼");
            adapter.addFragment(DetailFragment.newInstance(getAsset("book_menu.txt")), "白学");
        }else{
            adapter.addFragment(DetailFragment.newInstance(getAsset("book_fir.txt")), "序章");
            adapter.addFragment(DetailFragment.newInstance(getAsset("book_sec.txt")), "终章");
            adapter.addFragment(DetailFragment.newInstance(getAsset("book_las.txt")), "最终章");
        }

        mViewPager.setAdapter(adapter);

    }

    private String getAsset(String fileName) {
        AssetManager am = getResources().getAssets();
        InputStream is = null;
        try {
            is = am.open(fileName, AssetManager.ACCESS_BUFFER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Scanner(is).useDelimiter("\\Z").next();
    }


    static class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}

