package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 屈晓磊
 * @description 针对表【user_info(用户表)】的数据库操作Service实现
 * @createDate 2022-09-06 18:44:16
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
        implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 登录
     *
     * @param Info
     * @return
     */
    @Override
    public LoginSuccessVo login(UserInfo Info) {

        LoginSuccessVo vo = new LoginSuccessVo();

        // 1.查询数据库
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getName, Info.getLoginName())
                .eq(UserInfo::getPasswd, MD5.encrypt(Info.getPasswd())));

        // 2.登录成功
        if (userInfo != null) {
            // 生产令牌
            String token = UUID.randomUUID().toString().replace("-", "");

            // redis中绑定信息
            redisTemplate.opsForValue()
                    .set(SysRedisConst.LOGIN_USER + token,
                            Jsons.toStr(userInfo),
                            7, TimeUnit.DAYS);

            vo.setToken(token);
            vo.setNickName(userInfo.getNickName());

            return vo;
        }

        return null;
    }

    /**
     * 退出
     *
     * @param token
     */
    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }
}




