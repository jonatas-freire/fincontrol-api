package com.finance.control.model

import javax.persistence.*

@Entity
@Table(name="tb_receipt_category")
class CategoryReceipt(
        @Id
        @GeneratedValue
        @Column(name="cd_receipt_category", unique=true)
        override val id: Long = 0L,

        @Column(name="nm_receipt_category", length = 40)
        override val name: String,

        @Column(name="nm_icon_receipt_category", length = 40)
        override val icon: String,

        ) : Category