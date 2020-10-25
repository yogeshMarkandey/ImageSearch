package com.example.imagesearch.activities

import android.Manifest
import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.imagesearch.R
import com.example.imagesearch.datas.PhotoInfo
import kotlinx.android.synthetic.main.activity_full_screen.*
import kotlinx.android.synthetic.main.dialog_box_details_full_act.view.*
import java.io.File

// activity for displaying image
class FullScreenActivity : AppCompatActivity() {

   private var url = ""
    private var data : PhotoInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)

        // setting up toolbar
        val toolbar = toolbar_full_screen_act
        setSupportActionBar(toolbar)
        supportActionBar?.title = " "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // getting Intent data
         url = intent.getStringExtra("imageInfo")?.trim().toString()
        data = intent.getParcelableExtra<PhotoInfo>("imageData")

        // Checking if data is not null and setting toolbar values
        if(data != null){
            supportActionBar?.title = data?.title
            supportActionBar?.subtitle = url
        }else{
            supportActionBar?.title = "Title Not Available"
            supportActionBar?.subtitle = url
        }


        // Displaying image
        val option = RequestOptions.placeholderOf(R.drawable.background_image_view)
            .error(R.drawable.background_image_view)
        Glide.with(this)
            .load(url)
            .apply(option)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(image_view_full_screen_act)


        // when download button is clicked
        button_download_full_act.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // ask for permissions
                askForPermissions()
            } else {
                // if write permission was given
                // download image
                downloadImage(url)
            }
        }


        // when share button is clicked
        button_share_full_act.setOnClickListener{
            // share app link
            shareAppLink(url)
        }

        // when Information button is clicked
        button_retry_full_act.setOnClickListener{
            //Toast.makeText(this, "Do something", Toast.LENGTH_SHORT).show()

            // Building a alert Dialog box
            val builder = AlertDialog.Builder(this)
            val myView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_box_details_full_act, null)
            myView.text_view_url_dialog_full_act.text = url
            myView.text_view_title_dialog_full_act.text= if (data != null){
                data?.title
            } else
                "Title Not Available"
            builder.setView(myView)
            val dialog = builder.create()
            dialog.show()


        }
    }

    // for sharing image
     private fun shareAppLink(url: String) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "App Link")
        val share_data = "Hey!\nCheckOut this awesome wallpaper. \n\n$url"
        intent.putExtra(Intent.EXTRA_TEXT, share_data)
        startActivity(Intent.createChooser(intent, "Share via"))
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()// close this activity and return to preview activity (if there is any)
            return true
        }
        return false
    }


    // for checking permissions
    @TargetApi(Build.VERSION_CODES.M)
    fun askForPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // if permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // how a alert dialog box
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save photos from the Web.")
                    .setPositiveButton("Allow") { dialog, id ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                        finish()
                    }
                    .setNegativeButton("Deny") { dialog, id -> dialog.cancel() }
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )

            }
        } else {
            // if permission has already been granted
            // then download
            downloadImage(url)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Download the Image
                    downloadImage(url)
                } else {

                }
                return
            }

            else -> {

            }
        }
    }


    private var message: String? = ""
    private var lastMessage = ""

    // for downloading image
    private fun downloadImage(url: String) {
        val directory = File(Environment.DIRECTORY_PICTURES)

        // check if directory exist
        if (!directory.exists()) {
            // make directory
            directory.mkdirs()
        }

        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    url.substring(url.lastIndexOf("/") + 1)
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) ==
                    DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                message = showToastMessage(url, directory, status)
                if (message != lastMessage) {
                    this.runOnUiThread {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                    lastMessage = message ?: ""
                }
                cursor.close()
            }
        }).start()
    }

    // to get status of download
    private fun showToastMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }
        return msg
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }
}