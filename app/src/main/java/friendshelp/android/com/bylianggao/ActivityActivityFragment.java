package friendshelp.android.com.bylianggao;

import android.app.Fragment;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gao on 2015/12/9.
 */
public class ActivityActivityFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<String> testFriendNames;
    private CartAdapter mCartAdapter;
    private TextView itemName;
    private TextView itemPrice;
    private Button itemAcceptButton;
    private Button itemRejectButton;
    private customSuperCircularImageView itemPhoto;
    private String userId;
    private List<JSONObject> jsonArray;
    private String selectObjectId;

    private String selectName;


    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    getActivityListFromParse();
                    //updateUI();
                    break;
                case 2:
                    getNamesFromParse();
                    break;
                case 3:
                    findPhotoAndNameFromParse((String) msg.obj);
                    break;
                case 4:
                    updateUI();
                    break;
                case 5:
                    deleteJsonArray((Integer) msg.obj);
                    break;
                default:
                    break;
            }
        };
    };

    private void deleteJsonArray(int obj) {
        jsonArray.remove(obj);
        updateUI();
    }

    private void findPhotoAndNameFromParse(String message) {
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
                    String name = object.getString("userName");
                    JSONObject arrayObject = jsonArray.get(pos);
                    try {
                        arrayObject.put("photo", photoUrl);
                        arrayObject.put("name",name);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.what = 4;
                    handler.sendMessage(msg);
                } else {
                    Log.i("sssssss", "img failed " + e.toString());
                }

            }
        });
    }

    private void getNamesFromParse() {
        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject json = jsonArray.get(i);
                String id = json.getString("fromId");
                String message = String.valueOf(i) + "#" + id;
                Message msg = Message.obtain();
                msg.what = 3;
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
        View view = inflater.inflate(R.layout.activity_activity_listview_container, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.activity_activity_RecyclerView_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        jsonArray = new ArrayList<>();

        getUserId();

        updateUI();

        return view;
    }

    private void getUserId() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFUser");
        query.whereEqualTo("UserObjectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("sssssss", "img get success!!");
                    ParseObject object = objects.get(0);
                    userId = object.getString("userId");
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } else {
                    Log.i("sssssss", "img failed " + e.toString());
                }

            }
        });
    }

    private void getActivityListFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFActivity");
        query.whereEqualTo("toId", userId);
        query.whereEqualTo("state", "0");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        try {
                            ParseObject object = objects.get(i);
                            JSONObject json = new JSONObject();
                            json.put("fromId", object.getString("fromId"));
                            String total = object.getString("total");
                            String money = "Total:$" + total;
                            json.put("total", money);
                            json.put("objectId", object.getObjectId());
                            jsonArray.add(json);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                    Message msg = Message.obtain();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } else {
                    Log.i("sssssss", "img failed " + e.toString());
                }

            }
        });
    }

    private void addItemToList() {
        testFriendNames = new ArrayList<>();
        testFriendNames.add("zhang san");
        testFriendNames.add("li si");
        testFriendNames.add("wang wu");
        testFriendNames.add("zhao liu");
        testFriendNames.add("sha bi");
        testFriendNames.add("cao ni ma");
        testFriendNames.add("gun dan");
        testFriendNames.add("cai niao");
    }

    private void updateUI() {
        if(mCartAdapter == null){
            mCartAdapter = new CartAdapter(jsonArray);
            mRecyclerView.setAdapter(mCartAdapter);
        }else{
            mCartAdapter.notifyDataSetChanged();
        }
    }

    private class CartHolder extends RecyclerView.ViewHolder {

        public CartHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.activity_activity_item_name);
            itemPrice = (TextView) itemView.findViewById(R.id.activity_activity_item_price);
            itemAcceptButton = (Button) itemView.findViewById(R.id.activity_activity_item_accept_button);
            itemRejectButton = (Button) itemView.findViewById(R.id.activity_activity_item_reject_button);
            itemPhoto = (customSuperCircularImageView) itemView.findViewById(R.id.activity_activity_item_photo);

            itemAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"ACCEPT",Toast.LENGTH_LONG).show();
                    final int pos = getAdapterPosition();
                    try {
                        JSONObject object = jsonArray.get(pos);
                        selectObjectId = object.getString("objectId");

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFActivity");
                        query.whereEqualTo("objectId", selectObjectId);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    ParseObject object = objects.get(0);
                                    object.remove("state");
                                    object.put("state", "1");
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Log.i("ssssssss", "activity has accept");
                                                Message msg = Message.obtain();
                                                msg.what = 5;
                                                msg.obj = pos;
                                                handler.sendMessage(msg);
                                            } else {
                                                Log.i("ssssssss", "activity has failed");
                                            }
                                        }
                                    });
                                } else {
                                    Log.i("sssssss", "img failed " + e.toString());
                                }

                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            itemRejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"REJECT",Toast.LENGTH_LONG).show();
                    final int pos = getAdapterPosition();
                    try {
                        JSONObject object = jsonArray.get(pos);
                        selectObjectId = object.getString("objectId");

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFActivity");
                        query.whereEqualTo("objectId", selectObjectId);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    ParseObject object = objects.get(0);
                                    object.remove("state");
                                    object.put("state", "-1");
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Log.i("ssssssss", "activity REJECT sucess");
                                                Message msg = Message.obtain();
                                                msg.what = 5;
                                                msg.obj = pos;
                                                handler.sendMessage(msg);
                                            } else {
                                                Log.i("ssssssss", "activity reect FAILED");
                                            }
                                        }
                                    });
                                } else {
                                    Log.i("sssssss", "img failed " + e.toString());
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        public void bindService(String name, String price, String url) {
            itemName.setText(name);
            itemPrice.setText(price);
            itemPhoto.setImageURL(url);
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
            View v = inflater.inflate(R.layout.activity_activity_listview_item,parent,false);
            return new CartHolder(v);
        }

        @Override
        public void onBindViewHolder(CartHolder holder, int position) {
            try {
                JSONObject object = jsonArray.get(position);
                String name = object.getString("name");
                String price = object.getString("total");
                String url = object.getString("photo");
                holder.bindService(name, price, url);
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
