package com.finance.control.model
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name="tb_user")
data class User(

        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "id", updatable = false, unique = true, nullable = false)
        @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
        val id: UUID = UUID.randomUUID(),

        @Id
        @Column(name="cd_email", unique=true, length = 120 )
        val email: String,

        @Column(name="nm_user")
        val name: String = "",

        @Column(name="cd_password")
        val password: String = "",

        @JsonProperty(value = "qt_balance_available", access = JsonProperty.Access.READ_ONLY)
        @Column(name="qt_balance_available")
        val balanceAvailable: Double = 0.0,

        @Column(name="cd_link_photo")
        val photo: String = "",

        @JsonProperty(value = "ic_auth", access = JsonProperty.Access.READ_ONLY)
        @Column(name="ic_auth")
        val auth: Boolean = false,
)