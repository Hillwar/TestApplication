package com.hillwar.testapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class PairQA {
    @PrimaryKey
    var id: Long = 0
    var quation: String? = null
    var answer: String? = null
}