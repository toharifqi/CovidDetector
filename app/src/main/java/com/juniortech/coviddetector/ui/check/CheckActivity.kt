package com.juniortech.coviddetector.ui.check

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.juniortech.coviddetector.R
import com.juniortech.coviddetector.databinding.ActivityCheckBinding
import com.juniortech.coviddetector.ui.base.MainActivity
import dmax.dialog.SpotsDialog
import java.io.File
import java.io.IOException
import java.util.*

class CheckActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckBinding
    private var mRecorder: MediaRecorder? = null
    private lateinit var mFileName: String
    private var permissionToRecordAccepted = false
    private lateinit var mStorage: StorageReference

    private var permission: Array<String> = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    companion object {
        private const val LOG_TAG = "Record_log"
        private const val REQUEST_CODE = 111
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_CODE) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFileName = "${externalCacheDir?.absolutePath}/audiorecordtest.wav"
        mStorage = FirebaseStorage.getInstance().getReference()

        ActivityCompat.requestPermissions(this, permission, REQUEST_CODE)

        var isRecording = false
        binding.fab.setOnClickListener {
            isRecording = !isRecording
            setRecordState(isRecording)
        }
        binding.btnHome.setOnClickListener {
            startActivity(Intent(this@CheckActivity, MainActivity::class.java))
        }
        binding.btnRetry.setOnClickListener {
            reCheckUp()
        }
    }

    private fun setRecordState(isRecording: Boolean) {
        if (isRecording) {
            binding.fab.setImageResource(R.drawable.ic_baseline_pause_white)
            startRecording()
            binding.recordStatusText.text = getString(R.string.record_status_recording)
        } else {
            binding.fab.setImageResource(R.drawable.ic_baseline_play_arrow_white)
            stopRecording()
            uploadAudio()
            binding.progressBar.visibility = View.VISIBLE
            binding.recordStatusText.text = getString(R.string.record_status_analyzing)
        }
    }

    private fun startRecording() {
        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(AudioFormat.ENCODING_PCM_16BIT)
            setOutputFile(mFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioChannels(1)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(48000)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()

        }
    }

    private fun stopRecording() {
        mRecorder?.apply {
            stop()
            release()
        }
        mRecorder = null
    }

    private fun uploadAudio(){
        val fAuth = FirebaseAuth.getInstance()
        val fileId = Calendar.getInstance().time.toString().replace(" ", "");
        val uri = Uri.fromFile(File(mFileName))
        val filePath = fAuth.currentUser?.let {
            mStorage.child("UserCough").child(it.uid).child("$fileId.wav")
        }
        if (filePath != null) {
            filePath.putFile(uri).addOnSuccessListener {
//                if (getStatus()){
//                    populatePositive()
//                }else{
//                    populateNegative()
//                }
                populateNegative()
            }
        }

    }

    private fun populatePositive(){
        binding.progressBar.visibility = View.GONE
        binding.recordStatusText.visibility = View.GONE
        binding.guide2Text.visibility = View.GONE
        binding.fab.isEnabled = false
        binding.fab.visibility = View.GONE

        binding.imgResult.visibility = View.VISIBLE
        binding.textResult.visibility = View.VISIBLE
        binding.textResultDesc.visibility = View.VISIBLE
        binding.btnRetry.visibility = View.VISIBLE
        binding.btnHome.visibility = View.VISIBLE

        binding.imgResult.setImageResource(R.drawable.ic_baseline_warning)
        binding.textResult.text = getString(R.string.result_covid_positive)
        binding.textResult.setTextColor(ContextCompat.getColor(this@CheckActivity, R.color.death_color))
        binding.textResultDesc.text = getString(R.string.desc_covid_positive)
    }

    private fun populateNegative(){
        binding.progressBar.visibility = View.GONE
        binding.recordStatusText.visibility = View.GONE
        binding.guide2Text.visibility = View.GONE
        binding.fab.isEnabled = false
        binding.fab.visibility = View.GONE

        binding.imgResult.visibility = View.VISIBLE
        binding.textResult.visibility = View.VISIBLE
        binding.textResultDesc.visibility = View.VISIBLE
        binding.btnRetry.visibility = View.VISIBLE
        binding.btnHome.visibility = View.VISIBLE

        binding.imgResult.setImageResource(R.drawable.ic_baseline_check_circle)
        binding.textResult.text = getString(R.string.result_covid_negative)
        binding.textResult.setTextColor(ContextCompat.getColor(this@CheckActivity, R.color.recover_color))
        binding.textResultDesc.text = getString(R.string.desc_covid_negative)
    }

    private fun reCheckUp(){
        binding.recordStatusText.visibility = View.VISIBLE
        binding.recordStatusText.text = getString(R.string.record_status)
        binding.guide2Text.visibility = View.VISIBLE
        binding.fab.isEnabled = true
        binding.fab.visibility = View.VISIBLE

        binding.imgResult.visibility = View.GONE
        binding.textResult.visibility = View.GONE
        binding.textResultDesc.visibility = View.GONE
        binding.btnRetry.visibility = View.GONE
        binding.btnHome.visibility = View.GONE
    }

    fun getStatus(): Boolean{
        val state = Random()
        return state.nextBoolean()
    }
}