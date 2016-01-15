package friendshelp.android.com.bylianggao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gao on 2015/12/7.
 */
public class HomeFragment extends Fragment {
    private static final String ARG_TEXT = "text";
    private RecyclerView mRecyclerView;
    private TextView itemName;
    private TextView itemCity;
    private TextView itemTime;
    private customSuperCircularImageView itemPhoto;
    private LinearLayout itemFirstLinearLayout;
    private List<String> names;
    private CartAdapter mCartAdapter;
    private String selectName;
    private String selectTime;
    private String selectCity;
    private String selectId;
    private String selectPhoto;
    private String selectObjectId;
    private String userID;
    private JSONArray mArray;
    private List<JSONObject> jsonArray;
    private List<String> toNameList;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    parseFriendList();
                    //Log.i("ssssss", "activity case 1 Lisst: " + jsonArray.toString());
                    //updateUI();
                    break;
                case 2:
                    findPhotoFromParse((String) msg.obj);
                  // Log.i("ssssss", "activity case 2 Lisst: " + jsonArray.toString());
                    break;
                case 3:
                  //  Log.i("ssssss", "activity case 3 Lisst: " + jsonArray.toString());
                    updateUI();
                    break;
                default:
                    break;
            }
        };
    };

    private void findPhotoFromParse(String obj) {
        String[] ss = obj.split("#");
        String posString = ss[0];
        final int pos = Integer.parseInt(posString);
        final String id = ss[1];
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("userId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject object = objects.get(0);
                    String photoUrl = object.getString("userPhoto");
                    Log.i("sssssss", "img get homefragment success!!:::" + photoUrl);
                    JSONObject arrayObject = jsonArray.get(pos);
                    try {
                        arrayObject.put("photo", photoUrl);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.what = 3;
                    handler.sendMessage(msg);
                } else {
                    Log.i("sssssss", "img failed " + e.toString());
                }

            }
        });
    }

    private void parseFriendList() {
        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                final JSONObject jsonObject = jsonArray.get(i);
                String id = jsonObject.getString("toId");
                //String photo_url = findPhotoFromParse(id);
                //String city = object.getString("city");   *******************
                //friendName.add(name);
                //friendId.add(id);
                //friendCity.add(city);         **********************


//                String message = String.valueOf(i) + "#" + id;
//                Log.i("ssssss", "activity case -1 Lisst: " + object.toString() + "//" + i);
//                Message msg = Message.obtain();
//                msg.what = 2;
//                msg.obj = message;
//                handler.sendMessage(msg);

                ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
                query.whereEqualTo("userId", id);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            ParseObject object = objects.get(0);
                            String photoUrl = object.getString("userPhoto");
                            Log.i("sssssss", "img get homefragment success!!:::" + photoUrl);
                            try {
                                jsonObject.put("photo", photoUrl);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            Message msg = Message.obtain();
                            msg.what = 3;
                            handler.sendMessage(msg);
                        } else {
                            Log.i("sssssss", "img failed " + e.toString());
                        }

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_activity_item_container, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.home_activity_ListView_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        userID = getArguments().getString(ARG_TEXT).toString();
        Log.i("ssssss", "homeUserId:: " + userID);
        names = new ArrayList<>();
        jsonArray = new ArrayList<>();
        toNameList = new ArrayList<>();

        getActivityListFromParse();
        updateUI();
        return view;
    }

    private void getActivityListFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFActivity");
        query.whereEqualTo("fromId", userID);

        query.whereEqualTo("state", "1");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("ssssss", "object:Number:" + objects.size());
                    for (int i = 0; i < objects.size(); i++) {
                        try {
                            ParseObject object = objects.get(i);
                            String name = object.getString("toName");
                            String sDay = object.getString("startDay");
                            String eDay = object.getString("endDay");
                            String time = sDay + " -- " + eDay;
                            String city = object.getString("toCity");
                            String toId = object.getString("toId");
                            String objectId = object.getObjectId();
                            JSONObject json = new JSONObject();
                            json.put("name",name);
                            json.put("time",time);
                            json.put("city",city);  //**********************
                            json.put("toId",toId);
                            json.put("objectId", objectId);
                            jsonArray.add(json);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }


                    }
                    Log.i("ssssss", "activity case 0 Lisst: " + jsonArray.toString());
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } else {
                    Log.i("sssssss", "img failed " + e.toString());
                }

            }
        });
    }

    private void setList() {
        names.add("zhang san");
        names.add("li si");
        names.add("wang wu");
        names.add("zhao liu");
        names.add("sha bi");
        names.add("cao ni ma");
        names.add("gun dan");
        names.add("cai niao");
    }
    private void updateUI() {
        if(mCartAdapter == null){
            mCartAdapter = new CartAdapter(jsonArray);
            mRecyclerView.setAdapter(mCartAdapter);
        }else{
            mCartAdapter.notifyDataSetChanged();
        }
    }

    private class CartHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public CartHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.home_activity_item_name);
            itemCity = (TextView) itemView.findViewById(R.id.home_activity_item_city);
            itemTime = (TextView) itemView.findViewById(R.id.home_activity_item_time);
            itemPhoto = (customSuperCircularImageView) itemView.findViewById(R.id.home_activity_item_profile_img);

            itemView.setOnClickListener(this);
        }

        public void bindService(String name, String id, String time, String url) {
            Log.i("ssssss","bind!!!:: " + name + id + time + url);
            itemName.setText(name);
            itemCity.setText(id);
            itemTime.setText(time);
            itemPhoto.setImageURL(url);
        }


        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            try {
                JSONObject object = jsonArray.get(pos);
                selectName = object.getString("name");
                selectId = object.getString("toId");
                selectObjectId = object.getString("objectId");
                selectCity = object.getString("city");  //*************************
                selectTime = object.getString("time");
                selectPhoto = object.getString("photo");
                Intent intent = new Intent(getActivity(), HomeActivityDetail.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", selectName);
                bundle.putString("objectId", selectObjectId);
                bundle.putString("city",selectCity );
                bundle.putString("time",selectTime);
                bundle.putString("photo",selectPhoto);
                intent.putExtras(bundle);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class CartAdapter extends RecyclerView.Adapter<CartHolder> {

        private List<JSONObject> jsonArray;

        public CartAdapter(List<JSONObject> list) {
            jsonArray = list;
        }

        @Override
        public CartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.home_activity_item,parent,false);
            return new CartHolder(v);
        }

        @Override
        public void onBindViewHolder(CartHolder holder, int position) {
            try {
                JSONObject object = jsonArray.get(position);
                String name = object.getString("name");
                String id = object.getString("city");
                String url = object.getString("photo");
                String time = object.getString("time");
                Log.i("sssssss", "bind" + position + " // " + name + id + time);
                holder.bindService(name, id,time, url);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return jsonArray.size();
        }
    }
}
