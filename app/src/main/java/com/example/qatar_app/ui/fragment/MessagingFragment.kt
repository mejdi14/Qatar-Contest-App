package com.example.qatar_app.ui.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qatar_app.MainActivity
import com.example.qatar_app.R
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.snj.letschat.adapter.ChatFirebaseRecycleAdapter
import com.snj.letschat.model.File
import com.snj.letschat.model.Message
import com.snj.letschat.model.User
import com.snj.letschat.utils.SharedPrefConfigUtils
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText
import java.io.ByteArrayOutputStream
import java.util.*


class MessagingFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    private var storage = FirebaseStorage.getInstance()


    private var userModel: User? = null

    private var msgListRecyclerView: RecyclerView? = null
    private var mLinearLayoutManager: LinearLayoutManager? = null
    private var btSendMessage: ImageView? = null
    private var btEmoji: ImageView? = null
    private var edMessage: EmojiconEditText? = null
    private var contentRoot: View? = null
    private var emojIcon: EmojIconActions? = null

    private var filePathImageCamera: java.io.File = java.io.File("demo")

    companion object {

        const val CHAT_REFERENCE = "message"
        const val STORAGE_PATH = "gs://qatar-412e7.appspot.com"
        const val STORAGE_FOLDER = "images"


        private const val IMAGE_GALLERY_REQUEST = 1
        private const val IMAGE_CAMERA_REQUEST = 2
        private const val PLACE_PICKER_REQUEST = 3

        val TAG =  "message activity"

        // Storage Permissions
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for activity fragment
        var view = inflater.inflate(R.layout.fragment_messaging, container, false)

        mGoogleApiClient = GoogleApiClient.Builder((activity as MainActivity))
            .enableAutoManage((activity as MainActivity), this)
            .addApi(Auth.GOOGLE_SIGN_IN_API)
            .build()
        mGoogleApiClient?.connect()
        contentRoot = view.findViewById<View>(R.id.contentRoot)
        edMessage = view.findViewById<View>(R.id.editTextMessage) as EmojiconEditText?
        btSendMessage = view.findViewById<View>(R.id.buttonMessage) as ImageView
        btSendMessage!!.setOnClickListener(this)
        btEmoji = view.findViewById<View>(R.id.buttonEmoji) as ImageView
        emojIcon = EmojIconActions(activity, contentRoot, edMessage, btEmoji)
        emojIcon!!.ShowEmojIcon()
        msgListRecyclerView = view.findViewById<View>(R.id.messageRecyclerView) as RecyclerView?
        mLinearLayoutManager = LinearLayoutManager(activity)
        mLinearLayoutManager!!.stackFromEnd = true
        bindViews()
        checkUserLog()
        return view
    }

    override fun onResume() {
        super.onResume()
        if (!mGoogleApiClient?.isConnected!!) {
            mGoogleApiClient?.connect()
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val storageRef = storage?.getReferenceFromUrl(STORAGE_PATH)?.child(STORAGE_FOLDER)
        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val selectedImageUri = data!!.data
                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, selectedImageUri)
                } else {
                    //URI IS NULL
                }
            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera!!.exists()) {
                    val imageCameraRef = storageRef?.child(filePathImageCamera!!.name + "_camera")
                    sendFileFirebase(imageCameraRef, filePathImageCamera)
                } else {
                    //IS NULL
                }
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(activity, data)
                if (place != null) {
                    val latLng = place?.latLng
                    val mapModel = com.snj.letschat.model.Map("${latLng.latitude}", "${latLng.longitude}")
                    val chatModel = Message(user = userModel, timeStamp = "${Calendar.getInstance().time}", map = mapModel, file = null, id = null, messgage = null)
                    mFirebaseDatabaseReference!!.child(CHAT_REFERENCE).push().setValue(chatModel)
                } else {
                    //PLACE IS NULL
                }
            }
        }

    }

