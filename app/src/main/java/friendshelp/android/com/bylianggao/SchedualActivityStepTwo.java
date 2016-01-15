package friendshelp.android.com.bylianggao;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SchedualActivityStepTwo extends AppCompatActivity {

    private Button confirmButton;
    private String title;
    private String toProfileId;
    private TextView profileName;
    private TextView profileCity;
    private TextView profileTime;
    private TextView profileTotal;
    private customSuperCircularImageView profileImg;
    private String mName;
    private String mCity;
    private String mPhotoUrl;
    private String mTime;
    private String mTotal;
    private JSONArray jsonArray;
    private List<JSONObject> jsonList;
    private String activityObjectId;
    private String parseTotal;


    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    profileName.setText(mName);
                    profileCity.setText(mCity);   //************************ city
                    profileImg.setImageURL(mPhotoUrl);
                    break;
                case 2:
                    profileTotal.setText(mTotal);
                    break;
                default:

                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedual_activity_step_two);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        title = bundle.getString("name", "");
        toProfileId = bundle.getString("id","");
        activityObjectId = bundle.getString("objectId","");
        Log.i("sssssss", "step two::" + title + " // " + toProfileId);

        jsonList = new ArrayList<>();

        confirmButton = (Button) findViewById(R.id.schedual_activity_step_two_confirm_button);
        profileName = (TextView) findViewById(R.id.schedual_activity_step_two_profile_name);
        profileCity = (TextView) findViewById(R.id.schedual_activity_step_two_profile_city);
        profileTime = (TextView) findViewById(R.id.schedual_activity_step_two_profile_time);
        profileTotal = (TextView) findViewById(R.id.schedual_activity_step_two_profile_total);
        profileImg = (customSuperCircularImageView) findViewById(R.id.schedual_activity_step_two_profile_img);

        findDataInParse();
        getServiceListAndTotal();



        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setTitle("Confirm");
        setSupportActionBar(toolbar);

        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        SchedualActivityStepTwoFragment fragment = new SchedualActivityStepTwoFragment();
        Bundle bb = new Bundle();
        bb.putString("text", activityObjectId);
        fragment.setArguments(bb);
        fm.beginTransaction().add(R.id.schedual_activity_step_two_listView, fragment).commit();



        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchedualActivityStepTwo.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i("sssss", "select id:::" + id);
        if (id == 16908332) {
            Intent intent = new Intent(SchedualActivityStepTwo.this, SchedualActivityStepOne.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", title);
            bundle.putString("id",toProfileId);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void findDataInParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("userId", toProfileId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject user = objects.get(0);
                    String name = user.getString("userName");
                    String id = user.getString("userId");
                    JSONArray friendLists = user.getJSONArray("friendList");
                    String city = user.getString("location");
                    String photo_url = user.getString("userPhoto");
                    mName = name;
                    mCity = city;
                    mPhotoUrl = photo_url;

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

    public void getServiceListAndTotal() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFActivity");
        query.whereEqualTo("objectId",activityObjectId);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject user = objects.get(0);
                    parseTotal = user.getString("total");
                    mTotal = "Total: $" + parseTotal;
                    Message msg = Message.obtain();
                    msg.what = 2;
                    handler.sendMessage(msg);

                    //user.put("location",);                                *******************************   LOCATION

                } else {
                    Log.i("ssssss", "find User Failed:" + e.toString());
                }
            }
        });
    }

}
