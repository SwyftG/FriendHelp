package friendshelp.android.com.bylianggao;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SettingFragment.Callbacks{

    private List<FFService> serviceList, finalServiceList;
    private Button readyButton;

    private ParseUser PUser;
    private JSONArray myArray = new JSONArray();
    private CircularImageView profileImg;
    private TextView profileName;
    private TextView profileCity;
    private String urlstring;
    private String mName;
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
                    showImage();
                    break;
                case 2:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    profileImg.setImageBitmap(bitmap);
                    break;
                case 3:
                    View v = View.inflate(SettingActivity.this, R.layout.nav_header_home, null);
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navImg = (customSuperCircularImageView) v.findViewById(R.id.navbar_profile_img);
                    navName = (TextView) v.findViewById(R.id.navbar_profile_name);
                    navCity = (TextView) v.findViewById(R.id.navbar_profile_city);
                    profileCity.setText(userCity);
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
                                Intent intent = new Intent(SettingActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_friend) {
                                Intent intent = new Intent(SettingActivity.this,FriendActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_schedual) {
                                Intent intent = new Intent(SettingActivity.this,SchedualActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_profile) {
                                Intent intent = new Intent(SettingActivity.this,ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_activity) {
                                Intent intent = new Intent(SettingActivity.this,ActivityActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (id == R.id.nav_setting) {

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
        }
    };

    private void showImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlstring);
                    Log.i("ssssss", "urlstring::" + urlstring);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    if(code == 200){
                        InputStream is = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);

                        Message msg = Message.obtain();
                        msg.what = 2;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }else{
                        Log.i("sssssss", "img handle failed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        FacebookSdk.sdkInitialize(getApplicationContext());

        findIdFromParse();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();




        PUser = ParseUser.getCurrentUser();
        profileImg = (CircularImageView) findViewById(R.id.setting_activity_photo);
        profileName = (TextView) findViewById(R.id.setting_activity_name);
        profileCity = (TextView) findViewById(R.id.setting_activity_city);

        setProfileImg();



        serviceList = new ArrayList<>();
        finalServiceList = new ArrayList<>();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new SettingFragment();
        fm.beginTransaction().add(R.id.setting_activity_listView,fragment).commit();
        readyButton = (Button) findViewById(R.id.setting_activity_ready_Button);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "";

                for (int i = 0; i < serviceList.size(); i++) {
                    FFService service = serviceList.get(i);

                    text = text + service.getPrice() + "\n";
                }
                filterList();
                addServiceListToUser();

               Toast.makeText(getApplicationContext(), "Finished", Toast.LENGTH_SHORT).show();
            }
        });
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
                    msg.what = 3;
                    handler.sendMessage(msg);
                } else {
                    Log.i("sssssss", "img failed " + e.toString());
                }

            }
        });
    }

    public void filterList() {
        for(int i=0;i<serviceList.size();i++){
            if(!serviceList.get(i).getPrice().equals("0#00")){
                finalServiceList.add(serviceList.get(i));
            }
        }
    }

    public void addServiceListToUser() {

        for (int i = 0; i < finalServiceList.size(); i++) {
            String name = finalServiceList.get(i).getName();
            String price = getFinalServiceListPrice(i);
            JSONObject serviceObj = new JSONObject();
            try {
                serviceObj.put("name", name);
                serviceObj.put("price", price);
                myArray.put(serviceObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("UserObjectId", PUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject user = objects.get(0);
                    user.remove("serviceList");
                    user.put("serviceList", myArray);
                    //user.put("location",);                                *******************************   LOCATION
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("ssssss", "service List success!!");

                            } else {
                                //Log.i("ssssss", "service List failed: " + e.toString());
                            }
                        }
                    });
                } else {
                    Log.i("ssssss", "find User Failed:" + e.toString());
                }
            }
        });
    }

    public String getFinalServiceListPrice(int j) {
        String price = finalServiceList.get(j).getPrice();
        char[] priceChar = price.toCharArray();
        for (int i = 0; i < priceChar.length; i++) {
            if (priceChar[i] == '#') {
                priceChar[i] = '.';
            }
        }
        return new String(priceChar);
    }

    public void setProfileImg(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("UserObjectId", PUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("sssssss", "img get success!!");
                    ParseObject object = objects.get(0);
                    urlstring = object.getString("userPhoto");
                    mName = object.getString("userName");
                    Log.i("sssssss", "img get success!!:::" + urlstring);
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
            Intent intent = new Intent(SettingActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_friend) {
            Intent intent = new Intent(SettingActivity.this,FriendActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_schedual) {
            Intent intent = new Intent(SettingActivity.this,SchedualActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(SettingActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_activity) {
            Intent intent = new Intent(SettingActivity.this,ActivityActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onButtonClicked(List<FFService> list) {
        serviceList = list;
    }
}
