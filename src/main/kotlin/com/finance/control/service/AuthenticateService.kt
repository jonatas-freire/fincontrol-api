package com.finance.control.service

import EmailBuilder
import com.finance.control.helper.Auth
import com.finance.control.mail.Mail
import com.finance.control.model.Authenticate
import com.finance.control.model.AuthenticateType
import com.finance.control.model.User
import com.finance.control.repository.AuthenticateRepository
import com.finance.control.repository.UserRepository
import com.finance.control.validation.AuthenticationStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticateService {

    @Autowired
    private lateinit var authenticateRepository: AuthenticateRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var mailService: MailService

    fun shouldReAuthenticate(user: User): AuthenticationStatus {
        return when(user.auth) {
            true -> AuthenticationStatus.AUTHENTICATED
            false -> generateCode(user, AuthenticateType.AUTHENTICATE_USER)
        }
    }

    fun generateCode(user: User, type: AuthenticateType): AuthenticationStatus {
        return try {
            val generatedCode = Auth().generateCode(8)
            val userAuthenticate = Authenticate(
                    email = user.email, code = bCryptPasswordEncoder.encode(generatedCode),
                    type = type
            )
            authenticateRepository.save(userAuthenticate)
            sendCodeEmail(user, generatedCode, type)
        } catch (e: Exception) {
            AuthenticationStatus.ERROR_SEND_EMAIL
        }
    }

    fun existCodeUser(email: String, code: String): Authenticate? {
        val authenticateInfo = authenticateRepository.findByEmail(email)
                ?: return null

        return if(bCryptPasswordEncoder.matches(code, authenticateInfo.code)) authenticateInfo
               else null
    }

    fun deleteCodeUser(email: String) {
        authenticateRepository.deleteByEmail(email)
    }


    private fun sendCodeEmail(user: User, code: String, type: AuthenticateType): AuthenticationStatus {
        return try {

            val emailInfo = when( type ) {
                AuthenticateType.AUTHENTICATE_USER ->
                    mapOf( "subject" to "FinControl - Codigo de verificação",
                            "template" to "mail-template.html"  )
                AuthenticateType.RESET_PASSWORD ->
                    mapOf( "subject" to "FinControl - Codigo de recuperação de senha",
                            "template" to "forget-password-template.html")
            }


            val mail: Mail = EmailBuilder()
                    .from("jonatas.freire84@gmail.com")
                    .to(user.email)
                    .template(emailInfo["template"])
                    .addContext("name", user.name)
                    .addContext("code", code)
                    .subject(emailInfo["subject"])
                    .createMail()
            mailService.send(mail, true)
            AuthenticationStatus.AUTHENTICATE_CODE_SEND
        } catch (e: Exception) {
            AuthenticationStatus.ERROR_SEND_EMAIL
        }
    }

}