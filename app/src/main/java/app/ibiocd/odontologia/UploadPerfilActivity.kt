package app.ibiocd.odontologia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import app.ibiocd.lavanderia.Adapter.ClienteRespons
import app.ibiocd.lavanderia.Adapter.ProfesionalRespons
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_upload_perfil.*
import kotlinx.android.synthetic.main.list_paciente_a.*
import kotlinx.android.synthetic.main.list_paciente_a.txtname
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback

class UploadPerfilActivity : AppCompatActivity() {
    var dni:String?=""
   // var correo:String?=""
    var IDP:String?=""
    var emailB:Boolean=false
    var telefonoB:Boolean=false
    var ubicacionB:Boolean=false
    var identidadB:Boolean=false
    var matriculaB:Boolean=false
    var profesionB:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_perfil)
        if(intent.extras !=null){
            uploadrperfilback.setText(intent.getStringExtra("back").toString())
            //correo = intent.getStringExtra("correo")
            IDP = intent.getStringExtra("IDP")

            btnconfirmar
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
                btnimgedit?.setImageDrawable(getDrawable(R.drawable.ic_email))
                btnimgedit?.imageTintList = resources.getColorStateList(R.color.purple_500)


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
                btnconfirmar.setText("Enviar sms de confirmacion")
                btnimgedit?.setImageDrawable(getDrawable(R.drawable.ic_phone))
                btnimgedit?.imageTintList = resources.getColorStateList(R.color.purple_500)


            }
            else if (intent.getStringExtra("ubicacion").toString().toBoolean()){
                ubicacionB=true
                linearEmailPhoneDatos.visibility=View.GONE
                linearUbicacion.visibility =View.VISIBLE
                btnconfirmar.setText("Guardar datos")
                btnimgedit?.setImageDrawable(getDrawable(R.drawable.ic_location))
                btnimgedit?.imageTintList = resources.getColorStateList(R.color.purple_500)

            }
            else if (intent.getStringExtra("identidad").toString().toBoolean()){
                identidadB=true
                linearEmailPhoneDatos.visibility=View.VISIBLE
                linearUbicacion.visibility =View.GONE
                btnimgedit?.setImageDrawable(getDrawable(R.drawable.ic_account))
                btnimgedit?.imageTintList = resources.getColorStateList(R.color.purple_500)

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

                btnimgedit?.setImageDrawable(getDrawable(R.drawable.appointment))
                btnimgedit?.imageTintList = resources.getColorStateList(R.color.purple_500)

            }
            else if (intent.getStringExtra("profesion").toString().toBoolean()){
                profesionB=true
                linearEmailPhoneDatos.visibility=View.VISIBLE
                linearUbicacion.visibility =View.GONE

                txtalternativo.visibility=View.GONE
                txtdnid.visibility=View.GONE
                txtname.visibility=View.GONE
                txttitulo.setText("Ingresa tu profesion")
                txtcomentarios.setText("Ayuda a los pacientes a encontrarte")
                btnconfirmar.setText("Confimarmar profesion")

                btnimgedit?.setImageDrawable(getDrawable(R.drawable.ic_work))
                btnimgedit?.imageTintList = resources.getColorStateList(R.color.purple_500)

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
                        postCliente(Imagen,datos!!.TID,"${edtxtdato.text}","${datos.direccion}","${datos.nameprof}","${datos.celular}","insertar",datos,"${datos.matricula}","${datos.especialidad}")

                    }else if (telefonoB){
                        postCliente(Imagen,datos!!.TID,datos.correo,"${datos.direccion}","${datos.nameprof}","${edtxtdato.text}","modificar",datos,"${datos.matricula}","${datos.especialidad}")

                    }else if (ubicacionB){
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
                            postCliente(Imagen,datos!!.TID,datos.correo,"${direccion}","${datos.nameprof}","${datos.celular}","modificar",datos,"${datos.matricula}","${datos.especialidad}")

                        }

                    }else if (identidadB){
                        postCliente(Imagen,datos!!.TID,datos.correo,"${datos.direccion}","${edtxtdato.text}","${datos.celular}","modificar",datos,"${datos.matricula}","${datos.especialidad}")

                    }else if (matriculaB){
                        postCliente(Imagen,datos!!.TID,datos.correo,"${datos.direccion}","${edtxtdato.text}","${datos.celular}","modificar",datos,"${edtxtdato.text}","${datos.especialidad}")

                    }else if (profesionB){
                        postCliente(Imagen,datos!!.TID,datos.correo,"${datos.direccion}","${edtxtdato.text}","${datos.celular}","modificar",datos,"${datos.matricula}","${edtxtdato.text}")

                    }
                }
            }
        }
    }//


    private fun postCliente(imagen:String,token:String,correo:String,direccion:String,nombre:String,celular:String,accion:String,datos:ProfesionalRespons,matricula:String,profesion:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postProfesional("${datos.nameprof}","${datos.col}","${profesion}","${datos.celular}","${direccion}","${correo}","${datos.horarios}","${datos.prestacion}","${datos.verificar}","${datos.img}","${matricula}","$token",IDP.toString(),accion,accion)

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
                                                }
                                            }
                                    }
                                }
                        }

                    }else if (telefonoB){

                        //Activar una actividad o algo para que se reciba el codigo de confirmacion
                    }else if (ubicacionB){
                        Toast.makeText(applicationContext, "Ubicacion registrada", Toast.LENGTH_SHORT).show()

                    }else if (identidadB){
                        Toast.makeText(applicationContext, "Nombre actualizado", Toast.LENGTH_SHORT).show()

                    }else if (matriculaB){
                        Toast.makeText(applicationContext, "Matricula enviada", Toast.LENGTH_SHORT).show()

                    }else if (profesionB){
                        Toast.makeText(applicationContext, "Profesion actualizada", Toast.LENGTH_SHORT).show()

                    }

                }
            })
        }
    }


    fun Back(view: View){

        finish()
    }
}