package friendshelp.android.com.bylianggao;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.DatePicker;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SchedualActivityStepOne extends AppCompatActivity implements SchedualActivityStepOneFragment.Callbacks {

    private static Button startButton;
    private static Button endButton;
    private String title;
    private List<FFService> serviceList;
    private Button readyButton;
    private String fromId;
    private String toProfileId;
    private customSuperCircularImageView profileImg;
    private TextView profileName;
    private TextView profileCity;
    private String mName;
    private String mCity;
    private String mPhotoUrl;
    private List<JSONObject> jsonList;
    private String activityObjectId;
    private String fromName;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    profileName.setText(mName);
                    profileCity.setText(mCity);   //************************ city
                    profileImg.setImageURL(mPhotoUrl);
                    break;
                case 2:
                    Intent intent = new Intent(SchedualActivityStepOne.this, SchedualActivityStepTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", title);
                    bundle.putString("id",toProfileId);
                    bundle.putString("city", mCity);
                    bundle.putString("objectId", activityObjectId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                default:

                    break;
            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedual_activity_step_one);

        startButton = (Button) findViewById(R.id.schedual_activity_step_one_datapicker_start);
        endButton = (Button) findViewById(R.id.schedual_activity_step_one_datapicker_end);
        profileImg = (customSuperCircularImageView) findViewById(R.id.schedual_activity_step_one_profile_img);
        profileName = (TextView) findViewById(R.id.schedual_activity_step_one_profile_name);
        profileCity = (TextView) findViewById(R.id.schedual_activity_step_one_profile_city);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        title = bundle.getString("name", "");
        toProfileId = bundle.getString("id","");
        mCity = bundle.getString("city","");
        jsonList = new ArrayList<>();

        findFromId();
        findDataInParse();


        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setTitle("Select Services");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        serviceList = new ArrayList<>();
        FragmentManager fm = getSupportFragmentManager();
        SchedualActivityStepOneFragment fragment = new SchedualActivityStepOneFragment();
        Bundle bb = new Bundle();
        bb.putString("text", toProfileId);
        fragment.setArguments(bb);
        fm.beginTransaction().add(R.id.schedual_activity_step_one_listView,fragment).commit();



        readyButton = (Button) findViewById(R.id.schedual_activity_step_one_next_button);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "";

                List<JSONObject> activityServiceList = new ArrayList<JSONObject>();
                for (int i = 0; i < jsonList.size(); i++) {
                    try {
                        JSONObject service = jsonList.get(i);
                        int flg = service.getInt("flg");
                        if (flg % 2 == 1) {
                            activityServiceList.add(service);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                float total = 0f;
                for (int i = 0; i < activityServiceList.size(); i++) {
                    try {
                        float money = Float.parseFloat(activityServiceList.get(i).getString("price"));
                        total += money;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String totalMoney = String.valueOf(total);
                String sDate = startButton.getText().toString();
                String eDate = endButton.getText().toString();
                String startDate = getDateFromString(sDate);
                String endDate = getDateFromString(eDate);





                Log.i("sssssss", "total: " + totalMoney + ":: startdate: " + startDate + " //" + endDate);

                final ParseObject FFActivity = new ParseObject("FFActivity");
                FFActivity.put("fromId", fromId);
                FFActivity.put("toId", toProfileId);
                FFActivity.put("toName", mName);
                FFActivity.put("fromUserObjectId", ParseUser.getCurrentUser().getObjectId());
                FFActivity.put("total", totalMoney);
                FFActivity.put("toCity", mCity);   //city***********************
                FFActivity.put("startDay", startDate);
                FFActivity.put("endDay", endDate);
                FFActivity.put("serviceList", activityServiceList);
                FFActivity.put("state", "0");
                FFActivity.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            activityObjectId = FFActivity.getObjectId();
                            Log.i("sssssss", "activity successfully!" + FFActivity.getObjectId());
                            Message msg = Message.obtain();
                            msg.what = 2;
                            handler.sendMessage(msg);
                        } else {
                            Log.i("sssssss", "activity failed::" + e.toString());
                        }
                    }
                });


            }
        });



    }

    private String getDateFromString(String eDate) {
        String result;
        int j = eDate.length();
        int i = j - 1;
        while (eDate.charAt(i) != ' ') {
            i--;
        }
        result = eDate.substring(i + 1, j);
        return result;
    }

    private void findFromId() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("UserObjectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject user = objects.get(0);
                    fromId = user.getString("userId");
                    //user.put("location",);                                *******************************   LOCATION

                } else {
                    Log.i("ssssss", "find User Failed:" + e.toString());
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i("sssss", "select id:::" + id);
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


    public void showStartDateDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "startPicker");
    }
    public void showEndDateDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment2();
        newFragment.show(getFragmentManager(), "endPicker");
    }

    @Override
    public void onButtonClicked(List<JSONObject> list) {
        jsonList = list;
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return  new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            startButton.setText("Start: " + String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day));
            Log.i("sssss", startButton.getText().toString());
        }
    }


    public static class DatePickerFragment2 extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return  new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            showEndtime(year, month, day);

            Log.i("sssss", endButton.getText().toString());
        }
        public static void showEndtime( int year, int month, int day){
            endButton.setText("End: " + String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day));
        }
    }

}
