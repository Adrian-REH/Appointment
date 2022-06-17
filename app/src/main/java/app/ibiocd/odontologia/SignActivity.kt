package app.ibiocd.odontologia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign.*

class SignActivity : AppCompatActivity() {
   private var  auth = FirebaseAuth.getInstance()
    var URL:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        if(intent.extras !=null){
            URL = intent.getStringExtra("url").toString()
        }
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
        if (edtxmatricula.text.toString().isEmpty()){
            edtxmatricula.error="Ingrese su Matricula"
            edtxmatricula.requestFocus()
            return
        }else if (edtxtname.text.toString().isEmpty()){
            edtxmatricula.error="Ingrese su Nombre y Apellido"
            edtxmatricula.requestFocus()
            return
        }
       auth.createUserWithEmailAndPassword(edtxtcorreo.text.toString(),edtxtclave.text.toString()).addOnCompleteListener(this) {task ->
           if (task.isSuccessful){
               val user=auth.currentUser
               user?.sendEmailVerification()?.addOnCompleteListener { task ->
                   if (task.isSuccessful){

                           clickGuardar()



                   }
               }

           }else{
               Toast.makeText(baseContext,"Registro fallido. Intenta de nuevo mas tarde",Toast.LENGTH_LONG).show()
           }
       }
    }
    fun clickGuardar(){
        //Primero Verifico si esta registrado el TOKEN ID
        val queue = Volley.newRequestQueue(this)
        val url = "http://$URL/cuentas.php?correo=${edtxtuser.text.toString()}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }, { error ->

                val url = "http://$URL/cuentas.php"
                val queue= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                var resultadoPost = object : StringRequest(Method.POST,url,
                    Response.Listener<String> { response ->

                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        parametros.put("correo",edtxtcorreo.text.toString())
                        parametros.put("matricula",edtxmatricula.text.toString())
                        parametros.put("horarios","")
                        parametros.put("verificar","N")
                        parametros.put("prestaciones","")
                        parametros.put("insertar","insertar")
                        return parametros
                    }
                }
                // con esto envio o SEND todo
                queue.add(resultadoPost)
            }
        )
        queue.add(jsonObjectRequest)



    }

}