package com.example.administrator.day808;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

import static android.R.id.list;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public interface ProductService {

    @GET("ProductServer/servlet/Product")
    Call<ProductBean> getProductList(@QueryMap Map<String,String> map);

    @GET("DetectApkServlet/servlet/DetectApk")
    Call<DetectApk> detectApk(@QueryMap Map<String,Object> map);

    @FormUrlEncoded
    @POST("ProductServer/servlet/Product")
    Call<ProductBean> getProductPage(@FieldMap Map<String,String> map);

    @POST("ProductServer/servlet/Product")
    Call<ProductBean> getProductBody(@Body List<ProductRequest> request);

    @GET("ProductServer/servlet/{servletClass}")
    Call<ProductBean> getProductPath(@Path("servletClass") String path,
                                     @Query("method") String method);
    @Streaming
    @GET("ProductServer/{pic}")
    Call<ResponseBody> downPictrue(@Path("pic") String pictrue);

    @Streaming
    @GET
    Call<ResponseBody> downApk(@Url String url);

    @Multipart
    @POST("UpdateServlet/servlet/Upload")
    Call<String> uploadFile(@Part MultipartBody.Part file);
}
