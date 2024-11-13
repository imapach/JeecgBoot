package org.jeecg.modules.print.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddOrderNumberListIncrementGetRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddOrderNumberListIncrementGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddPopAuthTokenCreateResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
        long interval = 5*60*1000;
        long endUpdatedAt = System.currentTimeMillis();
        long startUpdatedAt = endUpdatedAt-interval;

        if (JSONUtil.isTypeJSON(parameter)) {
            JSONObject parameterJson = JSONUtil.parseObj(parameter);
            startUpdatedAt = DateUtil.parse(parameterJson.getStr("startUpdatedAt")).getTime();
            endUpdatedAt = DateUtil.parse(parameterJson.getStr("endUpdatedAt")).getTime();
        }

        List<PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse> shops = effectiveShopList();
        for (PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse shop : shops) {
            singleExecute(shop, startUpdatedAt, endUpdatedAt);
        }
    }

    private void singleExecute(PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse printerShopToken,
                               long startUpdateAt, long endUpdateAt) {
        try {
            pddOrderIncrementGetAll(printerShopToken, startUpdateAt, endUpdateAt);
        } catch (Exception e) {
            log.error("pddOrderNumberListIncrementGetAll error, printerShop: {}, startUpdatedAt: {}, endUpdatedAt: {}",
                    printerShopToken.getOwnerId(), startUpdateAt, endUpdateAt, e);
        }
    }

    private void pddOrderIncrementGetAll(PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse printerShopToken,
                                         long startUpdatedAt, long endUpdatedAt) throws Exception {
        int page = 1;
        PddOrderNumberListIncrementGetResponse response =
                pddOrderIncrementGet(printerShopToken, page, startUpdatedAt, endUpdatedAt);
        handle(printerShopToken, response);
        while (response.getOrderSnIncrementGetResponse().getHasNext()) {
            page++;
            response = pddOrderIncrementGet(printerShopToken,page, startUpdatedAt, endUpdatedAt);
            handle(printerShopToken, response);
        }
    }


    private PddOrderNumberListIncrementGetResponse pddOrderIncrementGet(
            PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse printerShopToken, Integer page,
            Long startUpdatedAt, Long endUpdatedAt) throws Exception {
        PddOrderNumberListIncrementGetRequest request = new PddOrderNumberListIncrementGetRequest();
        request.setEndUpdatedAt(endUpdatedAt);
        request.setIsLuckyFlag(0);

        request.setRefundStatus(5);
        request.setStartUpdatedAt(startUpdatedAt);
        request.setUseHasNext(true);
        return popHttpClient.syncInvoke(request, printerShopToken.getAccessToken());
    }

    public List<PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse> effectiveShopList() {
        Query query = new Query();
        query.addCriteria(Criteria.where("expiresAt").gt(System.currentTimeMillis()));
        return mongoTemplate.find(query, PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse.class);
    }



    private void handle(PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse printerShopToken,
                        PddOrderNumberListIncrementGetResponse response) {
        List<PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItem> orderSnList =
                response.getOrderSnIncrementGetResponse().getOrderSnList();
        for (PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItem order : orderSnList) {
            mongoHandle(printerShopToken, order);
        }
    }



    private void mongoHandle(PddPopAuthTokenCreateResponse.PopAuthTokenCreateResponse printerShopToken,
                             PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItem order) {
        JSONObject jsonObject = JSONUtil.parseObj(order);
        jsonObject.set("_id", order.getOrderSn());
        jsonObject.set("pdd_shop_id", printerShopToken.getOwnerId());
        mongoTemplate.save(jsonObject,"pdd_order");
    }

}
