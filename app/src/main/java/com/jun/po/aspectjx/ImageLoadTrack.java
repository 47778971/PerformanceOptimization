package com.jun.po.aspectjx;

import android.os.SystemClock;
import android.util.Log;

import com.jun.po.model.HttpRequestParams;
import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
public class ImageLoadTrack implements ITrack {
    public final String HttpUrlFetcherPath = "(com.bumptech.glide.load.data.HttpUrlFetcher)";

    private HashMap<String, HttpRequestParams> imageMap = new HashMap<>();

    @Before("execution(* " + HttpUrlFetcherPath + ".getStreamForSuccessfulRequest(..))")
    public void getStreamForSuccessfulRequest(JoinPoint joinPoint) {
        try {
            if (null != joinPoint && null != joinPoint.getArgs() && joinPoint.getArgs().length > 0 && null != joinPoint.getArgs()[0] && joinPoint.getArgs()[0] instanceof HttpURLConnection) {
                long download = 0;
//                String type = "";
                long sentMillis = 0;
                long receivedMillis = 0;
                long consumeTime = 0;
                String url = "";
                HttpURLConnection urlConnection = (HttpURLConnection) joinPoint.getArgs()[0];

                if (null != urlConnection.getURL()) {
                    url = urlConnection.getURL().toString();
                }
                Map<String, List<String>> headers = urlConnection.getHeaderFields();
                if (headers.containsKey("Content-Length")) {
                    List<String> ContentLength = headers.get("Content-Length");
                    if (ContentLength.size() > 0) {
                        download = Long.valueOf(ContentLength.get(0));
                    }
                }

//                if (headers.containsKey("Content-Type")) {
//                    List<String> ContentType = headers.get("Content-Type");
//                    if (ContentType.size() > 0) {
//                        type = ContentType.get(0);
//                    }
//                }

                if (headers.containsKey("X-Android-Sent-Millis")) {
                    List<String> SentMillis = headers.get("X-Android-Sent-Millis");
                    if (SentMillis.size() > 0) {
                        sentMillis = Long.valueOf(SentMillis.get(0));
                    }
                }

                if (headers.containsKey("X-Android-Received-Millis")) {
                    List<String> ReceivedMillis = headers.get("X-Android-Received-Millis");
                    if (ReceivedMillis.size() > 0) {
                        receivedMillis = Long.valueOf(ReceivedMillis.get(0));
                        consumeTime = receivedMillis - sentMillis;
                    }
                }

                Log.i("ImageHook", "源的全路径名：" + url + "\n"
                        + "类型：" + "photo" + "\n"
                        + "下载耗时：" + consumeTime + "\n"
                        + "下载数据量：" + download + "\n");
            }
        } catch (Exception e) {
        }
    }

    @AfterReturning(pointcut = "execution(* com.nostra13.universalimageloader.core.download.BaseImageDownloader.getStreamFromNetwork(..))", returning = "stream")
    public void getStreamFromNetwork(JoinPoint joinPoint, ContentLengthInputStream stream) {
        if (null != joinPoint && null != joinPoint.getArgs() && null != joinPoint.getArgs()[0] && joinPoint.getArgs()[0] instanceof String) {
            String url = (String) joinPoint.getArgs()[0];
            if (null != imageMap.get(url)) {
                long consumeTime = System.currentTimeMillis() - imageMap.get(url).getTime();
                Log.i("ImageHook", "源的全路径名：" + url + "\n"
                        + "类型：" + "photo" + "\n"
                        + "下载耗时：" + consumeTime + "\n"
                        + "下载数据量：" + stream.available() + "\n");
            }
        }
    }

    @Before("execution(* com.nostra13.universalimageloader.core.download.BaseImageDownloader.createConnection(..))")
    public void createConnection(JoinPoint joinPoint) {
        if (null != joinPoint && null != joinPoint.getArgs() && null != joinPoint.getArgs()[0] && joinPoint.getArgs()[0] instanceof String) {
            String url = (String) joinPoint.getArgs()[0];
            imageMap.put(url, new HttpRequestParams(url, System.currentTimeMillis()));
        }
    }

    @Before("execution(* com.jun.po.aspectjx.MyBaseImageDownloader.a(..))")
    public void a(JoinPoint joinPoint) {
        if (null != joinPoint && null != joinPoint.getArgs() && null != joinPoint.getArgs()[0] && joinPoint.getArgs()[0] instanceof String) {
            String url = (String) joinPoint.getArgs()[0];
            imageMap.put(url, new HttpRequestParams(url, System.currentTimeMillis()));
        }
    }
}