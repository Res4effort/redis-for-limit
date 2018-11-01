package com.example.limit.access.advice;

import com.example.limit.access.annotation.AccessLimit;
import com.example.limit.access.exception.AccessLimitException;
import com.example.limit.access.service.AccessLimitService;
import com.example.limit.access.util.AccessLimitUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class AccessLimitAspect {

    private static final ParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();
    private static final ExpressionParser parser = new SpelExpressionParser();

    @Resource
    private AccessLimitService accessLimitService;

    @Pointcut("@annotation(com.example.limit.access.annotation.AccessLimit)")
    private void point() {
    }

    @Around("point()")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {

        StandardEvaluationContext sec = new StandardEvaluationContext(joinPoint.getArgs());
        sec = setContextVariables(sec, joinPoint);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        AccessLimit accessLimit = methodSignature.getMethod().getAnnotation(AccessLimit.class);
        String spEL = accessLimit.SpEL();
        Expression expression = parser.parseExpression(spEL);
        String limitIp = expression.getValue(sec, String.class);
        // redis key: ip + method name
        String key = limitIp + methodSignature.getMethod().getName();

        if (accessLimitService.isLimited(key)) {
            log.info("[Access limit log] | The ip address {} visited too frequently.", limitIp);
            throw new AccessLimitException(accessLimit.tipMsg());
        }
        Object res = joinPoint.proceed();
        accessLimitService.saveAccessTimes(key, accessLimit.duration(), accessLimit.times());
        return res;
    }

    private StandardEvaluationContext setContextVariables(StandardEvaluationContext sec, ProceedingJoinPoint joinPoint) {
        try {
            Method getIp = AccessLimitUtil.class.getMethod("getUniqueId", HttpServletRequest.class);
            sec.setVariable("getUniqueId", getIp);

            Method tMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
            String[] parametersName = discover.getParameterNames(tMethod);
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    sec.setVariable(parametersName[i], args[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sec;
    }
}
