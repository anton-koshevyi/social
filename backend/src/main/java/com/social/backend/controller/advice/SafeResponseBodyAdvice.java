package com.social.backend.controller.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.social.backend.dto.ResponseMapper;

public abstract class SafeResponseBodyAdvice<T, R> implements ResponseBodyAdvice<Object> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final ResponseMapper<T, R> responseMapper;
    
    public SafeResponseBodyAdvice(ResponseMapper<T, R> responseMapper) {
        this.responseMapper = responseMapper;
    }
    
    public abstract R beforeBodyWriteSafely(T body,
                                            MethodParameter returnType,
                                            MediaType selectedContentType,
                                            Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                            ServerHttpRequest request,
                                            ServerHttpResponse response);
    
    @Override
    public final boolean supports(MethodParameter returnType,
                                  Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public final Object beforeBodyWrite(Object body,
                                        MethodParameter returnType,
                                        MediaType selectedContentType,
                                        Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                        ServerHttpRequest request,
                                        ServerHttpResponse response) {
        if (body == null) {
            logger.debug("Body is null");
            return null;
        }
        
        Class<?>[] types = GenericTypeResolver.resolveTypeArguments(this.getClass(), SafeResponseBodyAdvice.class);
        
        if (types == null) {
            logger.debug("Processed types are not specified");
            return body;
        }
        
        Class<?> expectedType = types[0];
        Class<?> actualType = body.getClass();
        
        if (!expectedType.isAssignableFrom(actualType)) {
            logger.debug("Expected type '{}' neither equal nor parent of body of type '{}'", expectedType, actualType);
            return body;
        }
        
        logger.debug("Processing body of type '{}'", actualType);
        return this.beforeBodyWriteSafely(
                (T) body,
                returnType,
                selectedContentType,
                selectedConverterType,
                request,
                response
        );
    }
}
