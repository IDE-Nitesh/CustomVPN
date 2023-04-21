package com.recover.chats.customvpn.VPNHandler;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetAPIInterface {

    @GET("api.php?action=get_servers")
    Call<DubaiVpnResponseModel> getDubaiVPNData();
}
