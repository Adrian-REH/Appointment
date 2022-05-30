package app.ibiocd.odontologia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign.*

class SignActivity : AppCompatActivity() {
   private var  auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
    }
    fun ClickRegistrarse(view: View){
        if (edtxtcorreo.text.toString().isEmpty()){
            edtxtcorreo.error="Por favor ingrese un email"
            edtxtcorreo.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(edtxtcorreo.text.toString()).matches()){
            edtxtcorreo.error="Por favor ingrese un email valido"
            edtxtcorreo.requestFocus()
            return
        }
        if (edtxtclave.text.toString().isEmpty()){
            edtxtclave.error="Por favor ingrese una clave"
            edtxtclave.requestFocus()
            return
        }
       auth.createUserWithEmailAndPassword(edtxtcorreo.text.toString(),edtxtclave.text.toString()).addOnCompleteListener(this) {task ->
           if (task.isSuccessful){
               val user=auth.currentUser
               user?.sendEmailVerification()?.addOnCompleteListener { task ->
                   if (task.isSuccessful){
                       startActivity(Intent(this,MainActivity::class.java))
                       finish()
                   }
               }

           }else{
               Toast.makeText(baseContext,"Registro fallido. Intenta de nuevo mas tarde",Toast.LENGTH_LONG).show()
           }
       }
    }
}