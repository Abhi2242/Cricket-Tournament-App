package com.smartgeek.crickettournamentapp.views.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartgeek.crickettournamentapp.R
import com.smartgeek.crickettournamentapp.contract.TableOperationContract
import com.smartgeek.crickettournamentapp.model.TeamData

class TableViewAdapter(
    private val context: Context,
    private val teamList: MutableList<TeamData>,
    private val clickListener: TableOperationContract
) : RecyclerView.Adapter<TableViewAdapter.ViewHolder>() {

    private var playerNames = ""
    // ViewHolder for rows
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.team_list_table_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return teamList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mPosition: Int = position
        val item = teamList[mPosition]

        val mId = mPosition + 1
        holder.txtId.text = mId.toString()

        holder.txtTeamName.text = item.tName

        holder.txtOwnerName.text = item.tOwner

        playerNames = ""
        for (p in 0 .. 5){
            playerNames += if (p >= 5){
                item.tPlayersData[p].pName
            } else{
                "${item.tPlayersData[p].pName}\n"
            }
        }

        Log.v("Player List", playerNames)

        holder.txtPlayerName.text = playerNames

        Glide.with(holder.itemView.context)
            .load(R.drawable.icon_delete)
            .into(holder.imgDelete)

        val statusList = context.resources.getStringArray(R.array.entry_status_list)
        val statusAdapter = ArrayAdapter(context, R.layout.spinner_item, statusList)
        holder.spinnerStatus.adapter = statusAdapter
        // Set default status
        val defaultValue = item.tEntryStatus
        holder.spinnerStatus.setSelection(defaultValue)

        holder.spinnerStatus.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                statusPosition: Int,
                id: Long
            ) {

                if (defaultValue != statusPosition && statusPosition != 0) {
                    item.tEntryStatus = statusPosition
                    clickListener.updateStatus(mPosition, statusPosition)
                }
                else{
                    item.tEntryStatus = defaultValue
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                item.tEntryStatus = defaultValue
            }
        }

        val categoryList = context.resources.getStringArray(R.array.entry_category_list)
        // Set default status
        holder.spinnerCategory.text = categoryList[item.tCategory]

        holder.imgDelete.setOnClickListener {
            if (teamList.size > 1) {
                clickListener.deleteRow(mPosition)
                for (i in 0 until (teamList.size)) {
                    val id = i + 1
                    holder.txtId.text = id.toString()
                }
            } else {
                clickListener.deleteRow(mPosition)
                clickListener.tableEmpty()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtId: AppCompatTextView = itemView.findViewById(R.id.txt_item_id)
        val txtTeamName: AppCompatTextView = itemView.findViewById(R.id.txt_item_team_name)
        val txtOwnerName: AppCompatTextView = itemView.findViewById(R.id.txt_item_team_owner)
        val spinnerStatus: AppCompatSpinner = itemView.findViewById(R.id.txt_item_spinner_status)
        val spinnerCategory: AppCompatTextView = itemView.findViewById(R.id.txt_item_spinner_category)
        val txtPlayerName: AppCompatTextView = itemView.findViewById(R.id.txt_item_player_name)
        val imgDelete: AppCompatImageButton = itemView.findViewById(R.id.txt_item_delete)
    }

}

