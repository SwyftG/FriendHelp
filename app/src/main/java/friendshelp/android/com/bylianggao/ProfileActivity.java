package friendshelp.android.com.bylianggao;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import org.json.JSONArray;

import java.util.List;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private customSuperCircularImageView profileImg;
    private TextView profileName;
    private TextView profileCity;
    private TextView profileFriends;
    private TextView profileHelp;
    private String mName;
    private String mFriend;
    private String mPhotoUrl;
    private String mCity;
    private String mHelp;
    private String mId;

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
                    profileName.setText(mName);
                    profileFriends.setText(mFriend);
                    profileHelp.setText(mHelp);
                    profileImg.setImageURL(mPhotoUrl);
                    profileCity.setText(userCity);

                    View v = View.inflate(ProfileActivity.this, R.layout.nav_header_home, null);
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
                                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_friend) {
                                Intent intent = new Intent(ProfileActivity.this, FriendActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_schedual) {
                                Intent intent = new Intent(ProfileActivity.this, SchedualActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_profile) {

                            } else if (id == R.id.nav_activity) {
                                Intent intent = new Intent(ProfileActivity.this, ActivityActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_setting) {
                                Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
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
                    setFragment();
                    break;
                default:

                    break;
            }
        };
    };

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        ProfileActivityFragment mHomeDetailActivityFragment = new ProfileActivityFragment();
        Bundle bb = new Bundle();
        bb.putString("text", mId);
        mHomeDetailActivityFragment.setArguments(bb);
        transaction.replace(R.id.profile_activity_listView, mHomeDetailActivityFragment);
        transaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileImg = (customSuperCircularImageView) findViewById(R.id.profile_activity_profile_Img);
        profileName = (TextView) findViewById(R.id.profile_activity_profile_Name);
        profileCity = (TextView) findViewById(R.id.profile_activity_profile_City);
        profileFriends = (TextView) findViewById(R.id.profile_activity_profile_Friends);
        profileHelp = (TextView) findViewById(R.id.profile_activity_profile_help);

        findDataInParse();
        findIdFromParse();



        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        FacebookSdk.sdkInitialize(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void findIdFromParse() {

    }

    private void findDataInParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("UserObjectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject user = objects.get(0);
                    String name = user.getString("userName");
                   // String city = user.getString("city");
                    String id = user.getString("userId");
                    JSONArray friendLists = user.getJSONArray("friendList");
                    String friends = String.valueOf(friendLists.length());
                    String help = String.valueOf(user.getInt("help"));
                    String photo_url = user.getString("userPhoto");
                    mName = name;
                    mFriend = friends;
                    mHelp = help;
                    mPhotoUrl = photo_url;
                    mId = id;
                    userCity = user.getString("location");  //******************** city
                    userName = user.getString("userName");
                    userPhotoUrl = user.getString("userPhoto");
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);

                    //user.put("location",);                                *******************************   LOCATION

                } else {
                    Log.i("ssssss", "find User Failed:" + e.toString());
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
            Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_friend) {
            Intent intent = new Intent(ProfileActivity.this,FriendActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_schedual) {
            Intent intent = new Intent(ProfileActivity.this,SchedualActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_activity) {
            Intent intent = new Intent(ProfileActivity.this,ActivityActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(ProfileActivity.this,SettingActivity.class);
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
