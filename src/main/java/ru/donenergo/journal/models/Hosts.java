package ru.donenergo.journal.models;

import java.util.List;

public class Hosts {
    private List<Host> lhosts;

    public List<Host> getHosts() {
        return lhosts;
    }

    public void setHosts(List<Host> hosts) {
        this.lhosts = hosts;
    }

    public Hosts(List<Host> hosts) {
        this.lhosts = hosts;
    }

    public Hosts() {
    }
}
