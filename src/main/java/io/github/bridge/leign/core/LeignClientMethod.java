package io.github.bridge.leign.core;

import com.alibaba.fastjson.JSONObject;
import io.github.bridge.leign.annotation.Post;
import io.github.bridge.leign.httpclient.HttpUtils;
import io.github.bridge.leign.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Slf4j
public class LeignClientMethod {

    private final Method method;
    private Class<?> returnType;

    private String[] params = null;

    private String url;

    private Annotation requestMethod = null;

    public LeignClientMethod(Method method) {
        this.method = method;
    }

    public void init() {
        Annotation[] annotations = method.getDeclaredAnnotations();

        this.returnType = method.getReturnType();
        if (ArrayUtils.isEmpty(annotations)) {
            throw new RuntimeException(method.getName() + " need have one of method: [Post,Get]");
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof Post) {
                this.requestMethod = annotation;
                Post post = (Post) annotation;
                String url = post.url();
                String host = "";
                host = post.host();
                if (host.endsWith("/")) {
                    host = host.substring(0, host.length() - 1);
                } else if (!host.startsWith("http")) {
                    host = "http://" + host;
                }
                if (!StringUtils.isBlank(url) && !url.startsWith("/")) {
                    url = "/" + url;
                }
                this.url = host + url;
            }
        }
    }

    public Object execute(Object[] args) {
        Object result = null;
        try {
            if (this.requestMethod instanceof Post) {
                if (returnType == String.class) {
                    result = HttpUtils.doPostWithStringResponse(url, null, null, args.length>0?args[0]:null);
                } else {
                    JSONObject jsonObject = null;
                    jsonObject = HttpUtils.doPost(url, null, null, args[0]);
                    result = JSONUtil.json2Obj(jsonObject, returnType);
                }
            }

        } catch (Exception e) {
            log.error("invoke url: {} error", this.url);
        }
        return result;
    }
}
