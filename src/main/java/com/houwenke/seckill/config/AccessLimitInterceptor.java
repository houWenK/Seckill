package com.houwenke.seckill.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.houwenke.seckill.entity.User;
import com.houwenke.seckill.service.UserService;
import com.houwenke.seckill.utils.CookieUtil;
import com.houwenke.seckill.vo.RespBean;
import com.houwenke.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author: LC
 * @date 2022/3/9 4:27 下午
 * @ClassName: AccessLimitInterceptor
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService itUserService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            User tUser = getUser(request, response);
            UserContext.setUser(tUser);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            if (needLogin) {
                if (tUser == null) {
                    render(response, RespBeanEnum.SESSION_ERROR);
                }
                key += ":" + tUser.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);
            if (count == null) {
                valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            } else if (count < maxCount) {
                valueOperations.increment(key);
            } else {
                render(response, RespBeanEnum.ACCESS_LIMIT_REAHCED);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter printWriter = response.getWriter();
        RespBean bean = RespBean.error(respBeanEnum);
        printWriter.write(new ObjectMapper().writeValueAsString(bean));
        printWriter.flush();
        printWriter.close();
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        return itUserService.getUserByCookie(userTicket, request, response);
    }
}
