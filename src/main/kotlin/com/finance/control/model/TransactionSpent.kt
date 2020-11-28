package com.finance.control.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "tb_spent")
class TransactionSpent(
        @Id
        @GeneratedValue
        @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
        @Column(name = "cd_spent", unique = true, nullable = false)
        override val id: Long = 0L,

        @Column(name = "nm_spent", length = 70, nullable = false)
        override val name: String = "",

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "dt_spent", nullable = false)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        override val date: Calendar,

        @Column(name = "ic_monthly_receipt")
        override val monthly: Boolean = false,

        @Column(name= "qt_installment")
        val installment: Int = 0,


        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cd_spent_category", nullable = false)
        override val category: CategoryReceipt,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cd_email", nullable = false)
        override val user: User
) : Transaction