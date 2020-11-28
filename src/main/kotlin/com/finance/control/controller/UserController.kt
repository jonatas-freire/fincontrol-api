package com.finance.control.controller


import com.finance.control.dto.DTO
import com.finance.control.dto.DTOResetPassword
import com.finance.control.dto.DTOSolicitResetPassword
import com.finance.control.dto.DTOUserCreate
import com.finance.control.model.Authenticate
import com.finance.control.model.User
import com.finance.control.service.AuthenticateService
import com.finance.control.service.UserService
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

    @ApiOperation(value = "Cadastro de Usuário")
    @PostMapping("/signup")
    fun signup(@RequestBody user: User): ResponseEntity<DTO<DTOUserCreate?>> {
        val statusSignup = userService.createOrAuthenticate(user)

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
    fun solicitResetPassword(@RequestBody solicitResetPassword: DTOSolicitResetPassword) :
            ResponseEntity<DTO<DTOSolicitResetPassword?>> {

        val solicitResetPassword = userService.solicitResetPassword(solicitResetPassword.email)

        val dto: DTO<DTOSolicitResetPassword?> = when (solicitResetPassword.status) {
            UserStatus.EMAIL_NOT_FOUND -> DTO(
                    status = 404, message = "Usuário nao encontrado!", content = null
            )
            UserStatus.ERROR_SEND_EMAIL -> DTO(
                    status = 500, message = "Houve um erro ao enviar o email!", content = null
            )
            UserStatus.SEND_EMAIL -> DTO (
                    status = 200, content = DTOSolicitResetPassword().transform(solicitResetPassword.result), message = "Codigo enviado com sucesso!"
            )
            else -> DTO(
                    status = 500, message = "Houve um erro no servidor!", content = null
                )
        }

        return ResponseEntity.status(dto.status).body(dto)

    }

    @PutMapping("/reset/password")
    fun resetPassword(@RequestBody resetInfo: DTOResetPassword) :
            ResponseEntity<DTO<Boolean?>> {

        val resetPassword = userService.resetPassword(
                resetInfo.email,
                resetInfo.code,
                resetInfo.password,
                resetInfo.confPassword
        )

        val dto: DTO<Boolean?> = when( resetPassword.status ) {
            UserStatus.EMAIL_NOT_FOUND -> DTO( status = 404, message = "Usuário nao encontrado")
            UserStatus.CODE_NOT_FOUND -> DTO( status = 404, message = "Codigo de autenticacao invalido")
            UserStatus.PASSWORDS_NOT_EQUAL ->  DTO( status = 400, message = "As senhas devem ser iguais")
            UserStatus.PASSWORD_RESETED -> DTO( status = 200, content = true, message = "A senha foi alterada!")
            else -> DTO( status = 500, message = "Houve um erro no servidor")
        }

        return ResponseEntity.status(dto.status).body(dto)
    }

}
