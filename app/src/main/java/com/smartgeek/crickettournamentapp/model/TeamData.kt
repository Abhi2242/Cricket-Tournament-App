package com.smartgeek.crickettournamentapp.model

data class TeamData(
    val tName: String,
    val tOwner: String,
    var tEntryStatus: Int = 0,
    val tCategory: Int = 0,
    val tPlayersData: List<PlayerData>
)
