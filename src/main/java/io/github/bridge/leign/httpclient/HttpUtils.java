package io.github.bridge.leign.httpclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.bridge.leign.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * http请求工具类
 *
 * @author yaochen
 * @date 2019/6/4 11:46
 */
@Slf4j
public class HttpUtils {

  /**
   * get
   *
   * @param url
   * @param headers
   * @param querys
   * @return
   * @throws Exception
   */
  public static JSONObject doGet(String url, Map<String, String> headers, Map<String, String> querys)
      throws Exception {
    HttpClient httpClient = createHttpClient();
    HttpGet request = new HttpGet(buildUrl(url, querys));
    addHeader(request, headers);
    return getJson(httpClient.execute(request));
  }

  public static InputStream getResponseStream(String url) throws Exception {
    HttpClient httpClient = createHttpClient();
    HttpGet request = new HttpGet(url);
    HttpResponse response = httpClient.execute(request);
    return response.getEntity().getContent();
  }
  /**
   * post form
   *
   * @param url
   * @param headers
   * @param querys
   * @param body
   * @return
   * @throws Exception
   */
  public static JSONObject doPost(
      String url, Map<String, String> headers, Map<String, String> querys, Object body)
      throws Exception {
    HttpClient httpClient = createHttpClient();
    HttpPost request = new HttpPost(buildUrl(url, querys));
    addHeader(request, headers);
    if (body != null) {
      if (body instanceof Map) {
        Iterator<Map.Entry> entryIterator = ((Map) body).entrySet().iterator();
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        while (entryIterator.hasNext()) {
          Map.Entry entry = entryIterator.next();
          nameValuePairList.add(
              new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
        }
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
        formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
        request.setEntity(formEntity);
      } else if (body instanceof String) {
        request.setEntity(new StringEntity((String) body, "utf-8"));
      } else if (body instanceof byte[]) {
        request.setEntity(new ByteArrayEntity((byte[]) body));
      } else {
        request.setEntity(new StringEntity(JSONUtil.obj2Json(body), "utf-8"));
      }
    }
    return getJson(httpClient.execute(request));
  }
  /**
   * post form
   *
   * @param url
   * @param headers
   * @param querys
   * @param body
   * @return
   * @throws Exception
   */
  public static String doPostWithStringResponse(
          String url, Map<String, String> headers, Map<String, String> querys, Object body)
          throws Exception {
    HttpClient httpClient = createHttpClient();
    HttpPost request = new HttpPost(buildUrl(url, querys));
    addHeader(request, headers);
    if (body != null) {
      if (body instanceof Map) {
        Iterator<Map.Entry> entryIterator = ((Map) body).entrySet().iterator();
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        while (entryIterator.hasNext()) {
          Map.Entry entry = entryIterator.next();
          nameValuePairList.add(
                  new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
        }
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
        formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
        request.setEntity(formEntity);
      } else if (body instanceof String) {
        request.setEntity(new StringEntity((String) body, "utf-8"));
      } else if (body instanceof byte[]) {
        request.setEntity(new ByteArrayEntity((byte[]) body));
      } else {
        request.setEntity(new StringEntity(JSONUtil.obj2Json(body), "utf-8"));
      }
    }
    return getString(httpClient.execute(request));
  }
  /**
   * Put String
   *
   * @param url
   * @param headers
   * @param querys
   * @param body
   * @return
   * @throws Exception
   */
  public static JSONObject doPut(
      String url, Map<String, String> headers, Map<String, String> querys, String body)
      throws Exception {
    HttpClient httpClient = createHttpClient();
    HttpPut request = new HttpPut(buildUrl(url, querys));
    addHeader(request, headers);
    if (StringUtils.isNotBlank(body)) {
      request.setEntity(new StringEntity(body, "utf-8"));
    }
    return getJson(httpClient.execute(request));
  }

  /**
   * Put stream
   *
   * @param url
   * @param headers
   * @param querys
   * @param body
   * @return
   * @throws Exception
   */
  public static JSONObject doPut(
      String url, Map<String, String> headers, Map<String, String> querys, byte[] body)
      throws Exception {
    HttpClient httpClient = createHttpClient();
    HttpPut request = new HttpPut(buildUrl(url, querys));
    addHeader(request, headers);
    if (body != null) {
      request.setEntity(new ByteArrayEntity(body));
    }
    return getJson(httpClient.execute(request));
  }

  /**
   * Delete
   *
   * @param url
   * @param headers
   * @param querys
   * @return
   * @throws Exception
   */
  public static JSONObject doDelete(String url, Map<String, String> headers, Map<String, String> querys)
      throws Exception {
    HttpClient httpClient = createHttpClient();
    HttpDelete request = new HttpDelete(buildUrl(url, querys));
    addHeader(request, headers);
    return getJson(httpClient.execute(request));
  }

  public static JSONObject upload(
      String url, Map<String, String> headers, Map<String, String> querys, byte[] data)
      throws Exception {
    HttpClient httpClient = createHttpClient();
    HttpPut request = new HttpPut(buildUrl(url, querys));
    addHeader(request, headers);
    HttpEntity entity = new ByteArrayEntity(data, ContentType.APPLICATION_OCTET_STREAM);
    request.setEntity(entity);
    return getJson(httpClient.execute(request));
  }
  /**
   * 构建请求的 url
   *
   * @param url
   * @param querys
   * @return
   * @throws UnsupportedEncodingException
   */
  private static String buildUrl(String url, Map<String, String> querys)
      throws UnsupportedEncodingException {
    StringBuilder sbUrl = new StringBuilder();
    if (!StringUtils.isBlank(url)) {
      sbUrl.append(url);
    }
    if (null != querys) {
      StringBuilder sbQuery = new StringBuilder();
      for (Map.Entry<String, String> query : querys.entrySet()) {
        if (0 < sbQuery.length()) {
          sbQuery.append("&");
        }
        if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
          sbQuery.append(query.getValue());
        }
        if (!StringUtils.isBlank(query.getKey())) {
          sbQuery.append(query.getKey());
          if (!StringUtils.isBlank(query.getValue())) {
            sbQuery.append("=");
            sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
          }
        }
      }
      if (0 < sbQuery.length()) {
        sbUrl.append("?").append(sbQuery);
      }
    }
    return sbUrl.toString();
  }

  /**
   * 在调用SSL之前需要重写验证方法，取消检测SSL 创建ConnectionManager，添加Connection配置信息
   *
   * @return HttpClient 支持https
   */
  private static HttpClient createHttpClient() {
    HttpClientBuilder clientBuilder =
        HttpClients.custom()
            .setConnectionManager(ConnectionManagerHolder.cm)
            .setDefaultRequestConfig(RequestConfigHolder.requestConfig);
    if (PropertiesHolder.needCredence()) {
      // 设置认证
      CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(
          new AuthScope(RequestConfigHolder.proxy),
          new UsernamePasswordCredentials(
              PropertiesHolder.getProxyUserName(), PropertiesHolder.getProxyPassword()));
      clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
    }
    CloseableHttpClient closeableHttpClient = clientBuilder.build();
    return closeableHttpClient;
  }

  private static class RequestConfigHolder {
    public static final RequestConfig requestConfig;
    public static final HttpHost proxy;

    static {
      RequestConfig.Builder builder =
          RequestConfig.custom()
              .setCookieSpec(CookieSpecs.STANDARD_STRICT)
              .setExpectContinueEnabled(Boolean.TRUE)
              .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
              .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
              .setSocketTimeout(1000)
              .setConnectTimeout(10000)
              .setConnectionRequestTimeout(5000);
      if (PropertiesHolder.isProxySwitchOn()) {
        proxy =
            new HttpHost(
                PropertiesHolder.getProxyHost(),
                Integer.valueOf(PropertiesHolder.getProxyPort()),
                "http");
        builder.setProxy(proxy);
      } else {
        proxy = null;
      }
      requestConfig = builder.build();
    }
  }

  private static class PropertiesHolder {
    public static final Properties properties = new Properties();

    static {
      try {
        InputStream in =
            PropertiesHolder.class.getClassLoader().getResourceAsStream("public.properties");
        if (in != null) {
          properties.load(in);
        }
      } catch (FileNotFoundException e) {
        log.error("init HttpUtils, can't load properties file [public.properties].", e);
      } catch (IOException e) {
        log.error("init HttpUtils, load properties file [public.properties] error.", e);
      } catch (Exception e) {
        log.error("init HttpUtils, load properties file [public.properties] error.", e);
      }
    }

    public static boolean isProxySwitchOn() {
      return "on".equals(properties.getProperty("proxy.switch"));
    }

    public static boolean needCredence() {
      return StringUtils.isNotBlank(properties.getProperty("proxy.username"));
    }

    public static String getProxyHost() {
      return properties.getProperty("proxy.host");
    }

    public static String getProxyPort() {
      return properties.getProperty("proxy.port");
    }

    public static String getProxyUserName() {
      return properties.getProperty("proxy.username");
    }

    public static String getProxyPassword() {
      return properties.getProperty("proxy.password");
    }

    public static String getAppId() {
      return properties.getProperty("cloud.app.id");
    }

    public static String getAppSecret() {
      return properties.getProperty("cloud.app.secret");
    }

    public static String getPermissionId() {
      return properties.getProperty("spring.permission.projcetId");
    }
  }

  private static class ConnectionManagerHolder {
    public static final PoolingHttpClientConnectionManager cm;

    static {
      try {
        X509TrustManager trustManager =
            new X509TrustManager() {
              @Override
              public X509Certificate[] getAcceptedIssuers() {
                return null;
              }

              @Override
              public void checkClientTrusted(X509Certificate[] xcs, String str) {}

              @Override
              public void checkServerTrusted(X509Certificate[] xcs, String str) {}
            };
        SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
        ctx.init(null, new TrustManager[] {trustManager}, null);
        SSLConnectionSocketFactory socketFactory =
            new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
        // 创建Registry
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", socketFactory)
                .build();
        // 创建ConnectionManager，添加Connection配置信息
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
      } catch (KeyManagementException ex) {
        throw new RuntimeException(ex);
      } catch (NoSuchAlgorithmException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * 将结果转换成JSONObject
   *
   * @param httpResponse
   * @return
   * @throws IOException
   */
  public static JSONObject getJson(HttpResponse httpResponse) throws IOException {
    HttpEntity entity = httpResponse.getEntity();
    String resp = EntityUtils.toString(entity, "UTF-8");
    EntityUtils.consume(entity);
    return JSON.parseObject(resp);
  }
  public static String getString(HttpResponse httpResponse) throws IOException {
    HttpEntity entity = httpResponse.getEntity();
    String resp = EntityUtils.toString(entity, "UTF-8");
    EntityUtils.consume(entity);
    return resp;
  }

  public static  void addHeader(HttpRequestBase request, Map<String, String> headers) {
    if (headers != null) {
      for (Map.Entry<String, String> e : headers.entrySet()) {
        request.addHeader(e.getKey(), e.getValue());
      }
    }
  }
}
