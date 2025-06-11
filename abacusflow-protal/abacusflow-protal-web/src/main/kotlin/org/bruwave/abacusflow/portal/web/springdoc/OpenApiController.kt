// package org.bruwave.abacusflow.portal.web.springdoc
//
// import org.springframework.core.io.ClassPathResource
// import org.springframework.core.io.Resource
// import org.springframework.http.MediaType
// import org.springframework.http.ResponseEntity
// import org.springframework.web.bind.annotation.GetMapping
// import org.springframework.web.bind.annotation.RestController
// import java.nio.charset.StandardCharsets
//
// @RestController
// class OpenApiController {
//
//    @GetMapping("/openapi.yaml")
//    fun serveYaml(): ResponseEntity<Resource?> {
//        val resource: Resource = ClassPathResource("static/openapi.yaml")
//        val mediaType = MediaType("text", "plain", StandardCharsets.UTF_8)
//
//        return ResponseEntity.ok()
//            .contentType(mediaType)
//            .body(resource)
//    }
// }
class OpenApiController
