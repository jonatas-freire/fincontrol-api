package com.finance.control.model

import java.util.*
import javax.persistence.*

@Entity
@Table(name="tb_category_transaction")
class CategoryTransaction (

        @Id
        @GeneratedValue
        @Column(name="cd_category", unique=true)
        val id: Int,

        @Column(name="nm_category", length = 40)
        val name: String,

        @Column(name="nm_icon_category", length = 40)
        val icon: String,
        )