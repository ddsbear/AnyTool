package com.dds.dbframwork.update;

import android.content.Context;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dds on 2019/7/17.
 * android_shuai@163.com
 */
public class UpdateManager {
    private final static String TAG = "dds_UpdateManager";
    private Context context;
    private File parentFile;
    private File bakFile;
    private File xmlFile;
    private String thisVersion;

    public UpdateManager(Context context, String dbName) {
        this.context = context;
        File databasePath = context.getDatabasePath(dbName);
        parentFile = databasePath.getParentFile();
        bakFile = new File(parentFile, "backDb");
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!bakFile.exists()) {
            bakFile.mkdirs();
        }
    }


    public void checkCreateTable(Context context) {
        // 解析xml中的创建表的语句
        UpdateDbXml updateDbXml = readDbXml(context);

        //读取当前版本号
        File filesDir = context.getFilesDir();
        xmlFile = new File(filesDir, "update.xml");
        thisVersion = readVersion(xmlFile);
        Log.i(TAG, "thisVersion=" + thisVersion);


    }


    // 解析xml
    private UpdateDbXml readDbXml(Context context) {
        InputStream is = null;
        Document document = null;
        try {
            is = context.getAssets().open("update.xml");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (document == null) {
            return null;
        }
        return new UpdateDbXml(document);
    }


    // 读取文件中xml的版本号
    private String readVersion(File file) {
        InputStream is = null;
        Document document = null;
        try {
            is = new FileInputStream(file);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(is);
            NodeList createVersions = document.getElementsByTagName("createVersion");
            Element ele = (Element) createVersions.item(createVersions.getLength() - 1);
            return ele.getAttribute("version");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;

    }


}
