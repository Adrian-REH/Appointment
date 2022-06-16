package app.ibiocd.odontologia

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.lavanderia.Adapter.Odontograma
import app.ibiocd.odontologia.Adapter.AdapterDientInfError
import app.ibiocd.odontologia.Adapter.AdapterDientSupError
import app.ibiocd.odontologia.Adapter.AdapterHorarios
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_odontograma.*
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_turno.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Error
import kotlinx.android.synthetic.main.activity_turno.txteprestacion as txteprestacion1

class OdontogramaActivity : AppCompatActivity(),AdapterDientInfError.onDientInfItemClick,AdapterDientSupError.onDientSupItemClick,
    AdapterView.OnItemClickListener {
    var name:String?=""
    var url:String?=""
    var correo:String?=""
    var dni:String?=""
    var Ndient:String?=""
    var JSONDiente:String?=""
    var JSONODONTOGRAMA: JSONObject? =null

    val arraylistOdnt= ArrayList<Odontograma>()
    val arraylisError= ArrayList<Odontograma>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_odontograma)

        for (j in 1 until 15) {
            arraylisError.add(Odontograma("http://$url/docs/dientes/inf$j.png","http://$url/docs/dientes/sup$j.png"))

        }
        val arrayList32=ArrayList<String>()
        for (i in 1 until 32) {
            arrayList32.add("$i")
        }

        if(intent.extras !=null){
            dni = intent.getStringExtra("dni")
            correo = intent.getStringExtra("correo")
            url = intent.getStringExtra("url")
            name = intent.getStringExtra("name")


        }
        ClickRefresh(View(applicationContext))


        val arrayAdapter= ArrayAdapter(this,R.layout.dropdown_item,arrayList32)
        with(txteprestacion){
            setAdapter(arrayAdapter)
            onItemClickListener = this@OdontogramaActivity

        }
    }


    fun Back(view: View){
        val intent = Intent(this, TurnoActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        intent.putExtra("name",name)
        intent.putExtra("JSONODONT",JSONODONTOGRAMA.toString())
        startActivity(intent)
        finish()
    }

    fun ClickImage(view: View){

    }
    fun ClickAyuda(view: View){

    }
    fun ClickRefresh(view: View){
        val queue = Volley.newRequestQueue(this)
        val url3 = "http://$url/odontograma.php?dni=${dni}&codigoprofesional=${correo}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url3,null,
            { response ->
                try {
                    JSONODONTOGRAMA=response
                    for (j in 1 until 32) {
                            val JsonOdont=JSONObject(response.getString("d$j").toString())
                            arraylistOdnt.add(Odontograma(JsonOdont.getString("url1").toString(),JsonOdont.getString("url2").toString()))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { error ->

                val url = "http://$url/odontograma.php"
                val queue= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                var resultadoPost = object : StringRequest(
                    Method.POST,url,
                    Response.Listener<String> { response ->
                        ClickRefresh(View(applicationContext))
                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        for (j in 1 until 32) {
                            parametros.put("d$j","{\n" +
                                    " \"url1\": \"http://23herrera.xyz:81/appointment/docs/dientes/inf$j.png\",\n" +
                                    " \"url2\": \"http://23herrera.xyz:81/appointment/docs/dientes/sup$j.png\"\n" +
                                    " }")
                        }


                        parametros.put("dni",dni.toString())
                        parametros.put("codigoprofesional",correo.toString())
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

    fun ClickSave(view: View){


        //Primero Verifico si esta registrado el TOKEN ID
        val queue = Volley.newRequestQueue(this)
        val url2 = "http://$url/odontograma.php?dni=${dni}&codigoprofesional=${correo}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url2,null,
            { response ->
                //Toast.makeText(this,"$JSONCompletHistorial",Toast.LENGTH_SHORT).show()
                val jsonArray = response.getJSONArray("data")
                val jsonObject = jsonArray.getJSONObject(0)
                val url = "http://$url/odontograma.php"
                val queue= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                var resultadoPost = object : StringRequest(
                    Method.POST,url,
                    Response.Listener<String> { response ->

                        //SaveHistorial()
                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        for (j in 1 until 32) {
                            if(Ndient== "$j"){
                                parametros.put("d$j",JSONDiente.toString())

                            }
                            parametros.put("d$j",response?.getString("d$j").toString())
                        }
                        parametros.put("dni",dni.toString())
                        parametros.put("codigoprofesional",correo.toString())
                        parametros.put("modificar","modificar")
                        return parametros
                    }
                }
                // con esto envio o SEND todo
                queue.add(resultadoPost)


            }, { error ->

                val url = "http://$url/odontograma.php"
                val queue= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                var resultadoPost = object : StringRequest(
                    Method.POST,url,
                    Response.Listener<String> { response ->
                        ClickRefresh(View(applicationContext))
                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        for (j in 1 until 32) {
                            if(Ndient== "$j"){
                                parametros.put("d$j",JSONDiente.toString())

                            }
                            parametros.put("d$j",JSONODONTOGRAMA?.getString("d$j").toString())
                        }
                        parametros.put("dni",dni.toString())
                        parametros.put("codigoprofesional",correo.toString())
                        parametros.put("insertar","insertar")
                        return parametros
                    }
                }
                // con esto envio o SEND todo
                queue.add(resultadoPost)

            }
        )
        queue.add(jsonObjectRequest)

    }//Guarda los datos del Turno y LLama a Save Historial


    fun dialog2(view: View){
            val builder= AlertDialog.Builder(this)
            builder.setTitle("Diente Sup")
            val view=layoutInflater.inflate(R.layout.dialog_fallos,null)
            val listerror=view.findViewById<RecyclerView>(R.id.horaini)

            val adaptercliente = AdapterDientSupError(arraylisError, this, this)
            listerror?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            listerror?.adapter = adaptercliente


            builder.setPositiveButton("Guardar", DialogInterface.OnClickListener { dialogInterface, i ->
                ClickSave(View(applicationContext))

            })
            builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            builder.show()

    }
    fun dialog1(view: View){
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Diente Inferior")
        val view=layoutInflater.inflate(R.layout.dialog_fallos,null)
        val listerror=view.findViewById<RecyclerView>(R.id.horaini)

        val adaptercliente = AdapterDientSupError(arraylisError, this, this)
        listerror?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        listerror?.adapter = adaptercliente


        builder.setPositiveButton("Guardar", DialogInterface.OnClickListener { dialogInterface, i ->
            ClickSave(View(applicationContext))

        })
        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i ->

        })
        builder.show()    }

    override fun onDientInfItemClick(url1: String) {
        val JsonOdont=JSONObject(JSONODONTOGRAMA?.getString("d$Ndient").toString())
        JSONDiente= "{\n" +
                " \"url1\": \"$url1\",\n" +
                " \"url2\": \"${JsonOdont.getString("url2")}\"\n" +
                " }"

        Glide.with(this)
            .load(JsonOdont.getString(url1).toString())
            .centerCrop()
            .into(dientesup)
    }

    override fun onDientSupItemClick(url2: String) {
        val JsonOdont=JSONObject(JSONODONTOGRAMA?.getString("d$Ndient").toString())
        JSONDiente= "[{\n" +
                " \"url1\": \"${JsonOdont.getString("url2")}\",\n" +
                " \"url2\": \"${url2}\"\n" +
                " }]"
        Glide.with(this)
            .load(JsonOdont.getString(url2).toString())
            .centerCrop()
            .into(dientesup)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Ndient = parent?.getItemAtPosition(position).toString()

            val JsonOdont=JSONObject(JSONODONTOGRAMA?.getString("d$Ndient").toString())
            Glide.with(this)
                .load(JsonOdont.getString("url1").toString())

                .into(dienteinf)
            Glide.with(this)
                .load(JsonOdont.getString("url2").toString())

                .into(dientesup)


    }

}

