package friendshelp.android.com.bylianggao;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class HomeActivityDetail extends AppCompatActivity {
    private String toName;
    private String selectObjectId;
    private String mCity;
    private String mTime;
    private String toPhotoUrl;

    private customSuperCircularImageView toProfileImg;
    private customSuperCircularImageView fromProfileImg;
    private TextView toProfileName;
    private TextView fromProfileName;
    private TextView City;
    private TextView Time;
    private String fromName;
    private String fromPhotoUrl;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    fromProfileName.setText(fromName);
                    fromProfileImg.setImageURL(fromPhotoUrl);
                    break;
                case 2:
                    break;
                default:

                    break;
            }
        };
    };


    private HomeDetailActivityFragment mHomeDetailActivityFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        toName = bundle.getString("name", "");
        selectObjectId = bundle.getString("objectId","");
        mCity = bundle.getString("city","");
        mTime = bundle.getString("time", "");
        toPhotoUrl = bundle.getString("photo", "");
//        this.setTitle(title);
        toProfileImg = (customSuperCircularImageView) findViewById(R.id.home_detail_activity_toName_photo);
        fromProfileImg = (customSuperCircularImageView) findViewById(R.id.home_detail_activity_fromName_photo);
        toProfileName = (TextView) findViewById(R.id.home_detail_activity_toName);
        fromProfileName = (TextView) findViewById(R.id.home_detail_activity_fromName);
        City = (TextView) findViewById(R.id.home_detail_activity_cityName);
        Time = (TextView) findViewById(R.id.home_detail_activity_time);

        toProfileName.setText(toName);
        toProfileImg.setImageURL(toPhotoUrl);
        City.setText(mCity);
        Time.setText(mTime);

        findUseDataInParse();


        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setTitle(toName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mHomeDetailActivityFragment = new HomeDetailActivityFragment();
        Bundle bb = new Bundle();
        bb.putString("text", selectObjectId);
        mHomeDetailActivityFragment.setArguments(bb);
        transaction.replace(R.id.home_detail_activity_listView, mHomeDetailActivityFragment);
        transaction.commit();

    }

    private void findUseDataInParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("UserObjectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject user = objects.get(0);
                    String name = user.getString("userName");
                    String photo_url = user.getString("userPhoto");
                    fromName = name;
                    fromPhotoUrl = photo_url;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

}
