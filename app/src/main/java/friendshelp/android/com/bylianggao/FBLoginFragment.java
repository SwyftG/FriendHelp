package friendshelp.android.com.bylianggao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gao on 2015/12/5.
 */
public class FBLoginFragment extends Fragment{


    private String[] permission = {"user_friends","email"};
    private final ArrayList<String> FB_PERMISSION = new ArrayList<>(Arrays.asList(permission));
    private CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokenTracker;
    private SharedPreferences mSharedPreferences;
    private final String SHAREDPREFERENCES = "ffconfig";
    private final String FIRSTTIMELOGINFLAG = "loginfttest";
   // public static FFUser User = new FFUser("-1","xiaohua");
    private ParseUser PUser;
    private String userFbId;
//    private Handler handler = new Handler() {
//       public void handleMessage(android.os.Message msg) {
//           switch (msg.what){
//               case 1:
//                   getFBPhoto(msg.obj);
//                   break;
//               case 2:
//                   storePic((String) msg.obj);
//                   Log.i("ssssss","000000:: " + String.valueOf(msg.obj));
//                   break;
//               default:
//                   break;
//           }
//       };
//   };

    private void getFBPhoto(Object obj) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("UserObjectId", PUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject user = objects.get(0);
                   userFbId = (String) user.get("userId");
                    //user.put("location",);                                *******************************   LOCATION
                    Message msg = Message.obtain();
                    msg.what = 2;
                    //handler.sendMessage(msg);

                } else {
                    Log.i("ssssss", "find User Failed:" + e.toString());
                }
            }
        });
    }

    private void storePic(final String obj) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("UserObjectId", PUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject user = objects.get(0);
                    user.remove("userPhoto");
                    user.put("userPhoto", obj);
                    //user.put("location",);                                *******************************   LOCATION
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("ssssss", "img update success");
                                Intent intent = new Intent(getActivity(),FirstLoginSettingActivity.class);  //***************************
                                startActivity(intent);
                                getActivity().finish();
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


    public FBLoginFragment(){

    }


    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();

            getFriendsOfFB(accessToken);

            Profile profile = Profile.getCurrentProfile();




//            Intent intent = new Intent(getActivity(), FirstLoginSettingActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("id", accessToken);
//            intent.putExtras(bundle);
            //startActivity(intent);
            //onDestroy();
            //getActivity().finish();
        }

        @Override
        public void onCancel() {
            onResume();
        }

        @Override
        public void onError(FacebookException error) {
            onResume();
        }
    };





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();

    }

    public void initFragment(){
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        mSharedPreferences = this.getContext().getSharedPreferences(SHAREDPREFERENCES, 0);
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
//                Log.i("sssssss", "OLD: " + oldAccessToken);
//                Log.i("sssssss", "NEW: " + newAccessToken);
         //       updateWithToken(newAccessToken);
            }
        };
    }

