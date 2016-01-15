package friendshelp.android.com.bylianggao;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.CheckBox;
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
 * Created by Gao on 2015/12/8.
 */
public class SchedualActivityStepOneFragment extends Fragment {
    private static final String ARG_TEXT = "text";
    private static int itemClicked = -1;
    private RecyclerView rv;
    private List<FFService> itemList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private FFService item;
    private static final int REQUEST_DIALOG = 0;
    //interface method
    private Callbacks mCallbacks;


    private String userID;
    private TextView serviceName;
    private TextView servicePrice;
    private CheckBox serviceCheckbox;
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
                object.put("flg", 0);
                jsonArray.add(object);
                Message msg = Message.obtain();
                msg.what = 2;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    public interface Callbacks{
        void onButtonClicked(List<JSONObject> list);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedual_activity_step_one_listview_container,container,false);
        rv = (RecyclerView) view.findViewById(R.id.schedual_activity_step_one_RecyclerView_container);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(true);

        userID = getArguments().getString(ARG_TEXT).toString();
        jsonArray = new ArrayList<>();

        getFriendListFromParse();

       // addItemToList();

//        if (recyclerViewAdapter == null) {
//            recyclerViewAdapter = new RecyclerViewAdapter(itemList);
//            rv.setAdapter(recyclerViewAdapter);
//        }

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

//    public ArrayList<FFService> createServiceList() {
//        ArrayList<FFService> serviceList = new ArrayList<>();
//        serviceList.add(new FFService(0,"Accommodation","0","00",1));
//        serviceList.add(new FFService(1, "Car Service", "0", "00", 1));
//        serviceList.add(new FFService(2, "Recommend Food", "0", "00", 1));
//        serviceList.add(new FFService(3, "City Tour", "0", "00", 1));
//        serviceList.add(new FFService(4,"Language Help","0","00",1));
//        serviceList.add(new FFService(5,"Airport Pickup","0","00",1));
//        return serviceList;
//    }
//
//    public List<FFService> getServiceList(){
//        return itemList;
//    }
//
//
//
//
//    private void addItemToList() {
//        itemList = createServiceList();
//        updateServiceInfo();
//    }
//

    private void updateUI() {
        if(recyclerViewAdapter == null){
            recyclerViewAdapter = new RecyclerViewAdapter(jsonArray);
            rv.setAdapter(recyclerViewAdapter);
        }else{
            recyclerViewAdapter.notifyDataSetChanged();
        }

        updateServiceInfo();

    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<ItemHolder> {
        private List<JSONObject> mJsonArray;

        public RecyclerViewAdapter(List<JSONObject> list) {
            mJsonArray = list;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.schedual_activity_step_one_listview_item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            try {
                JSONObject object = mJsonArray.get(position);
                String name = object.getString("name");
                String price = object.getString("price");
                int flg = object.getInt("flg");
                Log.i("ssssss", "bindView" + name + "  " + price + " //" + flg);
                holder.bindItem(name, price, flg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJsonArray.size();
        }
    }







    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CheckBox mCheckBox;

        public ItemHolder(View itemView) {
            super(itemView);
            serviceName = (TextView) itemView.findViewById(R.id.schedual_activity_step_one_item_serviceName);
            servicePrice = (TextView) itemView.findViewById(R.id.schedual_activity_step_one_item_servicePrice);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.schedual_activity_step_one_item_checkBox);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            try {
                int pos = getAdapterPosition();
                JSONObject item = jsonArray.get(pos);
                int flg = item.getInt("flg");
                flg++;
                Log.i("sssssss", "111111view:" + pos + "  flg ::" + flg + " Json// " + item.toString());
                item.remove("flg");
                item.put("flg",flg);
                if (flg % 2 == 1) {
                    mCheckBox.setChecked(true);
                } else {
                    mCheckBox.setChecked(false);
                }
                Log.i("sssssss", "2view:" + pos + "  flg ::" + flg + " Json// " + item.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void bindItem(String name, String price, int flg) {
            serviceName.setText(name);
            servicePrice.setText(price);
            //Log.i("ssssss", "bindItem:" + name + " // " + flg);
            if (flg % 2 == 0) {
                mCheckBox.setChecked(false);
            } else {
                mCheckBox.setChecked(true);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.i("ssssss", "!!!!! " + requestCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DIALOG) {
            String price = data.getStringExtra(FirstLoginSettingDialog.ITEM_PRICE);
            int pic = data.getIntExtra(FirstLoginSettingDialog.ITEM_PIC, 0);
            //Log.i("ssssss", price);
            item.setPrice(price);
            item.setPic(pic);
            printPrice(item.getId());
            updateUI();
        }
    }

    public void printPrice(int id) {
        for (int i = 0; i < itemList.size(); i++) {
            Log.i("ssssss", "service: " + id + "   price: " + itemList.get(i).getPrice() + " **pic: " + itemList.get(i).getPic());
        }
    }

    private void updateServiceInfo(){
        mCallbacks.onButtonClicked(jsonArray);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
