package com.finance.control.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.finance.control.model.User
import java.util.*

class DTOSolicitResetPassword(
        val email : String,
        @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
        val id: UUID = UUID.randomUUID()
) {



    companion object {
        fun fromUser( user: User? ): DTOSolicitResetPassword {
            return DTOSolicitResetPassword(user!!.email, user.id)
        }
    }

}