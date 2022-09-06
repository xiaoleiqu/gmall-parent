package com.atguigu.gmall.model.vo.user;

import lombok.Data;

/**
 * @author quxiaolei
 * @date 2022/9/6 - 19:10
 */
@Data
public class LoginSuccessVo {
    private String token; // 用户的令牌
    private String nickName; // 用户的昵称
}
