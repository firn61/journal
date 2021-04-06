package ru.donenergo.journal.models;

public class Host {
    private String ip;
    private String rights;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public Host(String ip, String rights) {
        this.ip = ip;
        this.rights = rights;
    }

    public Host() {
    }
}
