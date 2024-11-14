package org.jeecg.modules.print.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddOrderNumberListIncrementGetRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddOrderNumberListIncrementGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddPopAuthTokenCreateResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.print.entity.PddShopToken;
import org.jeecg.modules.print.service.IPddShopTokenService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@DisallowConcurrentExecution
@Component
public class PddOrderIncrementGetJob implements Job {

    /**
     *       {
     *           "startUpdatedAt": "2024-01-01 12:00:00",
     *           "endUpdatedAt": "2024-01-01 12:30:00"
     *       }
     */
    @Setter
    private String parameter;


    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    PopHttpClient popHttpClient;




    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 最近5分钟
        int interval = 5*60;
        long endUpdatedAt = System.currentTimeMillis()/1000;
        long startUpdatedAt = endUpdatedAt-interval;

        if (JSONUtil.isTypeJSON(parameter)) {
            JSONObject parameterJson = JSONUtil.parseObj(parameter);
            startUpdatedAt = DateUtil.parse(parameterJson.getStr("startUpdatedAt")).getTime();
            endUpdatedAt = DateUtil.parse(parameterJson.getStr("endUpdatedAt")).getTime();
        }

        List<PddShopToken> pddShopTokens = effectiveShopList();
        for (PddShopToken pddShopToken : pddShopTokens) {
            singleExecute(pddShopToken, startUpdatedAt, endUpdatedAt);
        }

    }

    private void singleExecute(PddShopToken pddShopToken,
                               long startUpdateAt, long endUpdateAt) {
        try {
            pddOrderIncrementGetAll(pddShopToken, startUpdateAt, endUpdateAt);
        } catch (Exception e) {
            log.error("pddOrderNumberListIncrementGetAll error, printerShop: {}, startUpdatedAt: {}, endUpdatedAt: {}",
                    pddShopToken.getOwnerId(), startUpdateAt, endUpdateAt, e);
        }
    }

    private void pddOrderIncrementGetAll(  PddShopToken pddShopToken,
                                         long startUpdatedAt, long endUpdatedAt) throws Exception {
        int page = 1;
        PddOrderNumberListIncrementGetResponse response =
                pddOrderIncrementGet(pddShopToken, page, startUpdatedAt, endUpdatedAt);
        handle(pddShopToken, response);
        while (response.getOrderSnIncrementGetResponse().getHasNext()) {
            page++;
            response = pddOrderIncrementGet(pddShopToken,page, startUpdatedAt, endUpdatedAt);
            handle(pddShopToken, response);
        }
    }


    private PddOrderNumberListIncrementGetResponse pddOrderIncrementGet(
            PddShopToken pddShopToken, Integer page,
            Long startUpdatedAt, Long endUpdatedAt) throws Exception {
        PddOrderNumberListIncrementGetRequest request = new PddOrderNumberListIncrementGetRequest();
        request.setEndUpdatedAt(endUpdatedAt);
        request.setIsLuckyFlag(0);
        request.setOrderStatus(5);
        request.setPage(page);
        request.setRefundStatus(5);
        request.setStartUpdatedAt(startUpdatedAt);
        request.setUseHasNext(true);
        return popHttpClient.syncInvoke(request, pddShopToken.getAccessToken());
    }

    @Autowired
    IPddShopTokenService pddShopTokenService;

    public List<PddShopToken> effectiveShopList() {
       return pddShopTokenService.list(new LambdaQueryWrapper<PddShopToken>()
                .gt(PddShopToken::getExpiresAt, System.currentTimeMillis()/1000)
        );


//        Query query = new Query();
//        query.addCriteria(Criteria.where("expiresAt").gt(System.currentTimeMillis()));
//        return mongoTemplate.find(query, PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse.class);
//        return new ArrayList<>();
    }



    private void handle(PddShopToken pddShopToken,
                        PddOrderNumberListIncrementGetResponse response) {
        List<PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItem> orderSnList =
                response.getOrderSnIncrementGetResponse().getOrderSnList();
        for (PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItem order : orderSnList) {
            mongoHandle(pddShopToken, order);
        }
    }



    private void mongoHandle(PddShopToken pddShopToken,
                             PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItem order) {
        JSONObject jsonObject = JSONUtil.parseObj(order);
        jsonObject.set("_id", order.getOrderSn());
        jsonObject.set("pdd_shop_id", pddShopToken.getOwnerId());
        mongoTemplate.save(jsonObject,"pdd_order");
    }

}
