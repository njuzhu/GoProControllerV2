package com.example.xinsun.wifitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {

    private static String TAG = "MainActivityNew";
    protected Button photoBtn;
    protected Button showBtn;
    protected Button listBtn;
    protected Button oneBtn;
    protected Button twoBtn;
    protected Button threeBtn;
    protected Button fourBtn;
    protected Button fiveBtn;
    protected Button sixBtn;
    protected Button quickLinkBtn;
    protected Button configBtn;
    protected Button shutdownBtn;

    protected Data data;
    protected int goproNum;
    protected boolean isQuickBtnClicked = false;

    protected ImageView preview;
    protected List<Fs> fsList;
    protected Fs[] fsArray;

    public static int downloadNum = 5;
    private final static String IMAGE_DOWNLOAD_PATH
            = Environment.getExternalStorageDirectory() + "/download_images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.registerReceiver(this.connReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        data = new Data("", null);
        initUI();
    }

    public void initUI(){
        photoBtn = (Button) findViewById(R.id.photo_btn);
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shot();
            }
        });
        showBtn = (Button) findViewById(R.id.show_btn);
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpAsyncTask().execute("http://10.5.5.9:8080/videos/DCIM/100GOPRO/");
            }
        });
        listBtn = (Button) findViewById(R.id.list_btn);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        preview = (ImageView) findViewById(R.id.image_view);

        oneBtn = (Button) findViewById(R.id.one_btn);
        oneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupGoProWifi(1);
            }
        });
        twoBtn = (Button) findViewById(R.id.two_btn);
        twoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupGoProWifi(2);
            }
        });
        threeBtn = (Button) findViewById(R.id.three_btn);
        threeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupGoProWifi(3);
            }
        });
        fourBtn = (Button) findViewById(R.id.four_btn);
        fourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupGoProWifi(4);
            }
        });
        fiveBtn = (Button) findViewById(R.id.five_btn);
        fiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupGoProWifi(5);
            }
        });
        sixBtn = (Button) findViewById(R.id.six_btn);
        sixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupGoProWifi(6);
            }
        });
        quickLinkBtn = (Button) findViewById(R.id.quick_link_btn);
        quickLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick");
                isQuickBtnClicked = true;
                setupGoProWifi(1);
            }
        });
        configBtn = (Button) findViewById(R.id.config_btn);
        configBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupModeAndParams();
            }
        });
        shutdownBtn = (Button) findViewById(R.id.shut_down_btn);
        shutdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpAsyncTask().execute("http://10.5.5.9/gp/gpControl/command/system/sleep");
            }
        });
    }

    public void Shot(){
        new HttpAsyncTask().execute("http://10.5.5.9/gp/gpControl/command/shutter?p=1");
    }

    public void saveBitmapToPath(Bitmap bitmap, String path) {
        try {
            OutputStream stream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGoProWifi(int num) {
        Log.i(TAG, "setopGoProWifi"+num);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"Auto3DPro" + num + "\"";
        wc.preSharedKey = "\"auto3d123\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
// connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
        goproNum = num;
    }

    private BroadcastReceiver connReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "BroadcastReceiver-onReceive");
            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
//                listAll();
                String ssid = getCurrentSSID();
                String expectSSID = "\"Auto3DPro" + goproNum + "\"";
                Log.i(TAG, ssid);
                Log.i(TAG, expectSSID);
                if(isQuickBtnClicked && ssid.equals(expectSSID)) {
                    Log.i(TAG, "success");
                    isQuickBtnClicked = false;
                    new ImageQuickHttpAsyncTask().execute();
//                    setupGoProWifi(goproNum+1);

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "连接Pro"+goproNum, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
//                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
            }
        }
    };

    private String getCurrentSSID() {
        Log.i(TAG, "getCurrentSSID");
        String ssid = "";
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    private void setupModeAndParams() {
        // change gopro to photo mode and also set resolution to 7pm med
        new HttpAsyncTask().execute("http://10.5.5.9/gp/gpControl/command/mode?p=1");
        new HttpAsyncTask().execute("http://10.5.5.9/gp/gpControl/setting/17/0");
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0], false);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

        }
    }

    private class ImageHttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... images) {

            return GET("http://10.5.5.9:8080/videos/DCIM/100GOPRO/"+images[0], true);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        }
    }

    private class ImageQuickHttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... images) {

            return getRequest("http://10.5.5.9/gp/gpMediaList");
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

        }
    }

    public String GET(String url, boolean isImage){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (isImage) {
                if(inputStream != null) {
                    setInputStreamToPreviewImage(inputStream);
                }
            } else {
                if(inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    System.out.println("Gopro: " + result);
                    Matcher m = Pattern.compile("\\\"GOPR\\d{4}\\.JPG\\\"").matcher(result);
                    int largest = -1;
                    while (m.find()) {
                        System.out.println("Found: " + m.group(0));
                        int digits = Integer.parseInt(m.group(0).replaceAll("[^0-9]", ""));
                        if (digits>largest)
                            largest = digits;
                    }
                    if (largest != -1) {
                        System.out.println("Latest Image: " + "GOPR"+String.format("%04d", largest) + ".JPG");
                        data.path = getOutputDir()+goproNum+"GOPR"+String.format("%04d", largest) + ".JPG";
                        new ImageHttpAsyncTask().execute("GOPR"+String.format("%04d", largest) + ".JPG");
                        //DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://10.5.5.9:8080/videos/DCIM/100GOPRO/"+"GOPR"+String.format("%04d", largest) + ".JPG"));
                        //request.setDescription("Some descrition");
                        //request.setTitle("Some title");
                        //request.allowScanningByMediaScanner();
                        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS+"/GOPRO", goproNum + "GOPR" + String.format("%04d", largest) + ".JPG");
                        //DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        //manager.enqueue(request);
                    }
                }
                else
                    result = "Did not work!";
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private String getRequest(String url) {
        Log.i(TAG, "getRequest");
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            Log.i(TAG, "httpSuccess");
            //statusCode == 200 正常
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            Log.i(TAG, "statuscode = "+statusCode);
            result = retrieveInputStream(httpResponse.getEntity());

            Log.i(TAG, result);
            jsonParse(result);

        }  catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private String retrieveInputStream(HttpEntity httpEntity) {
        int length = (int) httpEntity.getContentLength();
        //the number of bytes of the content, or a negative number if unknown. If the content length is known but exceeds Long.MAX_VALUE, a negative number is returned.
        //length==-1，下面这句报错，println needs a message
        if (length < 0) length = 10000;
        StringBuffer stringBuffer = new StringBuffer(length);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), HTTP.UTF_8);
            char buffer[] = new char[length];
            int count;
            while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
                stringBuffer.append(buffer, 0, count);
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return stringBuffer.toString();
    }

    private void jsonParse(String jsonData) {
        Log.i(TAG, "jsonParse");
        Gson gson = new Gson();
//        Log.i(TAG, jsonData);
//        String jsonData = "{\"id\":\"4455640950408444240\",\"media\":[{\"d\":\"100GOPRO\",\"fs\":" +
//                "[{\"n\":\"GOPR0023.JPG\",\"mod\":\"1435619000\",\"s\":\"2037620\"}," +
//                "{\"n\":\"GOPR0211.JPG\",\"mod\":\"1437221972\",\"s\":\"3977901\"},"+
//                "{\"n\":\"GOPR0001.JPG\",\"mod\":\"1431243123\",\"s\":\"3977901\"}]}]}";

//        Image image = gson.fromJson(jsonData, Image.class);
//        Log.i(TAG, image.toString());

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray mediaObject = jsonObject.getJSONArray("media");
            JSONArray fsArrayT = mediaObject.getJSONObject(0).getJSONArray("fs");
            fsList = new ArrayList<>();
            for(int i = 0; i<fsArrayT.length(); i++) {
//                Log.i(TAG, "loop:"+i);
                String n = (String) fsArrayT.getJSONObject(i).get("n");
                String mod = (String) fsArrayT.getJSONObject(i).get("mod");
                String s = (String) fsArrayT.getJSONObject(i).get("s");
                Fs fs = new Fs(n, mod, s);
//                Log.i(TAG, fs.toString());
                fsList.add(fs);
            }
            int length = fsList.size();
            fsArray = new Fs[length];
            for(int i = 0; i<length; i++) {
                fsArray[i] = fsList.get(i);
            }
            Arrays.sort(fsArray, new FsComprator());
            downloadImage();
//            for(Fs fsT: fsArray) {
//                Log.i(TAG, fsT.toString());
//            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }


    }

    private void downloadImage() {
        Log.i(TAG, "downloadImage");

//        Toast toast = Toast.makeText(getApplicationContext(),
//                "下载图片", Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();

//        "http://10.5.5.9:8080/videos/DCIM/100GOPRO/"
        ImageDownload[] imageDownloadArray = new ImageDownload[downloadNum];
        String basePath = "http://10.5.5.9:8080/videos/DCIM/100GOPRO/";
        for(int i = 0; i<downloadNum; i++) {
            String pathT = fsArray[i].getN();
            String fileNameT = ""+goproNum+pathT;
            pathT = basePath+pathT;
            ImageDownload imageDownload = new ImageDownload(pathT, fileNameT, "", null);
            imageDownloadArray[i] = imageDownload;
        }

        for(ImageDownload imageDownload: imageDownloadArray) {
            download(imageDownload);
        }
        isQuickBtnClicked = true;
        setupGoProWifi(goproNum+1);
    }

    private void download(ImageDownload imageDownload) {
        Log.i(TAG, "download");
        InputStream inputStream = null;
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(imageDownload.pathOnline));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            String fileName = imageDownload.fileName;

            File dirFile = new File(IMAGE_DOWNLOAD_PATH);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
            File myCaptureFile = new File(IMAGE_DOWNLOAD_PATH + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            Log.i(TAG, e.getLocalizedMessage());
        }
    }

    private void setInputStreamToPreviewImage(InputStream inputStream) throws IOException{
        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        saveBitmapToPath(bitmap, data.path);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                preview.setImageBitmap(bitmap);
            }
        });
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public String getOutputDir(){
        return "/sdcard/DCIM/GOPRO/";
    }

    private class Data {

        public String path;
        public Bitmap img;

        public Data (String path, Bitmap img) {
            this.path = path;
            this.img = img;
        }
    }

    private class ImageDownload {
        public String pathOnline;
        public String fileName;
        public String pathLocal;
        public Bitmap img;

        public ImageDownload(String pathOnline, String fileName, String pathLocal, Bitmap img) {
            this.pathOnline = pathOnline;
            this.fileName = fileName;
            this.pathLocal = pathLocal;
            this.img = img;
        }
    }
}
