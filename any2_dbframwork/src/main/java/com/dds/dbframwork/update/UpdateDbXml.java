package com.dds.dbframwork.update;

import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dds on 2019/7/17.
 * android_shuai@163.com
 */
public class UpdateDbXml {
    private final static String TAG = "dds_UpdateDbXml";
    private List<CreateVersion> createVersions;
    private List<UpdateStep> updateSteps;

    public UpdateDbXml(Document document) {
        // 获取创建数据库表
        NodeList createVersions = document.getElementsByTagName("createVersion");
        this.createVersions = new ArrayList<>();
        for (int i = 0; i < createVersions.getLength(); i++) {
            Element ele = (Element) (createVersions.item(i));
            Log.i(TAG, "升级版本:" + ele.toString());
            CreateVersion cv = new CreateVersion(ele);
            this.createVersions.add(cv);
        }
        // 获取升级步骤
        NodeList updateSteps = document.getElementsByTagName("updateStep");
        this.updateSteps = new ArrayList<>();
        for (int i = 0; i < updateSteps.getLength(); i++) {
            Element ele = (Element) updateSteps.item(i);
            UpdateStep updateStep = new UpdateStep(ele);
            this.updateSteps.add(updateStep);
        }

    }

    public List<UpdateStep> getUpdateSteps() {
        return updateSteps;
    }

    public void setUpdateSteps(List<UpdateStep> updateSteps) {
        this.updateSteps = updateSteps;
    }

    public List<CreateVersion> getCreateVersions() {
        return createVersions;
    }

    public void setCreateVersions(List<CreateVersion> createVersions) {
        this.createVersions = createVersions;
    }
}
