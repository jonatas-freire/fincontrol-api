package com.finance.control.controller


import com.finance.control.dto.*
import com.finance.control.model.Authenticate
import com.finance.control.model.User
import com.finance.control.security.JWTUtil
import com.finance.control.service.UserService
import com.finance.control.validation.AuthenticationStatus
import com.finance.control.validation.UserStatus
import io.swagger.annotations.ApiOperation
import org.cloudinary.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/user")
class UserController{

    @Autowired
    private lateinit var userService: UserService



    @Autowired
    private lateinit var  jwtUtil: JWTUtil

    @PostMapping("/signup")
    fun signup(@RequestBody body: User): ResponseEntity<DTO<DTOUserCreate?>> {
        val statusSignup = userService.createOrAuthenticate(body)

        val dto: DTO<DTOUserCreate?> = when (statusSignup.status){
            UserStatus.CREATED ->
                DTO(
                        status = 200,
                        content = DTOUserCreate.transform(statusSignup.result),
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
                        content = DTOUserCreate.transform(statusSignup.result),
                        message = "Autenticação é necessário"
                )

            else ->
                DTO(
                        status = 500,
                        message = "Houve um erro no servidor"
                )
        }

        return ResponseEntity.status(dto.status).body(dto)
    }

    @PostMapping("/solicit/password")
    fun solicitResetPassword(@RequestBody body: DTOSolicitResetPassword) :
            ResponseEntity<DTO<DTOSolicitResetPassword?>> {

        val solicitResetPassword = userService.solicitResetPassword(body.email)

        val dto: DTO<DTOSolicitResetPassword?> = when (solicitResetPassword.status) {
            UserStatus.EMAIL_NOT_FOUND -> DTO(
                    status = 404, message = "Usuário não encontrado!", content = null
            )
            UserStatus.ERROR_SEND_EMAIL -> DTO(
                    status = 500, message = "Houve um erro ao enviar o email!", content = null
            )
            UserStatus.SEND_EMAIL -> DTO(
                    status = 200, content = DTOSolicitResetPassword.fromUser(solicitResetPassword.result), message = "Codigo enviado com sucesso!"
            )
            else -> DTO(
                    status = 500, message = "Houve um erro no servidor!", content = null
            )
        }

        return ResponseEntity.status(dto.status).body(dto)

    }

    @PostMapping("/reset/password")
    fun resetPassword(@RequestBody body: DTOResetPassword) :
            ResponseEntity<DTO<Boolean?>> {

        val resetPassword = userService.resetPassword(
                body.email,
                body.code,
                body.password,
                body.confPassword
        )

        val dto: DTO<Boolean?> = when( resetPassword.status ) {
            UserStatus.EMAIL_NOT_FOUND -> DTO(status = 404, message = "Usuário não encontrado")
            UserStatus.CODE_NOT_FOUND -> DTO(status = 404, message = "Codigo de autenticacao invalido")
            UserStatus.PASSWORDS_NOT_EQUAL -> DTO(status = 400, message = "As senhas devem ser iguais")
            UserStatus.PASSWORD_RESETED -> DTO(status = 200, content = true, message = "A senha foi alterada!")
            else -> DTO(status = 500, message = "Houve um erro no servidor")
        }

        return ResponseEntity.status(dto.status).body(dto)
    }

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody body: Authenticate)
        :ResponseEntity<DTO<Map<String, String>?>> {


        val authenticate = userService.authenticate(body)

        val dto: DTO<Map<String, String>?> = when( authenticate.status ) {
            AuthenticationStatus.AUTHENTICATE_CODE_INVALID -> DTO(
                    status = 404,
                    message = "Codigo invalido ou usuário não encontrado"
            )

            AuthenticationStatus.AUTHENTICATION_SUCCESS -> DTO(
                    status = 200,
                    content = mapOf(
                                    "accessToken" to  jwtUtil.generateToken(body.email)
                            ),
                    message = "Usuário autenticado"
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
            UserStatus.EMAIL_NOT_FOUND ->  DTO( status = 404, message = "Usuário não encontrado")
            UserStatus.USER_UPDATED -> DTO(
                    status = 200,
                    content = DTOUserEdit.fromUser(editUser.result),
                    message = "Usuário atualizado!"
            )
            else -> DTO( status = 500, message = "Houve um erro no servidor")
        }
        return ResponseEntity.status(dto.status).body(dto)

    }

    @GetMapping("/info")
    fun info(): ResponseEntity<DTO<DTOUser?>> {

        val user = userService.getCurrentUser()

        val dto: DTO<DTOUser?> = when ( user ) {
            null -> DTO( status = 404, message = "Usuário não encontrado")
            else -> DTO(
                    status = 200,
                    message = "Aqui estão as informações dos usuários",
                    content = DTOUser.fromUser(user)
            )
        }

        return ResponseEntity.status(dto.status).body(dto)
    }

    @GetMapping( "/refreshToken")
    fun refreshToken() {

    }

}
