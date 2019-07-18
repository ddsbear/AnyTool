package com.dds.dbframwork.update;

import android.util.Log;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dds on 2019/7/17.
 * android_shuai@163.com
 */
public class CreateVersion {
    private final static String TAG = "dds_CreateVersion";
    private String version;
    private List<CreateDb> createDbs;

    public CreateVersion(Element ele) {
        // 获取版本信息
        version = ele.getAttribute("version");
        Log.i(TAG, "Version:" + ele.toString());
        createDbs = new ArrayList<>();
        NodeList cs = ele.getElementsByTagName("createDb");
        for (int i = 0; i < cs.getLength(); i++) {
            Element ci = (Element) (cs.item(i));
            CreateDb cd = new CreateDb(ci);
            this.createDbs.add(cd);
        }


    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<CreateDb> getCreateDbs() {
        return createDbs;
    }

    public void setCreateDbs(List<CreateDb> createDbs) {
        this.createDbs = createDbs;
    }
}
