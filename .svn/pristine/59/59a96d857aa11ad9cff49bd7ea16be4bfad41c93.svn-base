package com.sm.base.service.Impl;

import com.sm.base.common.DataUtil;
import com.sm.base.service.CommonsApiService;
import com.sm.base.service.RedirectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class RedirectServiceImpl implements RedirectService {

    private CommonsApiService commonsApiService;

    public RedirectServiceImpl(CommonsApiService commonsApiService) {
        this.commonsApiService = commonsApiService;
    }

    @Override
    public Object redirectFunction(String locate, String url, Object input, Map<String, String> mapHeader) throws Exception {

        //Validating
        if (DataUtil.isNullOrEmpty(url)) {
            throw new IllegalArgumentException("url cannot be empty");
        }
        // data to json
        String json = null;
        if (input != null) {
            json = DataUtil.objectToJson(input);
        }
        log.info("Request:" + url + (DataUtil.isNullOrEmpty(json) ? "" : "Body:" + json));
        return commonsApiService.getApiBase(HttpMethod.POST, url, json, mapHeader);
    }

}
