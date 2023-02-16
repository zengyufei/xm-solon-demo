//import lombok.extern.slf4j.Slf4j;
//import com.xunmo.annotations.ExceptionHandler;
//import org.noear.solon.annotation.Component;
//import org.noear.solon.core.handle.Context;
//
//@Slf4j
//@Component(index = 0)
//public class XmGlobalException {
//
//    @ExceptionHandler(NullPointerException.class)
//    public String handlerNullPointerException(Context ctx, Exception e) {
////        log.error(ExceptionUtil.stacktraceToString(e));
//        log.error("我是空异常处理器");
//        return e.getMessage();
//    }
//    @ExceptionHandler(ArithmeticException.class)
//    public String handlerArithmeticException(Context ctx, Exception e) {
////        log.error(ExceptionUtil.stacktraceToString(e));
//        log.error("我是除数为0处理器");
//        return e.getMessage();
//    }
//    @ExceptionHandler
//    public String handlerThrowable(Context ctx, Throwable e) {
////        log.error(ExceptionUtil.stacktraceToString(e));
//        log.error("我是默认异常处理器");
//        return e.getMessage();
//    }
//
//}
