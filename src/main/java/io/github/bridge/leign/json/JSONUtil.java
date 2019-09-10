package io.github.bridge.leign.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.TimeDeserializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class JSONUtil {

    private static SerializeConfig serializeConfig = new SerializeConfig();

    private static ParserConfig parserConfig = new ParserConfig();

    static {
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
//        parserConfig.putDeserializer(Date.class, CalendarCodec.instance );
        parserConfig.putDeserializer(Date.class, TimeDeserializer.instance);
    }

    public static String obj2Json(Object obj){
        return JSON.toJSONString(obj,serializeConfig);
    }

    public static <T> T json2Obj(String jsonStr, Class tClass){
        return (T) JSON.parseObject(jsonStr, tClass,parserConfig, JSON.DEFAULT_PARSER_FEATURE);
    }
    public static <T> T json2Obj(String jsonStr, TypeReference<T> tTypeReference){
        return JSON.parseObject(jsonStr, tTypeReference);
    }
    public static <T> T json2Obj(JSONObject jsonObject, Class tClass){
        return (T) JSON.parseObject(jsonObject.toJSONString(),tClass,parserConfig, JSON.DEFAULT_PARSER_FEATURE);
    }
    public static Map<String,Object> json2Map(String jsonStr){
        return JSON.parseObject(jsonStr,Map.class, JSON.DEFAULT_PARSER_FEATURE);
    }
    public static <T> List<T> json2ObjArray(String jsonStr, Class tClass){
        return (List<T>) JSON.parseArray(jsonStr, tClass);
    }
}
