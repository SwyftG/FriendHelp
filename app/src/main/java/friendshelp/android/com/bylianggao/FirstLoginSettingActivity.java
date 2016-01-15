package friendshelp.android.com.bylianggao;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FirstLoginSettingActivity extends AppCompatActivity implements FirstLoginSettingFragment.Callbacks {

    private LocationManager locationManager;
    private String provider;

    private List<FFService> serviceList, finalServiceList;
    private Button readyButton;
    private ParseUser PUser;
    private JSONArray myArray = new JSONArray();
    private CircularImageView profileImg;
    private TextView profileName;
    private String urlstring;
    private String mName;
    private String mCity;
    private TextView profileCity;
    private Handler handler = new Handler() {
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
                    mCity = (String) msg.obj;
                    profileCity.setText(mCity);
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
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);

                        Message msg = Message.obtain();
                        msg.what = 2;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    } else {
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
        setContentView(R.layout.activity_first_login_setting);
        profileCity = (TextView) findViewById(R.id.first_login_setting_location_text);
        serviceList = new ArrayList<>();
        finalServiceList = new ArrayList<>();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new FirstLoginSettingFragment();
        fm.beginTransaction().add(R.id.first_login_setting_mainFragment, fragment).commit();

        PUser = ParseUser.getCurrentUser();
        Log.i("ssssss", PUser.getObjectId() + "**FirstLocinActivity");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> providerList = locationManager.getProviders(true);

        // select one way to get the location
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "No location provider to use", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i("sssssss", "provider:" + provider);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            getCityNameFromInt(location);
        }
        locationManager.requestLocationUpdates(provider, 5000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getCityNameFromInt(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });



        profileImg = (CircularImageView) findViewById(R.id.first_login_setting_profile_img);
        profileName = (TextView) findViewById(R.id.first_login_name);
        setProfileImg();


        readyButton = (Button) findViewById(R.id.first_login_setting_ready_Button);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "";

                for(int i=0;i<serviceList.size();i++){
                    FFService service = serviceList.get(i);

                    text = text + service.getPrice() + "\n";
                }

                filterList();
                addServiceListToUser();

            }
        });
    }

    private void getCityNameFromInt(final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // geocoding
                HttpURLConnection connection = null;
                // stream
                try{
                    StringBuilder pp = new StringBuilder();
                    // website
                    pp.append("http://maps.googleapis.com/maps/api/geocode/json?latlng=");
                    pp.append(location.getLatitude()).append(",");
                    pp.append(location.getLongitude());
                    pp.append("&sensor=false");

                    String ppString = pp.toString();

                    URL url = new URL(ppString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    // input stream read
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    StringBuilder response = new StringBuilder();
                    String line;

                    while((line=reader.readLine())!=null){
                        response.append(line);
                    }

                    JSONObject jsonObject = new JSONObject(response.toString());
                    //
                    JSONArray resultArray = jsonObject.getJSONArray("results");

                    if(resultArray.length()>0){
                        JSONObject subObject = resultArray.getJSONObject(0);
                        Log.i("sssssss", "json" + subObject.toString());
                        // format location
                        String address = subObject.getJSONArray("address_components").getJSONObject(2).getString("short_name");
                        Message message = new Message();
                        message.what = 3;
                        message.obj = address;

                        handler.sendMessage(message);
                    }


                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
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
                    user.put("location",mCity);                               // *******************************   LOCATION
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("ssssss", "service List success!!");
                                Intent intent = new Intent(FirstLoginSettingActivity.this, HomeActivity.class); //***************************************************
                                startActivity(intent);
                                finish();
                            } else {
                                Log.i("ssssss", "service List failed: " + e.toString());
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
    public void onButtonClicked(List<FFService> list) {
        serviceList = list;
    }
}
