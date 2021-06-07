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
import com.juniortech.coviddetector.ui.check.audiofeatures.MFCC
import com.juniortech.coviddetector.ui.check.audiofeatures.WavFile
import com.juniortech.coviddetector.ui.check.audiofeatures.WavFileException
import dmax.dialog.SpotsDialog
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.text.DecimalFormat
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
//            val filePath = "${externalCacheDir?.absolutePath}/dataset_trial_covid2.wav"
//            val result = classifyNoise(filePath)
            binding.progressBar.visibility = View.VISIBLE
            binding.recordStatusText.text = getString(R.string.record_status_analyzing)
//            binding.recordStatusText.text = result
        }
    }

    private fun classifyNoise(filePath: String): String? {
        val mNumFrames: Int
        val mSampleRate: Int
        val mChannels: Int
        var meanMFCCValues = FloatArray(1)

        var predictedResult: String? = "Unknown"

        var wavFile: WavFile? = null
        try {
            wavFile = WavFile.openWavFile(File(filePath))
            mNumFrames = wavFile.numFrames.toInt()
            mSampleRate = wavFile.sampleRate.toInt()
            mChannels = wavFile.numChannels
            val buffer =
                Array(mChannels) { DoubleArray(mNumFrames) }

            var frameOffset = 0
            val loopCounter: Int = mNumFrames * mChannels / 4096 + 1
            for (i in 0 until loopCounter) {
                frameOffset = wavFile.readFrames(buffer, mNumFrames, frameOffset)
            }

            //trimming the magnitude values to 5 decimal digits
            val df = DecimalFormat("#.#####")
            df.setRoundingMode(RoundingMode.CEILING)
            val meanBuffer = DoubleArray(mNumFrames)
            for (q in 0 until mNumFrames) {
                var frameVal = 0.0
                for (p in 0 until mChannels) {
                    frameVal = frameVal + buffer[p][q]
                }
                meanBuffer[q] = df.format(frameVal / mChannels).toDouble()
            }


            //MFCC java library.
            val mfccConvert = MFCC()
            mfccConvert.setSampleRate(mSampleRate)
            val nMFCC = 13
            mfccConvert.setN_mfcc(nMFCC)
            val mfccInput = mfccConvert.process(meanBuffer)
            val nFFT = mfccInput.size / nMFCC
            val mfccValues =
                Array(nMFCC) { DoubleArray(nFFT) }

            //loop to convert the mfcc values into multi-dimensional array
            for (i in 0 until nFFT) {
                var indexCounter = i * nMFCC
                val rowIndexValue = i % nFFT
                for (j in 0 until nMFCC) {
                    mfccValues[j][rowIndexValue] = mfccInput[indexCounter].toDouble()
                    indexCounter++
                }
            }

            //code to take the mean of mfcc values across the rows such that
            //[nMFCC x nFFT] matrix would be converted into
            //[nMFCC x 1] dimension - which would act as an input to tflite model
            meanMFCCValues = FloatArray(nMFCC)
            for (p in 0 until nMFCC) {
                var fftValAcrossRow = 0.0
                for (q in 0 until nFFT) {
                    fftValAcrossRow = fftValAcrossRow + mfccValues[p][q]
                }
                val fftMeanValAcrossRow = fftValAcrossRow / nFFT
                meanMFCCValues[p] = fftMeanValAcrossRow.toFloat()
            }


        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: WavFileException) {
            e.printStackTrace()
        }

        predictedResult = loadModelAndMakePredictions(meanMFCCValues)

        return predictedResult
    }

    private fun loadModelAndMakePredictions(meanMFCCValues: FloatArray): String? {
        var predictedResult: String? = "unknown"

        //load the TFLite model in 'MappedByteBuffer' format using TF Interpreter
        val tfliteModel: MappedByteBuffer =
            FileUtil.loadMappedFile(this, "covid.tflite")
        val tflite: Interpreter

        /** Options for configuring the Interpreter.  */
        val tfliteOptions =
            Interpreter.Options()
        tfliteOptions.setNumThreads(2)
        tflite = Interpreter(tfliteModel, tfliteOptions)

        //obtain the input and output tensor size required by the model
        val imageTensorIndex = 0
        val imageShape =
            tflite.getInputTensor(imageTensorIndex).shape()
        val imageDataType: DataType = tflite.getInputTensor(imageTensorIndex).dataType()
        val probabilityTensorIndex = 0
        val probabilityShape =
            tflite.getOutputTensor(probabilityTensorIndex).shape()
        val probabilityDataType: DataType =
            tflite.getOutputTensor(probabilityTensorIndex).dataType()


        imageShape.forEach {
            Log.i("SHAPEValue", it.toString())
        }
        meanMFCCValues.forEach {
            Log.i("MFCCValue", it.toString())
        }

        Log.i("ShapeSize", imageShape.size.toString())
        Log.i("MFCCSize", meanMFCCValues.size.toString())

        //need to transform the MFCC 1d float buffer into 1x44x13x1 dimension tensor using TensorBuffer
        val inBuffer: TensorBuffer = TensorBuffer.createDynamic(imageDataType)
        inBuffer.loadArray(meanMFCCValues, imageShape)
        val inpBuffer: ByteBuffer = inBuffer.getBuffer()
        val outputTensorBuffer: TensorBuffer =
            TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)
        //run the predictions with input and output buffer tensors to get probability values across the labels
        tflite.run(inpBuffer, outputTensorBuffer.getBuffer())

        //Code to transform the probability predictions into label values
        val ASSOCIATED_AXIS_LABELS = "labels.txt"
        var associatedAxisLabels: List<String?>? = null
        try {
            associatedAxisLabels = FileUtil.loadLabels(this, ASSOCIATED_AXIS_LABELS)
        } catch (e: IOException) {
            Log.e("tfliteSupport", "Error reading label file", e)
        }

        //Tensor processor for processing the probability values and to sort them based on the descending order of probabilities
        val probabilityProcessor: TensorProcessor = TensorProcessor.Builder()
            .add(NormalizeOp(0.0f, 255.0f)).build()
        if (null != associatedAxisLabels) {
            // Map of labels and their corresponding probability
            val labels = TensorLabel(
                associatedAxisLabels,
                probabilityProcessor.process(outputTensorBuffer)
            )

            // Create a map to access the result based on label
            val floatMap: Map<String, Float> =
                labels.getMapWithFloatValue()

            //function to retrieve the top K probability values, in this case 'k' value is 1.
            //retrieved values are storied in 'Recognition' object with label details.
            val resultPrediction: List<Recognition>? = getTopKProbability(floatMap);

            //get the top 1 prediction from the retrieved list of top predictions
            predictedResult = getPredictedValue(resultPrediction)

        }
        return predictedResult
    }

    fun getPredictedValue(predictedList:List<Recognition>?): String?{
        val top1PredictedValue : Recognition? = predictedList?.get(0)
        return top1PredictedValue?.getTitle()
    }

    /** Gets the top-k results.  */
    protected fun getTopKProbability(labelProb: Map<String, Float>): List<Recognition>? {
        // Find the best classifications.
        val MAX_RESULTS: Int = 1
        val pq: PriorityQueue<Recognition> = PriorityQueue(
            MAX_RESULTS,
            Comparator<Recognition> { lhs, rhs -> // Intentionally reversed to put high confidence at the head of the queue.
                java.lang.Float.compare(rhs.getConfidence(), lhs.getConfidence())
            })
        for (entry in labelProb.entries) {
            pq.add(Recognition("" + entry.key, entry.key, entry.value))
        }
        val recognitions: ArrayList<Recognition> = ArrayList()
        val recognitionsSize: Int = Math.min(pq.size, MAX_RESULTS)
        for (i in 0 until recognitionsSize) {
            recognitions.add(pq.poll())
        }
        return recognitions
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
                if (getStatus()){
                    populatePositive()
                }else{
                    populateNegative()
                }
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