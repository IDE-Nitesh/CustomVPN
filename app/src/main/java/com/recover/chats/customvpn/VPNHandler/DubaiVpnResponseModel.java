package com.recover.chats.customvpn.VPNHandler;

import java.util.ArrayList;

public class DubaiVpnResponseModel {
    String message;
    int status;
    ArrayList<serversDTO> servers;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<serversDTO> getServersList() {
        return servers;
    }

    public void setServersList(ArrayList<serversDTO> serversList) {
        this.servers = serversList;
    }

    public static class serversDTO {
        String server_id;
        String ServerStatus;
        String HostName;
        String city;
        String Flag;
        String IP;
        String certificate;
        String type;
        String username;
        String password;

        public String getServer_id() {
            return server_id;
        }

        public void setServer_id(String server_id) {
            this.server_id = server_id;
        }

        public String getServerStatus() {
            return ServerStatus;
        }

        public void setServerStatus(String serverStatus) {
            ServerStatus = serverStatus;
        }

        public String getHostName() {
            return HostName;
        }

        public void setHostName(String hostName) {
            HostName = hostName;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getFlag() {
            return Flag;
        }

        public void setFlag(String flag) {
            Flag = flag;
        }

        public String getIP() {
            return IP;
        }

        public void setIP(String IP) {
            this.IP = IP;
        }

        public String getCertificate() {
            return certificate;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
