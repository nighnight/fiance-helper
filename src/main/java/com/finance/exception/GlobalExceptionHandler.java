package com.finance.exception;

import com.finance.util.Result;
import com.finance.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 全局异常处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理所有业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return ResultUtil.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理所有其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        String requestURI = "";
        try {
            // 获取 HttpServletRequest，通常通过注入或RequestContextHolder
            // 这里为了简化，我们假设这是一个Ajax请求，返回JSON
            // 如果是普通页面请求，可以返回错误页面
            // 如果需要区分Ajax和页面请求，可以通过请求头 'X-Requested-With' == 'XMLHttpRequest' 或判断Content-Type
            return ResultUtil.error(500, "系统繁忙，请稍后再试: " + e.getMessage());
        } catch (Exception ex) {
            log.error("在处理异常时发生异常", ex);
            return ResultUtil.error(500, "系统未知错误");
        }
    }

    /**
     * 处理跳转页面时的错误，例如 404 (这里简单示例，实际404由Spring Boot处理)
     * 对于Spring Boot应用，通常不会直接在这里处理404，而是由默认的BasicErrorController或自定义ErrorController处理
     * 这个方法更适合返回一个错误页面而不是JSON
     */
    @ExceptionHandler({RuntimeException.class}) // 可以捕获更广泛的错误
    public ModelAndView handleErrorPage(RuntimeException e) {
        log.error("发生页面相关运行时错误: {}", e.getMessage(), e);
        ModelAndView mav = new ModelAndView("error/500"); // 假设有一个error/500.html页面
        mav.addObject("errorMessage", "页面加载失败或操作异常：" + e.getMessage());
        return mav;
    }
}