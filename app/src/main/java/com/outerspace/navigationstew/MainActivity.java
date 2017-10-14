package com.outerspace.navigationstew;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.outerspace.navigationstew.databinding.ActivityMainBinding;
import com.outerspace.navigationstew.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        MainViewModel vm = new MainViewModel();
        binding.setMainVM(vm);

        navigator = new Navigator();
        FragmentManager manager = this.getSupportFragmentManager();
        navigator.setRootFragment("A", R.id.main_fragment, manager);

    }

    public void onClickNavigationButton(View view) {
        String destination = (String) view.getTag();
        try {
            navigator.walkTo(destination);
        }
        catch (ExceptionPathToNodeNotAvailable ex) {
            Log.d("MAIN ACTIVITY", "The application has a misconfigured Navigation Node", ex);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(navigator.canStepBack())
            navigator.stepBack();
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.exit_title)
                    .setMessage(R.string.exit_message)
                    .setCancelable(true)
                    .setNegativeButton(R.string.btn_cancel, null)
                    .setPositiveButton(R.string.exit_yes_quit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }
}
