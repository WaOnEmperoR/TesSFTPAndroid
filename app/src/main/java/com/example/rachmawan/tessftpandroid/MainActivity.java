package com.example.rachmawan.tessftpandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private TextView browse_p12;
    private TextView browse_pdf;
    private TextView browse_img;
    private Button btnUpload;
    private Button btnDownload;
    private Button btnFileChooser;
    private Button btnSigning;
    private RadioGroup radioGroup_user;
    private RadioButton radio_asep;
    private RadioButton radio_lina;
    private RadioButton radio_magdalena;
    private ProgressBar progBar_UD;
    private String pdfPath;
    private String p12Path;
    private String imgPath;
    private EditText edtFilePath;
    private EditText edtPassphrase;
    private Button btnHitungLuas;
    private Button btnPindah;
    private Button btnQR;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int FILE_P12_SELECT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        initUI();
        initEvent();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void showP12Chooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pkcs12");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select P12 File"),
                    FILE_P12_SELECT_CODE);

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        btnFileChooser = (Button) findViewById(R.id.btn_choose);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnDownload = (Button) findViewById(R.id.btn_download);
        btnSigning = (Button) findViewById(R.id.btn_Signing);
        edtFilePath = (EditText) findViewById(R.id.edt_filename);
        edtPassphrase = (EditText) findViewById(R.id.edt_passphrase);
        progBar_UD = (ProgressBar) findViewById(R.id.progBar_updown);
        radioGroup_user = (RadioGroup) findViewById(R.id.radio_user);
        radio_asep = (RadioButton) findViewById(R.id.radioAsep);
        radio_lina = (RadioButton) findViewById(R.id.radioLina);
        radio_magdalena = (RadioButton) findViewById(R.id.radioMagdalena);
    }

    private void initEvent(){
        btnFileChooser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showP12Chooser();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case FILE_P12_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        Log.d(TAG, "File Uri: " + uri.toString());
                        String path = FileUtils.getPath(MainActivity.this, uri);
                        Log.d(TAG, "File Path: " + path);
                        p12Path = path;
                        edtFilePath.setText(p12Path);
                    } catch (Exception e) {
                        Log.e(TAG, "" + e);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
