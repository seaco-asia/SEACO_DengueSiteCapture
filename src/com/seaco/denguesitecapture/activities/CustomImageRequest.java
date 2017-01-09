//package com.seaco.denguesitecapture.activities;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.support.v4.util.LruCache;
//
///**
// * Created by Belal on 12/5/2015.
// */
//
//public class CustomImageRequest {
//
//    private static Context context;
//    private RequestQueue requestQueue;
//    private ImageLoader imageLoader;
//    
//    private CustomImageRequest(Context context) {
//        this.context = context;
//        this.requestQueue = getRequestQueue();
//
//        imageLoader = new ImageLoader(requestQueue,
//                new ImageLoader.ImageCache() {
//                    private final LruCache<String, Bitmap>
//                            cache = new LruCache<String, Bitmap>(20);
//
//                    @Override
//                    public Bitmap getBitmap(String url) {
//                        return cache.get(url);
//                    }
//
//                    @Override
//                    public void putBitmap(String url, Bitmap bitmap) {
//                        cache.put(url, bitmap);
//                    }
//                });
//    }
//
//    public static synchronized CustomVolleyRequest getInstance(Context context) {
//        if (customVolleyRequest == null) {
//            customVolleyRequest = new CustomVolleyRequest(context);
//        }
//        return customVolleyRequest;
//    }
//
//    public RequestQueue getRequestQueue() {
//        if (requestQueue == null) {
//            Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
//            Network network = new BasicNetwork(new HurlStack());
//            requestQueue = new RequestQueue(cache, network);
//            requestQueue.start();
//        }
//        return requestQueue;
//    }
//
//    public ImageLoader getImageLoader() {
//        return imageLoader;
//    }
//}