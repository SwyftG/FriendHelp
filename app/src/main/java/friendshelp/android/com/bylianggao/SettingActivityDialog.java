package friendshelp.android.com.bylianggao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Gao on 2015/12/8.
 */
public class SettingActivityDialog extends DialogFragment {
    public String name;
    public String price;
    public int pic;
    public TextView serviceName;
    public EditText leftPrice;
    public EditText rightPrice;
    public Button doneButton;
    public static final String ITEM_PRICE="hehe";
    public static final String ITEM_PIC="haha";

    public static FirstLoginSettingDialog newInstance(String name, String price, int pic) {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("price", price);
        bundle.putInt("pic", pic);
        FirstLoginSettingDialog myDialog = new FirstLoginSettingDialog();
        myDialog.setArguments(bundle);
        return myDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        name = getArguments().getString("name");
        price = getArguments().getString("price");
        pic = getArguments().getInt("pic");

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
        String sLeft = sbleft.toString();
        String sRight = sbright.toString();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.first_login_setting_dialog, null);
        serviceName = (TextView) view.findViewById(R.id.service_name_diaglo_textview);
        leftPrice = (EditText) view.findViewById(R.id.service_money_leftPart);
        rightPrice = (EditText) view.findViewById(R.id.service_money_rightPart);
        doneButton = (Button) view.findViewById(R.id.DoneButton);
        serviceName.setText(name);
        leftPrice.setHint(sLeft);
        rightPrice.setHint(sRight);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String leftPart = leftPrice.getText().toString();
                String rightPart = rightPrice.getText().toString();
                if (leftPart.equals("")) leftPart = "0";
                if (rightPart.equals("")) rightPart = "00";
                int picflag;
                if ((leftPart.equals("0")) && (rightPart.equals("00"))) {
                    picflag = 0;
                } else {
                    picflag = 1;
                }
                String price = leftPart + "." + rightPart;
                Log.i("ssssss", price + "!!!!");
                sendResult(Activity.RESULT_OK, price, picflag);
                getDialog().dismiss();
            }
        });
        return builder.create();
    }

    private void sendResult(int code, String price, int pic) {
        if (getTargetFragment() == null) return;
        Intent intent = new Intent();
        intent.putExtra(ITEM_PRICE, price);
        intent.putExtra(ITEM_PIC,pic);
        getTargetFragment().onActivityResult(getTargetRequestCode(),code, intent);

    }
}
