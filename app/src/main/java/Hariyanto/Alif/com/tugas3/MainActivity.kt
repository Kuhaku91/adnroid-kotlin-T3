package Hariyanto.Alif.com.tugas3


import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.app.DownloadManager
import android.content.Context


class MainActivity : AppCompatActivity(), View.OnClickListener{
    override fun onClick(v: View?) {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        when(v?.id){
            R.id.btnUpWord -> {
                fileType = ".docx"
                intent.setType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
                startActivityForResult(intent, RC_OK)
            }
            R.id.btnUpVid -> {
                fileType = ".mp4"
                intent.setType("video/*")
                startActivityForResult(intent, RC_OK)
            }
            R.id.btnUpImg -> {
                fileType = ".jpg"
                intent.setType("image/*")
                startActivityForResult(intent, RC_OK)
            }
            R.id.btnUpPdf -> {
                fileType = ".pdf"
                intent.setType("application/pdf")
                startActivityForResult(intent, RC_OK)
            }
            R.id.btnUpload -> {
                if(uri !=null){
                    fileName = SimpleDateFormat("yyyMMddHHmmssSSS").format(Date())
                    val fileRef = storage.child(fileName+fileType)
                    fileRef.putFile(uri)
                        .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            return@Continuation fileRef.downloadUrl
                        })
                        .addOnCompleteListener { task ->
                            val hm = HashMap<String, Any>()
                            hm.put(F_NAME, fileName)
                            hm.put(F_TYPE, fileType)
                            hm.put(F_URL, task.result.toString())
                            db.document(fileName).set(hm).addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "File successfully uploaded",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }

            R.id.btnDelete->{
                val storageRef = storage.child(txSelectedFile.text.toString())
                storageRef.delete().addOnSuccessListener {
                    db.document(selectedText).delete()
                        .addOnSuccessListener {
                            Toast.makeText(this,"Data Succesfully deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Data unsuccesfully deleted: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "File unsuccesfully deleted: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    lateinit var storage : StorageReference
    lateinit var db : CollectionReference
    lateinit var alFile : ArrayList<HashMap<String, Any>>
    lateinit var adapter : CustomAdapter
    lateinit var uri : Uri
    lateinit var selectedText:String
    val F_NAME = "file_name"
    val F_TYPE = "file_type"
    val F_URL = "file_url"
    val RC_OK = 100
    var fileType = ""
    var fileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUpPdf.setOnClickListener(this)
        btnUpImg.setOnClickListener(this)
        btnUpVid.setOnClickListener(this)
        btnUpWord.setOnClickListener(this)
        btnUpload.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        lsV.setOnItemClickListener(itemClick)

        alFile = ArrayList()
        uri = Uri.EMPTY
    }
    override fun onStart() {
        super.onStart()
        storage = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance().collection("files")
        db.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
            if(firebaseFirestoreException!=null){
                Log.e("firestore :", firebaseFirestoreException.message.toString())
            }
            showData()
        }
    }
    fun showData(){
        db.get().addOnSuccessListener { result ->
            alFile.clear()
            for(doc in result){
                val hm = HashMap<String, Any>()
                hm.put(F_NAME, doc.get(F_NAME).toString())
                hm.put(F_TYPE, doc.get(F_TYPE).toString())
                hm.put(F_URL, doc.get(F_URL).toString())
                alFile.add(hm)
            }
            adapter = CustomAdapter(this, alFile)
            lsV.adapter = adapter
        }
    }

    fun downloadFile(
        context: Context,
        fileName: String,
        destinationDirectory: String?,
        url: String?)
            : Long {
        val downloadmanager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName)
        Toast.makeText(this,"Data Succesfully downloaded", Toast.LENGTH_SHORT).show()
        return downloadmanager.enqueue(request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((resultCode == Activity.RESULT_OK)&&(requestCode == RC_OK)){
            if (data !=null){
                uri = data.data!!
                txSelectedFile.setText(uri.toString())

            }
        }
    }
    val itemClick = AdapterView.OnItemClickListener{ parent, view, position, id->
        val hm:HashMap<String,Any> = alFile.get(position)
        txSelectedFile.text = hm[F_NAME].toString()+hm.get(F_TYPE).toString()
        selectedText = hm[F_NAME].toString()
        fileName = hm[F_NAME].toString()
        fileType = hm.get(F_TYPE).toString()
        Log.d("data-ff",selectedText)

    }

}

