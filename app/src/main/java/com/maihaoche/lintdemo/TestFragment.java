package com.maihaoche.lintdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/5/14
 * Time: 下午7:56
 * Desc:
 */

public class TestFragment extends Fragment {

    public static final int REQUEST_TEST = 0x11111;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        startActivityForResult(new Intent(getActivity(), MainActivity.class), REQUEST_TEST);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
