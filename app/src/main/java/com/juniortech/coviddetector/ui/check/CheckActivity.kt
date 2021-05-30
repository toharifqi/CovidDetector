package com.juniortech.coviddetector.ui.check

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.juniortech.coviddetector.R
import com.juniortech.coviddetector.databinding.ActivityCheckBinding
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
    private lateinit var dialog: AlertDialog

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

        dialog = SpotsDialog.Builder().setContext(this@CheckActivity).build()
        dialog.setMessage("Uploading your cough sound...")

        ActivityCompat.requestPermissions(this, permission, REQUEST_CODE)

        var isRecording = false
        binding.fab.setOnClickListener {
            isRecording = !isRecording
            setRecordState(isRecording)
        }
    }

    private fun setRecordState(isRecording: Boolean) {
        if (isRecording) {
            binding.fab.setImageResource(R.drawable.ic_baseline_pause_white)
            startRecording()
            binding.recordLabelText.text = "Recording..."
        } else {
            binding.fab.setImageResource(R.drawable.ic_baseline_play_arrow_white)
            stopRecording()
            uploadAudio()
            binding.recordLabelText.text = "Recording stopped"
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
        dialog.show()
        if (filePath != null) {
            filePath.putFile(uri).addOnSuccessListener {
                dialog.dismiss()
                binding.recordLabelText.text = "Uploading completed..."
            }
        }
    }
}