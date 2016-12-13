package com.example.rachmawan.tessftpandroid;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private TextView browse_p12;
    private TextView browse_pdf;
    private TextView browse_img;
    private Button btnUpload;
    private Button btnDownload;
    private Button btnFileChooser;
    private RadioGroup radioGroup_user;
    private ProgressBar progBar_UD;
    private String pdfPath;
    private String p12Path;
    private String imgPath;
    private EditText edtNamaFile;
    private EditText edtPassphrase;
    private EditText edtLuas;
    private Button btnHitungLuas;
    private Button btnPindah;
    private Button btnQR;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int FILE_PDF_SELECT_CODE = 1;
    private static final int FILE_P12_SELECT_CODE = 0;
    private static final int FILE_IMAGE_SELECT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void showP12Chooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pkcs12");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select P12 File"),
                    0);

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }
}
