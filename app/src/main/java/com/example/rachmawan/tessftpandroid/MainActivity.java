package com.example.rachmawan.tessftpandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
    private EditText edtJSON;
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

    private void getDataJSON()
    {
        new RetrieveFeedTask().execute("");

    }

    private void initUI() {
        btnFileChooser = (Button) findViewById(R.id.btn_choose);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnDownload = (Button) findViewById(R.id.btn_download);
        btnSigning = (Button) findViewById(R.id.btn_Signing);
        edtFilePath = (EditText) findViewById(R.id.edt_filename);
        edtPassphrase = (EditText) findViewById(R.id.edt_passphrase);
        edtJSON = (EditText) findViewById(R.id.edtBalikan);
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

    /*
    private void getJSON(String myurl) throws IOException {
        URL url = new URL(myurl);

        Log.d("dhedy", "The response is: " + myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000  milliseconds );
        //conn.setConnectTimeout(15000  milliseconds );
        /*conn.setRequestMethod("POST");
        conn.setDoInput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", token));
            params.add(new BasicNameValuePair("token_app", token_app));
            params.add(new BasicNameValuePair("username", serialnumber));

        String urlParameters = "token="+token+"&token_app="+token_app+"&username="+serialnumber;
        conn.getOutputStream().write(urlParameters.getBytes("UTF-8"));

        conn.connect();

        int response = conn.getResponseCode();
        Log.d("dhedy", "The response is: " + response);
        is = conn.getInputStream();

        // Convert the InputStream into a string
        String contentAsString = convertInputStreamToString(is, length);
        return contentAsString;

    }*/

    public class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                InputSource is = new InputSource(url.openStream());
                String jadi = is.toString();

                return jadi;
            } catch (Exception e) {
                this.exception = e;

                return "";
            }
        }

        protected void onPostExecute(String hasil) {
            edtJSON.setText(hasil);
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}
