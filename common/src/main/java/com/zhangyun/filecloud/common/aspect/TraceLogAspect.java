package com.zhangyun.filecloud.common.aspect;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/7/23 16:47
 * @since: 1.0
 */
@Aspect
@Component
@Slf4j
@Order(20)
public class TraceLogAspect {

    @Pointcut("@annotation(com.zhangyun.filecloud.common.annotation.TraceLog)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object methodTimer(ProceedingJoinPoint jp) throws Throwable {
        long start = System.currentTimeMillis();
        Object outParam = jp.proceed();
        long cost = System.currentTimeMillis() - start;

        // 获取方法名
        String methodName = jp.getTarget().getClass().getName() + "."
                + ((MethodSignature) jp.getSignature()).getName();
        // 请求参数
        StringBuilder params = new StringBuilder();
        Object[] argValues = jp.getArgs();
        String[] argNames = ((MethodSignature) jp.getSignature()).getParameterNames();
        if (argValues != null) {
            for (int i = 0; i < argValues.length; i++) {
                params.append(argNames[i]).append(":").append(JSONObject.toJSONString(argValues[i]));
            }
        }
        // 输出
        log.info("执行方法: {}, 参数: {} ====> {}, 时间: {}ms", methodName, params, outParam, cost);
        return outParam;
    }

}
