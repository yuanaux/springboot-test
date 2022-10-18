package com.yuan.utils.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: yuanxiaolong
 * @Title: HttpClientUtils
 * @ProjectName: product_two
 * @Description: Apache HttpClient连接池工具类
 * @date: 2022/10/13 15:06
 */
public class HttpConnectionPoolUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpConnectionPoolUtils.class);

    private final static String ENCODING = "UTF-8";
    private final static int MAX_TOTAL = 1000;//连接池中最大连接数
    private final static int DEFAULT_MAX_PERROUTE = 100;//每个路由的最大连接数
    private final static int DEFAULT_CONNECT_TIMEOUT = 1000;// 连接超时时间
    private final static int DEFAULT_SOCKET_TIMEOUT = 3000;// 读超超时时间
    private final static int DEFAULT_CONNECT_REQUEST_TIMEOUT = 1000;// 从连接池中获取连接超时时间

    private static PoolingHttpClientConnectionManager manager;//连接池管理类
    private static CloseableHttpClient httpClient; // 发送请求的客户端单例
    private static RequestConfig requestConfig;// 默认请求时间设置
    private static ScheduledExecutorService monitorExecutor;// 监控线程,对异常和空闲线程进行关闭

    private final static Object syncLock = new Object(); // 相当于线程锁,用于线程安全

    static {
        // 配置同时支持HTTP和HTTPS
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build();

        // 创建连接管理器，并设置相关参数
        manager = new PoolingHttpClientConnectionManager(registry);
        manager.setDefaultMaxPerRoute(DEFAULT_MAX_PERROUTE); // 每个路由的最大连接数
        manager.setMaxTotal(MAX_TOTAL); // 连接池中最大连接数
        // 设置到某个路由的最大连接数，会覆盖defaultMaxPerRoute
        manager.setMaxPerRoute(new HttpRoute(new HttpHost("localhost", 8000)), 500);

        // socket默认配置
        SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true) // 是否立即发送数据，设置为true会关闭Socket缓冲，默认为false
                .setSoReuseAddress(true) // 是否可以在一个进程关闭Socket后，即使它还没有释放端口，其它进程还可以立即重用端口
                .setSoTimeout(500) // 接收数据的等待超时时间，单位ms
                .setSoLinger(60) // 关闭Socket时，要么发送完所有数据，要么等待60s后，就关闭连接，此时socket.close()是阻塞的
                .setSoKeepAlive(true) // 开启监视TCP连接是否有效
                .build();
        manager.setDefaultSocketConfig(socketConfig);

        // request默认配置
        requestConfig = RequestConfig.custom().setConnectionRequestTimeout(DEFAULT_CONNECT_REQUEST_TIMEOUT)// 从连接池中获取连接超时时间
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)// 连接超时时间
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)// 读超超时时间
                .build();
    }

    /**
     * 从连接池中获取HttpClient
     *
     * @return CloseableHttpClient
     * @date 2022/10/14 11:10
     * @author yuanxiaolong
     */
    private static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            //多线程下多个线程同时调用getHttpClient容易导致重复创建httpClient对象的问题,所以加上了同步锁
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = HttpClientBuilder.create().setConnectionManager(manager).setDefaultRequestConfig(requestConfig).build();
                    //开启监控线程,对异常和空闲线程进行关闭
                    monitorExecutor = Executors.newScheduledThreadPool(1);
                    monitorExecutor.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            manager.closeExpiredConnections();//关闭异常连接
                            manager.closeIdleConnections(30, TimeUnit.SECONDS);//关闭30s空闲的连接
                        }
                    }, 0, 5, TimeUnit.SECONDS);
                }
            }
        }
        return httpClient;
    }

    /**
     * 释放回连接池
     *
     * @param httpResponse
     * @date 2022/10/14 11:20
     * @author yuanxiaolong
     */
    private static void closeResponse(CloseableHttpResponse httpResponse) {
        if (!ObjectUtils.isEmpty(httpResponse)) {
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置请求头
     *
     * @param headers
     * @param httpMethod
     * @date 2022/10/17 11:14
     * @author yuanxiaolong
     */
    private static void packageHeader(Map<String, String> headers, HttpRequestBase httpMethod) {
        if (!ObjectUtils.isEmpty(headers)) {
            headers.forEach(httpMethod::setHeader);
        }
    }

    /**
     * 封装请求参数
     *
     * @param params
     * @param httpMethod
     * @date 2022/10/17 11:14
     * @author yuanxiaolong
     */
    private static void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod) throws UnsupportedEncodingException {
        if (!ObjectUtils.isEmpty(params)) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            params.forEach((key, value) -> nameValuePairs.add(new BasicNameValuePair(key, value)));
            httpMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, ENCODING));
        }
    }

    /**
     * 封装请求参数为json格式
     *
     * @param json
     * @param httpMethod
     * @date 2022/10/17 11:14
     * @author yuanxiaolong
     */
    private static void packageJson(String json, HttpEntityEnclosingRequestBase httpMethod) {
        if (!ObjectUtils.isEmpty(json)) {
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpMethod.setEntity(stringEntity);
        }
    }

    /**
     * 执行请求获取响应体并释放资源
     *
     * @param httpClient
     * @param httpMethod
     * @return String
     * @date 2022/10/17 11:14
     * @author yuanxiaolong
     */
    private static String getHttpClientResult(CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws IOException {
        String content = "";
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpMethod);
            if (!ObjectUtils.isEmpty(httpResponse) && !ObjectUtils.isEmpty(httpResponse.getEntity())) {
                HttpEntity entity = httpResponse.getEntity();
                content = EntityUtils.toString(entity, ENCODING);
                EntityUtils.consume(entity);// 确保消耗完内容
            }
            return content;
        } finally {
            closeResponse(httpResponse);
        }
    }

    /**
     * 发送get请求;不带请求头和请求参数
     */
    public static String doGet(String url) throws Exception {
        return doGet(url, 0, 0, null, null);
    }

    /**
     * 发送get请求;带请求参数
     */
    public static String doGet(String url, Map<String, String> params) throws Exception {
        return doGet(url, 0, 0, null, params);
    }

    /**
     * 发送get请求;带请求头参数
     */
    public static String doGet(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        return doGet(url, 0, 0, headers, params);
    }

    /**
     * 发送get请求
     *
     * @param url            接口地址
     * @param connectTimeout 连接超时时间，毫秒，传0使用默认配置
     * @param socketTimeout  读取超时时间，毫秒，传0使用默认配置
     * @param headers        请求头
     * @param params         请求参数
     * @return String
     * @date 2022/10/17 11:25
     * @author yuanxiaolong
     */
    public static String doGet(String url, int connectTimeout, int socketTimeout, Map<String, String> headers, Map<String, String> params) throws Exception {
        // 创建httpClient对象
        CloseableHttpClient httpClient = getHttpClient();

        // 创建访问地址
        URIBuilder uriBuilder = new URIBuilder(url);
        if (!ObjectUtils.isEmpty(params)) {
            params.forEach(uriBuilder::setParameter);
        }

        // 创建http对象
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        // 设置请求超时时间及响应超时时间
        if (connectTimeout > 0 && socketTimeout > 0) {
            httpGet.setConfig(RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build());
        }

        // 设置请求头
        packageHeader(headers, httpGet);

        // 执行请求获取响应体并释放资源
        return getHttpClientResult(httpClient, httpGet);
    }

    /**
     * 发送post请求;不带请求头和请求参数
     */
    public static String doPost(String url) throws IOException {
        return doPost(url, 0, 0, null, null);
    }

    /**
     * 发送post请求;带请求参数
     */
    public static String doPost(String url, Map<String, String> params) throws IOException {
        return doPost(url, 0, 0, null, params);
    }

    /**
     * 发送post请求
     *
     * @param url            接口地址
     * @param connectTimeout 连接超时时间，毫秒，传0使用默认配置
     * @param socketTimeout  读取超时时间，毫秒，传0使用默认配置
     * @param headers        请求头
     * @param params         请求参数
     * @return String
     * @date 2022/10/17 11:31
     * @author yuanxiaolong
     */
    public static String doPost(String url, int connectTimeout, int socketTimeout, Map<String, String> headers, Map<String, String> params) throws IOException {
        // 创建httpClient对象
        CloseableHttpClient httpClient = getHttpClient();

        // 创建http对象
        HttpPost httpPost = new HttpPost(url);

        // 设置请求超时时间及响应超时时间
        if (connectTimeout > 0 && socketTimeout > 0) {
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build());
        }

        // 设置请求头
        packageHeader(headers, httpPost);

        // 封装请求参数
        packageParam(params, httpPost);

        // 执行请求获取响应体并释放资源
        return getHttpClientResult(httpClient, httpPost);
    }

    /**
     * 发送post请求;json格式参数
     *
     * @param url  接口地址
     * @param json 请求参数
     * @return String
     * @date 2022/10/17 11:35
     * @author yuanxiaolong
     */
    public static String doPostJson(String url, String json) throws IOException {
        return doPostJson(url, 0, 0, null, json);
    }

    /**
     * 发送post请求;json格式参数
     *
     * @param url            接口地址
     * @param connectTimeout 连接超时时间，毫秒，传0使用默认配置
     * @param socketTimeout  读取超时时间，毫秒，传0使用默认配置
     * @param headers        请求头
     * @param json           请求参数
     * @return String
     * @date 2022/10/17 11:35
     * @author yuanxiaolong
     */
    public static String doPostJson(String url, int connectTimeout, int socketTimeout, Map<String, String> headers, String json) throws IOException {
        // 创建httpClient对象
        CloseableHttpClient httpClient = getHttpClient();

        // 创建http对象
        HttpPost httpPost = new HttpPost(url);

        // 设置请求超时时间及响应超时时间
        if (connectTimeout > 0 && socketTimeout > 0) {
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build());
        }

        // 设置请求头
        packageHeader(headers, httpPost);

        // 封装请求参数为json格式
        packageJson(json, httpPost);

        // 执行请求获取响应体并释放资源
        return getHttpClientResult(httpClient, httpPost);
    }

    /**
     * 发送put请求;不带请求头和请求参数
     *
     * @param url 接口地址
     * @return String
     * @date 2022/10/17 11:40
     * @author yuanxiaolong
     */
    public static String doPut(String url) throws IOException {
        return doPut(url, 0, 0, null, null);
    }

    /**
     * 发送put请求;带请求参数
     *
     * @param url    接口地址
     * @param params 请求参数
     * @return String
     * @date 2022/10/17 11:40
     * @author yuanxiaolong
     */
    public static String doPut(String url, Map<String, String> params) throws IOException {
        return doPut(url, 0, 0, null, params);
    }

    /**
     * 发送put请求;带请求头和请求参数
     *
     * @param url            接口地址
     * @param connectTimeout 连接超时时间，毫秒，传0使用默认配置
     * @param socketTimeout  读取超时时间，毫秒，传0使用默认配置
     * @param headers        请求头
     * @param params         请求参数
     * @return String
     * @date 2022/10/17 11:40
     * @author yuanxiaolong
     */
    public static String doPut(String url, int connectTimeout, int socketTimeout, Map<String, String> headers, Map<String, String> params) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPut httpPut = new HttpPut(url);
        // 设置请求超时时间及响应超时时间
        if (connectTimeout > 0 && socketTimeout > 0) {
            httpPut.setConfig(RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build());
        }
        packageHeader(headers, httpPut);
        packageParam(params, httpPut);
        return getHttpClientResult(httpClient, httpPut);
    }

    /**
     * 发送delete请求;不带请求头和请求参数
     *
     * @param url 接口地址
     * @return String
     * @date 2022/10/17 11:40
     * @author yuanxiaolong
     */
    public static String doDelete(String url) throws Exception {
        return doDelete(url, 0, 0, null, null);
    }

    /**
     * 发送delete请求;带请求参数
     *
     * @param url    接口地址
     * @param params 请求参数
     * @return String
     * @date 2022/10/17 11:40
     * @author yuanxiaolong
     */
    public static String doDelete(String url, Map<String, String> params) throws Exception {
        return doDelete(url, 0, 0, null, params);
    }

    /**
     * 发送delete请求;带请求头
     *
     * @param url            接口地址
     * @param connectTimeout 连接超时时间，毫秒，传0使用默认配置
     * @param socketTimeout  读取超时时间，毫秒，传0使用默认配置
     * @param headers        请求头
     * @param params         请求参数
     * @return String
     * @date 2022/10/17 11:40
     * @author yuanxiaolong
     */
    public static String doDelete(String url, int connectTimeout, int socketTimeout, Map<String, String> headers, Map<String, String> params) throws Exception {
        CloseableHttpClient httpClient = getHttpClient();
        URIBuilder uriBuilder = new URIBuilder(url);
        if (!ObjectUtils.isEmpty(params)) {
            params.forEach(uriBuilder::setParameter);
        }
        HttpDelete httpDelete = new HttpDelete(uriBuilder.build());
        // 设置请求超时时间及响应超时时间
        if (connectTimeout > 0 && socketTimeout > 0) {
            httpDelete.setConfig(RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build());
        }
        packageHeader(headers, httpDelete);
        return getHttpClientResult(httpClient, httpDelete);
    }
}
