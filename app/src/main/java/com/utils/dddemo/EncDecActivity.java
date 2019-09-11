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
            "MIIESzCCAzOgAwIBAgICCG8wDQYJKoZIhvcNAQEFBQAwYzEQMA4GA1UECAwHQmVp\n" +
            "amluZzEQMA4GA1UEBwwHSGFpZGlhbjESMBAGA1UECgwJVHJ1c3Rtb2JpMRIwEAYD\n" +
            "VQQLDAlUcnVzdG1vYmkxFTATBgNVBAMMDFRydXN0bW9iaSBDQTAgFw0xODExMjYw\n" +
            "NDU1NTNaGA8yMDY4MTExMzA0NTU1M1owgYsxCzAJBgNVBAYTAkNOMRAwDgYDVQQI\n" +
            "DAdCZWlqaW5nMRAwDgYDVQQHDAdCZWlqaW5nMRIwEAYDVQQKDAlUcnVzdG1vYmkx\n" +
            "DjAMBgNVBAsMBW1peGluMREwDwYDVQQDDAhMb25nbG9uZzEhMB8GCSqGSIb3DQEJ\n" +
            "ARYSdHJ1c3Rtb2JpQG1vYmkuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\n" +
            "CgKCAQEA3L5TSCdxkn6iP9S3/D7+M4b6VjQf9pmnYFIudpUDSeQZAtKxcabaGKIV\n" +
            "X5IAiTfIFWPLRW6f9wbUsHtg2gdlzh1HmBYLgZUQTcSu9W3GKqEfnE3vfybAWNn2\n" +
            "uPJflXaHd1qNkz1jpvROkCDQKFPihHaxHnyvPGQG8XaaMl1BN3LoyI9qUhOq6yaH\n" +
            "Ei3aaTwPTCLOYn0/jKXDkaFMo1DYdsYUq96qF8gDIUVhk//Bu7pISxVAxvV0mugd\n" +
            "YoIslVvU3TDm4U3B/BNMkx+r181Kf1bTz2cRQI7ea6NVbNZf+zMC9KWvQhxmZxEx\n" +
            "sJthxZ0fXbNGBz7sXf3JtQ2CFblNUwIDAQABo4HdMIHaMAkGA1UdEwQCMAAwHQYD\n" +
            "VR0OBBYEFPH8g3Bm7rWwMWXh6mSOGKJTTJgAMIGNBgNVHSMEgYUwgYKAFK7490tD\n" +
            "OT1PtSLnLkDWdE5uMqCBoWekZTBjMRAwDgYDVQQIDAdCZWlqaW5nMRAwDgYDVQQH\n" +
            "DAdIYWlkaWFuMRIwEAYDVQQKDAlUcnVzdG1vYmkxEjAQBgNVBAsMCVRydXN0bW9i\n" +
            "aTEVMBMGA1UEAwwMVHJ1c3Rtb2JpIENBggEBMAsGA1UdDwQEAwIE8DARBglghkgB\n" +
            "hvhCAQEEBAMCBaAwDQYJKoZIhvcNAQEFBQADggEBAIfEKc5GL7w1+7epHA1zX4th\n" +
            "t2Wdjx/A2JEcJ32As6IQpSoQ/E7ibKJUCraR5iezOlh10eFu0y3N2gNB7sxVjRbK\n" +
            "ft1jXRYMz6NI69sp03zYlQST0oK+VJbWEZnbDUb4EHmrewc5Xy0C7MAQMMsEQU/U\n" +
            "G5RoxCYE9I+eJbxHUVwrjO0cX//0Qw1J3fflSmrOjYg1GOiJilJQ9M3Ga7VHCXOm\n" +
            "RNLMgLsL3kPv95a2aMWKiups/mnQA4ai0gHDr95NBm9Tyf2WIlHFf6mGAEC/JaGH\n" +
            "C64TZfus/FREnswzaEdXjVWp1eVswMaiI1+0dR4gOt8kBfBcZ0PjqtvwLn+Kj1s=\n" +
            "-----END CERTIFICATE-----\n";

    String content = "3E3Cun3375779C357z19q1QP953Yce79";


    String priKey = "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";
    String pwd = "LCdAe53vkkQK5kL/envm0yVWxHKul5DW5ynZo3jB/jM=";
    String decContent = "EAFrd2JhuXbkLxx+PC/9o+itx7jM3YCpNdzhYmEIcI1JhvCt9ifdaY0uTUf4V7C5NhXm8TwjBPYshah1MNRqJkNVOreIcVeeaOFShFnAE07Lb617+1Aht8AuyQ8NzKEuqP5Q/hIfC8Ldx0F8unKYScOCJQzVhGYiROEFJJrGG9L6uM28pWnOuZDiho6+nLqtEEp06UDYQaQTMIfl0X14z97Z3zZMMjHu1nyuz6lETNjpLHWONcstaMhr1iuzCdc96BfoESaCayhH4RUfwZBBF5TUAa2dfe4Iw1DV3RMOT81QTUgbkWJxeQzMdFj5MKBaV+Sgm0rB0ANfCabRTTRY2w==";

    String result = "ZBDSYJABNJMQTVJMVAZAQVKCTVNVBLYI";

    public void rsaEnc(View view) {
        JavaRSA rsa = new JavaRSA();
        //RSA.KeyPair keyPair = rsa.generateAsymmetricKey();
        String s = rsa.encByPubKey(content, pubkey);
        text.setText(s);
    }

    public void rsaDec(View view) {
        JavaRSA rsa = new JavaRSA();
        String s = rsa.decByPriKey(decContent, priKey, pwd);
        if (BuildConfig.DEBUG) {
            assert s.equals(result);
        }
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
