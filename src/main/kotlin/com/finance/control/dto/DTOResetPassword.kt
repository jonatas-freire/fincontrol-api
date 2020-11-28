package com.finance.control.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.finance.control.model.User
import java.util.*

class DTOResetPassword(
        @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
        val password: String,
        @JsonProperty(value = "confPassword", access = JsonProperty.Access.WRITE_ONLY)
        val confPassword: String,
        @JsonProperty(value = "code", access = JsonProperty.Access.WRITE_ONLY)
        val code: String,
        @JsonProperty(value = "email", access = JsonProperty.Access.WRITE_ONLY)
        val email: String,
)