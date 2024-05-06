package com.smartgeek.crickettournamentapp.contract

interface TableOperationContract {

    fun deleteRow(position: Int)

    fun tableEmpty()

    fun updateStatus(position: Int, pStatus: Int)

    fun updateDate(position: Int, date: String)
}