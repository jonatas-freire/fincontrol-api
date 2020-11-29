package com.finance.control.controller

import com.finance.control.model.Authenticate
import com.finance.control.service.AuthenticateService
import com.finance.control.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthenticateController {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var  authenticateService: AuthenticateService



}