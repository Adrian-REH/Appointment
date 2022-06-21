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
import app.ibiocd.lavanderia.Adapter.OdontogramaRespons
import app.ibiocd.lavanderia.Adapter.TurnoRespons
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.lang.Error
import java.lang.Exception
import kotlinx.android.synthetic.main.activity_turno.txteprestacion as txteprestacion1

class OdontogramaActivity : AppCompatActivity(),AdapterDientInfError.onDientInfItemClick,AdapterDientSupError.onDientSupItemClick,
    AdapterView.OnItemClickListener {
    var name:String?=""
    var url:String?=""
    var correo:String?=""
    var dni:String?=""
    var Ndient:String?=""
    var codigo:Int=0
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
            codigo = intent.getStringExtra("codigo")?.toInt() ?: 0

            if (codigo==0){
                for (j in 1 until 33) {
                    JSONDiente= ("{\n" +
                            " \"url1\": \"http://23herrera.xyz:81/appointment/docs/dientes/inf$j.png\",\n" +
                            " \"url2\": \"http://23herrera.xyz:81/appointment/docs/dientes/sup$j.png\"\n" +
                            " }")
                    JSONODONTOGRAMA = JSONObject("{\n" +
                            " \"d$j\": \"${JSONDiente}\"\n" +
                            " }")
                }
            }


        }
        getOdontogramaDatos(View(applicationContext))


        val arrayAdapter= ArrayAdapter(this,R.layout.dropdown_item,arrayList32)
        with(txteprestacion){
            setAdapter(arrayAdapter)
            onItemClickListener = this@OdontogramaActivity

        }
    }

    fun getOdontogramaDatos(view: View){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call=RetrofitClient.instance.getOdontograma(dni.toString(),correo.toString())
                val datos: List<OdontogramaRespons>? =call.body()
                runOnUiThread{
                    if(call.isSuccessful){
                        for (i in 0 until datos?.size!!){
                            if(codigo==datos[i].ID){
                                //BUSCAR EL ARRAY LOS ODONTOGRAMAS Y DETERMINAR EL IDENTIFICADOR
                                try {
                                        JSONODONTOGRAMA=JSONObject(datos[i].dientes)
                                    for (j in 1 until 32) {
                                        val JsonOdont=JSONObject(JSONODONTOGRAMA?.getString("d$j").toString())
                                        arraylistOdnt.add(Odontograma(JsonOdont.getString("url1").toString(),JsonOdont.getString("url2").toString()))
                                    }

                                      /*  arraylistOdnt.add(Odontograma(JSONObject(datos[i].d1).getString("url1").toString(),JSONObject(datos[i].d1).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d2).getString("url1").toString(),JSONObject(datos[i].d2).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d3).getString("url1").toString(),JSONObject(datos[i].d3).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d4).getString("url1").toString(),JSONObject(datos[i].d4).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d5).getString("url1").toString(),JSONObject(datos[i].d5).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d6).getString("url1").toString(),JSONObject(datos[i].d6).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d7).getString("url1").toString(),JSONObject(datos[i].d7).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d8).getString("url1").toString(),JSONObject(datos[i].d8).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d9).getString("url1").toString(),JSONObject(datos[i].d9).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d10).getString("url1").toString(),JSONObject(datos[i].d10).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d11).getString("url1").toString(),JSONObject(datos[i].d11).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d12).getString("url1").toString(),JSONObject(datos[i].d12).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d13).getString("url1").toString(),JSONObject(datos[i].d13).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d14).getString("url1").toString(),JSONObject(datos[i].d14).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d15).getString("url1").toString(),JSONObject(datos[i].d15).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d16).getString("url1").toString(),JSONObject(datos[i].d16).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d17).getString("url1").toString(),JSONObject(datos[i].d17).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d18).getString("url1").toString(),JSONObject(datos[i].d18).getString("url2").toString()))
+                                       arraylistOdnt.add(Odontograma(JSONObject(datos[i].d19).getString("url1").toString(),JSONObject(datos[i].d19).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d20).getString("url1").toString(),JSONObject(datos[i].d20).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d21).getString("url1").toString(),JSONObject(datos[i].d21).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d22).getString("url1").toString(),JSONObject(datos[i].d22).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d23).getString("url1").toString(),JSONObject(datos[i].d23).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d24).getString("url1").toString(),JSONObject(datos[i].d24).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d25).getString("url1").toString(),JSONObject(datos[i].d25).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d26).getString("url1").toString(),JSONObject(datos[i].d26).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d27).getString("url1").toString(),JSONObject(datos[i].d27).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d28).getString("url1").toString(),JSONObject(datos[i].d28).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d29).getString("url1").toString(),JSONObject(datos[i].d29).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d31).getString("url1").toString(),JSONObject(datos[i].d31).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d31).getString("url1").toString(),JSONObject(datos[i].d31).getString("url2").toString()))
                                        arraylistOdnt.add(Odontograma(JSONObject(datos[i].d32).getString("url1").toString(),JSONObject(datos[i].d32).getString("url2").toString()))

*/
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                            }else{
                                codigo=0

                            }
                        }


                    }else{



                    }
                }
            }catch (e: Exception){

            }


        }

    }//
    fun getOdontogramaSave(view: View){
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val call=RetrofitClient.instance.getOdontograma(dni.toString(),correo.toString())
                val datos: List<OdontogramaRespons>? =call.body()
                if (datos != null) {
                    for (i in 0 until datos.size){
                        if(call.isSuccessful){
                            if (codigo==0){
                                postOdontograma("insertar")

                            }else{
                                postOdontograma("modificar")

                            }

                        }else{
                            postOdontograma("insertar")
                        }
                    }
                }


            }catch (e:Exception){
                postOdontograma("insertar")

            }




        }
    }//
    private fun postOdontograma(accion:String) {

        for (j in 1 until 33) {
            if(Ndient== "$j"){
                JSONODONTOGRAMA = JSONObject("{\n" +
                        " \"d$j\": \"$JSONDiente\"\n" +
                        " }")
            }
            JSONODONTOGRAMA = JSONObject("{\n" +
                    " \"d$j\": \"${JSONODONTOGRAMA?.getString("d$j")}\"\n" +
                    " }")
        }


        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postOdontograma(JSONODONTOGRAMA.toString(),codigo.toInt(),"${correo}","${dni}",accion,accion)
            call.enqueue(object : Callback<OdontogramaRespons> {
                override fun onFailure(call: Call<OdontogramaRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<OdontogramaRespons>, response: retrofit2.Response<OdontogramaRespons>) {
                    getOdontogramaDatos(View(applicationContext))

                }
            })



        }
    }



    fun dialog2(view: View){
            val builder= AlertDialog.Builder(this)
            builder.setTitle("Diente Sup")
            val view=layoutInflater.inflate(R.layout.dialog_fallos,null)
            val listerror=view.findViewById<RecyclerView>(R.id.horaini)

            val adaptercliente = AdapterDientSupError(arraylisError, this, this)
            listerror?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            listerror?.adapter = adaptercliente


            builder.setPositiveButton("Guardar", DialogInterface.OnClickListener { dialogInterface, i ->
                getOdontogramaSave(View(applicationContext))

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
            getOdontogramaSave(View(applicationContext))

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
    fun ClickImage(view: View){

    }
    fun ClickAyuda(view: View){

    }
    fun Back(view: View){
        if(name!=""){
            val intent = Intent(this, TurnoActivity::class.java)
            intent.putExtra("url",url)
            intent.putExtra("correo",correo)
            intent.putExtra("dni",dni)
            intent.putExtra("name",name)
            intent.putExtra("JSONODONT",JSONODONTOGRAMA.toString())
            startActivity(intent)

        }

        finish()
    }

}

