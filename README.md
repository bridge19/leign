# leign
集成httpclient 和 springboot, 使用@EnableLeignClients和@LeignClient， 访问网络资源

##通过定义interface访问网络资源
```java
@LeignClient(host="http://<ip>:<port>")
public interface MyService{
    @io.github.bridge.leign.annotation.Post(url="/title")
    ReturnObject service1(@Param("titile") String title);
}
```
将被解析成报文（有省略）：
```text
POST http://<ip>:<port> http1.1
Content-type application/json
Content-length (实时计算)
{"title": ${title}}
```

