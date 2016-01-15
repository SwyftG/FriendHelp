package friendshelp.android.com.bylianggao;


import android.app.FragmentTransaction;
import android.os.Handler;
import android.os.Message;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int activityNum ;
    private HomeNullFragment mHomeNullFragment;
    private HomeFragment mHomeFragment;
    private String userId;

    private customSuperCircularImageView navImg;
    private TextView navName;
    private TextView navCity;
    private String userName;
    private String userCity;
    private String userPhotoUrl;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    getActivityNum();
                    View v = View.inflate(HomeActivity.this, R.layout.nav_header_home, null);
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navImg = (customSuperCircularImageView) v.findViewById(R.id.navbar_profile_img);
                    navName = (TextView) v.findViewById(R.id.navbar_profile_name);
                    navCity = (TextView) v.findViewById(R.id.navbar_profile_city);
                    navImg.setImageURL(userPhotoUrl);
                    navName.setText(userName);
                    navCity.setText(userCity);
                    navigationView.addHeaderView(v);
                    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem item) {
                            // Handle navigation view item clicks here.
                            int id = item.getItemId();

                            if (id == R.id.nav_home) {

                            } else if (id == R.id.nav_friend) {
                                Intent intent = new Intent(HomeActivity.this,FriendActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_schedual) {
                                Intent intent = new Intent(HomeActivity.this,SchedualActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_profile) {
                                Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_activity) {
                                Intent intent = new Intent(HomeActivity.this,ActivityActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_setting) {
                                Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_logout) {
                                LoginManager.getInstance().logOut();
                                finish();
                            }

                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            return true;
                        }
                    });

                    break;
                case 2:
                           //*******************************
                    if (activityNum == 0) {
                        Log.i("sssss", "00 " + activityNum + "!");
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        mHomeNullFragment = new HomeNullFragment();
                        transaction.replace(R.id.home_activity_content_fragment, mHomeNullFragment);
                        transaction.commit();
                    } else {
                        Log.i("sssss", "11" + activityNum + "!");
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        mHomeFragment = new HomeFragment();
                        Bundle bb = new Bundle();
                        bb.putString("text", userId);
                        mHomeFragment.setArguments(bb);
                        transaction.replace(R.id.home_activity_content_fragment, mHomeFragment);
                        transaction.commit();
                    }
                    break;

                default:
                    break;
            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        FacebookSdk.sdkInitialize(getApplicationContext());


        ParseInstallation.getCurrentInstallation().saveInBackground();
        findIdFromParse();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.home_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SchedualActivity.class);
                startActivity(intent);
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();





    }

    private void getNavNameCiyPhotoFromParse() {

    }

    private void findIdFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("UserObjectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("sssssss", "img get success!!");
                    ParseObject object = objects.get(0);
                    userId = object.getString("userId");
                    userCity = object.getString("location");  //******************** city
                    userName = object.getString("userName");
                    userPhotoUrl = object.getString("userPhoto");
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } else {
                    Log.i("sssssss", "img failed " + e.toString());
                }

            }
        });
    }

    public void getActivityNum() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFActivity");
        query.whereEqualTo("fromId", userId);
        ArrayList<Integer> state = new ArrayList<>();
        state.add(1);
        state.add(2);
        query.whereEqualTo("state","1");
        Log.i("sssssss", "ff:id:: " + userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if(objects.size() == 0) {
                        activityNum = 0;
                    } else {
                        Log.i("sssssss", "img get success!!");
                        ParseObject object = objects.get(0);
                        activityNum = objects.size();
                    }
                    Log.i("sssss", "parse" + activityNum + "!!!!!!");
                    Message msg = Message.obtain();
                    msg.what = 2;
                    handler.sendMessage(msg);

                } else {
                    Log.i("sssssss", "img failed " + e.toString());
                }

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_friend) {
            Intent intent = new Intent(HomeActivity.this,FriendActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_schedual) {
            Intent intent = new Intent(HomeActivity.this,SchedualActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_activity) {
            Intent intent = new Intent(HomeActivity.this,ActivityActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
