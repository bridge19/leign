package io.github.bridge.leign.service;

import io.github.bridge.leign.annotation.Header;
import io.github.bridge.leign.annotation.LeignClient;
import io.github.bridge.leign.annotation.Param;
import io.github.bridge.leign.annotation.method.Post;

@LeignClient(host = "www.163.com")
public interface ClientTestService2 {
    @Post(host = "www.163.com", url = "")
    @Header("afdsaf:29437")
    String hello(@Param("name") String name, @Param("value") int value, MyObject myObject);
}
