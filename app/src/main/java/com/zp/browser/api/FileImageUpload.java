package com.zp.browser.api;

import android.os.AsyncTask;
import android.util.Log;

import org.kymjs.kjframe.http.HttpCallBack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * 实现文件上传的工具类
 *
 * @author 甘伦
 * @version V0.1
 * @Description
 * @date 2015-8-23 下午2:39:33
 */
public class FileImageUpload {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";

    /**
     * * android上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param RequestURL 请求的rul
     * @param key        请求的参数
     * @return 返回响应的内容
     */
    public static void uploadFile (final File file, final String RequestURL, final String key,
                                   final HttpCallBack httpCallBack) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground (Object[] params) {
                String BOUNDARY = UUID.randomUUID().toString (); // 边界标识 随机生成
                String PREFIX = "--", LINE_END = "\r\n";
                String CONTENT_TYPE = "multipart/form-data"; // 内容类型
                try {
                    URL url = new URL(RequestURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection ();
                    conn.setReadTimeout (TIME_OUT);
                    conn.setConnectTimeout (TIME_OUT);
                    conn.setDoInput (true); // 允许输入流
                    conn.setDoOutput (true); // 允许输出流
                    conn.setUseCaches (false); // 不允许使用缓存
                    conn.setRequestMethod ("POST"); // 请求方式
                    conn.setRequestProperty ("Charset", CHARSET);
                    // 设置编码
                    conn.setRequestProperty ("Connection", "Keep-Alive");
                    conn.setRequestProperty ("Content-Type", CONTENT_TYPE + ";boundary="
                            + BOUNDARY);
                    if (file != null) {
                        /** * 当文件不为空，把文件包装并且上传 */
                        OutputStream outputSteam = conn.getOutputStream ();
                        DataOutputStream dos = new DataOutputStream(outputSteam);
                        StringBuffer sb = new StringBuffer();
                        sb.append (PREFIX);
                        sb.append (BOUNDARY);
                        sb.append (LINE_END);
                        /**
                         * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                         * filename是文件的名字，包含后缀名的 比如:abc.png
                         */
                        System.out.println (file.getName ());
                        // sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
                        // + file.getName() + "\"" + LINE_END);
                        sb.append ("Content-Disposition: form-data; name=\"" + key
                                + "\"; filename=\"" + file.getName () + "\"" + LINE_END);
                        sb.append ("Content-Type: application/octet-stream; charset="
                                + CHARSET + LINE_END);
                        sb.append (LINE_END);
                        dos.write (sb.toString ().getBytes ());
                        InputStream is = new FileInputStream(file);
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while ((len = is.read (bytes)) != -1) {
                            dos.write (bytes, 0, len);
                        }
                        is.close ();
                        dos.write (LINE_END.getBytes ());
                        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                                .getBytes ();
                        dos.write (end_data);
                        dos.flush ();
                        /**
                         * 获取响应码 200=成功 当响应成功，获取响应的流
                         */
                        int res = conn.getResponseCode ();
                        System.out.println (res);
                        Log.e(TAG, "response code:" + res);
                        if (res == 200) {
                            // 获取返回的流
                            InputStream inptStream = conn.getInputStream ();
                            return dealResponseResult (inptStream);

                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
                return FAILURE;
            }

            @Override
            protected void onPostExecute (Object o) {
                super.onPostExecute (o);
                httpCallBack.onSuccess (o.toString ());
            }
        };
        task.execute ();
    }

    /**
     * 处理服务器的响应结果（将输入流转化成字符串） Param : inputStream服务器的响应输入流
     *
     * @param inputStream
     * @return
     * @Description
     * @author zipeng
     */
    public static String dealResponseResult (InputStream inputStream) {
        String resultData = null; // 存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read (data)) != -1) {
                byteArrayOutputStream.write (data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }
        resultData = new String(byteArrayOutputStream.toByteArray ());
        return resultData;
    }
}
