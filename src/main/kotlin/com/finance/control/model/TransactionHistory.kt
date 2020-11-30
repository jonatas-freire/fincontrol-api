package com.finance.control.model


import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "tb_transaction")
data class TransactionHistory(

        @Id
        @GeneratedValue
        @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
        @Column(name = "cd_transaction", unique = true, nullable = false)
        val id: Long = 0L,

        @Column(name ="vl_transaction")
        val amount: Double,

        @Column(name ="nm_transaction")
         val name: String,

        @Column(name="ds_transaction")
         val description: String,

        @Column(name ="ic_monthly")
         val monthly: Boolean = false,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "dt_transaction", nullable = false)
        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
         val date: Calendar,

        @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
        @JoinColumn(name = "cd_email")
        val user: User,

        @OneToOne()
        @JoinColumn(name = "cd_category")
         val category: CategoryTransaction,

        @JsonProperty(value = "cd_type_transaction")
        @Enumerated(EnumType.STRING)
        @Column( name="cd_type_transaction")
         val type: TransactionType


)