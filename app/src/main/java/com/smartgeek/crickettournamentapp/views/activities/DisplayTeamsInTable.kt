package com.smartgeek.crickettournamentapp.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smartgeek.crickettournamentapp.R
import com.smartgeek.crickettournamentapp.model.TeamData
import com.smartgeek.crickettournamentapp.views.adapter.TableViewAdapter
import java.io.File

class DisplayTeamsInTable : AppCompatActivity() {

    private var folderName: String = ""
    private lateinit var folder: File
    private lateinit var pdfTableDataFile: File
    private lateinit var existingData: MutableList<TeamData>
    private lateinit var tableRCV: RecyclerView
    private var mAdapter: TableViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_teams_in_table)

        initView()
        checkStoragePermission()
    }

    private fun initView() {
        tableRCV = findViewById(R.id.rcv_table)
        folderName = this.getString(R.string.app_name)
        folder = File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), folderName)
        pdfTableDataFile = File(folder, "$folderName.json")
        // if folder is empty then create
        folder.mkdirs()
        existingData = readDataFromFile(pdfTableDataFile)

        initTableRCV()
    }

    private fun initTableRCV() {
        tableRCV.layoutManager = LinearLayoutManager(this)
        mAdapter = TableViewAdapter(this, existingData, this)
        tableRCV.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
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