package com.recover.chats.customvpn.VPNHandler;

import static android.os.Build.VERSION.SDK_INT;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VpnServiceHandler extends VpnService {
    private static final String TAG = "VpnServiceHandler";
    private Thread mThread;
    String customIP = "192.168.0.1";
    VpnServiceHandler mService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        getCountryData();

        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mService = this;


        // Configure the VPN connection
    }

    private void getCountryData() {
        GetAPIInterface apiInterface = RetroFit_APIClient.getInstance().getClient(getApplicationContext(), "https://codingcafe.in/Uae/128.90.143/").create(GetAPIInterface.class);
        Call<DubaiVpnResponseModel> call = apiInterface.getDubaiVPNData();
        call.enqueue(new Callback<DubaiVpnResponseModel>() {
            @Override
            public void onResponse(Call<DubaiVpnResponseModel> call, Response<DubaiVpnResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    startVPN(response.body());
                }

            }

            @Override
            public void onFailure(Call<DubaiVpnResponseModel> call, Throwable t) {

            }
        });
    }

    PendingIntent pendingIntent;
    ParcelFileDescriptor pfd = null;

    private void startVPN(DubaiVpnResponseModel responseModel) {
        if (responseModel != null) {
            Random rand = new Random();
            int pos = rand.nextInt(responseModel.getServersList().size());

         /*   Intent vpnIntent = new Intent(getApplicationContext(), VpnServiceHandler.class);
// Create a PendingIntent for starting the VPN service
            pendingIntent = PendingIntent.getService(getApplicationContext(), 0, vpnIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            VpnService.Builder builder = new VpnService.Builder();
// Set the VPN service configuration
            builder.setSession("MyVPNService");
// Set the VPN service interface
            builder.setConfigureIntent(pendingIntent);
// Set the VPN service handler
            builder.setMtu(1500);
            builder.addAddress("10.0.0.2", 24);
            builder.addRoute("0.0.0.0", 0);*/


            VpnService.Builder builder = new VpnService.Builder();

//            String newRemoteIP = "80.92.205.63";
            String newRemoteIP = responseModel.getServersList().get(pos).getIP();

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = connectivityManager.getAllNetworks();
            Network network = null;
            for (Network n : networks) {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(n);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    network = n;
                    break;
                }
            }


            if (SDK_INT >= Build.VERSION_CODES.Q) {
                pfd = builder
                        .setSession("MyVPN")
                        .addAddress(newRemoteIP, 24)
                        .setMtu(1500)
                        .setBlocking(true)
                        .setUnderlyingNetworks(new Network[] { network })
    //                    .setConfigureIntent(pendingIntent)
    //                    .addRoute("0.0.0.0", 0)
    //                    .addDnsServer("8.8.8.8")
                        .addRoute("::", 0)
                        .setMetered(false)
    //                    .allowBypass()
                        .establish();
            }


            // Start the VPN connection
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (pfd != null) {
                            // Route all traffic through the VPN interface

                            // Use the VPN connection
                            DatagramChannel tunnel = DatagramChannel.open();
//                            mService.protect(tunnel.socket());
//                            if (!mService.protect(tunnel.socket())) {
//                                throw new IllegalStateException("Cannot protect the tunnel");
//                            }

                            tunnel.connect(new InetSocketAddress(newRemoteIP, 443));
//                            tunnel.bind(new InetSocketAddress(newRemoteIP, 443));

                            Log.e(TAG, "run: " + tunnel.getLocalAddress() + "    " + tunnel.getRemoteAddress());
                            tunnel.configureBlocking(false);

//                            String parameters = handshakeServer(tunnel);
//                            pfd = configureVirtualInterface(parameters);
//                            Log.i(TAG, "New interface: " + pfd + " (" + parameters + ")");


                            tunnel.setOption(StandardSocketOptions.SO_SNDBUF, 0);
                            tunnel.setOption(StandardSocketOptions.SO_RCVBUF, 0);
                            try {
                                tunnel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                            }catch (Exception ignored){
                            }
                            tunnel.setOption(StandardSocketOptions.IP_TOS, 0x08);
                            tunnel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 0);
                            tunnel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);
//                        tunnel.setOption(ExtendedSocketOptions.IP_RECVERR, true);
                            Selector selector = Selector.open();
                            tunnel.register(selector, SelectionKey.OP_READ);

                            Log.e(TAG, "VPN connection established successfully   " + tunnel.isConnected());

//                            getCountryData();

                            setRoute();

                        } else {
                            Log.e(TAG, "VPN connection failed");
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                }
            });
            mThread.start();
        }
    }

    private void setRoute() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = mConnectivityManager.getAllNetworks();
        for (Network network : networks) {
            // Check if the network has Internet connectivity
            NetworkCapabilities capabilities = mConnectivityManager.getNetworkCapabilities(network);
            if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                // Get the network's interface name
                LinkProperties props = mConnectivityManager.getLinkProperties(network);
                if (props != null) {
                    String interfaceName = props.getInterfaceName();
                    Log.e(TAG, "setRoute: "+interfaceName );
                    // If the interface name matches the name of the VPN interface, use this network for traffic routing
                    if (interfaceName.equals("tun0")) {
                        // Set this network as the default network for traffic routing
                        mConnectivityManager.bindProcessToNetwork(network);
                        return;
                    }
                }
            }
        }

    }

    private List<String> getAppPackages() {
        List<String> list = new ArrayList<>();
        List<PackageInfo> apps = getPackageManager().getInstalledPackages(0);
        for (PackageInfo info : apps) {
            list.add(info.packageName);
        }

        return list;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mThread != null) {
            mThread.interrupt();
            Log.e("onDestroy: ", "called");
            stopService(VpnService.prepare(getApplicationContext()));
        }
    }
}
