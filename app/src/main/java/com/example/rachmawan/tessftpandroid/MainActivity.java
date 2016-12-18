package com.example.rachmawan.tessftpandroid;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String[] list_file;

    private final String TAG = MainActivity.class.getSimpleName();
    private TextView browse_p12;
    private TextView browse_pdf;
    private TextView browse_img;
    private Button btnUpload;
    private Button btnDownload;
    private Button btnImage;
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
    private EditText edtImage;
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
    private static final int FILE_IMAGE_SELECT_CODE = 1;

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

    private void showImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select Image File"),
                    FILE_IMAGE_SELECT_CODE);

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataJSON()
    {
        new RetrieveFeedTask().execute("http://202.46.3.34/sisumaker/api/get_file_ttd", "1fbf7a34d75febed78719aacdb60c2d3", "197907082010011007");
    }

    private void initUI() {
        btnFileChooser = (Button) findViewById(R.id.btn_choose);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnDownload = (Button) findViewById(R.id.btn_download);
        btnSigning = (Button) findViewById(R.id.btn_Signing);
        edtFilePath = (EditText) findViewById(R.id.edt_filename);
        edtPassphrase = (EditText) findViewById(R.id.edt_passphrase);
        edtJSON = (EditText) findViewById(R.id.edtBalikan);
        edtImage = (EditText) findViewById(R.id.edt_imagepath);
        progBar_UD = (ProgressBar) findViewById(R.id.progBar_updown);
        radioGroup_user = (RadioGroup) findViewById(R.id.radio_user);
        radio_asep = (RadioButton) findViewById(R.id.radioAsep);
        radio_lina = (RadioButton) findViewById(R.id.radioLina);
        radio_magdalena = (RadioButton) findViewById(R.id.radioMagdalena);
        btnImage = (Button) findViewById(R.id.btn_image);
    }

    private void initEvent(){
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataJSON();
            }
        });

        btnFileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showP12Chooser();
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        btnSigning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SigningTask();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FTPUpload();
            }
        });
    }

    private void SigningTask() throws IOException, DocumentException {

        for (int i=0; i<list_file.length; i++)
        {
            StampQRCode("http://www.bppt.go.id", "/mnt/sdcard/QR_Gen/Hasil.png", "/mnt/sdcard/FTPSample/" + list_file[i], FileUtils.SplitExtensionPath("/mnt/sdcard/FTPSample/" + list_file[i]) + "_Stamped.pdf" );
            Single_TTD(FileUtils.SplitExtensionPath("/mnt/sdcard/FTPSample/" + list_file[i]) + "_Stamped.pdf", edtFilePath.getText().toString(), edtPassphrase.getText().toString(), edtImage.getText().toString());
        }
    }

    private void FTPUpload()
    {
        String [] arr_path_upload = new String[list_file.length];
        for (int i=0; i<list_file.length; i++)
        {
            arr_path_upload[i] = FileUtils.SplitExtensionPath(list_file[i]) + "_Stamped_SGN01.pdf";
        }

        new UploadSecureFTPTask().execute(arr_path_upload);
    }

    private void Single_TTD(String pathPDF, String pathP12, String passphrase, String imagePath)
    {
        String result = FileUtils.SplitExtensionPath(pathPDF);
        result += "_SGN01.pdf";

        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

        try {
            String src = "", dest = "", RESOURCE = "";
            src = pathPDF;
            dest = result;
            RESOURCE = imagePath;

            String path = pathP12;
            String keystore_password = passphrase;
            String key_password = passphrase;
            KeyStore ks = KeyStore.getInstance("pkcs12");

            ks.load(new FileInputStream(path), keystore_password.toCharArray());
            String alias = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(alias, key_password.toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);

            // reader and stamper
            PdfReader reader = new PdfReader(src);
            FileOutputStream os = new FileOutputStream(dest);
            PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
            // appearance
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
            appearance.setSignatureGraphic(Image.getInstance(RESOURCE));
            appearance.setReason("I've written this.");
            appearance.setLocation("Foobar");
            appearance.setVisibleSignature(new Rectangle(72, 732, 144, 780), 1, "first");
            appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);

            // digital signature
            ExternalSignature es = new PrivateKeySignature(pk, "SHA-256", "BC");
            ExternalDigest digest = new BouncyCastleDigest();
            MakeSignature.signDetached(appearance, digest, es, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

        } catch (KeyStoreException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (CertificateException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Toast.makeText(getApplicationContext(), "Dokumen sukses ditanda tangani", Toast.LENGTH_LONG).show();
    }

    private void StampQRCode (String text_to_QR, String temp_img, String filePDFsrc, String filePDFdest) throws IOException, DocumentException {
        //Upon Stamping QR Code in PDF, firstly must generate QR Code itself from text_to_QR, and save it into temp_img
        QRCode qc = new QRCode();
        qc.generateQRCode(text_to_QR, temp_img);

        //And after QR Code image was generated, stamp it right into PDF
        qc.manipulatePdf(filePDFsrc, filePDFdest, temp_img);
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
            case FILE_IMAGE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        Log.d(TAG, "File Uri: " + uri.toString());
                        String path = FileUtils.getPath(MainActivity.this, uri);
                        Log.d(TAG, "File Path: " + path);
                        imgPath = path;
                        edtImage.setText(imgPath);
                    } catch (Exception e) {
                        Log.e(TAG, "" + e);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Set up HTTP post
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000 /* milliseconds */);
                connection.setConnectTimeout(15000 /* milliseconds */);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);

                String urlParameters = "token="+params[1]+"&nip="+params[2];
                connection.getOutputStream().write(urlParameters.getBytes("UTF-8"));
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }
                return buffer.toString();

            } catch (Exception e) {
                this.exception = e;
                return "";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        protected void onPostExecute(String hasil) {
            super.onPostExecute(hasil);
            edtJSON.setText(hasil);
            Log.d("Jadi", hasil);

            try {
                JSONObject myobj = new JSONObject(hasil);
                JSONArray data = myobj.getJSONArray("data");

                list_file = new String[data.length()];
                for (int i=0; i<data.length(); i++)
                {
                    JSONObject c = data.getJSONObject(i);
                    list_file[i] = c.getString("file");
                    Log.d("Data ke " + (i+1), c.getString("file"));
                }

                new DownloadSecureFTPTask().execute(list_file);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadSecureFTPTask extends AsyncTask<String, Integer, Boolean>
    {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(String... params) {
            JSch obj_jsch = new JSch();
            Session session = null;

            boolean success = false;

            String ip_address = "202.46.3.34";
            int port = 22;
            String user = "ftpuser";
            String pass = "langsungmasuk";

            try {
                session = obj_jsch.getSession(user, ip_address, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(pass);
                session.connect();

                Channel channel = session.openChannel("sftp");
                channel.connect();
                ChannelSftp sftpChannel = (ChannelSftp) channel;

                for (int j=0; j<params.length; j++)
                {
                    Log.d("Param", params[j]);
                    progressMonitor pm = new progressMonitor();
                    try (FileOutputStream out = new FileOutputStream("/mnt/sdcard/FTPSample/" + params[j])) {
                        try (InputStream in = sftpChannel.get("www/" + params[j], pm)) {
                            // read from in, write to out
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = in.read(buffer)) != -1) {
                                out.write(buffer, 0, len);
                                publishProgress((new BigDecimal(pm.percent).intValueExact()));
                            }
                            success = true;
                        } catch (SftpException e) {
                            e.printStackTrace();
                            success = false;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }

                }

            } catch (JSchException e) {
                e.printStackTrace();
                Log.d("Coba", e.getMessage());
                return false;
            }

            return success;
        }

        protected void onProgressUpdate(Integer... values) {
            progBar_UD.setProgress(values[0]);
        }

        protected void onPostExecute(Boolean result) {
            edtJSON.setText((result==true)?"Download Berhasil":"Download Gagal");
        }
    }

    private class UploadSecureFTPTask extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params) {
            JSch obj_jsch = new JSch();
            Session session = null;

            boolean success = false;

            String ip_address = "202.46.3.34";
            int port = 22;
            String user = "ftpuser";
            String pass = "langsungmasuk";

            try {
                session = obj_jsch.getSession(user, ip_address, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(pass);
                session.connect();

                Channel channel = session.openChannel("sftp");
                channel.connect();
                ChannelSftp sftpChannel = (ChannelSftp) channel;

                for (int j=0; j<params.length; j++)
                {
                    progressMonitor pm = new progressMonitor();
                    sftpChannel.put("/mnt/sdcard/FTPSample/" + params[j], "/www/upload/" + params[j], pm);
                    publishProgress((new BigDecimal(pm.percent).intValueExact()));
                }

                success = true;
            } catch (JSchException e) {
                e.printStackTrace();
                return false;
            } catch (SftpException e) {
                e.printStackTrace();
                return false;
            }

            return success;
        }

        protected void onProgressUpdate(Integer... values) {
            progBar_UD.setProgress(values[0]);
        }

        protected void onPostExecute(Boolean result) {
            edtJSON.setText((result==true)?"Upload Berhasil":"Upload Gagal");
        }
    }

    public class progressMonitor implements SftpProgressMonitor {
        private long max                = 0;
        private long count              = 0;
        private long percent            = 0;

        @Override
        public void init(int i, String src, String dest, long max) {
            this.max = max;
            Log.d("TaskMonitor-Start", "starting");
            Log.d("TaskMonitor-Start", src); // Origin destination
            Log.d("TaskMonitor-Start", dest); // Destination path
            Log.d("TaskMonitor-Start", String.valueOf(max)); // Total filesize
        }

        @Override
        public boolean count(long bytes) {
            this.count += bytes;
            long percentNow = this.count*100/max;
            if(percentNow>this.percent){
                this.percent = percentNow;
                Log.d("TaskMonitor-Progress",String.valueOf(this.percent)); // Progress 0,0
                Log.d("TaskMonitor-Progress", String.valueOf(max)); //Total filesize
                Log.d("TaskMonitor-Progress", String.valueOf(this.count)); // Progress in bytes from the total
            }

            return(true);
        }

        @Override
        public void end() {
            Log.d("TaskMonitor-Finish", "finished");// The process is over
            Log.d("TaskMonitor-Finish", String.valueOf(this.percent)); // Progress
            Log.d("TaskMonitor-Finish", String.valueOf(max)); // Total filesize
            Log.d("TaskMonitor-Finish", String.valueOf(this.count)); // Process in bytes from the total

        }
    }


}
