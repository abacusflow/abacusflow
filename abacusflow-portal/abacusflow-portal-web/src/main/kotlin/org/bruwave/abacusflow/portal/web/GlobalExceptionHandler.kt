package org.bruwave.abacusflow.portal.web

import jakarta.servlet.http.HttpServletRequest
import org.bruwave.abacusflow.portal.web.model.ErrorVO
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    // 处理参数校验异常（比如 @Valid 校验失败）
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorVO> {
        val errorMsg =
            ex.bindingResult.fieldErrors.joinToString("; ") {
                "${it.field}：${it.defaultMessage}"
            }

        return ResponseEntity.badRequest().body(ErrorVO(400, "参数校验错误：$errorMsg"))
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(ex: DataIntegrityViolationException): ResponseEntity<ErrorVO> {
        val rootMsg = ex.rootCause?.message ?: "违反数据完整性约束"
        val match = DUPLICATE_KEY_REGEX.find(rootMsg)

        val userFriendlyMsg =
            if (match != null) {
                val value = match.groupValues[1]
                "操作失败: 值 `$value` 已存在!"
            } else {
                "数据提交失败，请检查输入的数据是否符合要求。"
            }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorVO(409, userFriendlyMsg),
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArg(ex: IllegalArgumentException): ResponseEntity<ErrorVO> =
        ResponseEntity.badRequest().body(ErrorVO(400, "参数非法：${ex.message}"))

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ResponseEntity<ErrorVO> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorVO(409, "状态异常：${ex.message}"))

    companion object {
        val DUPLICATE_KEY_REGEX = Regex("""Key \(.+?\)=\((.+?)\) already exists""")
    }
}
