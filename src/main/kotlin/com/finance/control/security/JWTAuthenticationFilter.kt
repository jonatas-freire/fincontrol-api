package com.finance.control.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.finance.control.authorization
import com.finance.control.bearer
import com.finance.control.model.Credentials
import com.finance.control.model.UserDetailsImpl
import org.cloudinary.json.JSONObject
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.PrintWriter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter: UsernamePasswordAuthenticationFilter {

    private var jwtUtil: JWTUtil

    constructor(authenticationManager: AuthenticationManager, jwtUtil: JWTUtil) : super() {
        this.authenticationManager = authenticationManager
        this.jwtUtil = jwtUtil
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication? {
        try {
            val (email, password) = ObjectMapper().readValue(request.inputStream, Credentials::class.java)

            val token = UsernamePasswordAuthenticationToken(email, password)

            return authenticationManager.authenticate(token)
        } catch (e: Exception) {
            throw UsernameNotFoundException("User not found!")
        }
    }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse, chain: FilterChain?, authResult: Authentication) {
        val username = (authResult.principal as UserDetailsImpl).username
        val token = jwtUtil.generateToken(username)
        response.addHeader(authorization, "$bearer $token")
        response!!.status = HttpServletResponse.SC_OK
        response.contentType = "application/json"

        val out: PrintWriter = response.writer

        out.print(
                JSONObject(mapOf(
                        "status" to 200,
                        "content" to mapOf( "accessToken" to token ),
                        "message" to "Usuário logado!"
                )).toString()
        )
    }

    override fun unsuccessfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, failed: AuthenticationException?) {
        response!!.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"

        val out: PrintWriter = response.writer

        out.print(
                JSONObject(mapOf(
                        "status" to 403,
                        "message" to "Usuário nao encontrado"
                )).toString()
        )

    }

}

