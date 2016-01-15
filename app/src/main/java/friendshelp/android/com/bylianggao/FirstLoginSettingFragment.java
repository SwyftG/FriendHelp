package friendshelp.android.com.bylianggao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gao on 2015/12/6.
 */
public class FirstLoginSettingFragment extends Fragment {
    private static int itemClicked = -1;
    private RecyclerView rv;
    private List<FFService> itemList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private FFService item;
    private static final int REQUEST_DIALOG = 0;
    //interface method
    private Callbacks mCallbacks;

    public interface Callbacks{
        void onButtonClicked(List<FFService> list);
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
        View view = inflater.inflate(R.layout.first_login_setting_listviewcontinar,container,false);

        rv = (RecyclerView) view.findViewById(R.id.RV_item);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(true);

        addItemToList();

//        if (recyclerViewAdapter == null) {
//            recyclerViewAdapter = new RecyclerViewAdapter(itemList);
//            rv.setAdapter(recyclerViewAdapter);
//        }

        updateUI();

        return view;
    }

    public ArrayList<FFService> createServiceList() {
        ArrayList<FFService> serviceList = new ArrayList<>();
        serviceList.add(new FFService(0,"Accommodation","0","00",0));
        serviceList.add(new FFService(1, "Car Service", "0", "00", 0));
        serviceList.add(new FFService(2, "Recommend Food", "0", "00", 0));
        serviceList.add(new FFService(3, "City Tour", "0", "00", 0));
        serviceList.add(new FFService(4,"Language Help","0","00",0));
        serviceList.add(new FFService(5,"Airport Pickup","0","00",0));
        serviceList.add(new FFService(6,"City Guide","0","00",0));
        serviceList.add(new FFService(7,"Homemade Food","0","00",0));
        serviceList.add(new FFService(8, "First-aid", "0", "00", 0));
        return serviceList;
    }

    public List<FFService> getServiceList(){
        return itemList;
    }




    private void addItemToList() {
        itemList = createServiceList();
        updateServiceInfo();
    }


    private void updateUI() {
        if(recyclerViewAdapter == null){
            recyclerViewAdapter = new RecyclerViewAdapter(itemList);
            rv.setAdapter(recyclerViewAdapter);
        }else{
            if(itemClicked < 0){
                recyclerViewAdapter.notifyDataSetChanged();
            }else{
                recyclerViewAdapter.notifyItemChanged(itemClicked);
                itemClicked = -1;
            }
        }
        updateServiceInfo();
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<ItemHolder> {
        private List<FFService> itemList;
        private FFService item;

        public RecyclerViewAdapter(List<FFService> list) {
            itemList = list;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.first_login_setting_item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            item = itemList.get(position);
            holder.bindItem(item);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }







    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView itemName;
        private String sLeft;
        private String sRight;
        private TextView priceView;
        private ImageView imageView;

        public ItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemName = (TextView) itemView.findViewById(R.id.service_name);
            priceView = (TextView) itemView.findViewById(R.id.service_name_price);
            imageView = (ImageView) itemView.findViewById(R.id.done_imageView);
            String price = priceView.getText().toString();

            int flag = 0;
            StringBuffer sbleft = new StringBuffer();
            StringBuffer sbright = new StringBuffer();
            for (int i = 0; i < price.length(); i++) {
                if (price.charAt(i) != '#') {
                    if (flag == 0) {
                        sbleft.append(price.charAt(i));
                    } else {
                        sbright.append(price.charAt(i));
                    }
                } else {
                    flag = 1;
                }
            }
            sLeft = sbleft.toString();
            sRight = sbright.toString();

        }



        @Override
        public void onClick(View v) {
            itemClicked = getAdapterPosition();
            item = itemList.get(itemClicked);

            FragmentManager fm = getFragmentManager();
            FirstLoginSettingDialog dialog = FirstLoginSettingDialog.newInstance(item.getName(), item.getPrice(), item.getPic());

            dialog.setTargetFragment(FirstLoginSettingFragment.this, REQUEST_DIALOG);
            dialog.show(fm, "aa");
        }

        public void bindItem(FFService item) {
            String price = item.getPrice();
            int pic = item.getPic();
            int flag = 0;
            StringBuffer sbleft = new StringBuffer();
            StringBuffer sbright = new StringBuffer();
            for (int i = 0; i < price.length(); i++) {
                if (price.charAt(i) != '#') {
                    if (flag == 0) {
                        sbleft.append(price.charAt(i));
                    } else {
                        sbright.append(price.charAt(i));
                    }
                } else {
                    flag = 1;
                }
            }
            String monyLeft = sbleft.toString();
            String monyRight = sbright.toString();

            String finalprice = monyLeft + "." + monyRight;
            priceView.setText(finalprice);
            item.setPrice(finalprice);
            itemName.setText(item.getName());
            if (pic == 0) {
                imageView.setVisibility(View.INVISIBLE);
                item.setPic(pic);
            } else {
                imageView.setVisibility(View.VISIBLE);
                item.setPic(pic);
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
            //printPrice(item.getId());
            updateUI();
        }
    }

    public void printPrice(int id) {
        for (int i = 0; i < itemList.size(); i++) {
            Log.i("ssssss","service: " + id + "   price: " + itemList.get(i).getPrice() + " **pic: " + itemList.get(i).getPic());
        }
    }

    private void updateServiceInfo(){
        mCallbacks.onButtonClicked(itemList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

}
