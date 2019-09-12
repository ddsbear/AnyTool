package com.utils.dddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dds.cipher.javaImpl.JavaAES;
import com.dds.cipher.javaImpl.JavaRSA;

public class EncDecActivity extends AppCompatActivity {
    private EditText edit;
    private TextView text;


    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, EncDecActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enc_dec);
        edit = findViewById(R.id.edit);
        text = findViewById(R.id.text);
    }

    String pubkey = "-----BEGIN CERTIFICATE-----\n" +
            "MIIESzCCAzOgAwIBAgICIWcwDQYJKoZIhvcNAQEFBQAwYzEQMA4GA1UECAwHQmVp\n" +
            "amluZzEQMA4GA1UEBwwHSGFpZGlhbjESMBAGA1UECgwJVHJ1c3Rtb2JpMRIwEAYD\n" +
            "VQQLDAlUcnVzdG1vYmkxFTATBgNVBAMMDFRydXN0bW9iaSBDQTAgFw0xOTA2Mjgw\n" +
            "MjQ4MjNaGA8yMDY5MDYxNTAyNDgyM1owgYsxCzAJBgNVBAYTAkNOMRAwDgYDVQQI\n" +
            "DAdCZWlqaW5nMRAwDgYDVQQHDAdCZWlqaW5nMRIwEAYDVQQKDAlUcnVzdG1vYmkx\n" +
            "DjAMBgNVBAsMBW1peGluMREwDwYDVQQDDAh4dWViaTEyMzEhMB8GCSqGSIb3DQEJ\n" +
            "ARYSdHJ1c3Rtb2JpQG1vYmkuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\n" +
            "CgKCAQEA26VKYXqNmHClEs8pds9tJoI9kYYIlkA5FwKtrd+EO2akb1xSSXoE2lj/\n" +
            "1l5d7/DVMTI97uFgYF+obtiYnYMFxNQBkSOOeaJLh+5xMLZ3Nabx7Uj+Av/NN1Mk\n" +
            "czPDWHQLr1MPZ0wJA/Ci9tFSheQZ9crFlFhJ+63iHJXltutDGaer9/aDgohfvWki\n" +
            "1FouyXVrRdC0Qmmk/MzLbdOfmFViEbZAlfTUVjvKiyYN+mAICNez19nmIoHuEHly\n" +
            "ZF29j6A0CC/VJsc8CW7VBctqtDH0akn/VX5rwdSWEEfDSvqVwhag000vk1Wj9i9s\n" +
            "kHkMjich2tcqAVVIsHmG1GmHSKa/RwIDAQABo4HdMIHaMAkGA1UdEwQCMAAwHQYD\n" +
            "VR0OBBYEFNPqpJZe7ZdFt2rlHNn66jVtjRP2MIGNBgNVHSMEgYUwgYKAFK7490tD\n" +
            "OT1PtSLnLkDWdE5uMqCBoWekZTBjMRAwDgYDVQQIDAdCZWlqaW5nMRAwDgYDVQQH\n" +
            "DAdIYWlkaWFuMRIwEAYDVQQKDAlUcnVzdG1vYmkxEjAQBgNVBAsMCVRydXN0bW9i\n" +
            "aTEVMBMGA1UEAwwMVHJ1c3Rtb2JpIENBggEBMAsGA1UdDwQEAwIE8DARBglghkgB\n" +
            "hvhCAQEEBAMCBaAwDQYJKoZIhvcNAQEFBQADggEBAH3Tic4y2vmkrKqLBjD0p8fe\n" +
            "YVinTEUpeXdqpANwg6ngh1rKC+Hq7lODEOuDHossxXh+UC2j2t5nzdCiTdjjcyQr\n" +
            "aAeN0iqUrVx1DX6xLOviaM5H1ipa9yJnlN1ijuphg384CZ0rDXjWtWqSXhziqGW+\n" +
            "MZFgLJ1ntuib+6w3TxPRAxhD1C4pIiw97HJ5ZmLIKvL5uORPEGx+3ilmdAidyNMZ\n" +
            "GR8t0e+a1522uhxP5QKBtaCCk3+c5esEoZihGS88HJtOBDY/Gu9HAgemH2//CGZS\n" +
            "apUoBfkcRw9SdUfHLIOry+IBMLhVDVPwnxdgCP79SVdz2hIu+p/TCku/RnVipgk=\n" +
            "-----END CERTIFICATE-----\n";

    String content = "1234567890";


    String priKey = "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";
    String pwd = "qoE9VMiDjF60n1NI+mxUsKpHbzC8rSzq3sRZEpMIERk=";
    String decContent = "";

    public void rsaEnc(View view) {
        JavaRSA rsa = new JavaRSA();
        String s = rsa.encByPubKey(content, pubkey);
        decContent = s;
        text.setText(s);
    }

    public void rsaDec(View view) {
        JavaRSA rsa = new JavaRSA();
        String s = rsa.decByPriKey(decContent, priKey, pwd);
        text.setText(s);
    }

    String aesContent = "5d173A31lO9Yb5k57x7oC5m3915ai5g7";
    String aesKey = "GAOQXQQ99QPKOMTZE9YF96OLTD8EU6T9";
    String aesResult = "Noo8JUZKgjEwBa689psm8lTt9GNu1a2B3Tlx2Q4fNdtiFZDR3J0MNtxJ/loMiUrU";


    String aesDecContent = "UjluZ76iMpzkuZbvnSOu8g==";
    String aesDecKey = "MJCJBPHSNLPGPKGJIPNCWKTANLUEYMCA";
    String aesDecResult = "test";


    public void aesEnc(View view) {
        JavaAES aes = new JavaAES(32);
        String s = aes.encText(aesContent, aesKey);
        text.setText(s);
    }

    public void aesDec(View view) {
        JavaAES aes = new JavaAES(32);
        String s = aes.decText(aesDecContent, aesDecKey, 32);
        text.setText(s);
    }
}
