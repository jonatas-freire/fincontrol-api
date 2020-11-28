package com.finance.control.mail

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*


@Configuration
class MailerConfig {
    @Value("\${mail.protocol}")
    private val protocol: String? = null

    @Value("\${mail.host}")
    private val host: String? = null

    @Value("\${mail.port}")
    private val port = 0

    @Value("\${mail.smtp.socketFactory.port}")
    private val socketPort = 0

    @Value("\${mail.smtp.auth}")
    private val auth = false

    @Value("\${mail.smtp.starttls.enable}")
    private val starttls = false

    @Value("\${mail.smtp.starttls.required}")
    private val startlls_required = false

    @Value("\${mail.smtp.debug}")
    private val debug = false

    @Value("\${mail.smtp.socketFactory.fallback}")
    private val fallback = false

    @Value("\${mail.from}")
    private val from: String? = null

    @Value("\${mail.username}")
    private val username: String? = null

    @Value("\${mail.password}")
    private val password: String? = null
    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailProperties = Properties()
        mailProperties["mail.smtp.auth"] = auth
        mailProperties["mail.smtp.starttls.enable"] = starttls
        mailProperties["mail.smtp.starttls.required"] = startlls_required
        mailProperties["mail.smtp.socketFactory.port"] = socketPort
        mailProperties["mail.smtp.debug"] = debug
        mailProperties["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        mailProperties["mail.smtp.socketFactory.fallback"] = fallback
        val mailSender = JavaMailSenderImpl()
        mailSender.javaMailProperties = mailProperties
        mailSender.host = host
        mailSender.port = port
        mailSender.protocol = protocol
        mailSender.username = username
        mailSender.password = password
        return mailSender
    }
}