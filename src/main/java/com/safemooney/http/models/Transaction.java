package com.safemooney.http.models;

import java.util.Date;

public class Transaction
{
    private int id;
    private int user1Id;
    private int user2Id;
    private String count;
    private Date date;
    private int period;
    private boolean isPermited;
    private boolean isClosed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2id) {
        this.user2Id = user2id;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public boolean isPermited() {
        return isPermited;
    }

    public void setPermited(boolean permited) {
        isPermited = permited;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
