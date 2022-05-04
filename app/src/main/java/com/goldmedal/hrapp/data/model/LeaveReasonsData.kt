package com.goldmedal.hrapp.data.model

data class LeaveReasonsData(val LeaveReasonID: String? = null,
                            val Remarks: String? = null) {

    override fun toString(): String {
        return Remarks.toString()
    }
}