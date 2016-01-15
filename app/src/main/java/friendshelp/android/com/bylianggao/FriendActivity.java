package friendshelp.android.com.bylianggao;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class FriendActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FriendActivityFragment mFriendActivityFragment;
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
                    View v = View.inflate(FriendActivity.this, R.layout.nav_header_home, null);
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
                                Intent intent = new Intent(FriendActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_friend) {

                            } else if (id == R.id.nav_schedual) {
                                Intent intent = new Intent(FriendActivity.this,SchedualActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_profile) {
                                Intent intent = new Intent(FriendActivity.this,ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_activity) {
                                Intent intent = new Intent(FriendActivity.this,ActivityActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_setting) {
                                Intent intent = new Intent(FriendActivity.this,SettingActivity.class);
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
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        FacebookSdk.sdkInitialize(getApplicationContext());

        findIdFromParse();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mFriendActivityFragment = new FriendActivityFragment();
        transaction.replace(R.id.friend_activity_content_listview, mFriendActivityFragment);
        transaction.commit();
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
            Intent intent = new Intent(FriendActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_friend) {

        } else if (id == R.id.nav_schedual) {
            Intent intent = new Intent(FriendActivity.this,SchedualActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(FriendActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_activity) {
            Intent intent = new Intent(FriendActivity.this,ActivityActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(FriendActivity.this,SettingActivity.class);
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
