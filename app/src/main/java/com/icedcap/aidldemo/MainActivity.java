package com.icedcap.aidldemo;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.icedcap.aidldemo.databinding.ActivityMainBinding;
import com.icedcap.aidldemo.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = new MainViewModel(this);
        binding.setVm(mViewModel);
    }

    @Override
    protected void onDestroy() {
        mViewModel.unbindService();
        super.onDestroy();
    }
}
