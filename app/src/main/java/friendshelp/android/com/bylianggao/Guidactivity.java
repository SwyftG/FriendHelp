package friendshelp.android.com.bylianggao;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;

public class Guidactivity extends AppCompatActivity {

    private int[] layouts = {R.layout.guid_screen_1, R.layout.guid_screen_2, R.layout.guid_screen_3};
    private ImageView getStartBottomImageview;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    public void initView() {
        setContentView(R.layout.activity_guidactivity);
        ViewPager slashViewPager = (ViewPager) findViewById(R.id.slashViewPager);
        slashViewPager.setAdapter(new MyPagerAdapter(this.getSupportFragmentManager()));
        CirclePageIndicator titleIndicator = (CirclePageIndicator)findViewById(R.id.slashViewPagerIndicator);
        titleIndicator.setViewPager(slashViewPager);
        getStartBottomImageview = (ImageView) findViewById(R.id.Describe_get_start_imageView);
        getStartBottomImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guidactivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return MyFragment.newInstance(layouts[position]);
        }

        @Override
        public int getCount() {
            return layouts.length;
        }
    }

    public static class MyFragment extends android.support.v4.app.Fragment{
        int resId;
        public MyFragment() {

        }
        public static MyFragment newInstance(int resId) {
            MyFragment gaoFragment = new MyFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("resId", resId);
            gaoFragment.setArguments(bundle);
            return gaoFragment;
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            resId = this.getArguments().getInt("resId");
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(resId, container, false);
            return view;
        }
    }

}