//    private void updateWithToken(AccessToken currentAccessToken) {
//        boolean loginornot =currentAccessToken != null;
//        Log.i("sssssss", " ******* " + loginornot);
//
//        int flag = mSharedPreferences.getInt(FIRSTTIMELOGINFLAG, 0);
//        Log.i("ssssss", "FLAG://: " + flag);
//        if (currentAccessToken != null) {
////                    if (flag == 0) {
////                        SharedPreferences.Editor editor = mSharedPreferences.edit();
////                        editor.putInt(FIRSTTIMELOGINFLAG, 0);
////                        editor.commit();
////
////                        Intent intent = new Intent(getActivity(), FirstLoginSetting.class);
////                        Bundle bundle = new Bundle();
////                        bundle.putString("id", User.fb_id);
////                        intent.putExtras(bundle);
////                        startActivity(intent);
////                        getActivity().finish();
////                    }else {
////                        SharedPreferences.Editor editor = mSharedPreferences.edit();
////                        editor.putInt(FIRSTTIMELOGINFLAG, 0);
////                        editor.commit();
////                        Intent intent = new Intent(getActivity(), Hehetest.class);   //********************** need to detect have appointment or not
////                        startActivity(intent);
////                        getActivity().finish();
////                    }
//            Intent intent = new Intent(getActivity(), FirstLoginSettingActivity.class);
//           // startActivity(intent);
//            //getActivity().finish();
//            Toast.makeText(getContext(), "DNEGLU", Toast.LENGTH_SHORT).show();
//
//        }
//    }



    synchronized private void getFriendsOfFB(final AccessToken accessToken)  {

        GraphRequest request1 = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {    //id
                        Log.i("sssssss","On success Json: di yi ge111  " + object.toString());

                        try {
                            final String fbId = object.getString("id");
                            final String fbName = object.getString("name");
                            final String fb_photo_url = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            Log.i("sssssss", "****" + fbId + "///" + fbName + " // " + fb_photo_url);
                            final JSONArray friend_array = object.getJSONObject("friends").getJSONArray("data");

                            ParseUser PUser = new ParseUser();
                            PUser.setUsername(fbId);
                            PUser.setPassword(fbName);
                            PUser.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                        String objectId = ParseUser.getCurrentUser().getObjectId();
                                        Log.i("ssssss", "sign up success!***** " + objectId);
                                        ParseObject FFUser = new ParseObject("FFUser");
                                        FFUser.put("userId", fbId);
                                        FFUser.put("userName", fbName);
                                        FFUser.put("location","");
                                        FFUser.put("help", 0);
                                        FFUser.put("UserObjectId", objectId);
                                        FFUser.put("userPhoto", fb_photo_url);              //000000000000000
                                        FFUser.put("serviceList", new JSONArray());
                                        FFUser.put("friendList", friend_array);
                                        FFUser.put("activityList", new JSONArray());
                                        FFUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Log.i("ssssss", "sign up success!!!!!! ");
                                                } else {
                                                    Log.i("ssssss", "sig+++++" + e.toString());
                                                }
                                            }
                                        });
                                        Intent intent = new Intent(getActivity(),FirstLoginSettingActivity.class);  //***************************
                                        startActivity(intent);
                                        getActivity().finish();


                                    } else {
                                        Log.i("ssssss", "sign up failed:: " + e.toString());
                                        ParseUser.logInInBackground(fbId, fbName, new LogInCallback() {
                                            @Override
                                            public void done(ParseUser user, ParseException e) {
                                                if (user != null) {
                                                    Log.i("ssssss", user.getObjectId().toString());
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,friends,email,picture");
        parameters.putString("redirect", "false");
        parameters.putInt("height", 150);
        parameters.putInt("width", 150);
        request1.setParameters(parameters);



        GraphRequest request2 = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {    //id
                        try {
                            Log.i("ssssss", "request2:: " + object.toString());
                            String fb_photo_url = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            Log.i("ssssss", "request2// " + fb_photo_url);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = fb_photo_url;
//                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        Bundle parameters2 = new Bundle();
        parameters2.putString("fields", "picture");
        parameters2.putString("redirect", "false");
        parameters2.putInt("height", 150);
        parameters2.putInt("width", 150);
        request2.setParameters(parameters2);

        GraphRequestBatch batch = new GraphRequestBatch(request1);
        batch.addCallback(new GraphRequestBatch.Callback(){
            @Override
            public void onBatchCompleted(GraphRequestBatch batch) {
               // Log.i("sssssss","On33333" + batch.toString());
            }
        });
        batch.executeAsync();

//        GraphRequest request = GraphRequest.newMeRequest(
//                accessToken,
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(
//                            JSONObject object,
//                            GraphResponse response) {
//                        try {         //*********************************************************  json
//                            String fb_name = object.getString("name");
//                            String fb_id = object.getString("id");
//                            String fb_email = object.getString("email");
////                            User.setFb_id(fb_id);
////                            User.setFb_name(fb_name);
////                            User.setEmail(fb_email);
//                            JSONArray friend_array = object.getJSONObject("friends").getJSONArray("data");
//
//                            for (int i = 0; i < friend_array.length(); i++) {   //    add friend to User's friend list
//                                JSONObject person = friend_array.getJSONObject(i);
//                                String person_name = person.getString("name");
//                                String person_id = person.getString("id");
////                                FFUser friend = new FFUser(person_id, person_name);
////                                User.friends.add(friend);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        // Log.i("sssssss","On success :: "  + response.toString()); //********************
//                        Log.i("sssssss","On success Json:: " + object.toString()); //********************
//                    }
//                });

//        request.executeAsync();
//        getPhotoOfFB(accessToken);
    }


//    public void getPhotoOfFB(AccessToken accessToken) {
////        String id = "";
////
////        String path = "/1133223033385247/picture?height=150&redirect=false&width=150";
////
////        Bundle parameters = new Bundle();
////        parameters.putString("redirect", "false");
////        parameters.putInt("height", 150);
////        parameters.putInt("width",150);
////        new GraphRequest(
////                AccessToken.getCurrentAccessToken(),
////                "/1133223033385247/picture",
////                parameters,
////                HttpMethod.GET,
////                new GraphRequest.Callback() {
////                    public void onCompleted(GraphResponse response) {
////                        Log.i("sssssss", "!!!!!" + response.getJSONObject().toString() + " success //:: " + response.toString()); //********************
////                        try {
////                            String fb_photo_url = response.getJSONObject().getJSONObject("data").getString("url");
////                            Log.i("ssssss","+++++::" + fb_photo_url); //***************
////                            User.setFb_photo(fb_photo_url);
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////            /* handle the result */
////                    }
////                }
////        ).executeAsync();
//
////
////
////        GraphRequest request = GraphRequest.newMeRequest(
////                accessToken,
////                new GraphRequest.GraphJSONObjectCallback() {
////                    @Override
////                    public void onCompleted(
////                            JSONObject object,
////                            GraphResponse response) {
////                        try {
////                            String fb_photo_url = object.getJSONObject("picture").getJSONObject("data").getString("url");
////                            Log.i("ssssss","+++++::" + object.toString()); //***************
////                            Log.i("ssssss", "+++++22222::" + fb_photo_url); //***************
////                            //User.setFb_photo(fb_photo_url);
////                        } catch (Exception e) {
////                            e.printStackTrace();
////                        }
////
////                    }
////                });
//
////        request.executeAsync();
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fb_login_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions(FB_PERMISSION);
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }



}
