package friendshelp.android.com.bylianggao;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gao on 2015/12/13.
 */
public class ProfileActivityFragment extends Fragment {
    private static final String ARG_TEXT = "text";
    private static int itemClicked = -1;
    private RecyclerView recyclerView;
    private List<FFService> testList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private String userID;
    private TextView servicePrice;
    private TextView serviceName;

    private List<JSONObject> jsonArray;
    private JSONArray mArray;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    parseFriendList();
                    //updateUI();
                    break;
                case 2:
                    updateUI();
                    break;
                default:
                    break;
            }
        };
    };

    public void parseFriendList(){
        for (int i = 0; i < mArray.length(); i++) {
            try {
                JSONObject object = (JSONObject) mArray.get(i);
                jsonArray.add(object);
                Message msg = Message.obtain();
                msg.what = 2;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static FriendProfileFragment newInstance(String text){
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        FriendProfileFragment fragment = new FriendProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_activity_listview_container,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.profile_activity_item_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        userID = getArguments().getString(ARG_TEXT).toString();
        jsonArray = new ArrayList<>();


        getFriendListFromParse();
        // addItemToList();
        updateUI();
        return view;
    }

    private void getFriendListFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("userId", userID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject object = objects.get(0);
                    mArray = object.getJSONArray("serviceList");
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
        testList = new ArrayList<>();
        testList.add(new FFService(0,"Accommodation","0","00",0));
        testList.add(new FFService(1, "Car Service", "0", "00", 0));
        testList.add(new FFService(2, "Recommend Food", "0", "00", 0));
        testList.add(new FFService(3, "City Tour", "0", "00", 0));
        testList.add(new FFService(4,"Language Help","0","00",0));
    }

    private void updateUI() {
        if(recyclerViewAdapter == null){
            recyclerViewAdapter = new RecyclerViewAdapter(jsonArray);
            recyclerView.setAdapter(recyclerViewAdapter);
        }else{
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }



    private class RecyclerViewAdapter extends RecyclerView.Adapter<ItemHolder> {
        private List<JSONObject> mJsonArray;


        public RecyclerViewAdapter(List<JSONObject> list) {
            mJsonArray = list;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.profile_activity_item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            try {
                JSONObject object = mJsonArray.get(position);
                String name = object.getString("name");
                String price = object.getString("price");
                Log.i("ssssss", "bindView" + name + "  " + price);
                holder.bindItem(name, price);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJsonArray.size();
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {


        public ItemHolder(View itemView) {
            super(itemView);
            serviceName = (TextView) itemView.findViewById(R.id.profile_activity_item_serviceName);
            servicePrice = (TextView) itemView.findViewById(R.id.profile_activity_item_servicePrice);
        }

        public void bindItem(String name, String price) {
            serviceName.setText(name);
            servicePrice.setText(price);
        }
    }

}
