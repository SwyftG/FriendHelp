package friendshelp.android.com.bylianggao;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.List;

public class FriendProfile extends AppCompatActivity {

    private String profileId;
    private customSuperCircularImageView profilePhoto;
    private TextView profileName;
    private TextView profileCity;
    private TextView profileFriends;
    private TextView profileHelped;
    private String mName;
    private String mFriend;
    private String mPhotoUrl;
    private String mCity;
    private String mHelp;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    profileName.setText(mName);
                    profileFriends.setText(mFriend);
                    profileHelped.setText(mHelp);
                    profilePhoto.setImageURL(mPhotoUrl);
                    profileCity.setText(mCity);
                    break;
                default:

                    break;
            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String title = bundle.getString("name", "");
        profileId = bundle.getString("id","");
//        this.setTitle(title);

        profilePhoto = (customSuperCircularImageView) findViewById(R.id.friend_activity_profile_photoImg);
        profileName = (TextView) findViewById(R.id.friend_activity_profile_profileName);
        profileCity = (TextView) findViewById(R.id.friend_activity_profile_profileCity);
        profileFriends = (TextView) findViewById(R.id.friend_activity_profile_profileFriends);
        profileHelped = (TextView) findViewById(R.id.friend_activity_profile_profileHelped);


        findDataInParse();



        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
         FriendProfileFragment mHomeDetailActivityFragment = new FriendProfileFragment();
        //mHomeDetailActivityFragment.newInstance(profileId);
        Bundle bb = new Bundle();
        bb.putString("text", profileId);
        mHomeDetailActivityFragment.setArguments(bb);
        transaction.replace(R.id.friend_activity_profile_listview, mHomeDetailActivityFragment);
        transaction.commit();

    }

    private void findDataInParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("userId", profileId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject user = objects.get(0);
                    String name = user.getString("userName");
                    //String city = user.getString("city");
                    String id = user.getString("userId");
                    JSONArray friendLists = user.getJSONArray("friendList");
                    String friends = String.valueOf(friendLists.length());
                    String help = String.valueOf(user.getInt("help"));
                    String photo_url = user.getString("userPhoto");
                    mName = name;
                    mFriend = friends;
                    mHelp = help;
                    mPhotoUrl = photo_url;
                    mCity = user.getString("location");

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
}
