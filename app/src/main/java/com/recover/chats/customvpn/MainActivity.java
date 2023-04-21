package com.recover.chats.customvpn;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.recover.chats.customvpn.VPNHandler.VpnServiceHandler;
import com.recover.chats.customvpn.databinding.ActivityMainBinding;

import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.btnConnectVPN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prepareVPN();
            }
        });
    }

    private void prepareVPN() {
        Intent intent = VpnService.prepare(this);

        if (intent != null) {
            openDialog.launch(intent);
        } else startVPNService();

    }

    ActivityResultLauncher<Intent> openDialog = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Here, no request code
                startVPNService();
            } else prepareVPN();
        }
    });

    private void startVPNService() {
        this.startService(new Intent(getApplicationContext(),VpnServiceHandler.class));
    }
}
