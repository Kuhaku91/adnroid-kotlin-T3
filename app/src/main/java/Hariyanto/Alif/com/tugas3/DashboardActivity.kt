package Hariyanto.Alif.com.tugas3

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var builder:AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        builder = AlertDialog.Builder(this)

        btnProfil.setOnClickListener(this)
        btnDisplayData.setOnClickListener(this)
        btnLogOut.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnProfil -> {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("User Profile").setMessage(
                        "Nama : ${currentUser!!.displayName}\n" +
                                "Email : ${currentUser!!.email}"
                    ).setNeutralButton("OK",null)
                    builder.show()
                }
            }
            R.id.btnDisplayData ->{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                }
            R.id.btnLogOut ->{
                builder.setTitle("Konfirmasi").setMessage("Yakin Ingin Log Out?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("YA", signOut)
                    .setNegativeButton("Tidak", null)
                builder.show()
            }
        }
    }

    val signOut= DialogInterface.OnClickListener{ dialog, which ->
        auth.signOut()
        Intent(this, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }
}