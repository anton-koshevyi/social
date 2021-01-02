package com.social.backend.resolver.exception;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.social.backend.exception.LocalizedException;

@Component
public class GlobalExceptionHandler extends LocalizedHandlerExceptionResolver {

  /**
   * Must be ordered lower than HandlerExceptionResolver, implemented by
   * {@link org.springframework.boot.web.servlet.error.DefaultErrorAttributes},
   * otherwise {@code javax.servlet.error.exception} can be read incorrectly.
   */
  @Autowired
  public GlobalExceptionHandler(MessageSource messageSource) {
    super(messageSource);
    super.setOrder(HIGHEST_PRECEDENCE + 1);
  }

  @Override
  public final ModelAndView doResolveException(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    ModelAndView parentResolveResult = super.doResolveException(request, response, handler, ex);

    if (parentResolveResult != null) {
      return parentResolveResult;
    }

    try {
      if (ex instanceof LocalizedException) {
        return handleLocalized((LocalizedException) ex, request, response);
      }

      super.sendServerError(ex, request, response);
    } catch (Exception e) {
      logger.error("Failed to handle exception", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    logger.error("Global exception", ex);
    return new ModelAndView();
  }

  private ModelAndView handleLocalized(
      LocalizedException ex, HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String message = messageSource.getMessage(ex, request.getLocale());
    response.sendError(ex.getStatusCode(), message);
    return new ModelAndView();
  }

}
