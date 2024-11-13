package org.jeecg.modules.print.controller;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddPopAuthTokenCreateRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddPopAuthTokenCreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.config.shiro.IgnoreAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/callback")
public class PddAuthCallbackController {


    String code = "287a56fa4a9046fb945b8a0a7722dd6227d4f4e9";

    String clientId = "4d3167f8f01e4ce2ab319a92d96e4cc1";
    String clientSecret = "5137550a8c9f236088f4f72234fbf4d68c5e0edb";
    String webAuthUri = "https://fuwu.pinduoduo.com/service-market/auth";
    String h5AuthUri = "https://mai.pinduoduo.com/h5-login.html";
    String redirectUri = UnicodeUtil.toUnicode("http://159.75.35.119:8080/jeecg-boot/callback/pdd");

    public String getAuthUrl(String type) {
        String authUrl = webAuthUri + "?response_type=code&client_id=" + clientId
                + "&redirect_uri="+redirectUri;
        return authUrl;
    }


    @Autowired
    MongoTemplate mongoTemplate;

    @IgnoreAuth
    @GetMapping("/pdd")
    public void pdd(@RequestParam(name = "code") String code) throws Exception {
        PopClient client = new PopHttpClient(clientId, clientSecret);
        PddPopAuthTokenCreateRequest request = new PddPopAuthTokenCreateRequest();
        request.setCode(code);
        PddPopAuthTokenCreateResponse response = client.syncInvoke(request);
        PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse popAuthTokenCreateResponse = response.getPopAuthTokenCreateResponse();
        JSONObject jsonObject = JSONUtil.parseObj(popAuthTokenCreateResponse);
        jsonObject.set("_id", popAuthTokenCreateResponse.getOwnerId());
        mongoTemplate.save(jsonObject,"pdd_shop_token");
    }



}
