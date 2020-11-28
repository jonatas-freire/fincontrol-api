package com.finance.control.model


import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "tb_transaction_history")
class TransactionHistory(

        @Id
        @GeneratedValue
        @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
        @Column(name = "cd_transaction", unique = true, nullable = false)
        val id: Long = 0L,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "dt_transaction", nullable = false)
        val date: Calendar,

        @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
        @JoinColumn(name = "cd_email")
        val user: User,

        @OneToOne()
        @JoinColumn(name = "cd_receipt")
        val transactionReceipt: TransactionReceipt,

        @OneToOne()
        @JoinColumn(name = "cd_spent")
        val transactionSpent: TransactionSpent
)