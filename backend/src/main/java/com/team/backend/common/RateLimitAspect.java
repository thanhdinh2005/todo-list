package com.team.backend.common;

import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {

  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

  @Around("@annotation(com.team.backend.common.RateLimit) || @within(com.team.backend.common.RateLimit)")
  public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
    HttpServletRequest request = getHttpServletRequest();

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    Class<?> targetClass = joinPoint.getTarget().getClass();

    RateLimit rateLimit = method.getAnnotation(RateLimit.class);
    if (rateLimit == null) {
      rateLimit = targetClass.getAnnotation(RateLimit.class);
    }

    if (rateLimit == null) {
      return joinPoint.proceed();
    }

    final RateLimit finalRateLimit = rateLimit;

    String clientIp = getClientIp(request);
    String key = clientIp + ":" + targetClass.getName() + "." + method.getName();

    Bucket bucket = cache.computeIfAbsent(key, k -> createNewBucket(finalRateLimit));

    if (bucket.tryConsume(1)) {
      return joinPoint.proceed();
    } else {
      throw new AppException(ErrorCode.TOO_MANY_REQUESTS);
    }
  }

  private Bucket createNewBucket(RateLimit rateLimit) {
    Bandwidth limit = Bandwidth.builder()
      .capacity(rateLimit.capacity())
      .refillIntervally(rateLimit.capacity(), Duration.ofSeconds(rateLimit.durationInSeconds()))
      .build();
    return Bucket.builder().addLimit(limit).build();
  }

  private HttpServletRequest getHttpServletRequest() {
    ServletRequestAttributes attributes =
      (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      return attributes.getRequest();
    }
    throw new IllegalStateException("Cannot find HttpServletRequest");
  }

  private String getClientIp(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isEmpty()) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}
