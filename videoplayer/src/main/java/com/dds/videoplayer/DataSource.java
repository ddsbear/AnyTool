package com.dds.videoplayer;

import java.util.LinkedHashMap;

/**
 * Created by dds on 2019/7/4.
 * android_shuai@163.com
 */
public class DataSource {

    public static final String URL_KEY_DEFAULT = "URL_KEY_DEFAULT";
    public String title = "";
    public LinkedHashMap<String, String> urlsMap = new LinkedHashMap<>();
    public int currentUrlIndex;


    public DataSource(String url) {
        urlsMap.put(URL_KEY_DEFAULT, url);
        currentUrlIndex = 0;
    }

    public DataSource(String url, String title) {
        urlsMap.put(URL_KEY_DEFAULT, url);
        this.title = title;
        currentUrlIndex = 0;
    }

    public DataSource(LinkedHashMap<String, String> urlsMap) {
        this.urlsMap.clear();
        this.urlsMap.putAll(urlsMap);
        currentUrlIndex = 0;
    }

    public DataSource(LinkedHashMap<String, String> urlsMap, String title) {
        this.urlsMap.clear();
        this.urlsMap.putAll(urlsMap);
        this.title = title;
        currentUrlIndex = 0;
    }

    public Object getCurrentUrl() {
        return getValueFromLinkedMap(currentUrlIndex);
    }

    public Object getValueFromLinkedMap(int index) {
        int currentIndex = 0;
        for (Object key : urlsMap.keySet()) {
            if (currentIndex == index) {
                return urlsMap.get(key);
            }
            currentIndex++;
        }
        return null;
    }

    public boolean containsTheUrl(String object) {
        if (object != null) {
            return urlsMap.containsValue(object);
        }
        return false;
    }


    public DataSource cloneMe() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>(urlsMap);
        return new DataSource(map, title);
    }
}
