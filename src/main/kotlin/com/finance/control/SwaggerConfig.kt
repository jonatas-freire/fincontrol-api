package com.finance.control

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.service.ResponseMessage
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*


@Configuration
@EnableSwagger2
class SwaggerConfig {
    private val m401 = simpleMessage(401, "Não autorizado")
    private val m403 = simpleMessage(403, "Acesso negado")
    private val m404 = simpleMessage(404, "Não encontrado")
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, Arrays.asList(m401, m403, m404))
                .globalResponseMessage(RequestMethod.POST, Arrays.asList(m401, m403))
                .globalResponseMessage(RequestMethod.PUT, Arrays.asList(m401, m403, m404))
                .globalResponseMessage(RequestMethod.DELETE, Arrays.asList(m401, m403, m404))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.finance.control"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfo(
                "FinControl API",
                "API do projeto de conclusao de curso ",
                "Versão 1.0",
                null,
                Contact("Jonatas Freire dos Santos",
                        "https://github.com/jonatas-freire",
                        "jonatas.freire84@gmail.com"),
                "Permitido uso para estudantes",
                null,
                emptyList()
        )
    }

    private fun simpleMessage(code: Int, msg: String): ResponseMessage {
        return ResponseMessageBuilder()
                .code(code).message(msg).build()
    }
}