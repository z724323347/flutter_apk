package com.flutter.plugin.app.flutter_download;

import java.util.List;

public class AppInfo {
    private String url;
    private String name;
    private String id;
    private String android_version;
    private int android_build;
    private String android_time;
    private String android_size;
    private String ios_version;
    private int ios_build;
    private String ios_time;
    private String ios_size;
    private List<String> tips;
    private String msg;

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setAndroid_version(String android_version) {
        this.android_version = android_version;
    }
    public String getAndroid_version() {
        return android_version;
    }

    public void setAndroid_build(int android_build) {
        this.android_build = android_build;
    }
    public int getAndroid_build() {
        return android_build;
    }

    public void setAndroid_time(String android_time) {
        this.android_time = android_time;
    }
    public String getAndroid_time() {
        return android_time;
    }

    public void setAndroid_size(String android_size) {
        this.android_size = android_size;
    }
    public String getAndroid_size() {
        return android_size;
    }

    public void setIos_version(String ios_version) {
        this.ios_version = ios_version;
    }
    public String getIos_version() {
        return ios_version;
    }

    public void setIos_build(int ios_build) {
        this.ios_build = ios_build;
    }
    public int getIos_build() {
        return ios_build;
    }

    public void setIos_time(String ios_time) {
        this.ios_time = ios_time;
    }
    public String getIos_time() {
        return ios_time;
    }

    public void setIos_size(String ios_size) {
        this.ios_size = ios_size;
    }
    public String getIos_size() {
        return ios_size;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }
    public List<String> getTips() {
        return tips;
    }

}