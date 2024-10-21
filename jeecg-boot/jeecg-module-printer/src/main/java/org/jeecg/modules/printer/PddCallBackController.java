package org.jeecg.modules.printer;


import lombok.extern.slf4j.Slf4j;
import org.jeecg.config.shiro.IgnoreAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/callback")
public class PddCallBackController {


    @IgnoreAuth
    @GetMapping("/pdd")
    public void pdd() {

    }
}
