package com.example.xinsun.wifitest;

import java.util.Comparator;

/**
 * Created by aecdvkl on 15/7/20.
 */
public class Fs {
    private String n;
    private String mod;
    private String s;

    public Fs() {
        super();
    }

    public Fs(String n, String mod, String s) {
        this.n = n;
        this.mod = mod;
        this.s = s;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return "Fs{" +
                "n='" + n + '\'' +
                ", mod='" + mod + '\'' +
                ", s='" + s + '\'' +
                '}';
    }
}

class FsComprator implements Comparator {
    @Override
    public int compare(Object arg0, Object arg1) {
        Fs fs1 = (Fs) arg0;
        Fs fs2 = (Fs) arg1;
        return (fs2.getMod()).compareTo(fs1.getMod());
    }
}