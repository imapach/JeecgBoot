package org.jeecg.modules.printer;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.printer.dto.UploadImgDTO;
import org.jeecg.modules.printer.vo.ShopInfoVO;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/miniApp")
public class MiniAppController {


    @IgnoreAuth
    @GetMapping("/shopInfo")
    public Result<ShopInfoVO> getShopInfo(@RequestParam(name = "shopId") String shopId) {
        ShopInfoVO shopInfoVO = new ShopInfoVO();
        return Result.OK(shopInfoVO);
    }

    @IgnoreAuth
    @GetMapping("/checkOrder")
    public Result<Object> checkOrder(@RequestParam(name = "shopId") String shopId,
                                     @RequestParam(name = "orderId") String orderId) {

        return Result.OK();

    }

    @IgnoreAuth
    @PostMapping("/uploadImg")
    public Result<Object> uploadImg(@RequestBody UploadImgDTO uploadImgDTO) {
        return Result.ok();
    }


}
