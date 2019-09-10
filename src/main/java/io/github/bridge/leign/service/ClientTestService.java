package io.github.bridge.leign.service;

import io.github.bridge.leign.annotation.LeignClient;
import io.github.bridge.leign.annotation.Post;

@LeignClient
public interface ClientTestService {
    @Post(host = "www.163.com", url = "")
    String hello();
}
