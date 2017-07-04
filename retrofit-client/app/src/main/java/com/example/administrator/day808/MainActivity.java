package com.example.administrator.day808;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    ProductService service;
    ImageView iv;
    long currLen = 0;
    Bitmap bm;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.iv);
        initRetrofit();

    }

    public void uploadFile(View view) {
//        MultipartBody.Part part = new MultipartBody.Part(RequestBody.create(MediaType.parse("multipart/form-data"));
        File file = new File("/mnt/sdcard/mypic.png");
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("pictrue",file.getName(),requestFile);
        Call<String> call = service.uploadFile(part);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("wang","url " +response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    public void apkDetect(View view){
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.administrator.day808",0);
            int versionCode = info.versionCode;
            Map<String,Object> map = new HashMap<>();
            map.put("version",versionCode);
            Call<DetectApk> call = service.detectApk(map);
            call.enqueue(new Callback<DetectApk>() {
                @Override
                public void onResponse(Call<DetectApk> call, final Response<DetectApk> response) {
                    if(response.body().getUpdate() == 1) {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("检查版本")
                                .setMessage("有新版本发现")
                                .setPositiveButton("确定",new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        downloadApk(response.body().getUrl());
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .create();
                        alertDialog.show();

                    }
                }

                @Override
                public void onFailure(Call<DetectApk> call, Throwable t) {
                   t.printStackTrace();
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void retrofitDown(View view) {

        Call<ResponseBody> call = service.downPictrue("lianx.jpg");

        //http://192.168.0.2:8080/ProductServer/servlet/Product?method=productPage&id=14
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {

                new Thread(){
                  @Override
                  public void run() {
                      super.run();
                      InputStream is = null;
                      try {
                          is = response.body().byteStream();
                          OutputStream os = new FileOutputStream("/mnt/sdcard/aat.jpg");
                          byte[] buf = new byte[1024];
                          int len = 0;
                          while ((len = is.read(buf)) != -1) {

                              os.write(buf,0,len);
                          }
                          os.close();
                          is.close();
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                      bm = BitmapFactory.decodeFile("/mnt/sdcard/aat.jpg");

                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              iv.setImageBitmap(bm);
                          }
                      });

                  }
              }.start();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void downloadApk(String url) {


        Call<ResponseBody> call = service.downApk(url);

        //http://192.168.0.2:8080/ProductServer/servlet/Product?method=productPage&id=14
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax((int) response.body().contentLength());
                progressDialog.setIndeterminate(false);
                progressDialog.setTitle("apk下载中....");
                progressDialog.show();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        InputStream is = null;
                        try {
                            //文件总长度
                            long total = response.body().contentLength();
                            is = response.body().byteStream();
                            OutputStream os = new FileOutputStream("/mnt/sdcard/update.apk");
                            byte[] buf = new byte[1024];
                            int len = 0;
                            //当前已经下载的长度

                            while ((len = is.read(buf)) != -1) {
                                Log.d("wang","total " + total + " currLen " + currLen);
                                currLen += len;
                                os.write(buf,0,len);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.setProgress((int) currLen);
                                    }
                                });
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            });
                            os.close();
                            is.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/update.apk")), "application/vnd.android.package-archive");
                                MainActivity.this.startActivity(intent);
                            }
                        });

                    }
                }.start();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    public void retrofitApk(View view) {


        Call<ResponseBody> call = service.downPictrue("update.apk");

        //http://192.168.0.2:8080/ProductServer/servlet/Product?method=productPage&id=14
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        InputStream is = null;
                        try {
                            //文件总长度
                            long total = response.body().contentLength();
                            is = response.body().byteStream();
                            OutputStream os = new FileOutputStream("/mnt/sdcard/update.apk");
                            byte[] buf = new byte[1024];
                            int len = 0;
                            //当前已经下载的长度
                            long currLen = 0;
                            while ((len = is.read(buf)) != -1) {
                                Log.d("wang","total " + total + " currLen " + currLen);
                                currLen += len;
                                os.write(buf,0,len);
                            }

                            os.close();
                            is.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/update.apk")), "application/vnd.android.package-archive");
                                MainActivity.this.startActivity(intent);
                            }
                        });

                    }
                }.start();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    public void retrofitPath(View view) {

        Call<ProductBean> call = service.getProductPath("Product","productList");

        //http://192.168.0.2:8080/ProductServer/servlet/Product?method=productPage&id=14
        call.enqueue(new Callback<ProductBean>() {
            @Override
            public void onResponse(Call<ProductBean> call, Response<ProductBean> response) {
                List<ProductChild> child = response.body().getResult();
                tv.setText(child.get(0).getDesc() + " " + child.get(1).getDesc());
            }

            @Override
            public void onFailure(Call<ProductBean> call, Throwable t) {

            }
        });
    }
    public void retrofitBody(View view) {

       ProductRequest request = new ProductRequest();
      request.method = "productPage";
        request.id = "14";
        List<ProductRequest> list = new ArrayList<>();
        list.add(request);
        ProductRequest request1 = new ProductRequest();
        request1.method = "productPage1";
        request1.id = "141";
        list.add(request1);
        Call<ProductBean> call = service.getProductBody(list);

        //http://192.168.0.2:8080/ProductServer/servlet/Product?method=productPage&id=14
        call.enqueue(new Callback<ProductBean>() {
            @Override
            public void onResponse(Call<ProductBean> call, Response<ProductBean> response) {
                List<ProductChild> child = response.body().getResult();
                tv.setText(child.get(0).getDesc() + " " + child.get(1).getDesc());
            }

            @Override
            public void onFailure(Call<ProductBean> call, Throwable t) {

            }
        });
    }
    public void retrofitGet(View view) {

        Map<String,String> map = new HashMap<>();
        map.put("method","productList");
//        map.put("id","14");
        Call<ProductBean> call = service.getProductList(map);

        //http://192.168.0.2:8080/ProductServer/servlet/Product?method=productPage&id=14
        call.enqueue(new Callback<ProductBean>() {
            @Override
            public void onResponse(Call<ProductBean> call, Response<ProductBean> response) {
                List<ProductChild> child = response.body().getResult();
                tv.setText(child.get(0).getDesc() + " " + child.get(1).getDesc());
            }

            @Override
            public void onFailure(Call<ProductBean> call, Throwable t) {

            }
        });
    }
    public void retrofitPost(View view) {
        Map<String,String> map = new HashMap<>();
        map.put("method","productList");
//        map.put("id","14");
        Call<ProductBean> call = service.getProductPage(map);

        //http://192.168.0.2:8080/ProductServer/servlet/Product?method=productPage&id=14
        call.enqueue(new Callback<ProductBean>() {
            @Override
            public void onResponse(Call<ProductBean> call, Response<ProductBean> response) {
                List<ProductChild> child = response.body().getResult();
                tv.setText(child.get(0).getDesc() + " " + child.get(1).getDesc());
            }

            @Override
            public void onFailure(Call<ProductBean> call, Throwable t) {

            }
        });
    }

    private void initRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                //把固定不变的url写到这里
                .baseUrl("http://192.168.0.2:8080/")
                //支持返回字符串,先写字符串
                .addConverterFactory(ScalarsConverterFactory.create())
                //支持解析返回的json，后写json解析
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //创建一个接口的实现类
        service = retrofit.create(ProductService.class);

    }
}
