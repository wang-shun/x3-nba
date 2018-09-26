package com.ftkj.manager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Service {
    private Set<User> users = ConcurrentHashMap.newKeySet();
    private String key;

    public void putUser(User user) {
        if (user == null) { return; }
        this.users.add(user);
    }

    public void remove(User user) {
        if (user == null) {
            return;
        }
        this.users.remove(user);
    }

    public int size() {
        return this.users.size();
    }

    public String getKey() {
        return this.key;
    }

    public Set<User> getUsers() {
        return users;
    }

}
