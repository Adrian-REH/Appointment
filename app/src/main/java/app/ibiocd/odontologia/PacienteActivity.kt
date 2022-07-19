package app.ibiocd.odontologia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import app.ibiocd.lavanderia.Adapter.ClienteRespons
import app.ibiocd.lavanderia.Adapter.EnlaceRespons
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_paciente.*
import kotlinx.android.synthetic.main.activity_paciente.txtcel
import kotlinx.android.synthetic.main.activity_paciente.txtcorreo
import kotlinx.android.synthetic.main.activity_paciente.txtdirec
import kotlinx.android.synthetic.main.activity_paciente.txtnombre
import kotlinx.android.synthetic.main.activity_paciente.viewimageperfil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class PacienteActivity : AppCompatActivity() {
    var url:String?=""
    var IDP:String?=""
    var BACKTXT:String?=""
    var dni:String?=""
    var especialidad:String?=""
    var name:String?=""
    var HISTORIAL:String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente)

        if(intent.extras !=null){
            dni = intent.getStringExtra("dni")
            //correo = intent.getStringExtra("correo")
            IDP = intent.getStringExtra("IDP")
            url = intent.getStringExtra("url")
            name = intent.getStringExtra("name")
            especialidad = intent.getStringExtra("especialidad")
            BACKTXT=intent.getStringExtra("back").toString()
            pacbacktext.setText("$BACKTXT")
        }
        getClienteDatos()

        ClickEnlazar(View(applicationContext))
    }

    fun ClickEnlazar(view: View){
        getClienteSave(View(applicationContext),"OCUPADO")

    }

    fun ClickBorrar(view: View){
        getClienteSave(View(applicationContext),"SN")
    }

    fun getClienteSave(view: View,estado: String){
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.getEnlace(dni.toString(),IDP.toString())
            val datos: EnlaceRespons? =call.body()
            runOnUiThread{
                if(call.isSuccessful){
                    if (datos!=null){
                        postCliente("modificar",datos.especialidad,estado,datos.nameprof)

                    }

                }else{
                    if (datos!=null){
                        postCliente("insertar",datos.especialidad,estado,datos.nameprof)

                    }
                }
            }
        }
    }//

    private fun postCliente(accion:String,especialidad:String,estado:String,nameprof:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postEnlace("${IDP}","${dni}","${especialidad}","$estado","${nameprof}","${txtnombre.text}",accion,accion)
            call.enqueue(object : Callback<EnlaceRespons> {
                override fun onFailure(call: Call<EnlaceRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<EnlaceRespons>, response: retrofit2.Response<EnlaceRespons>) {
                    if(estado=="SN"){
                        handshake.visibility = View.VISIBLE

                    }else if(estado=="OCUPADO"){
                        handshake.visibility = View.GONE

                    }


                }
            })



        }
    }

    private fun getClienteDatos(){
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.getCliente(dni.toString())
            val datos: ClienteRespons?=call.body()
            runOnUiThread{
                if(call.isSuccessful){
                    val name = datos?.name ?: ""
                    val direc = datos?.direccion ?: ""
                    val celul = datos?.cel ?: ""
                    val email = datos?.correo ?: ""
                    val image= datos?.img ?: ""
                    val dni= datos?.dni ?: ""
                    txtcorreo?.setText(email)
                    txtnombre?.setText(name)
                    txtcel?.setText(celul)
                    txtdirec?.setText(direc)
                    txtdni?.setText(dni)
                    Glide.with(applicationContext)
                        .load(image)
                        .centerCrop()
                        .into(viewimageperfil)
                }else{
                    Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                }
            }

        }
    }//

    fun Back(view: View){
        finish()
    }
    fun ClickTurno(view: View){
        val intent = Intent(this, TurnoActivity::class.java)
        intent.putExtra("dni",dni)
        intent.putExtra("url",url)
        intent.putExtra("IDP",IDP)
        intent.putExtra("name",name)

        intent.putExtra("especialidad",especialidad)
        intent.putExtra("clientename",txtnombre.text.toString())

        intent.putExtra("back","Paciente")


        intent.putExtra("fecha","0")
        intent.putExtra("id","0")
        intent.putExtra("codigo","0")
        intent.putExtra("JSONODONT","")

        startActivity(intent)
    }

    fun ClickHistorial(view: View){
        Toast.makeText(this,"$especialidad",Toast.LENGTH_LONG).show()
        val intent = Intent(this, HistorialActivity::class.java)
        intent.putExtra("IDP",IDP)
        intent.putExtra("dni",dni)
        intent.putExtra("url",url)
        intent.putExtra("especialidad",especialidad)
        intent.putExtra("back",titlepaciente.text.toString())
        startActivity(intent)
    }
}