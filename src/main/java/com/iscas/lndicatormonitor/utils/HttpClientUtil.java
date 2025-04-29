package com.iscas.lndicatormonitor.utils;



import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpClientUtil {


    /**
     * 以post方式调用第三方接口,以form-data 形式  发送 MultipartFile 文件数据
     *
     * @param url           post请求url
     * @param fileParamName 文件参数名称
     * @param multipartFile 文件
     * @param paramMap      表单里其他参数
     * @return
     */
    public static String doPostFormData(String url, String fileParamName, MultipartFile multipartFile, Map<String, String> paramMap) {
        // 创建Http实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建HttpPost实例
        HttpPost httpPost = new HttpPost(url);

        // 请求参数配置
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000)
                .setConnectionRequestTimeout(10000).build();
        httpPost.setConfig(requestConfig);

        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(StandardCharsets.UTF_8);
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (StringUtils.isNotBlank(fileParamName) && null != multipartFile) {
                String fileName = multipartFile.getOriginalFilename();
                // 文件流
                builder.addBinaryBody(fileParamName, multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);

            }
            //表单中其他参数
            if (null != paramMap) {
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.create("text/plain", Consts.UTF_8)));
                }
            }


            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static HashMap<String, Object> doPost(String url, HashMap<String, String> headers, HashMap<String, String> params, File file) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        if (null !=file){
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "multipart/form-data");
        }
        if (headers != null) {
            for (String key : headers.keySet()) {
                httpPost.setHeader(key, headers.get(key));
            }
        }

        // 设置请求参数
        if (params != null) {
            List<NameValuePair> paramsEntity = new ArrayList<>();
            for (String key : params.keySet()) {
                paramsEntity.add(new BasicNameValuePair(key, params.get(key)));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(paramsEntity));
        }

        // 设置文件参数
        if (file != null) {
            FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
            StringBody stringBody = new StringBody("file description", ContentType.TEXT_PLAIN);
            HttpEntity fileEntity = MultipartEntityBuilder.create()
                    .addPart("file", fileBody)
                    .addPart("description", stringBody)
                    .build();
            // 发送请求
            httpPost.setEntity(fileEntity);
        }


        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", response.getStatusLine().getStatusCode());
        if (responseEntity != null) {
            String responseString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
//            System.out.println(responseString);
            result.put("msg", responseString);
        }
        httpClient.close();


        return result;
    }

}
