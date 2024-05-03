package com.smartgeek.crickettournamentapp.views.adapter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartgeek.crickettournamentapp.R
import com.smartgeek.crickettournamentapp.model.TeamData
import com.smartgeek.paymenttracker.R
import com.smartgeek.paymenttracker.contract.TableOperationContract
import com.smartgeek.paymenttracker.model.PlayerDetails
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

class TableViewAdapter(
    private val context: Context,
    private val playerList: MutableList<TeamData>,
    private val clickListener: TableOperationContract
) : RecyclerView.Adapter<TableViewAdapter.ViewHolder>() {

    // ViewHolder for rows
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.team_list_table_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mPosition = position
        val item = playerList[mPosition]

        val mId = mPosition + 1
        holder.txtId.text = mId.toString()

        holder.txtUserName.text = item.userName

        holder.txtPNo.text = item.number

        val inputDateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val displayDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

        val startDate = inputDateFormat.parse(item.startDate)
        holder.txtStartDate.text = startDate?.let { displayDateFormat.format(it) }

        val endDate = inputDateFormat.parse(item.endDate)
        holder.txtEndDate.text = endDate?.let { displayDateFormat.format(it) }

        val daysRemaining = getDayCount(item.endDate)
        holder.txtTotalDays.text = daysRemaining.toString()

        Glide.with(holder.itemView.context)
            .load(R.drawable.icon_delete)
            .into(holder.imgDelete)

        val taskStatus = context.resources.getStringArray(R.array.payment_method_list)
        val mAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, taskStatus)
        holder.mySpinner.adapter = mAdapter

        // Set default status
        val defaultValue = item.paymentType
        holder.mySpinner.setSelection(defaultValue)

        holder.mySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (defaultValue != position && position != 0) {
                    item.paymentType = position
                    clickListener.updateStatus(mPosition, position)
                }

//                if (taskStatus[position] == "Pending"){
//                    holder.mySpinner.setBackgroundResource(R.color.spinnerRed)
//                }
//                else if (taskStatus[position] == "In Process"){
//                    holder.mySpinner.setBackgroundResource(R.color.spinnerBlue)
//                }
//                else if (taskStatus[position] == "Done"){
//                    holder.mySpinner.setBackgroundResource(R.color.spinnerGreen)
//                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                item.paymentType = defaultValue
            }
        }

        holder.imgDelete.setOnClickListener {
            if (playerList.size > 1) {
                clickListener.deleteRow(mPosition)
                for (i in 0 until (playerList.size)) {
                    val id = i + 1
                    holder.txtId.text = id.toString()
                }
            } else {
                clickListener.tableEmpty()
                clickListener.deleteRow(mPosition)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtId: AppCompatTextView = itemView.findViewById(R.id.txt_item_id)
        val txtTeamName: AppCompatTextView = itemView.findViewById(R.id.txt_item_team_name)
        val txtOwnerName: AppCompatTextView = itemView.findViewById(R.id.txt_item_team_owner)
        val spinnerStatus: AppCompatTextView = itemView.findViewById(R.id.txt_item_spinner_status)
        val spinnerCategory: AppCompatSpinner = itemView.findViewById(R.id.txt_item_spinner_category)
        val txtPlayerName: AppCompatTextView = itemView.findViewById(R.id.txt_item_player_name)
        val imgDelete: AppCompatImageButton = itemView.findViewById(R.id.txt_item_delete)
    }

}

