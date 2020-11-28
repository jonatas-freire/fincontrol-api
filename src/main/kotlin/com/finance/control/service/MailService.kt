package com.finance.control.service

import com.finance.control.mail.Mail
import com.finance.control.mail.MailerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import javax.mail.MessagingException


@Service
class MailService {

    @Autowired
    lateinit var  mailSender: JavaMailSender

    @Throws(MessagingException::class)
    fun send(message: Mail, isHtml: Boolean) {
        val emailMessage = mailSender.createMimeMessage()
        val mailBuilder = MimeMessageHelper(emailMessage, true)
        mailBuilder.setTo(message.mailTo)
        mailBuilder.setFrom(message.mailFrom)
        mailBuilder.setText(message.mailContent, isHtml) // Second parameter indicates that this is HTML mail
        mailBuilder.setSubject(message.mailSubject)
        mailSender.send(emailMessage)
    }
}


