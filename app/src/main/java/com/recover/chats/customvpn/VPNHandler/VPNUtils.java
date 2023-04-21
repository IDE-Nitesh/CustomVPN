package com.recover.chats.customvpn.VPNHandler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.RouteInfo;
import android.net.VpnManager;
import android.net.VpnService;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class VPNUtils {

    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private RouteInfo mDefaultRoute;

    public VPNUtils(Context context) {
        mContext = context;
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void setRoute() {
        Network[] networks = mConnectivityManager.getAllNetworks();
        for (Network network : networks) {
            // Check if the network has Internet connectivity
            NetworkCapabilities capabilities = mConnectivityManager.getNetworkCapabilities(network);
            if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                // Get the network's interface name
                LinkProperties props = mConnectivityManager.getLinkProperties(network);
                if (props != null) {
                    String interfaceName = props.getInterfaceName();
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
}
