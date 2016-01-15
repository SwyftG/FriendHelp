package friendshelp.android.com.bylianggao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.parse.Parse;
import com.parse.ParseUser;

public class Splash extends AppCompatActivity {

    private LinearLayout splash_LinearLayout;
    private final int PLAY_TIME = 3000;

    private SharedPreferences mSharedPreferences;
    private String mSharedPreferencesName = "familyfriendconfig";









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        initView();
        initAnimation();
        goToNextPage();
    }

    private void goToNextPage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(PLAY_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkLogin();
            }
        }).start();

    }


    private void initView() {
        setContentView(R.layout.activity_splash);
        splash_LinearLayout = (LinearLayout) findViewById(R.id.Splash_LinerLayout);
        mSharedPreferences = getSharedPreferences(mSharedPreferencesName,0);
        Log.i("SSSSSS", mSharedPreferences.toString());

    }

    private void initAnimation() {
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        aa.setDuration(PLAY_TIME);
        aa.setFillAfter(true);
        splash_LinearLayout.startAnimation(aa);
    }

    public void checkLogin(){
        if (ParseUser.getCurrentUser() != null) {
            Intent intent  = new Intent(this,HomeActivity.class); //****************************************************************************************
            startActivity(intent);
            finish();
        } else {
            Intent intent  = new Intent(this,Guidactivity.class); //****************************************************************************************
            startActivity(intent);
            finish();
        }

    }







//    public void goToNextActivity() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    goPage();
//                }
//            }
//        }).start();
//    }
//
//    private void goPage() {
//        hasLogIn = mSharedPreferences.getString(LOGIN_ID, "0") == "0" ? 0 : 1; // 0 means first, 1 means has login
//        if (hasLogIn == 0) {
//            // go to guid activity  ************************
//            Intent intent  = new Intent(this,HomeActivity.class); //****************************************************************************************
//            startActivity(intent);
//            finish();
//        } else {
//            //go to home page ************************
//
//        }
//    }
//
}
