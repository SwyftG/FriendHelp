package friendshelp.android.com.bylianggao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import friendshelp.android.com.bylianggao.R;

/**
 * Created by Gao on 2015/12/7.
 */
public class HomeNullFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_null_fragment,container,false);
        return view;
    }
}
