package org.jeecg.modules.print.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddPopAuthTokenCreateRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddPopAuthTokenRefreshRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddPopAuthTokenCreateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddPopAuthTokenRefreshResponse;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.print.entity.PddShopToken;
import org.jeecg.modules.print.service.IPddShopTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/callback")
public class PddAuthCallbackController {

    String code = "287a56fa4a9046fb945b8a0a7722dd6227d4f4e9";
    String clientId = "4d3167f8f01e4ce2ab319a92d96e4cc1";
    String clientSecret = "5137550a8c9f236088f4f72234fbf4d68c5e0edb";
    String webAuthUri = "https://fuwu.pinduoduo.com/service-market/auth";
    String h5AuthUri = "https://mai.pinduoduo.com/h5-login.html";
    String redirectUri = UnicodeUtil.toUnicode("http://127.0.0.1:8080/jeecg-boot/callback/pdd");


    @Autowired
    PopHttpClient popHttpClient;

    @Autowired
    private IPddShopTokenService pddShopTokenService;


    @IgnoreAuth
    @GetMapping("/pdd")
    public void pdd(@RequestParam(name = "code") String code) throws Exception {

        PddPopAuthTokenCreateRequest request = new PddPopAuthTokenCreateRequest();
        request.setCode(code);
        PddPopAuthTokenCreateResponse response = popHttpClient.syncInvoke(request);
        PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse popAuthTokenCreateResponse = response.getPopAuthTokenCreateResponse();
        String ownerId = popAuthTokenCreateResponse.getOwnerId();
        Optional<PddShopToken> oneOpt = pddShopTokenService.getOneOpt(new LambdaQueryWrapper<PddShopToken>().eq(PddShopToken::getOwnerId, ownerId));
        PddShopToken pddShopToken = oneOpt.orElseGet(PddShopToken::new);
        BeanUtil.copyProperties(popAuthTokenCreateResponse, pddShopToken);
        pddShopTokenService.saveOrUpdate(pddShopToken);

    }



}
