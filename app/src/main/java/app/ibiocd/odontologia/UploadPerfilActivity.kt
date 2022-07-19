package app.ibiocd.odontologia

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.ibiocd.lavanderia.Adapter.EspecialidadesRespons
import app.ibiocd.lavanderia.Adapter.ProfesionalRespons
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_upload_perfil.*
import kotlinx.android.synthetic.main.list_paciente_a.*
import kotlinx.android.synthetic.main.list_paciente_a.txtname
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*
import java.util.concurrent.TimeUnit


class UploadPerfilActivity : AppCompatActivity() {
    var dni:String?=""
   // var correo:String?=""
    var storedVerificationId:String=""
    var IDPIDP:String?=""

    var IDP:String?=""
    var url:String?=""
    var CLAVE:String?=""
    var emailB:Boolean=false
    var telefonoB:Boolean=false
    var ubicacionB:Boolean=false
    var identidadB:Boolean=false
    var matriculaB:Boolean=false
    var profesionB:Boolean=false
    var seguridadB:Boolean=false

    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_perfil)
        auth = Firebase.auth

        if(intent.extras !=null){
            uploadrperfilback.setText(intent.getStringExtra("back").toString())
            CLAVE=(intent.getStringExtra("CLAVE").toString())
            //correo = intent.getStringExtra("correo")
            IDP = intent.getStringExtra("IDP").toString()
            url = intent.getStringExtra("url").toString()

            btnenviar.visibility=View.GONE
            btnverificar.visibility=View.GONE

            selectord.visibility=View.GONE
            swisesionUpdate.visibility=View.GONE
            if (intent.getStringExtra("email").toString().toBoolean()){
                emailB=true
                linearEmailPhoneDatos.visibility=View.VISIBLE
                linearUbicacion.visibility =View.GONE

                txtalternativo.visibility=View.GONE
                txtdnid.visibility=View.GONE
                txtname.visibility=View.GONE
                txttitulo.setText("Ingresa tu e-mail")
                txtcomentarios.setText("Te enviaremos un mensaje para confirmarlo")
                btnconfirmar.setText("Enviar e-mail de confirmacion")
                IconUpload?.setImageDrawable(getDrawable(R.drawable.ic_email))
                IconUpload?.imageTintList = resources.getColorStateList(R.color.purple_500)


            }
            else if (intent.getStringExtra("telefono").toString().toBoolean()){
                telefonoB=true
                linearEmailPhoneDatos.visibility=View.VISIBLE
                linearUbicacion.visibility =View.GONE

                txtalternativo.visibility=View.GONE
                txtdnid.visibility=View.GONE
                txtname.visibility=View.GONE
                txttitulo.setText("Ingresa tu Telefono")
                txtcomentarios.setText("Te enviaremos un mensaje para confirmarlo")
                helptextdato.helperText = "Completar con +54 9 ..."
                btnconfirmar.setText("Enviar sms de confirmacion")
                IconUpload?.setImageDrawable(getDrawable(R.drawable.ic_phone))
                IconUpload?.imageTintList = resources.getColorStateList(R.color.purple_500)


                btnenviar.visibility=View.VISIBLE
                btnconfirmar.visibility=View.GONE
            }
            else if (intent.getStringExtra("ubicacion").toString().toBoolean()){
                ubicacionB=true
                linearEmailPhoneDatos.visibility=View.GONE
                linearUbicacion.visibility =View.VISIBLE
                btnconfirmar.setText("Guardar datos")
                IconUpload?.setImageDrawable(getDrawable(R.drawable.ic_location))
                IconUpload?.imageTintList = resources.getColorStateList(R.color.purple_500)

            }
            else if (intent.getStringExtra("identidad").toString().toBoolean()){
                identidadB=true
                linearEmailPhoneDatos.visibility=View.VISIBLE
                linearUbicacion.visibility =View.GONE
                IconUpload?.setImageDrawable(getDrawable(R.drawable.ic_account))
                IconUpload?.imageTintList = resources.getColorStateList(R.color.purple_500)

                txttitulo.setText("Ingrese sus datos")
                txtcomentarios.setText("Usaremos la informacion para verificar tu identidad")
                btnconfirmar.setText("Actualizar informacion")
                txtname.setText("Ingrese su nombre completo")
                txtdnid.setText("Ingrese su DNI")
                txtalternativo.visibility=View.VISIBLE
                txtdnid.visibility=View.VISIBLE
                txtname.visibility=View.VISIBLE
            }
            else if (intent.getStringExtra("matricula").toString().toBoolean()){
                matriculaB=true
                linearEmailPhoneDatos.visibility=View.VISIBLE
                linearUbicacion.visibility =View.GONE

                txtalternativo.visibility=View.GONE
                txtdnid.visibility=View.GONE
                txtname.visibility=View.GONE
                txttitulo.setText("Ingresa tu matricula")
                txtcomentarios.setText("Verificaremos la matricula dentro de 24hs")
                btnconfirmar.setText("Enviar matricula para verificar")

                IconUpload?.setImageDrawable(getDrawable(R.drawable.appointment))
                IconUpload?.imageTintList = resources.getColorStateList(R.color.purple_500)

            }
            else if (intent.getStringExtra("profesion").toString().toBoolean()){
                profesionB=true
                linearEmailPhoneDatos.visibility=View.VISIBLE
                linearUbicacion.visibility =View.GONE

                txtalternativo.visibility=View.GONE
                txtdnid.visibility=View.GONE
                txtname.visibility=View.GONE
                helptextdato.visibility=View.GONE


                txttitulo.setText("Selecciona tu profesion")
                txtcomentarios.setText("Ayuda a los pacientes a encontrarte")
                btnconfirmar.setText("Confimarmar profesion")
                getListAllEspecialidades()
                IconUpload?.setImageDrawable(getDrawable(R.drawable.ic_work))
                IconUpload?.imageTintList = resources.getColorStateList(R.color.purple_500)

            }
            else if (intent.getStringExtra("seguridad").toString().toBoolean()){
                seguridadB=true

                edtxtdato.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                edtxtalternativo.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                linearEmailPhoneDatos.visibility=View.VISIBLE
                linearUbicacion.visibility =View.GONE
                swisesionUpdate.visibility=View.VISIBLE

                IconUpload?.setImageDrawable(getDrawable(R.drawable.ic_lock))
                IconUpload?.imageTintList = resources.getColorStateList(R.color.purple_500)
                txtcomentarios.setText("")

                txttitulo.setText("Cambia tu clave")
                btnconfirmar.setText("Cambiar clave")
                txtname.setText("Ingresa tu anterior clave ")
                txtdnid.setText("Ingresa tu nueva clave")
                txtalternativo.visibility=View.VISIBLE
                txtdnid.visibility=View.VISIBLE
                txtname.visibility=View.VISIBLE

            }


        }

    }
    fun ClickEnviar(view: View){
        btnenviar.visibility=View.GONE
        btnverificar.visibility=View.VISIBLE
        txtalternativo.visibility=View.VISIBLE
        txtdnid.visibility=View.VISIBLE
        txtdnid.setText("Por favor ingrese el codigo de verificacion")

        val phoneNumber:String=edtxtdato.text.toString()
        val option = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        auth.setLanguageCode(Locale.getDefault().language)
        PhoneAuthProvider.verifyPhoneNumber(option)

    }
    fun ClickVerificar(view: View){
        val codeverifi=edtxtalternativo.text.toString()
        val credencial = PhoneAuthProvider.getCredential(storedVerificationId,codeverifi)
        auth.signInWithCredential(credencial)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "Telefono ha verificado espera mientras se guarda", Toast.LENGTH_SHORT).show()
                    Upload(View(applicationContext))

                }else{
                    if (task.exception is FirebaseAuthInvalidCredentialsException){
                        btnverificar.visibility=View.GONE
                        btnenviar.visibility=View.VISIBLE
                    }
                }
            }

    }

    fun Upload(view: View){
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.getProfesional(IDP.toString())
            val datos: ProfesionalRespons? =call.body()
            runOnUiThread{
                var Imagen=""
                if(call.isSuccessful){
                    if (emailB){
                        postCliente(Imagen,datos!!.TID,"${edtxtdato.text}","${datos.direccion}","${datos.nameprof}","${datos.celular}","insertar",datos,"${datos.matricula}","${datos.especialidad}","${datos.DNI}")

                    }
                    else if (telefonoB){
                        postCliente(Imagen,datos!!.TID,datos.correo,"${datos.direccion}","${datos.nameprof}","${edtxtdato.text}","modificar",datos,"${datos.matricula}","${datos.especialidad}","${datos.DNI}")

                    }
                    else if (ubicacionB){
                        if (edtxtcalle.text.isEmpty()){
                            edtxtcalle.error="Por favor ingrese una calle"
                            edtxtcalle.requestFocus()

                        }else if (edtxtcpo.text.isEmpty()){
                            edtxtcpo.error="Por favor ingrese un codigo postal"
                            edtxtcpo.requestFocus()

                        }else if (edtxtprv.text.isEmpty()){
                            edtxtprv.error="Por favor ingrese una provincia"
                            edtxtprv.requestFocus()
                        }else{
                            val direccion =  edtxtcalle.text.toString() +" "+ edtxtnum.text.toString() +" "+edtxtpisodepto.text.toString() +" CPO:"+edtxtcpo.text.toString() +" "+ edtxtprv.text.toString()
                            postCliente(Imagen,datos!!.TID,datos.correo,"${direccion}","${datos.nameprof}","${datos.celular}","modificar",datos,"${datos.matricula}","${datos.especialidad}","${datos.DNI}")

                        }

                    }
                    else if (identidadB){
                        postCliente(Imagen,datos!!.TID,datos.correo,"${datos.direccion}","${edtxtdato.text}","${datos.celular}","modificar",datos,"${datos.matricula}","${datos.especialidad}","${edtxtalternativo.text}")

                    }
                    else if (matriculaB){
                        postCliente(Imagen,datos!!.TID,datos.correo,"${datos.direccion}","${edtxtdato.text}","${datos.celular}","modificar",datos,"${edtxtdato.text}","${datos.especialidad}","${datos.DNI}")

                    }
                    else if (profesionB){
                        postCliente(Imagen,datos!!.TID,datos.correo,"${datos.direccion}","${edtxtdato.text}","${datos.celular}","modificar",datos,"${datos.matricula}","${edtxtdato.text}","${datos.DNI}")

                    }
                    else if (seguridadB){

                        if (edtxtalternativo.text.toString().isEmpty()){
                            edtxtalternativo.error="Por favor ingresa tu nuevaclave"
                            edtxtalternativo.requestFocus()

                        }else if (edtxtdato.text.toString().isEmpty()){
                            edtxtdato.error="Por favor ingresa tu clave"
                            edtxtdato.requestFocus()

                        }else if (edtxtdato.text.toString()==edtxtalternativo.text.toString()){

                            edtxtalternativo.error="Por favor ingresa tu nueva clave"
                            edtxtalternativo.requestFocus()
                        }else if (edtxtdato.text.toString()==CLAVE){
                            val user = Firebase.auth.currentUser

                            user!!.updatePassword(edtxtalternativo.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(applicationContext, "Se actualizo la contraseña", Toast.LENGTH_SHORT).show()

                                        if (swisesionUpdate.isChecked){
                                            //BORRO

                                            try {
                                                val archivo = OutputStreamWriter(openFileOutput("passwC.txt", MODE_PRIVATE))
                                                archivo.write("")

                                                archivo.flush()
                                                archivo.close()
                                            } catch (e: IOException) {
                                            }
                                            //GUARDO
                                            try {
                                                val archivo = OutputStreamWriter(openFileOutput("passwC.txt", MODE_PRIVATE))
                                                archivo.write(edtxtalternativo.text.toString())

                                                archivo.flush()
                                                archivo.close()
                                            } catch (e: IOException) {
                                            }

                                            val intent = Intent(applicationContext, InicioActivity::class.java)
                                            intent.putExtra("idprofesional",IDP)
                                            intent.putExtra("contraseña",edtxtalternativo.text.toString())
                                            intent.putExtra("url",url)
                                            startActivity(intent)
                                            finish()
                                        }

                                        else if (swisesionUpdate.isChecked){
                                            val intent = Intent(applicationContext, MainActivity::class.java)
                                            intent.putExtra("DATA","A")

                                            startActivity(intent)
                                            finish()
                                        }


                                    }else{
                                        Toast.makeText(applicationContext, "Fallo la actualizacion, intentelo mas tarde", Toast.LENGTH_SHORT).show()

                                    }
                                }

                        }else if (edtxtdato.text.toString()!=CLAVE){
                            edtxtdato.error="Su clave es incorrecta"
                            edtxtdato.requestFocus()
                        }

                    }
                }
            }
        }
    }//


    private fun postCliente(imagen:String,token:String,correo:String,direccion:String,nombre:String,celular:String,accion:String,datos:ProfesionalRespons,matricula:String,profesion:String,DNI:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postProfesional("${nombre}","${datos.col}","${profesion}","${celular}","${direccion}","${correo}","${datos.horarios}","${datos.prestacion}","${datos.verificar}","${datos.img}","${matricula}","$token",IDP.toString(),"$DNI",accion,accion)

            call.enqueue(object : Callback<ProfesionalRespons> {
                override fun onFailure(call: Call<ProfesionalRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message, Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<ProfesionalRespons>, response: retrofit2.Response<ProfesionalRespons>) {
                    if (emailB){
                        //Activar una actividad o algo para que se reciba el codigo de confirmacion
                        val user = Firebase.auth.currentUser
                        response.body()?.let {
                            user!!.updateEmail(it.correo)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        user!!.sendEmailVerification()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val intent = Intent(applicationContext, MainActivity::class.java)
                                                    intent.putExtra("DATA","A")
                                                    //Agregar una pantalla de carga
                                                    startActivity(intent)

                                                    Toast.makeText(applicationContext, "User email adress update, review email verification", Toast.LENGTH_SHORT).show()
                                                    finish()

                                                }
                                            }
                                    }
                                }
                        }

                    }else if (telefonoB){
                        Toast.makeText(applicationContext, "Telefono guardado", Toast.LENGTH_SHORT).show()
                        finish()
                        //Activar una actividad o algo para que se reciba el codigo de confirmacion
                    }else if (ubicacionB){
                        Toast.makeText(applicationContext, "Ubicacion registrada", Toast.LENGTH_SHORT).show()
                        finish()

                    }else if (identidadB){
                        Toast.makeText(applicationContext, "Informacion actualizada", Toast.LENGTH_SHORT).show()
                        finish()

                    }else if (matriculaB){
                        Toast.makeText(applicationContext, "Matricula enviada", Toast.LENGTH_SHORT).show()
                        finish()

                    }else if (profesionB){
                        Toast.makeText(applicationContext, "Profesion actualizada", Toast.LENGTH_SHORT).show()
                        finish()

                    }

                }
            })
        }
    }

    fun Back(view: View){

        finish()
    }

    fun getListAllEspecialidades(){
        selectord.visibility=View.VISIBLE

        var arraylisEspec = ArrayList<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.getEspecialidades("")
            runOnUiThread{
                val datos: List<EspecialidadesRespons>? = call.body()
                for (i in 0 until datos?.size!!){
                    val espec= datos[i].especialidad
                    val img= datos[i].img
                    arraylisEspec.add(espec)
                }
                val arrayAdapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,arraylisEspec)//
                with(edtxtespecialidad){
                    setAdapter(arrayAdapter)

                }


            }
        }
    }



    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {

        }

        override fun onVerificationFailed(p0: FirebaseException) {
            if (p0 is FirebaseAuthInvalidCredentialsException){

            }else if(p0 is FirebaseTooManyRequestsException){

            }


        }

        override fun onCodeSent(verificationId: String, Token: PhoneAuthProvider.ForceResendingToken) {
            storedVerificationId = verificationId
            //resendToken=Token
        }
    }
}