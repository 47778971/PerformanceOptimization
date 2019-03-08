package com.jun.po.aspectjx;

import android.content.Context;

import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class MyBaseImageDownloader extends BaseImageDownloader {
    public MyBaseImageDownloader(Context context) {
        super(context);
    }

    @Override
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        HttpURLConnection conn = this.createConnection(imageUri, extra);
        for(int redirectCount = 0; conn.getResponseCode() / 100 == 3 && redirectCount < 5; ++redirectCount) {
            conn = this.createConnection(conn.getHeaderField("Location"), extra);
        }
        InputStream imageStream;
        try {
            imageStream = conn.getInputStream();
        } catch (IOException var7) {
            IoUtils.readAndCloseStream(conn.getErrorStream());
            throw var7;
        }
        int contentLength = conn.getContentLength();
        return a(imageStream, conn);
    }

    private ContentLengthInputStream a(InputStream imageStream, HttpURLConnection conn){
        return new ContentLengthInputStream(new BufferedInputStream(imageStream, 32768), conn.getContentLength());
    }
}
