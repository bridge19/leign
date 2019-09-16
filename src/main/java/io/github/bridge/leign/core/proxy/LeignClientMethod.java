package io.github.bridge.leign.core.proxy;

import com.alibaba.fastjson.JSONObject;
import io.github.bridge.leign.annotation.*;
import io.github.bridge.leign.enums.VariableType;
import io.github.bridge.leign.httpclient.HttpUtils;
import io.github.bridge.leign.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LeignClientMethod {

    private final Method method;

    private Class<?> returnType;

    private ILeignClientArgument[] params = null;

    private Map<String, String> headerValue = new HashMap<>();

    private String url;

    private Annotation requestMethod = null;

    public LeignClientMethod(Method method) {
        this.method = method;
    }

    public void init() {
        Annotation[] annotations = method.getDeclaredAnnotations();

        this.returnType = method.getReturnType();
        if (ArrayUtils.isEmpty(annotations)) {
            throw new RuntimeException(method.getName() + " need have one of method: [Post,Get,Put,Delete]");
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof Post) {
                this.requestMethod = annotation;
                Post post = (Post) annotation;
                String url = post.url();
                String host = post.host();
                parseHostAndUrl(host, url);
            } else if (annotation instanceof Get) {
                this.requestMethod = annotation;
                Get post = (Get) annotation;
                String url = post.url();
                String host = post.host();
                parseHostAndUrl(host, url);
            } else if (annotation instanceof Put) {
                this.requestMethod = annotation;
                Put post = (Put) annotation;
                String url = post.url();
                String host = post.host();
                parseHostAndUrl(host, url);
            } else if (annotation instanceof Delete) {
                this.requestMethod = annotation;
                Delete post = (Delete) annotation;
                String url = post.url();
                String host = post.host();
                parseHostAndUrl(host, url);
            } else if (annotation instanceof RepeatedHeader) {
                RepeatedHeader repeatedHeader = (RepeatedHeader) annotation;
                Header[] headers = repeatedHeader.value();
                if (!ArrayUtils.isEmpty(headers)) {
                    for (Header header : headers) {
                        String headerVal = header.value();
                        parseHeaderValue(headerVal);
                    }
                }
            } else if (annotation instanceof Header) {
                Header header = (Header) annotation;
                String headerVal = header.value();
                parseHeaderValue(headerVal);
            }
        }

        Parameter[] parameters = method.getParameters();
        int paramCount = method.getParameterCount();
        params = new ILeignClientArgument[paramCount];
        if (paramCount > 0) {
            for (int i = 0; i < paramCount; i++) {
                if (parameters[i].getType().isPrimitive() || (parameters[i].getType() == String.class)) {
                    params[i] = new LeignClientPrimitiveArgument(parameters[i]);

                } else {
                    params[i] = new LeignClientObjectArgument(parameters[i]);
                }
                params[i].parse();
            }
        }
    }

    private void parseHeaderValue(String headerVal) {
        if (StringUtils.isEmpty(headerVal)) {
            return;
        }
        String[] splitedValue = headerVal.split(":");
        if (splitedValue.length == 2 && StringUtils.isNotEmpty(splitedValue[0])) {
            String key = splitedValue[0].trim();
            String value = splitedValue[1].trim();
            if (StringUtils.isNotEmpty(value)) {
                headerValue.put(key, value);
            }
        }
    }

    private void parseHostAndUrl(String host, String url) {
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        } else if (!host.startsWith("http")) {
            host = "http://" + host;
        }
        if (!StringUtils.isBlank(url) && !url.startsWith("/")) {
            url = "/" + url;
        }
        String fullUrl = host + url;
        this.url = fullUrl.endsWith("/") ? fullUrl.substring(0, fullUrl.length() - 1) : fullUrl;
    }

    public Object execute(Object[] args) {
        Object result = null;
        int argLen = args.length;
        Map<String, String> headers = new HashMap<>();
        Map<String, String> querys = new HashMap<>();
        if (headerValue.size() > 0) {
            headers.putAll(headerValue);
        }
        Map<String, Object> arguments = new HashMap<>();

        if (argLen > 0) {
            for (int i = 0; i < argLen; i++) {
                if (params[i].getVariableType() == VariableType.HEADER) {
                    params[i].getHeaders(args[i], headers);
                } else if (params[i].getVariableType() == VariableType.PARAMETER) {
                    params[i].getParameters(args[i], querys);
                } else if (params[i].getVariableType() == VariableType.QUERYBODY) {
                    arguments.put(params[i].getVariableName(), args[i]);
                } else if (params[i].getVariableType() == VariableType.MIX) {
                    params[i].getHeaders(args[i], headers);
                    params[i].getParameters(args[i], querys);
                    Map bodyMap = params[i].getBody(args[i]);
                    if (bodyMap != null) {
                        arguments.put(params[i].getVariableName(), bodyMap);
                    }
                }
            }
        }
        Object body = null;
        if (arguments.size() == 1) {
            body = arguments.entrySet().iterator().next().getValue();
        } else if (arguments.size() > 1) {
            body = arguments;
        }
        try {
            if (this.requestMethod instanceof Post) {
                if (returnType == String.class) {
                    result = HttpUtils.doPostWithStringResponse(url, headers, querys, body);
                } else {
                    JSONObject jsonObject = null;
                    jsonObject = HttpUtils.doPost(url, null, null, body);
                    result = JSONUtil.json2Obj(jsonObject, returnType);
                }
            }

        } catch (Exception e) {
            log.error("invoke url: {} error", this.url);
        }
        return result;
    }
}
