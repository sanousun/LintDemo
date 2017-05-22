package com.maihaoche.lintdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/5/5
 * Time: 下午4:56
 * Desc:
 */

public class MainActivity extends AppCompatActivity {

    private static final int TEST = 2;
    private static final String  TEST2 = "3";

    public static final int SOME_CONSTANT = 42;
    public int publicField;
    private static String sSingleton;
    int mPackagePrivate;
    private int mPrivate;
    protected int mProtected;

    private TextView mTextView;
    private int code;
    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("xyz", "xyz");
        TextView textView;
        switch (9) {
            case 1:
                break;
            case TEST:
                break;
            case TestFragment.REQUEST_TEST:
                break;
        }
        Observable.just(1).subscribe();
    }

    private void unSubscription(Subscription subscription){
        subscription.isUnsubscribed();
    }
}
