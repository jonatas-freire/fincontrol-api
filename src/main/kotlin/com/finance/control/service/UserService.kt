package com.finance.control.service

import com.finance.control.cloudinary.CloudinaryService
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
    @Autowired
    private lateinit var cloudinaryService: CloudinaryService

    fun createOrAuthenticate(user: User): UserValidation<User?, UserStatus> {
        val existsUser = userRepository.findByEmail(user.email)
                ?: return create(user)

        return when (authenticateService.shouldReAuthenticate(existsUser)){
            AuthenticationStatus.AUTHENTICATED ->
                UserValidation(UserStatus.EMAIL_ALREADY_REGISTERED)
            else -> UserValidation(UserStatus.EMAIL_NOT_AUTHENTICATED, existsUser)
        }
    }

    private fun create(user: User): UserValidation<User?, UserStatus> {
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

    fun solicitResetPassword(email: String): UserValidation<User?, UserStatus> {
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

    fun resetPassword(email: String, code: String, password: String, confPassword: String): UserValidation<User?, UserStatus> {
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

    fun authenticate( auth: Authenticate ): UserValidation<Boolean?, AuthenticationStatus> {
        return try {
            val existAuth = authenticateService.existCodeUser(auth.email, auth.code)
                    ?: return UserValidation(AuthenticationStatus.AUTHENTICATE_CODE_INVALID)

            val user = userRepository.findByEmail(auth.email)
                    ?: return UserValidation(AuthenticationStatus.AUTHENTICATE_CODE_INVALID)

            val updatedUser = user.copy( auth = true )

            authenticateService.deleteCodeUser(auth.email)

            userRepository.save(updatedUser)

            UserValidation(AuthenticationStatus.AUTHENTICATION_SUCCESS, true)

        } catch (e: Exception) {
            UserValidation(AuthenticationStatus.ERROR, true)
        }

    }

    fun exist(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun edit(user: User): UserValidation<User?, UserStatus>  {
        val tokenEmail = getCurrentUserEmail()
        val actualUserInfo = exist(tokenEmail)
                ?: return UserValidation(UserStatus.EMAIL_NOT_FOUND)

        try {
            val hasNewPhoto = if (user.photo != "")
                    cloudinaryService.upload(user.photo, mapOf(
                            "overwrite" to true,
                            "unique_filename" to true,
                            "public_id" to user.email
                    ))?.url
                            ?: actualUserInfo.photo
                else actualUserInfo.photo

            val hasNewPassword = if(user.password != "")
                    bCryptPasswordEncoder.encode(user.password)
                else actualUserInfo.password

            val updatedUser = userRepository.save(actualUserInfo.copy(
                    name = user.name, photo = hasNewPhoto,
                    password = hasNewPassword
            ))

            return UserValidation(UserStatus.USER_UPDATED, updatedUser)
        } catch ( e: Exception ) {
            return UserValidation(UserStatus.ERROR_UPDATE_USER)
        }
    }

    private fun getCurrentUserEmail() = (
                SecurityContextHolder
                        .getContext()
                        .authentication
                        .principal as UserDetailsImpl
                ).username


}