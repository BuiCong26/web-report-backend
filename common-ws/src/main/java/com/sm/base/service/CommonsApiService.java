package com.sm.base.service;

import org.springframework.http.HttpMethod;
import java.util.Map;

public interface CommonsApiService {
    Object getApiBase(HttpMethod method, String url, Object request, Map<String,String> mapHeader) throws Exception;
   }
