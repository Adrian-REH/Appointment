package app.ibiocd.odontologia

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import app.ibiocd.lavanderia.Adapter.*
import app.ibiocd.odontologia.Adapter.AdapterArchivo
import app.ibiocd.odontologia.Adapter.AdapterHorarios
import app.ibiocd.odontologia.Adapter.AdapterPrestacion
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_prestaciones.*
import kotlinx.android.synthetic.main.activity_turno.*
import kotlinx.android.synthetic.main.activity_turno.rviewcliente
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class PrestacionesActivity : AppCompatActivity(), AdapterPrestacion.onPrestacionItemClick {
    private var JSONAgregadoPrestacion = ""
    private var JSONCompletPrestacion = ""
     //var correo = ""
     var IDP = ""
     var url = ""
    val arraylisP= ArrayList<Prestacion>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestaciones)
        if(intent.extras !=null){
            JSONCompletPrestacion = intent.getStringExtra("prestacion").toString()
            IDP = intent.getStringExtra("IDP").toString()
            url = intent.getStringExtra("url").toString()

            prestbacktext.setText(intent.getStringExtra("back").toString())

            if (JSONCompletPrestacion!=""){
                refresh(intent.getStringExtra("prestacion").toString())

            }else{
                JSONAgregadoPrestacion = "{\n" +
                        "\"Prestacion\": \"S/N\",\n" +
                        "\"Detalle\": \"S/N\"\n" +
                        "}"
                JSONCompletPrestacion="{\"dato\":[$JSONAgregadoPrestacion]}"
            }
        }

    }

    private fun refresh(response:String){
        arraylisP.clear()
        JSONAgregadoPrestacion=""
        val jsonvalor=JSONObject(response)
        val HistJSONArray=jsonvalor.getJSONArray("dato")


        if (HistJSONArray.length()>0){

            for (i in 0 until HistJSONArray.length()){
                val jsonObject2= HistJSONArray.getJSONObject(i)
                var JSONArchivo="{\n" +
                        "\"Prestacion\": \"${jsonObject2.getString("Prestacion")}\",\n" +
                        "\"Detalle\": \"${jsonObject2.getString("Detalle")}\"\n" +
                        "}"

                if (JSONAgregadoPrestacion==""){
                    JSONAgregadoPrestacion = JSONArchivo
                }else if (JSONAgregadoPrestacion!=""){
                    JSONAgregadoPrestacion = JSONAgregadoPrestacion+","+JSONArchivo
                }
                arraylisP.add(Prestacion(jsonObject2.getString("Prestacion"),jsonObject2.getString("Detalle")))
            }
            JSONCompletPrestacion="{\"dato\":[$JSONAgregadoPrestacion]}"





        }else{
            arraylisP.clear()
        }
        val adapterprestacion = AdapterPrestacion(arraylisP, this, this)
        rviewcliente?.layoutManager = GridLayoutManager(this,2)
        rviewcliente?.adapter = adapterprestacion


    }
    fun Back(view: View){
        finish()
    }

    fun PrestacionDialogSee(nombre:String,response:String){
        val builder= AlertDialog.Builder(this)
        builder.setTitle(nombre)
        val view=layoutInflater.inflate(R.layout.dialog_prestacion,null)
        val prestdetalle=view.findViewById<TextView>(R.id.prestaciondetalle)



        val textdet=view.findViewById<TextInputLayout>(R.id.textdet)
        val textprest=view.findViewById<TextInputLayout>(R.id.textprest)//TEXT INPUT LAYOUT

        val detallesdate=view.findViewById<CardView>(R.id.detallesdate)

        val edtxtdet=view.findViewById<EditText>(R.id.edtxtdet)
        val edtxtprest=view.findViewById<EditText>(R.id.edtxtprest)

        val jsonvalor=JSONObject(response)

        var b="dd"
        val HistJSONArray=jsonvalor.getJSONArray("dato")
        for (i in 0 until HistJSONArray.length()){
            var jsonObject2= HistJSONArray.getJSONObject(i)
            if (nombre==jsonObject2.getString("Prestacion").toString()){

                prestdetalle.setText(jsonObject2.getString("Detalle").toString())
                edtxtdet.setText(jsonObject2.getString("Detalle").toString())
            }

        }
        textdet.visibility = View.GONE
        textprest.visibility = View.GONE
        prestdetalle.visibility = View.VISIBLE
        detallesdate.setOnClickListener {
            prestdetalle.visibility = View.GONE
            textdet.visibility = View.VISIBLE

        }


       // prestacionname.setText(nombre)

        builder.setView(view)

        builder.setNeutralButton("Guardar", DialogInterface.OnClickListener { dialogInterface, i ->
            agregarJson(nombre,edtxtdet.text.toString())
        })

        builder.setNegativeButton("Borrar", DialogInterface.OnClickListener { dialogInterface, i ->
            DeletJson(nombre)
        })
        builder.setPositiveButton("Close", DialogInterface.OnClickListener { dialogInterface, i ->
           // refresh(JSONCompletPrestacion)
        })

        builder.show()
    }//DIALOGO PARA VER EL JSON
    fun ClickCrear(view: View){
        val builder= AlertDialog.Builder(this)
        val view =layoutInflater.inflate(R.layout.dialog_prestacion,null)
        val prestdetalle=view.findViewById<TextView>(R.id.prestaciondetalle)
        builder.setTitle("Ingrese un Servicio")

        val textdet=view.findViewById<TextInputLayout>(R.id.textdet)
        val textprest=view.findViewById<TextInputLayout>(R.id.textprest)//TEXT INPUT LAYOUT

        val edtxtdet=view.findViewById<EditText>(R.id.edtxtdet)
        val edtxtprest=view.findViewById<EditText>(R.id.edtxtprest)

        val solodetalle=view.findViewById<TextView>(R.id.solodetalle)
        prestdetalle.visibility = View.GONE
        solodetalle.visibility = View.GONE

        textdet.visibility = View.VISIBLE
        textprest.visibility = View.VISIBLE

        builder.setView(view)
        builder.setPositiveButton("Crear", DialogInterface.OnClickListener { dialogInterface, i ->
            agregarJson(edtxtprest.text.toString(),edtxtdet.text.toString())



        })
        builder.setNegativeButton("Close", DialogInterface.OnClickListener { dialogInterface, i ->

        })
        builder.show()
    } //DIALOGO PARA CREAR JSON

    override fun onPrestacionItemClick(nombre: String) {
        PrestacionDialogSee(nombre,JSONCompletPrestacion)
    } //CLICK EN EL RECYLCER VIEW
    fun DeletJson(nombre:String){
        JSONAgregadoPrestacion=""
        try{
            val jsonvalor = JSONObject(JSONCompletPrestacion)
            var jsonArray = jsonvalor.getJSONArray("dato")
            val info= jsonvalor.getString("dato")


            if(jsonArray.length()>0){
                for (i in 0 until jsonArray.length()){
                    var jsonObject= jsonArray.getJSONObject(i)

                    if(jsonObject.getString("Prestacion").toString()==nombre){


                    }else if(jsonObject.getString("Prestacion").toString()!=nombre){
                        var JSONArchivo="{\n" +
                                "\"Prestacion\": \"${jsonObject.getString("Prestacion")}\",\n" +
                                "\"Detalle\": \"${jsonObject.getString("Detalle")}\"\n" +
                                "}"

                        if (JSONAgregadoPrestacion==""){
                            JSONAgregadoPrestacion = JSONArchivo
                        }else if (JSONAgregadoPrestacion!=""){
                            JSONAgregadoPrestacion = JSONAgregadoPrestacion+","+JSONArchivo
                        }
                    }else {
                        Toast.makeText(this,"No se pudo Borrar", Toast.LENGTH_SHORT).show()}
                }
            }

            JSONCompletPrestacion="{\"dato\":[$JSONAgregadoPrestacion]}"

            refresh(JSONCompletPrestacion)
            getProfesionalSave(JSONCompletPrestacion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    } //BORRO UN ARCHIVO DEL JSON, DE LA LISTA Y DEL SERVIDOR
    fun agregarJson(nombre:String,detalle:String){
        var JSONArchivo="{\n" +
                "\"Prestacion\": \"${nombre}\",\n" +
                "\"Detalle\": \"${detalle}\"\n" +
                "}"

        if (JSONAgregadoPrestacion==""){
            JSONAgregadoPrestacion = JSONArchivo
        }else if (JSONAgregadoPrestacion!=""){JSONAgregadoPrestacion = JSONAgregadoPrestacion+","+JSONArchivo }
        JSONCompletPrestacion="{\"dato\":[$JSONAgregadoPrestacion]}"

        getProfesionalSave(JSONCompletPrestacion)
    } //AGREGO LOS DATOS PARA UN NUEVO JSON






    fun getProfesionalSave(JSON: String){
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.getProfesional(IDP.toString())
            val datos: ProfesionalRespons? =call.body()
            runOnUiThread{
                if(call.isSuccessful){
                    if (datos!=null){
                        postProfesional(datos,JSON)
                    }
                }
            }
        }
    }//
    private fun postProfesional(datos: ProfesionalRespons,JSON:String) {

        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postProfesional("${datos.nameprof}","${datos.col}","${datos.especialidad}","${datos.celular}","${datos.direccion}","${datos.correo}","${datos.horarios}","$JSON","${datos.verificar}","${datos.img}","${datos.matricula}","${datos.TID}","$IDP","modificar","modificar")
            call.enqueue(object : Callback<ProfesionalRespons> {
                override fun onFailure(call: Call<ProfesionalRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<ProfesionalRespons>, response: retrofit2.Response<ProfesionalRespons>) {

                    DeletJson("S/N")

                }
            })



        }
    }
    fun SaveHistorial(){

        JSONCompletPrestacion="{\"dato\":[$JSONAgregadoPrestacion]}"


        val queue = Volley.newRequestQueue(this)
        val url2 = "http://$url/cuentas.php?idprofesional=${IDP}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url2,null,
            { response ->
                val jsonArray = response.getJSONArray("data")
                val jsonObject = jsonArray.getJSONObject(0)
                val url = "http://$url/cuentas.php"
                val queue2= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                var resultadoPost = object : StringRequest(Method.POST,url,
                    Response.Listener<String> { response ->

                        DeletJson("S/N")

                    }, Response.ErrorListener { error ->

                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        parametros.put("modificar","modificar")
                        parametros.put("correo",jsonObject.getString("correo").toString())
                        parametros.put("celular",jsonObject.getString("celular").toString())
                        parametros.put("direccion",jsonObject.getString("direccion").toString())
                        parametros.put("nombreapellido",jsonObject.getString("nombreapellido").toString())
                        parametros.put("prestaciones",JSONCompletPrestacion)
                        parametros.put("horarios",jsonObject.getString("horarios").toString())
                        parametros.put("especialidad",jsonObject.getString("especialidad").toString())
                        parametros.put("img",jsonObject.getString("img").toString())
                        parametros.put("idprofesional",IDP)

                        return parametros
                    }
                }
                // con esto envio o SEND todo
                queue2.add(resultadoPost)

            }, { error ->

            }
        )
        queue.add(jsonObjectRequest)


    }//Guarda los datos de la lista de PRESTACION


    fun Save(JSON: String){
        //Primero Verifico si esta registrado el TOKEN ID
        val queue = Volley.newRequestQueue(this)
        val url2 = "http://$url/cuentas.php?idprofesional=${IDP}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url2,null,
            { response ->
                val jsonArray = response.getJSONArray("data")
                val jsonObject = jsonArray.getJSONObject(0)
                val url = "http://$url/cuentas.php"
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
                        parametros.put("correo",jsonObject.getString("correo").toString())
                        parametros.put("celular",jsonObject.getString("celular").toString())
                        parametros.put("direccion",jsonObject.getString("direccion").toString())
                        parametros.put("nombreapellido",jsonObject.getString("nombreapellido").toString())
                        parametros.put("prestaciones",JSON)
                        parametros.put("horarios",jsonObject.getString("horarios").toString())
                        parametros.put("especialidad",jsonObject.getString("especialidad").toString())
                        parametros.put("img",jsonObject.getString("img").toString())
                        parametros.put("modificar","modificar")
                        return parametros
                    }
                }
                // con esto envio o SEND todo
                queue.add(resultadoPost)

            }, { error ->

            }
        )
        queue.add(jsonObjectRequest)

    } //GUARDO EN EL SERVIDOR
    fun ClickSave(view: View){
        //Primero Verifico si esta registrado el TOKEN ID
        val queue = Volley.newRequestQueue(this)
        val url2 = "http://$url/cuentas.php?idprofesional=${IDP}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url2,null,
            { response ->
                val jsonArray = response.getJSONArray("data")
                val jsonObject = jsonArray.getJSONObject(0)
                val url3 = "http://$url/cuentas.php"
                val queue3= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                val URL=url
                var resultadoPost = object : StringRequest(Method.POST,url3,
                    Response.Listener<String> { response ->

                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){

                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        parametros.put("correo",jsonObject.getString("correo").toString())
                        parametros.put("celular",jsonObject.getString("celular").toString())
                        parametros.put("direccion",jsonObject.getString("direccion").toString())
                        parametros.put("nombreapellido",jsonObject.getString("nombreapellido").toString())
                        parametros.put("prestaciones","")
                        parametros.put("horarios",jsonObject.getString("horarios").toString())
                        parametros.put("especialidad",jsonObject.getString("especialidad").toString())
                        parametros.put("img",jsonObject.getString("img").toString())
                        parametros.put("modificar","modificar")
                        return parametros
                    }
                }
                // con esto envio o SEND todo
                queue3.add(resultadoPost)

            }, { error ->

            }
        )
        queue.add(jsonObjectRequest)

    }
fun borrar(){
    for ((i,hora) in arraylisP.distinct().withIndex()){

        arraylisP.add(Prestacion(hora.toString(),""))

    }
}
}