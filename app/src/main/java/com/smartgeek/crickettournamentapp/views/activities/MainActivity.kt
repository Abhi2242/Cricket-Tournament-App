package com.smartgeek.crickettournamentapp.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smartgeek.crickettournamentapp.R
import com.smartgeek.crickettournamentapp.model.PlayerData
import com.smartgeek.crickettournamentapp.model.TeamData
import java.io.File
import java.io.FileWriter

class MainActivity : AppCompatActivity() {

    private var folderName: String = ""
    private lateinit var folder: File
    private lateinit var pdfTableDataFile: File
    private lateinit var existingData: MutableList<TeamData>
    private lateinit var addTeamDetails: TeamData
    private lateinit var spinnerStatus: Spinner
    private var spinnerStatusValue: Int = 0
    private lateinit var spinnerCategory: Spinner
    private var spinnerCategoryValue: Int = 0
    private lateinit var teamName: TextView
    private lateinit var teamOwnerName: TextView
    private lateinit var p1Name: TextView
    private lateinit var p2Name: TextView
    private lateinit var p3Name: TextView
    private lateinit var p4Name: TextView
    private lateinit var p5Name: TextView
    private lateinit var p6Name: TextView
    private lateinit var btnAddTeam: Button
    private lateinit var btnDisplayTeams: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkStoragePermission()
        initView()
    }

    private fun initView() {
        spinnerStatus = findViewById(R.id.spinnerStatus)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        teamName = findViewById(R.id.teamName)
        teamOwnerName = findViewById(R.id.teamOwnerName)
        p1Name = findViewById(R.id.p1Name)
        p2Name = findViewById(R.id.p2Name)
        p3Name = findViewById(R.id.p3Name)
        p4Name = findViewById(R.id.p4Name)
        p5Name = findViewById(R.id.p5Name)
        p6Name = findViewById(R.id.p6Name)
        btnAddTeam = findViewById(R.id.btnAddTeam)
        btnDisplayTeams = findViewById(R.id.btnDisplayTeams)

        if (checkStoragePermission()){
            getData()
        }
        else{
            Toast.makeText(this, "Storage permission is required", Toast.LENGTH_LONG).show()
            checkStoragePermission()
        }

        btnAddTeam.setOnClickListener {
            if (checkStoragePermission()) {
                submitTeam()
            } else {
                checkStoragePermission()
            }
        }

        btnDisplayTeams.setOnClickListener {
            if (existingData.isNotEmpty()){
                startActivity(Intent(this, DisplayTeamsInTable::class.java))
            }
            else{
                Toast.makeText(this, "Add a Team First!", Toast.LENGTH_LONG).show()
            }
        }

        setSpinnerView()
    }

    private fun submitTeam() {
        if (checkForEmpty()) {
            val playerList = listOf(
                PlayerData(p1Name.text.toString()),
                PlayerData(p2Name.text.toString()),
                PlayerData(p3Name.text.toString()),
                PlayerData(p4Name.text.toString()),
                PlayerData(p5Name.text.toString()),
                PlayerData(p6Name.text.toString())
            )
            addTeamDetails = TeamData(
                teamName.text.toString(),
                teamOwnerName.text.toString(),
                spinnerStatusValue,
                spinnerCategoryValue,
                playerList
            )

            addTeam()
        }
    }

    private fun addTeam() {
        val teamName = addTeamDetails.tName
        val playerNames = addTeamDetails.tPlayersData.map { it.pName } // Extracting player names from TeamData

        if (existingData.isNotEmpty()){
            if (existingData.any { it.tName != teamName }) {
                if (playerNames.any { playerName -> existingData.any { teamData ->
                        teamData.tPlayersData.any { playerData -> playerData.pName == playerName }
                    } })
                {
                    Toast.makeText(this, "Player Already Exists", Toast.LENGTH_LONG).show()
                } else {
                    addDataToTable()
                }
            } else {
                Toast.makeText(this, "Team Name Already Exist", Toast.LENGTH_LONG).show()
            }
        }
        else{
            addDataToTable()
        }
    }

    private fun addDataToTable() {
        existingData.add(addTeamDetails)

        val json = Gson().toJson(existingData)

        FileWriter(pdfTableDataFile).use { writer ->
            writer.write(json)
        }
        Log.v("Pdf Table", json.toString())
        existingData.clear()
        existingData = readDataFromFile(pdfTableDataFile)
        Toast.makeText(this, "Team added to table", Toast.LENGTH_LONG).show()

        startActivity(Intent(this, DisplayTeamsInTable::class.java))
    }

    private fun getData() {
        folderName = this.getString(R.string.app_name)
        folder = File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), folderName)
        pdfTableDataFile = File(folder, "$folderName.json")
        // if folder is empty then create
        folder.mkdirs()
        existingData = readDataFromFile(pdfTableDataFile)
    }

    private fun readDataFromFile(file: File): MutableList<TeamData> {
        if (!file.exists()) {
            return mutableListOf()
        }
        val json = file.readText()
        val type =
            TypeToken.getParameterized(MutableList::class.java, TeamData::class.java).type
        return Gson().fromJson(json, type)
    }

    private fun checkForEmpty(): Boolean {
        return if (teamName.text.isNotEmpty()) {
            if (teamOwnerName.text.isNotEmpty()) {
                if (spinnerStatusValue != 0) {
                    if (spinnerCategoryValue != 0) {
                        if (p1Name.text.isNotEmpty() && p2Name.text.isNotEmpty() &&
                            p3Name.text.isNotEmpty() && p4Name.text.isNotEmpty() &&
                            p5Name.text.isNotEmpty() && p6Name.text.isNotEmpty()
                        ) {
                            true
                        } else {
                            Toast.makeText(this@MainActivity, "Missing Player", Toast.LENGTH_SHORT)
                                .show()
                            false
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Select Category", Toast.LENGTH_SHORT)
                            .show()
                        false
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Select Entry Status", Toast.LENGTH_SHORT)
                        .show()
                    false
                }
            } else {
                Toast.makeText(this@MainActivity, "Missing Owner Name", Toast.LENGTH_SHORT).show()
                false
            }
        } else {
            Toast.makeText(this@MainActivity, "Missing Team Name", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun setSpinnerView() {
        val statusList = resources.getStringArray(R.array.entry_status_list)
        val statusAdapter = ArrayAdapter(this, R.layout.spinner_item, statusList)
        spinnerStatus.adapter = statusAdapter
        spinnerStatus.setSelection(spinnerStatusValue)

        spinnerStatus.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {
                spinnerStatusValue = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val categoryList = resources.getStringArray(R.array.entry_category_list)
        val categoryAdapter = ArrayAdapter(this, R.layout.spinner_item, categoryList)
        spinnerCategory.adapter = categoryAdapter
        spinnerCategory.setSelection(spinnerCategoryValue)

        spinnerCategory.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {
                spinnerCategoryValue = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Permission not granted, request it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
                    ),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
                    ),
                    STORAGE_PERMISSION_CODE
                )
            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted, continue with your operations
            } else {
                checkStoragePermission()
            }
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 100
    }
}