package friendshelp.android.com.bylianggao;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Gao on 2015/12/12.
 */
public class StartApplication extends Application {

    public static final String TAG = "Parse";

    public static final String LIST_TO_PROFILE = "listToProfile";
    public static final String ARGS_FRIEND_SERVICE = "friendProfileService";

    public static final String SERVICETYPE = "ServiceType";
    public static final String SERVICEPRICE = "ServicePrice";
    public static final String CITY = "City";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}