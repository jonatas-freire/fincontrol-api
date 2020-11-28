package com.finance.control.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.finance.control.model.User
import java.util.*

class DTOSolicitResetPassword{

    lateinit var email : String
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    lateinit var id: UUID

    fun transform( user: User? ): DTOSolicitResetPassword {
        this.email = user!!.email
        this.id = user.id
        return this
    }
}