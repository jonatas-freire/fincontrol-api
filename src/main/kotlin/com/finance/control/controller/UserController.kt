package com.finance.control.controller


import com.finance.control.dto.*
import com.finance.control.model.Authenticate
import com.finance.control.model.User
import com.finance.control.security.JWTUtil
import com.finance.control.service.AuthenticateService
import com.finance.control.service.UserService
import com.finance.control.validation.AuthenticationStatus
import com.finance.control.validation.UserStatus
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/user")
class UserController{

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var authenticateService: AuthenticateService

    @Autowired
    private lateinit var  jwtUtil: JWTUtil

    @ApiOperation(value = "Cadastro de Usuário")
    @PostMapping("/signup")
    fun signup(@RequestBody body: User): ResponseEntity<DTO<DTOUserCreate?>> {
        val statusSignup = userService.createOrAuthenticate(body)

        val userCreate = DTOUserCreate().transform(statusSignup.result) ?: null
        val dto: DTO<DTOUserCreate?> = when (statusSignup.status){
            UserStatus.CREATED ->
                DTO(
                        status = 200,
                        content = userCreate,
                        message = "Usuário criado!"
                )

            UserStatus.EMAIL_ALREADY_REGISTERED ->
                DTO(
                        status = 403,
                        message = "O email informado já foi cadastrado"
                )

            UserStatus.EMAIL_NOT_AUTHENTICATED ->
                DTO(
                        status = 201,
                        content = userCreate,
                        message = "Autenticacao é necessário"
                )

            else ->
                DTO(
                        status = 500,
                        message = "Houve um erro no servidor"
                )
        }

        return ResponseEntity.status(dto.status).body(dto)
    }

    @PostMapping("/reset/password")
    fun solicitResetPassword(@RequestBody body: DTOSolicitResetPassword) :
            ResponseEntity<DTO<DTOSolicitResetPassword?>> {

        val solicitResetPassword = userService.solicitResetPassword(body.email)

        val dto: DTO<DTOSolicitResetPassword?> = when (solicitResetPassword.status) {
            UserStatus.EMAIL_NOT_FOUND -> DTO(
                    status = 404, message = "Usuário nao encontrado!", content = null
            )
            UserStatus.ERROR_SEND_EMAIL -> DTO(
                    status = 500, message = "Houve um erro ao enviar o email!", content = null
            )
            UserStatus.SEND_EMAIL -> DTO(
                    status = 200, content = DTOSolicitResetPassword().transform(solicitResetPassword.result), message = "Codigo enviado com sucesso!"
            )
            else -> DTO(
                    status = 500, message = "Houve um erro no servidor!", content = null
            )
        }

        return ResponseEntity.status(dto.status).body(dto)

    }

    @PutMapping("/reset/password")
    fun resetPassword(@RequestBody body: DTOResetPassword) :
            ResponseEntity<DTO<Boolean?>> {

        val resetPassword = userService.resetPassword(
                body.email,
                body.code,
                body.password,
                body.confPassword
        )

        val dto: DTO<Boolean?> = when( resetPassword.status ) {
            UserStatus.EMAIL_NOT_FOUND -> DTO(status = 404, message = "Usuário nao encontrado")
            UserStatus.CODE_NOT_FOUND -> DTO(status = 404, message = "Codigo de autenticacao invalido")
            UserStatus.PASSWORDS_NOT_EQUAL -> DTO(status = 400, message = "As senhas devem ser iguais")
            UserStatus.PASSWORD_RESETED -> DTO(status = 200, content = true, message = "A senha foi alterada!")
            else -> DTO(status = 500, message = "Houve um erro no servidor")
        }

        return ResponseEntity.status(dto.status).body(dto)
    }

    @PutMapping("/authenticate")
    fun authenticate(@RequestBody body: Authenticate)
        :ResponseEntity<DTO<String?>> {


        val authenticate = userService.authenticate(body)

        val dto: DTO<String?> = when( authenticate.status ) {
            AuthenticationStatus.AUTHENTICATE_CODE_INVALID -> DTO(
                    status = 404,
                    message = "Codigo invalido ou usuário nao encontrado"
            )

            AuthenticationStatus.AUTHENTICATION_SUCCESS -> DTO(
                    status = 200, content = jwtUtil.generateToken(body.email), message = "Usuário autenticado"
            )

            else -> DTO(status = 500, message = "Houve um erro no servidor")

        }

        return ResponseEntity.status(dto.status).body(dto)
    }

    @PutMapping("/edit")
    fun editUser(@RequestBody body: User): ResponseEntity<DTO<DTOUserEdit?>> {

        val editUser = userService.edit(body)
        val dto: DTO<DTOUserEdit?> = when ( editUser.status ) {
            UserStatus.ERROR_UPDATE_USER -> DTO( status = 500, message = "Houve um erro ao atualizar o usuário")
            UserStatus.EMAIL_NOT_FOUND ->  DTO( status = 404, message = "Usuário nao encontrado")
            UserStatus.USER_UPDATED -> DTO(
                    status = 200,
                    content = DTOUserEdit.fromUser(editUser.result),
                    message = "Usuário atualizado!"
            )
            else -> DTO( status = 500, message = "Houve um erro no servidor")
        }
        return ResponseEntity.status(dto.status).body(dto)

    }

}
