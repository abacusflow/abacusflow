package org.bruwave.abacusflow.portal.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

sealed class ControllerException(
    msg: String,
    val code: Int,
) : Exception(msg)

class NotFoundException(
    msg: String,
    code: Int = HttpStatus.NOT_FOUND.value(),
) : ControllerException(msg, code)

@RestControllerAdvice
class GlobalExceptionHandler {
    //    // 处理自定义业务异常
//    @ExceptionHandler(NotFoundException::class)
//    fun handleBusinessException(
//        ex: ApiException,
//        request: HttpServletRequest,
//        response: HttpServletResponse
//    ): Unit {
//        response.sendError(ex.code, ex.message)
//    }
//
//    // 处理参数校验异常（比如 @Valid 校验失败）
//    @ExceptionHandler(MethodArgumentNotValidException::class)
//    fun handleValidationException(
//        ex: MethodArgumentNotValidException,
//        request: HttpServletRequest
//    ): ResponseEntity<ErrorVO> {
//        val errorMsg = ex.bindingResult.fieldErrors.joinToString("; ") {
//            "${it.field}：${it.defaultMessage}"
//        }
//
//        return ResponseEntity.badRequest().body(ErrorVO(400, "参数校验错误：$errorMsg"))
//    }

//    // 处理其他未捕获的异常
//    @ExceptionHandler(Exception::class)
//    fun handleGenericException(
//        ex: Exception,
//        request: HttpServletRequest
//    ): ResponseEntity<ErrorVO> {
//        ex.printStackTrace()  // 记录日志或上报
//
//        return ResponseEntity.internalServerError().body(ErrorVO(500, "服务器内部错误：${ex.message ?: "未知异常"}"))
//    }
}
