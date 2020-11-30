package com.finance.control.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.finance.control.model.User
import java.util.*
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

class DTOUser (
        val id: UUID,
        val email: String,
        val name: String,
        val balanceAvailable: Double,
        val photo: String,
) {
    companion object {
        fun fromUser( user: User): DTOUser {
            return DTOUser(
                    id = user.id,
                    email = user.email,
                    name = user.name,
                    balanceAvailable = user.balanceAvailable,
                    photo = user.photo
            )
        }
    }
}