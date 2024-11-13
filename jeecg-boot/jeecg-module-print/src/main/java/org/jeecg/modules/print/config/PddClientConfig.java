package org.jeecg.modules.print.config;

import com.pdd.pop.sdk.http.PopHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PddClientConfig {



    String clientId = "4d3167f8f01e4ce2ab319a92d96e4cc1";
    String clientSecret = "5137550a8c9f236088f4f72234fbf4d68c5e0edb";



   @Bean
    public PopHttpClient popHttpClient() {
       return new PopHttpClient(clientId, clientSecret);
    }

}
