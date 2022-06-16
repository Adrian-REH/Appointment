package app.ibiocd.odontologia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_turno.*
import org.json.JSONException
import org.json.JSONObject

class PacienteActivity : AppCompatActivity() {
    var url:String?=""
    var correo:String?=""
    var dni:String?=""
    var name:String?=""
    var HISTORIAL:String?=""
    var edtxtnombre: EditText?=null
    var edtxttelefono: EditText?=null
    var edtxtemail: EditText?=null
    var edtxtdirec: EditText?=null
    var edtxtdni: EditText?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente)
        edtxtnombre = findViewById(R.id.edtxtnombre)
        edtxttelefono = findViewById(R.id.edtxttelefono)
        edtxtemail = findViewById(R.id.edtxtemail)
        edtxtdirec = findViewById(R.id.edtxtdirec)
        edtxtdni = findViewById(R.id.edtxtdni)
        if(intent.extras !=null){
            dni = intent.getStringExtra("dni")
            correo = intent.getStringExtra("correo")
            url = intent.getStringExtra("url")
            name = intent.getStringExtra("name")

        }
        ClickRefresh()
        txtinpname?.visibility = View.GONE
        txtinpcel?.visibility = View.GONE
        txtinpmail?.visibility = View.GONE
        txtinpdire?.visibility = View.GONE
        txtinpdni?.visibility = View.GONE
        ClickEnlazar(View(applicationContext))
    }

    fun ClickEnlazar(view: View){

        //Primero Verifico si esta registrado el TOKEN ID
        val queue = Volley.newRequestQueue(this)
        val url2 = "http://$url/enlace.php?pacientedni=${dni}&profecionalemail=${correo}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url2,null,
            { response ->

                val url = "http://$url/enlace.php"
                val queue= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                var resultadoPost = object : StringRequest(
                    Method.POST,url,
                    Response.Listener<String> { response ->
                        handshake.visibility = View.GONE
                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        parametros.put("estadoprofecional","OCUPADO")
                        parametros.put("historial",response.getString("historial"))
                        parametros.put("profecionalemail",correo.toString())
                        parametros.put("pacientedni",dni.toString())
                        parametros.put("nombreprofesional",name.toString())
                        parametros.put("modificar","modificar")
                        return parametros
                    }
                }
                // con esto envio o SEND todo
                queue.add(resultadoPost)

            }, { error ->

                val url = "http://$url/enlace.php"
                val queue= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                var resultadoPost = object : StringRequest(
                    Method.POST,url,
                    Response.Listener<String> { response ->
                        handshake.visibility = View.GONE
                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        parametros.put("estadoprofecional","OCUPADO")
                        parametros.put("historial","")
                        parametros.put("profecionalemail",correo.toString())
                        parametros.put("pacientedni",dni.toString())
                        parametros.put("nombreprofesional",name.toString())
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
    fun ClickBorrar(view: View){
        //Primero Verifico si esta registrado el TOKEN ID
        val queue = Volley.newRequestQueue(this)
        val url2 = "http://$url/enlace.php?pacientedni=${dni}&profecionalemail=${correo}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url2,null,
            { response ->

                val url = "http://$url/enlace.php"
                val queue= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                var resultadoPost = object : StringRequest(
                    Method.POST,url,
                    Response.Listener<String> { response ->
                        handshake.visibility = View.VISIBLE
                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        parametros.put("historial",response.getString("historial"))
                        parametros.put("estadoprofecional","SN")
                        parametros.put("profecionalemail",correo.toString())
                        parametros.put("pacientedni",dni.toString())
                        parametros.put("modificar","modificar")
                        return parametros
                    }
                }

                queue.add(resultadoPost)
            }, { error ->}
        )
        queue.add(jsonObjectRequest)
    }
    fun ClickRefresh(){
        val queue = Volley.newRequestQueue(this)
        val url = "http://$url/clientes.php?dni=${dni}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray("data")
                    val jsonObject = jsonArray.getJSONObject(0)
                    val name = jsonObject.getString("nombreapellido").toString()
                    val direc = jsonObject.getString("direccion").toString()
                    val celul = jsonObject.getString("celular").toString()
                    val email = jsonObject.getString("correo").toString()

                    val image= jsonObject.getString("img").toString()
                    val dni2= jsonObject.getString("dni").toString()



                    txtcorreo?.setText(email)
                    edtxtemail?.setText(email)
                    txtnombre?.setText(name)
                    edtxtnombre?.setText(name)
                    txtcel?.setText(celul)
                    edtxttelefono?.setText(celul)
                    txtdirec?.setText(direc)
                    edtxtdirec?.setText(direc)
                    txtdni?.setText(dni2)
                    edtxtdni?.setText(dni2)
                    Glide.with(this)
                        .load(image)
                        .centerCrop()
                        .into(viewimageperfil)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { error ->
            }
        )
        queue.add(jsonObjectRequest)
    }
    fun Back(view: View){
        finish()
    }
    fun ClickHistorial(view: View){
        val intent = Intent(this, HistorialActivity::class.java)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        intent.putExtra("url",url)
        startActivity(intent)
    }
}