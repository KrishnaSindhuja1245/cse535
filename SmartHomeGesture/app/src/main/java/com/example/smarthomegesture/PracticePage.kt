package com.example.smarthomegesture

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class PracticePage : AppCompatActivity() {
    lateinit var videoview: VideoView
    lateinit var recordButton: Button
    lateinit var uploadButton: Button
    private var ourRequest: Int = 23 //any number
    private val CAMERA_REQUEST_CODE = 200
    private val STORAGE_REQUEST_CODE = 100
    private val client = OkHttpClient()
    private var videoFullPath: String? = null
    lateinit var gesture: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice)

        gesture = intent.getStringExtra("gesture").toString()
        print("gesture = $gesture")
        videoview = findViewById(R.id.videoView_capture)
        recordButton = findViewById(R.id.record_button)
        uploadButton = findViewById(R.id.upload_button)

        val mediaCollection = MediaController(this)
        mediaCollection.setAnchorView(videoview)
        videoview.setMediaController(mediaCollection)
        if (allPermissionsGranted()) {
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        recordButton.setOnClickListener {
            //checkPermission(Manifest.permission.CAMERA,"Camera",CAMERA_REQUEST_CODE)
            startvideo()
        }
        uploadButton.setOnClickListener {
            //checkPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE,"Storage",STORAGE_REQUEST_CODE)
            uploadFile()
        }
    }

    private fun uploadFile() {
        val sourceFile: File
        var uploadedFileName: String = gesture + "_PRACTICE_";

        if(videoFullPath != null){
        sourceFile = File(videoFullPath)
        if (sourceFile != null) {
            Thread {
                val mimeType = getMimeType(sourceFile);
                if (mimeType == null) {
                    Log.e("file error", "Not able to get mime type")
                    return@Thread
                }
                try {
                    val requestBody: RequestBody =
                        MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart(
                                "file",
                                uploadedFileName,
                                sourceFile.asRequestBody(mimeType.toMediaTypeOrNull())
                            )
                            .build()

                    val request: Request =
                        Request.Builder().url("http://10.0.2.2:5000/upload-video")
                            .post(requestBody)
                            .build()

                    val response: Response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        //Log.d("File upload","success, path: $serverUploadDirectoryPath$fileName")
                        this.runOnUiThread {
                            Toast.makeText(
                                this,
                                "File uploaded successfully ",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            //at $serverUploadDirectoryPath$fileName
                        }
                    } else {
                        Log.e("File upload", "failed")
                        this.runOnUiThread {
                            Toast.makeText(this, "File Upload Failed", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.e("File upload", "failed")
                    this.runOnUiThread {
                        Toast.makeText(this, "File Upload Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
            goBacktoMain()
        }
        }
     else
    {
        this.runOnUiThread {
            Toast.makeText(this, "Select a file and try again", Toast.LENGTH_LONG).show()
        }
    }
}
    private fun goBacktoMain(){
        val goBacktoMain = Intent(applicationContext, MainActivity::class.java)
        startActivity(goBacktoMain)
    }

    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
    fun getRealPathFromURI(contentUri: Uri?): String? {
        var path: String? = null
        val proj = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = contentResolver.query(contentUri!!, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            path = cursor.getString(column_index)
        }
        cursor.close()
        return path
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = uri?.let { context.getContentResolver().query(it, projection, selection, selectionArgs,null) }
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    private fun startvideo(){
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        if(intent.resolveActivity(packageManager) != null){
            startActivityForResult(intent,ourRequest)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            if (intent?.data != null) {
                videoFullPath = getRealPathFromURI( intent.data!!) // Use this video path according to your logic
                // if you want to play video just after recording it to check is it working (optional)
                if (videoFullPath != null) {
                    videoview.setVideoURI( intent.data!!)
                    videoview.start()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


}