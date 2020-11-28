package com.finance.control.service

import com.finance.control.model.Authenticate
import com.finance.control.model.AuthenticateType
import com.finance.control.model.User
import com.finance.control.model.UserDetailsImpl
import com.finance.control.repository.UserRepository
import com.finance.control.validation.AuthenticationStatus
import com.finance.control.validation.UserStatus
import com.finance.control.validation.UserValidation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*


@Service
class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var authenticateService: AuthenticateService
    @Autowired
    private lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    fun createOrAuthenticate(user: User): UserValidation<User?> {
        val existsUser = userRepository.findByEmail(user.email)
                ?: return create(user)

        return when (authenticateService.shouldReAuthenticate(existsUser)){
            AuthenticationStatus.AUTHENTICATED ->
                UserValidation(UserStatus.EMAIL_ALREADY_REGISTERED)
            else -> UserValidation(UserStatus.EMAIL_NOT_AUTHENTICATED, existsUser)
        }
    }

    private fun create(user: User): UserValidation<User?> {
        return try {
            val normalizedUser = user.copy(
                    password = bCryptPasswordEncoder.encode(user.password),
            )
            val createdUser = userRepository.save(normalizedUser)

            when (authenticateService.generateCode(createdUser, AuthenticateType.AUTHENTICATE_USER)) {
                AuthenticationStatus.AUTHENTICATE_CODE_SEND ->
                    UserValidation(UserStatus.CREATED, createdUser)
                else -> UserValidation(UserStatus.ERROR_SEND_EMAIL)
            }
        } catch (e: Exception) {
            UserValidation(UserStatus.ERROR_CREATE_USER)
        }
    }

    fun solicitResetPassword(email: String): UserValidation<User?> {
        val userExist = exist(email)
                ?: return UserValidation(UserStatus.EMAIL_NOT_FOUND)

        return try {
            when(authenticateService.generateCode(userExist, AuthenticateType.RESET_PASSWORD)) {
                AuthenticationStatus.AUTHENTICATE_CODE_SEND -> UserValidation(UserStatus.SEND_EMAIL, userExist)
                else -> UserValidation(UserStatus.ERROR_SEND_EMAIL)
            }
        } catch (e: Exception) {
            UserValidation(UserStatus.ERROR_SEND_EMAIL)
        }
    }

    fun resetPassword(email: String, code: String, password: String, confPassword: String): UserValidation<User?> {
        return try {

            if (password != confPassword)
                return UserValidation(UserStatus.PASSWORDS_NOT_EQUAL)

            authenticateService.existCodeUser(email, code)
                    ?: return UserValidation(UserStatus.CODE_NOT_FOUND)

            authenticateService.deleteCodeUser(email)

            val existsUser = userRepository.findByEmail(email)
                    ?: return UserValidation(UserStatus.EMAIL_NOT_FOUND)

            val normalizedUser = existsUser.copy( password = bCryptPasswordEncoder.encode(password) )

            return UserValidation(UserStatus.PASSWORD_RESETED, userRepository.save(normalizedUser))
        } catch ( e: Exception) {
            return UserValidation(UserStatus.ERROR_UPDATE_USER)
        }

    }

    fun exist(email: String): User? {
        return userRepository.findByEmail(email)
    }

    private fun getCurrentUserEmail() = (
                SecurityContextHolder
                        .getContext()
                        .authentication
                        .principal as UserDetailsImpl
                ).username


}