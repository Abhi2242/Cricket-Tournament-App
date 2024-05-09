package com.smartgeek.crickettournamentapp.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smartgeek.crickettournamentapp.R
import com.smartgeek.crickettournamentapp.model.TeamData
import java.io.File

class StartPageActivity : AppCompatActivity() {

    private var folderName: String = ""
    private lateinit var folder: File
    private lateinit var pdfTableDataFile: File
    private lateinit var existingData: MutableList<TeamData>
    private lateinit var addTeam: Button
    private lateinit var displayTable: Button
    private lateinit var displayMatches: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page)

        checkStoragePermission()
        initView()
    }

    private fun initView() {
        addTeam = findViewById(R.id.add_team)
        displayTable = findViewById(R.id.display_table)
        displayMatches = findViewById(R.id.display_match)

        if (checkStoragePermission()){
            getData()
        }
        else{
            Toast.makeText(this, "Storage permission is required", Toast.LENGTH_LONG).show()
            checkStoragePermission()
        }

        addTeam.setOnClickListener {
            startActivity(Intent(this, AddTeamsActivity::class.java))
        }

        displayTable.setOnClickListener {
            startActivity(Intent(this, DisplayTeamsInTable::class.java))
        }

        displayMatches.setOnClickListener {
            Toast.makeText(this, "Display Matches", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getData() {
        folderName = this.getString(R.string.app_name)
        folder = File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), folderName)
        pdfTableDataFile = File(folder, "$folderName.json")
        // if folder is empty then create
        folder.mkdirs()
        existingData = readDataFromFile(pdfTableDataFile)

        if (existingData.isNotEmpty()){
            displayTable.visibility = View.VISIBLE
            displayMatches.visibility = View.VISIBLE
        }
        else{
            displayTable.visibility = View.GONE
            displayMatches.visibility = View.GONE
        }
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