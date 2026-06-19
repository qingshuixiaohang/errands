package fun.cyhgraph.handler;

import fun.cyhgraph.constant.MessageConstant;
import fun.cyhgraph.exception.BaseException;
import fun.cyhgraph.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result excepitonHandler(BaseException ex, HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        log.info("Authorization: {}", authHeader);
        log.error("【抓内鬼】异常堆栈跟踪：", ex);
        log.info("【Debug】异常接口路径: {}, 请求方法: {}", request.getRequestURI(), request.getMethod());

        log.info("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        System.out.println("什么逆天sql错误到是打印啊！！！" + message);
        if (message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXiST;
            return Result.error(msg);
        }else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    @ExceptionHandler
    public Result exceptionHandler(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        log.info("参数校验异常：{}", message);
        return Result.error(message);
    }

    @ExceptionHandler
    public Result exceptionHandler(HttpMessageNotReadableException ex){
        log.error("请求参数解析失败：{}", ex.getMessage());
        return Result.error("请求参数格式错误，请检查输入数据");
    }
}