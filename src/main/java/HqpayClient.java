import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.util.*;

public class HqpayClient {

    private final List<String> segments = new ArrayList<>(Arrays.asList("business-platform.web.services.merchant-api-v3", "rest", "api", "v3"));
    private final String appId;
    private final String secret;
    private final String pkcs8PemKey;
    private String host = "https://www.upaypal.cn";
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient = HttpClientBuilder.create().build();


    public HqpayClient(String appId, String secret, String pkcs8PemKey) {
        this.appId = appId;
        this.secret = secret;
        this.pkcs8PemKey = pkcs8PemKey;
    }

    public void setHost(String host) {
        this.host = host;
    }


    public String executeForList(List<Map<String, Object>> pMaps, String path, String httpMethod) throws Exception {
        for (Map<String, Object> map : pMaps) {
            map.put("signature", HqpaySignature.sign(createLinkString(map), pkcs8PemKey));
        }
        return execute(path,httpMethod , pMaps);
    }


    public String executeForOne(Map<String, Object> pMap, String path, String httpMethod) throws Exception {
        pMap.put("signature", HqpaySignature.sign(createLinkString(pMap), pkcs8PemKey));
        return execute(path, httpMethod, pMap);
    }


    private String execute(String path, String httpMethod, Object o) throws Exception {
        segments.add(path);
        HttpUriRequest httpRequest = RequestBuilder.create(httpMethod)
                .setUri(new URIBuilder(host).setPathSegments(segments).build())
                .setEntity(new StringEntity(objectMapper.writeValueAsString(o)))
                .build();
        httpRequest.setHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
        httpRequest.setHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, secret));
        httpRequest.setHeader(new BasicHeader("AppId", appId));
        return EntityUtils.toString(httpClient.execute(httpRequest).getEntity());
    }


    private static String createLinkString(Map<String, Object> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder prestr = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = params.get(key);
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr.append(key).append("=").append(value);
            } else {
                prestr.append(key).append("=").append(value).append("&");
            }
        }
        return prestr.toString();
    }

}
