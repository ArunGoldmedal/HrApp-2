package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.db.entities.Quote

data class QuotesResponse (
    val isSuccessful: Boolean,
    val quotes: List<Quote>
)