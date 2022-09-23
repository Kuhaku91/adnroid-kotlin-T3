package Hariyanto.Alif.com.tugas3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnRegister ->{
                val email :String = etRegEmail.text.toString().trim()
                val password :String = etRegPassword.text.toString().trim()
                val username :String = etRegUsername.text.toString().trim()
                val alamat :String = etRegAlamat.text.toString().trim()
                val no_telp :String = etRegTelp.text.toString().trim()
                val no_ktp :String = etRegKtp.text.toString().trim()

                if(email.isEmpty()){
                    etRegEmail.error = "Email harus diisi"
                    etRegEmail.requestFocus()
                    return
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    etRegEmail.error = "Email tidak valid"
                    etRegEmail.requestFocus()
                    return
                }

                if(password.isEmpty() || password.length < 8){
                    etRegPassword.error = "Password harus diisi"
                    etRegPassword.requestFocus()
                    return
                }

                if(username.isEmpty()){
                    etRegUsername.error = "Email harus diisi"
                    etRegUsername.requestFocus()
                    return
                }
                if(alamat.isEmpty()){
                    etRegAlamat.error = "Alamat harus diisi"
                    etRegAlamat.requestFocus()
                    return
                }
                if(no_telp.isEmpty()){
                    etRegTelp.error = "Nomor Telepon harus diisi"
                    etRegTelp.requestFocus()
                    return
                }
                if(no_ktp.isEmpty()){
                    etRegKtp.error = "Nomor KTP harus diisi"
                    etRegKtp.requestFocus()
                    return
                }

                val DocumentReference = db.collection("users").document(email)
                val userdata = hashMapOf(
                    "email" to email,
                    "username" to username,
                    "password" to password,
                    "alamat" to alamat,
                    "no_telp" to no_telp,
                    "no_ktp" to no_ktp,
                )
                DocumentReference.set(userdata)
                registerUser(email, password)
            }
        }
    }

    companion object {
        var TAG = SignUpActivity::class.java.simpleName
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        btnRegister.setOnClickListener(this)
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val currentUser = auth.currentUser!!
                    if (currentUser!=null){
                        currentUser!!.updateProfile(userProfileChangeRequest {
                            displayName = etRegUsername.text.toString()
                        })
                        currentUser!!.sendEmailVerification()
                    }
                    Toast.makeText(
                        this, "Berhasil mendaftarkan user, silahkan cek email anda untuk memferivikasi akun anda",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent =Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    return@addOnCompleteListener
                }
            }
    }
}