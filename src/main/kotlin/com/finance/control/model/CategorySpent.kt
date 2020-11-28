package com.finance.control.model

import javax.persistence.*

@Entity
@Table(name="tb_spent_category")
class CategorySpent(
        @Id
        @GeneratedValue
        @Column(name="cd_spent_category", unique=true)
        override val id: Long = 0L,

        @Column(name="nm_spent_category", length = 40)
        override val name: String,

        @Column(name="nm_icon_spent_category", length = 40)
        override val icon: String,
) : Category