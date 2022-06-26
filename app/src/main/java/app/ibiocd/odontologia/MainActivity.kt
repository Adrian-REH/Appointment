package app.ibiocd.odontologia

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import app.ibiocd.lavanderia.Adapter.ClienteRespons
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.lavanderia.Adapter.ProfesionalRespons
import app.ibiocd.odontologia.Adapter.AdapterClienteA
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_inicio.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    private var DATA:String?="false"
    var DATOS:String?=""
    val auth = FirebaseAuth.getInstance()
    var md5user = ""
    var md5pass = ""
    var URL = "23herrera.xyz:81/appointment"

    var sw1b: Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Odontologia)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fullscrean.visibility=View.VISIBLE
        lny.visibility=View.GONE
        cards.visibility=View.GONE

        if(intent.extras !=null){
            DATA = intent.getStringExtra("DATA")
            if (DATA=="A"){
                BorrarSesion()
                fullscrean.visibility=View.GONE
                lny.visibility=View.VISIBLE
                cards.visibility=View.VISIBLE
            }
        }
        if (fileList().contains("usuarioC.txt")) {
            try {
                val archivo = InputStreamReader(openFileInput("usuarioC.txt"))
                val br = BufferedReader(archivo)
                var linea = br.readLine()
                val todo = StringBuilder()
                while (linea != null) {
                    todo.append(linea)
                    linea = br.readLine()
                }
                br.close()
                archivo.close()
                md5user = (todo.toString())
            } catch (e: IOException) {
            }
        }else{
            md5user = ""
        }
        if (fileList().contains("passwC.txt")) {
            try {
                val archivo = InputStreamReader(openFileInput("passwC.txt"))
                val br = BufferedReader(archivo)
                var linea = br.readLine()
                val todo = StringBuilder()
                while (linea != null) {
                    todo.append(linea)
                    linea = br.readLine()
                }
                br.close()
                archivo.close()
                md5pass = (todo.toString())
            } catch (e: IOException) {
            }
        }else{
            md5pass = ""
        }

        swisesion.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                sw1b=true
            }else if (!b){
                sw1b=false
                BorrarSesion()
            }
        }
        FinFullScreen()
    }

    fun profecionales(view: View){
        val usuario=edtxtuser.text.toString()
        if (usuario.isNotEmpty()){

            CoroutineScope(Dispatchers.IO).launch {

                try {

                    val call=RetrofitClient.instance.getProfesional(usuario.toString())
                    val datos: ProfesionalRespons? =call.body()
                    runOnUiThread{
                        if(call.isSuccessful){
                            val usuario = datos?.correo ?: ""
                            doLigin(usuario)

                        }else{
                            Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                        }
                    }


                }catch (e: java.lang.Exception){

                }
            }


        }



    }
    private fun FinFullScreen(){

        Handler().postDelayed(Runnable {

            if (md5pass!=""&&md5user!=""){
                swisesion.isChecked = true
                edtxtuser.setText(md5user)
                edtxtpass.setText(md5pass)
                doLigin(md5user)

            } else if (md5pass==""&&md5user==""){
                fullscrean.visibility=View.GONE
                lny.visibility=View.VISIBLE
                cards.visibility=View.VISIBLE
                swisesion.isChecked = false
            }


        },3000)


    }
   private fun updateUI(currentUser:FirebaseUser?,correo:String){
        if (currentUser !=null){
            if (currentUser.isEmailVerified){
                val intent = Intent(this, InicioActivity::class.java)
                intent.putExtra("correo",correo)
                intent.putExtra("url",URL)
                startActivity(intent)
                finish()

            }else{
                Toast.makeText(this,"Por favor verifique su Email",Toast.LENGTH_SHORT).show()

            }
        }else{
            Toast.makeText(this,"Fallo el incio de Sesion",Toast.LENGTH_SHORT).show()

        }
    }

    fun ClickRegistrar(view: View){
        val intent = Intent(this, SignActivity::class.java)
        startActivity(intent)
    }

    fun doLigin(correo:String){
        if (edtxtuser.text.toString().isEmpty()){
            edtxtuser.error="Por favor ingrese un email o su DNI"
            edtxtuser.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            edtxtuser.error="Por favor ingrese un email valido"
            edtxtuser.requestFocus()
            return
        }
        if (edtxtpass.text.toString().isEmpty()){
            edtxtpass.error="Por favor ingrese una clave"
            edtxtpass.requestFocus()
            return
        }
        auth.signInWithEmailAndPassword(correo,edtxtpass.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val user=auth.currentUser

                updateUI(user,correo)
                GuardarSesion(correo)
            }else{
                edtxtpass.error="La clave es incorrecta"
                edtxtpass.requestFocus()
                updateUI(null,"null")
            }
        }
    }

    fun ClickRecuperarClave(view: View){
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Recuperar Clave")
        val view=layoutInflater.inflate(R.layout.dialog_forgot,null)
        val username=view.findViewById<EditText>(R.id.et_correo)
        builder.setView(view)
        builder.setPositiveButton("Reset", DialogInterface.OnClickListener { dialogInterface, i ->
            forgotPassword(username)
        })
        builder.setNegativeButton("Close", DialogInterface.OnClickListener { dialogInterface, i ->

        })
        builder.show()
    }

    private fun forgotPassword(username: EditText){
        if (username.text.toString().isEmpty()){
            username.error="Please enter email"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()){
            return
        }
        auth.sendPasswordResetEmail(username.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this,"Email enviado.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun GuardarSesion(usuario:String){
        md5user = usuario
        md5pass = edtxtpass.text.toString()

        try {
            val archivo = OutputStreamWriter(openFileOutput("usuarioC.txt", MODE_PRIVATE))
            archivo.write(md5user.toString())

            archivo.flush()
            archivo.close()
        } catch (e: IOException) {
        }

        try {
            val archivo = OutputStreamWriter(openFileOutput("passwC.txt", MODE_PRIVATE))
            archivo.write(md5pass.toString())

            archivo.flush()
            archivo.close()
        } catch (e: IOException) {
        }



    }

    fun BorrarSesion(){

        md5user = ""
        md5pass = ""

        try {
            val archivo = OutputStreamWriter(openFileOutput("usuarioC.txt", MODE_PRIVATE))
            archivo.write(md5user.toString())

            archivo.flush()
            archivo.close()
        } catch (e: IOException) {
        }

        try {
            val archivo = OutputStreamWriter(openFileOutput("passwC.txt", MODE_PRIVATE))
            archivo.write(md5pass.toString())

            archivo.flush()
            archivo.close()
        } catch (e: IOException) {
        }


    }

}