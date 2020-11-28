package com.finance.control.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import javax.persistence.*

@Entity
@Table(name="tb_authenticate")
data class Authenticate (
        @Id
        @Column(name = "email", unique = true)
        val email: String,

        @Column(name = "cd_auth_code")
        val code: String,

        @JsonProperty(value = "dt_code_created", access = JsonProperty.Access.READ_ONLY)
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "dt_code_created")
        val createdAt: Calendar = Calendar.getInstance(),

        @Enumerated(EnumType.STRING)
        @Column( name="cd_type_auth")
        val type: AuthenticateType

)