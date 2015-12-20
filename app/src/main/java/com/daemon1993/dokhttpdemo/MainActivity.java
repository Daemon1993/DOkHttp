package com.daemon1993.dokhttpdemo;

import android.app.ProgressDialog;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daemon1993.dokhttp.OkHttpUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_get;
    private Button bt_post;
    private Button bt_upload;
    private Button bt_download;

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog1;
    private TextView tv_show;
    private File file;

    public static final String dir= Environment.getExternalStorageDirectory()+"/OkHttpDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_get=(Button)findViewById(R.id.bt_get);
        bt_post=(Button)findViewById(R.id.bt_post);
        bt_upload=(Button)findViewById(R.id.bt_upload);
        bt_download=(Button)findViewById(R.id.bt_download);
        tv_show=(TextView)findViewById(R.id.tv_show);


        bt_download.setOnClickListener(this);
        bt_get.setOnClickListener(this);
        bt_post.setOnClickListener(this);
        bt_upload.setOnClickListener(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog1=new ProgressDialog(this);
        progressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        File file =new File(dir);
        if(file!=null){
            file.mkdirs();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_download:
                dowmload();
                break;
            case R.id.bt_get:
                getAction();
                break;

            case R.id.bt_post:
                postAction();
                break;

            case R.id.bt_upload:
                upload();
                break;

        }
    }

    private void upload() {


        if(file==null){
            Toast.makeText(this,"请先下载文件",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("upload");
        progressDialog.show();
        progressDialog.setProgress(0);

//
        MultipartBuilder mb = new MultipartBuilder();
        mb.type(MultipartBuilder.FORM);
        mb.addFormDataPart("user_id", "74");
        mb.addFormDataPart("user_head", file.getName(), RequestBody.create(null, file));
        RequestBody requestBody = mb.build();

        String url="http://120.25.200.175:8080/jjying_pc/upd_setUser.action";

        OkHttpUtil.getInstance().uploadPost2ServerProgress(this,url,requestBody,new OkHttpUtil.MyCallBack(){

            @Override
            public void onFailure(Request request, IOException e) {
                tv_show.setText(e.getMessage());
            }

            @Override
            public void onResponse(String json) {
                progressDialog.dismiss();
                tv_show.setText(json);
            }
        },new OkHttpUtil.UIchangeListener(){

            @Override
            public void progressUpdate(long bytesWrite, long contentLength, boolean done) {
                int progress= (int) (bytesWrite*100/contentLength);

                progressDialog.setProgress(progress);

                tv_show.append(progress+"\n");
            }
        });

    }

    private void postAction() {

        progressDialog1.setMessage("postAction");
        progressDialog1.show();

        RequestBody requestBody=new FormEncodingBuilder()
                .add("key","value")
                .build();

        Request request=new Request.Builder()
                .post(requestBody)
                .tag(this)
                .url("http:www.baidu.com")
                .build();

        OkHttpUtil.getInstance().getData4Server(request, new OkHttpUtil.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(String json) {
                tv_show.setText(json);
                progressDialog1.dismiss();
            }
        });

    }

    private void getAction() {

        progressDialog1.setMessage("getAction");
        progressDialog1.show();

        Request request=new Request.Builder()
                .get()
                .tag(this)
                .url("http:www.baidu.com")
                .build();

        OkHttpUtil.getInstance().getData4Server(request, new OkHttpUtil.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(String json) {
                tv_show.setText(json);
                progressDialog1.dismiss();
            }
        });


    }

    private void dowmload() {

        progressDialog.setMessage("dowmload");
        progressDialog.setMax(100);
        progressDialog.show();


        Request request=new Request.Builder()
                .tag(this)
                .get()
                .url("http://7xnbj0.com1.z0.glb.clouddn.com/IMG_1919.jpg")
                .build();

        OkHttpUtil.getInstance().download4ServerListener(request, new OkHttpUtil.MyCallBack_Progress() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) {
                saveFile2Local(response);
                progressDialog.dismiss();
            }
        }, new OkHttpUtil.UIchangeListener() {
            @Override
            public void progressUpdate(long bytesWrite, long contentLength, boolean done) {
                int progress= (int) (bytesWrite*100/contentLength);
                Log.e("Download",progress+"");

                progressDialog.setProgress(progress);

                tv_show.append(progress+"\n");
            }
        });

    }

    private void saveFile2Local(Response response) {
        InputStream inputStream = null;
        OutputStream output = null;

        try {
            inputStream = response.body().byteStream();
            file = new File(dir, "download.jpg");
            output = new FileOutputStream(file);
            byte[] buff = new byte[1024 * 4];
            while (true) {
                int readed = inputStream.read(buff);
                if (readed == -1) {
                    break;
                }
                //write buff
                output.write(buff, 0, readed);
            }
            output.flush();
        } catch (IOException e) {
            file = null;
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtil.getInstance().mOkHttpClient.cancel(this);
    }
}