/*    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.sendPhoto -> verifyStoragePermissions()
            R.id.sendPhotoGallery -> photoGalleryIntent()
            R.id.sendLocation -> locationPlacesIntent()
            R.id.sign_out -> signOut()
        }//                photoCameraIntent();

        return super.onOptionsItemSelected(item)
    }*/

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:$connectionResult")
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonMessage -> sendMessageFirebase()
        }
    }

    private fun sendFileFirebase(storageReference: StorageReference?, file: Uri) {
        if (storageReference != null) {
            val name = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
            val imageGalleryRef = storageReference?.child(name + "_gallery")

            val uploadTask = imageGalleryRef.putFile(file)
            uploadTask.addOnFailureListener({ e ->
                Log.e("Upload fail", "onFailure sendFileFirebase " + e.message)
            }).addOnCompleteListener(
                object : OnCompleteListener<UploadTask.TaskSnapshot> {
                    override fun onComplete(p0: Task<UploadTask.TaskSnapshot>) {
                        imageGalleryRef.downloadUrl.addOnSuccessListener { e ->
                            run {
                                Log.e("Upload..", "onSuccess sendFileFirebase")
                                val fileModel = File("img", e.toString(), name, "")
                                val chatModel = Message(user = userModel, timeStamp = "${Calendar.getInstance().time}", map = null, file = fileModel, id = null, messgage = null)
                                mFirebaseDatabaseReference!!.child(CHAT_REFERENCE).push().setValue(chatModel)
                                Log.d("File..", fileModel.toString())
                            }


                        }

                    }
                }

            )

        } else {
            //IS NULL
        }

    }

    private fun sendFileFirebase(storageReference: StorageReference?, file: java.io.File) {
        if (storageReference != null) {
            val photoURI = FileProvider.getUriForFile(activity as MainActivity,
                "ibas.provider",
                file)
            val name = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
            val imageGalleryRef = storageReference?.child(name)
            var bmp: Bitmap = MediaStore.Images.Media.getBitmap((activity as MainActivity).getContentResolver(), photoURI)
            var baos: ByteArrayOutputStream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos)
            var data = baos.toByteArray()
            Log.e("Upload..", "Asyntask...")
            val uploadTask = imageGalleryRef.putBytes(data)
            uploadTask.addOnFailureListener({ e ->
                Log.e("Upload fail", "onFailure sendFileFirebase " + e.message)
            }).addOnCompleteListener(
                object : OnCompleteListener<UploadTask.TaskSnapshot> {
                    override fun onComplete(p0: Task<UploadTask.TaskSnapshot>) {
                        imageGalleryRef.downloadUrl.addOnSuccessListener { e ->
                            run {
                                Log.e("Upload..", "onSuccess sendFileFirebase")
                                val fileModel = File("img", e.toString(), name, "")
                                val chatModel = Message(user = userModel, timeStamp = "${Calendar.getInstance().time}", map = null, file = fileModel, id = null, messgage = null)
                                mFirebaseDatabaseReference!!.child(CHAT_REFERENCE).push().setValue(chatModel)
                                Log.d("File..", fileModel.toString())
                            }


                        }

                    }
                }

            )
        } else {
            Log.d(TAG,"Storage ref is null!")
        }

    }


    private fun locationPlacesIntent() {
        try {
            val builder = PlacePicker.IntentBuilder()
            startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }

    }

    private fun photoCameraIntent() {
        val nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
        filePathImageCamera = java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto + "camera.jpg")
        val it = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoURI = FileProvider.getUriForFile(activity as MainActivity,
            "ibas.provider",
            filePathImageCamera!!)
        it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(it, IMAGE_CAMERA_REQUEST)
    }

    private fun photoGalleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST)
    }


    private fun sendMessageFirebase() {
        Log.d(TAG, "sendMessageFirebase: in here")
        val model = Message(user = userModel, messgage = edMessage!!.text.toString(), timeStamp = "${Calendar.getInstance().time}", file = null, id = null, map = null)
        Log.d(TAG, "sendMessageFirebase: model : ${model}")
        mFirebaseDatabaseReference!!.child(CHAT_REFERENCE).push().setValue(model)
        Log.d(TAG, "sendMessageFirebase: reference : ${CHAT_REFERENCE}")
        edMessage!!.setText("")
    }


    private fun readMessageFirebase() {

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        mFirebaseDatabaseReference?.keepSynced(true)
        var options: FirebaseRecyclerOptions<Message> =
            FirebaseRecyclerOptions.Builder<Message>()
                .setIndexedQuery(mFirebaseDatabaseReference!!.child(CHAT_REFERENCE).orderByKey(), mFirebaseDatabaseReference!!.child(CHAT_REFERENCE), Message::class.java)
//                        .setQuery(teamQuery, Message::class.java)
                .setLifecycleOwner(activity)
                .build()
        val firebaseAdapter = ChatFirebaseRecycleAdapter(activity as MainActivity, options, userModel!!.email!!, userModel!!.name!!)
        firebaseAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val friendlyMessageCount = firebaseAdapter.itemCount
                val lastVisiblePosition = mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1 || positionStart >= friendlyMessageCount - 1 && lastVisiblePosition == positionStart - 1) {
                    msgListRecyclerView!!.scrollToPosition(positionStart)
                }
            }
        })

        msgListRecyclerView!!.layoutManager = mLinearLayoutManager
        msgListRecyclerView!!.adapter = firebaseAdapter

        firebaseAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val friendlyMessageCount = firebaseAdapter.itemCount
                val lastVisiblePosition = mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition()
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                msgListRecyclerView?.post(Runnable { msgListRecyclerView?.smoothScrollToPosition(firebaseAdapter.itemCount - 1) })

                if (lastVisiblePosition == -1 || positionStart >= friendlyMessageCount - 1 && lastVisiblePosition == positionStart - 1) {

                }
            }
        })



    }


    private fun checkUserLog() {
   /*     mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth!!.currentUser
        if (mFirebaseUser == null) {
           *//* signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            finish()*//*
        } else {*/
            userModel = User("123456", "mejdihaf", "url", "mejdihaf@gmail.com")
            readMessageFirebase()
     //   }
    }

    private fun bindViews() {

    }

  /*  private fun signOut() {
        SharedPrefConfigUtils.clear(activity )
        startActivity(Intent(activity, LoginActivity::class.java))
        finish()
    }


    private fun verifyStoragePermissions() {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity@MainActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            //No permission
            ActivityCompat.requestPermissions(
                activity@MainActivity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        } else {
            // permission given
            photoCameraIntent()
        }
    }*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE ->
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    photoCameraIntent()
                }
        }
    }


}