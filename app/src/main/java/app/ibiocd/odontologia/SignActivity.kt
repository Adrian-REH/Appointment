package app.ibiocd.odontologia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import app.ibiocd.lavanderia.Adapter.ProfesionalRespons
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_sign.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback

class SignActivity : AppCompatActivity() {
   private var  auth = FirebaseAuth.getInstance()
    var BACKTXT:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        BACKTXT=intent.getStringExtra("back").toString()
        signbacktext.setText("$BACKTXT")

    }
    fun Back(view: View){
        finish()
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
                getprofesionalSave()
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener(this){ ask ->
                    if (ask.isSuccessful){
                        Toast.makeText(baseContext,"Se envio un correo de verificacion", Toast.LENGTH_LONG).show()
                    }

                }
            }else{
                Toast.makeText(baseContext,"Registro fallido. Intenta de nuevo mas tarde", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun getprofesionalSave(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call=RetrofitClient.instance.getProfesional("${edtxtcorreo.text.toString()}")
                //val datos: ClienteRespons?=call.body()
                runOnUiThread{
                    if(call.isSuccessful){
                        edtxtcorreo.error="El usuario ingresado existe, pruebe otro"

                    }else{
                        postProfesionalSave()

                    }
                }
            }catch (e:Exception){
                postProfesionalSave()

            }
        }
    }//
    private fun postProfesionalSave() {
        var token=""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
             token = task.result


        })
        CoroutineScope(Dispatchers.IO).launch {

            val call=RetrofitClient.instance.postProfesional("${edtxtname.text}","","","","","${edtxtcorreo.text}","","","N","http://23herrera.xyz:81/appointment/docs/blank_profile.png","${edtxmatricula.text.toString()}","$token","insertar","")
            call.enqueue(object : Callback<ProfesionalRespons> {
                override fun onFailure(call: Call<ProfesionalRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<ProfesionalRespons>, response: retrofit2.Response<ProfesionalRespons>) {
//Nunca se registro con nosotros
                    startActivity(Intent(applicationContext,MainActivity::class.java))
                    finish()


                }
            })



        }
    }

}