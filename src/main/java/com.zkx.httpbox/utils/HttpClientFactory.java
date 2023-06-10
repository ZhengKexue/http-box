package com.zkx.httpbox.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * Description :
 *
 * @author kexue.zheng
 * Date 2022/1/24 15:38
 */
@Slf4j
public class HttpClientFactory {

    /**
     * http客户端
     */
    private static OkHttpClient client;


   static {
            X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            SSLSocketFactory sslSocketFactory = null ;
            try {
                //信任任何链接
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
                sslSocketFactory = sslContext.getSocketFactory();

                client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory, x509TrustManager)
                        .retryOnConnectionFailure(true)//修复java.io.IOException: unexpected end of stream on  问题
                        .connectionPool(new ConnectionPool(400, 5, TimeUnit.MINUTES))//连接池
                        .connectTimeout(45L, TimeUnit.SECONDS)
                        .readTimeout(45L, TimeUnit.SECONDS)
                        .build();
                log.info(">> 初始化完成 OkHttpClient {}",client);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

    }


    public static OkHttpClient getInstance(){
        return client;
    }

    public static void setInstance(OkHttpClient client){
        HttpClientFactory.client = client;
    }

}
