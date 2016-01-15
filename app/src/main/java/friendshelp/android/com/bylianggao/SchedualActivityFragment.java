package friendshelp.android.com.bylianggao;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gao on 2015/12/8.
 */
public class SchedualActivityFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<String> friendName;
    private List<String> friendId;
    private List<String> friendCity;
    private CartAdapter mCartAdapter;
    private TextView itemCity;
    private TextView itemName;
    private customSuperCircularImageView itemPhoto;
    private JSONArray mArray;
    private List<JSONObject> jsonArray;

    private String selectName;
    private String selectId;
    private String selectCity;
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    parseFriendList();
                    //updateUI();
                    break;
                case 2:
                    findPhotoFromParse((String) msg.obj);
                    break;
                case 3:
                    updateUI();
                    break;
                default:
                    break;
            }
        };
    };

    public void findPhotoFromParse(String message) {
        String[] ss = message.split("#");
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
                    Log.i("sssssss", "img get success!!:::" + photoUrl);
                    JSONObject arrayObject = jsonArray.get(pos);
                    try {
                        arrayObject.put("photo", photoUrl);
                        arrayObject.put("location", object.getString("location"));
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
    public void parseFriendList(){
        for (int i = 0; i < mArray.length(); i++) {
            try {
                JSONObject object = (JSONObject) mArray.get(i);
                String name = object.getString("name");
                String id = object.getString("id");
                //String photo_url = findPhotoFromParse(id);
                //String city = object.getString("city");   *******************
                //friendName.add(name);
                //friendId.add(id);
                //friendCity.add(city);         **********************
                String message = String.valueOf(i) + "#" + id;
                jsonArray.add(object);
                Message msg = Message.obtain();
                msg.what = 2;
                msg.obj = message;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedual_activity_listview_container, container,false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.schedual_activity_RecyclerView_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        friendName = new ArrayList<>();
        friendId = new ArrayList<>();
        friendCity = new ArrayList<>();
        jsonArray = new ArrayList<>();

        getFriendListFromParse();
        updateUI();

        updateUI();

        return view;
    }

    private void getFriendListFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("UserObjectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("sssssss", "img get success!!");
                    ParseObject object = objects.get(0);
                    mArray = object.getJSONArray("friendList");
                    Log.i("sssssss", "friendList get success!!:::" + mArray.toString());
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } else {
                    Log.i("sssssss", "img failed " + e.toString());
                }

            }
        });
    }

    private void addItemToList() {

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
            itemName = (TextView) itemView.findViewById(R.id.friend_activity_list_item_name);
            itemCity = (TextView) itemView.findViewById(R.id.friend_activity_list_item_city);
            itemPhoto = (customSuperCircularImageView) itemView.findViewById(R.id.friend_activity_list_item_photo);

            itemView.setOnClickListener(this);
        }

        public void bindService(String name, String id, String url) {
            itemName.setText(name);
            itemCity.setText(id);
            itemPhoto.setImageURL(url);
        }

        @Override
        public void onClick(View v) {
//            int pos = getAdapterPosition();
//            selectName = testFriendNames.get(pos);
//            Intent intent = new Intent(getActivity(), SchedualActivityStepOne.class);
//            Bundle bundle = new Bundle();
//            Log.i("sssss", selectName + " !!");
//            bundle.putString("name", selectName);
//            intent.putExtras(bundle);
//            startActivity(intent);
            int pos = getAdapterPosition();
            try {
                JSONObject object = jsonArray.get(pos);
                selectName = object.getString("name");
                selectId = object.getString("id");
                selectCity = object.getString("location");
                Intent intent = new Intent(getActivity(), SchedualActivityStepOne.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", selectName);
                bundle.putString("id", selectId);
                bundle.putString("city",selectCity);
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
            View v = inflater.inflate(R.layout.friend_activity_list_item,parent,false);
            return new CartHolder(v);
        }

        @Override
        public void onBindViewHolder(CartHolder holder, int position) {
            try {
                JSONObject object = jsonArray.get(position);
                String name = object.getString("name");
                String id = object.getString("location");
                String url = object.getString("photo");
                holder.bindService(name, id, url);
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
