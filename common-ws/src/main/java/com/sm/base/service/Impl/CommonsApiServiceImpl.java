
package com.sm.base.service.Impl;

import com.sm.base.service.CommonsApiService;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class CommonsApiServiceImpl implements CommonsApiService {

    @Override
    public Object getApiBase(HttpMethod method, String url, Object request, Map<String, String> mapHeader) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, String> entry : mapHeader.entrySet()) {
            httpHeaders.set(entry.getKey(), entry.getValue());

        }
        HttpEntity<Object> httpEntity = new HttpEntity<>(request, httpHeaders);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(new SSLContextBuilder()
                .loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build()).setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate.exchange(url, method, httpEntity, Object.class).getBody();
    }

}
