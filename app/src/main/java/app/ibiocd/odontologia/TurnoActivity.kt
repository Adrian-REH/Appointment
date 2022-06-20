package app.ibiocd.odontologia

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import app.ibiocd.lavanderia.Adapter.Archivos
import app.ibiocd.lavanderia.Adapter.TurnoRespons
import app.ibiocd.lavanderia.FileDataPart
import app.ibiocd.lavanderia.VolleyFileUploadRequest
import app.ibiocd.odontologia.Adapter.AdapterArchivo
import app.ibiocd.odontologia.Adapter.ArchivoController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_turno.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException

class TurnoActivity : AppCompatActivity(), AdapterArchivo.onArchivoItemClick {
    private val SELECT_ACTIVITY=50
    private var ARCHIVONAME = ""
    private var ESPECIALIDAD = ""
    var a:Int=0
    var fecha=0
    var id=0

    var odont:Boolean=false
    var JSONAgregadoArchivo:String=""
    var JSONAgregadoOdontograma:String=""

    var JSONCompletArchivos:String=""

    var JSONODONTOGRAMA: JSONObject? =null
    lateinit var datos: List<TurnoRespons>

    private var imageData: ByteArray? = null

    var url:String?=""
    var IMG:String?=""
    var correo:String?=""
    var dni:String?=""
    var name:String?=""
    var MATRI:String?=""
    val arraylisFecha= ArrayList<String>()
    val arraylisHora= ArrayList<String>()

    val arraylisP= ArrayList<String>()
    val arraylisPres= ArrayList<String>()
    val displayListC=ArrayList<Archivos>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turno)
        btnodontologia.visibility=View.GONE

        if(intent.extras !=null){
            dni = intent.getStringExtra("dni")
            correo = intent.getStringExtra("correo")
            url = intent.getStringExtra("url")
            name = intent.getStringExtra("name")
            fecha=intent.getStringExtra("fecha").toString().toInt()


            id=intent.getStringExtra("id").toString().toInt()
            if (id!=0){
                getListTurnos()

            }

            if (intent.getStringExtra("JSONODONT")!=""){
                odont=true
                JSONODONTOGRAMA= JSONObject(intent.getStringExtra("JSONODONT").toString())
                for (i in 1 until 32){
                    var JSON="\"d$i\": ${JSONODONTOGRAMA?.getString("d$i").toString()}\n"


                    if (JSONAgregadoOdontograma==""){
                        JSONAgregadoOdontograma = JSON
                    }else if (JSONAgregadoOdontograma!=""){JSONAgregadoOdontograma = JSONAgregadoOdontograma+","+JSON }

                }

                //ClickAgregarJson(View(applicationContext))
            }





        }

    }



    fun ClickOdontograma(view: View,codigo:String){
        getTurnoSave(View(applicationContext))
        val intent = Intent(this, OdontogramaActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        intent.putExtra("name",name)
        intent.putExtra("codigo",codigo)
        startActivity(intent)
        finish()

    }

    private fun getListTurnos(){

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val call=RetrofitClient.instance.getListTurnodc(dni.toString(),correo.toString())
                val datos: List<TurnoRespons>? =call.body()
                runOnUiThread{

                    for (i in 0 until datos?.size!!){

                        if(call.isSuccessful){
                            if (id==datos[i].ID.toInt()){

                                val namep = datos[i].namepac
                                val coment = datos[i].comentario
                                val img = datos[i].img
                                val espc = datos[i].especialidad
                                val prest = datos[i].nameprof

                                txthora.text=datos[i].hora
                                txtdia.text=datos[i].fecha
                                txtespecialidad?.setText(espc)

                                if(espc=="ODONTOLOGIA"){
                                    btnodontologia.visibility=View.VISIBLE
                                }

                                txtepaciente?.setText(namep)
                                txteprofesional?.setText(name)
                                txteprestacion?.setText(prest)
                                Glide.with(applicationContext)
                                    .load(img)
                                    .centerCrop()
                                    .into(viewimageperfilc)
                                txtcomentario?.text = coment

                                ESPECIALIDAD=espc
                                if (datos[i].archivo1!=""){
                                    displayListC.add(Archivos(JSONObject(datos[i].archivo1).getString("Nombre").toString(), JSONObject(datos[i].archivo1).getString("Url").toString()))
                                }else if (datos[i].archivo2!=""){
                                    displayListC.add(Archivos(JSONObject(datos[i].archivo2).getString("Nombre").toString(), JSONObject(datos[i].archivo2).getString("Url").toString()))

                                }else if (datos[i].archivo3!=""){
                                    displayListC.add(Archivos(JSONObject(datos[i].archivo3).getString("Nombre").toString(), JSONObject(datos[i].archivo3).getString("Url").toString()))

                                }else if (datos[i].archivo4!=""){
                                    displayListC.add(Archivos(JSONObject(datos[i].archivo4).getString("Nombre").toString(), JSONObject(datos[i].archivo4).getString("Url").toString()))

                                }else if (datos[i].archivo5!=""){
                                    displayListC.add(Archivos(JSONObject(datos[i].archivo5).getString("Nombre").toString(), JSONObject(datos[i].archivo5).getString("Url").toString()))

                                }
                                if (datos[i].estado=="C"){
                                    floatcanc?.visibility=View.GONE
                                }

                                val adapterarchivo = AdapterArchivo(displayListC, applicationContext, this@TurnoActivity)
                                rviewcliente?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
                                rviewcliente?.adapter = adapterarchivo
                                rviewcliente.visibility=View.VISIBLE
                                odont=true

                               // Historial(response.getString("fecha").toString())
                            }
                        }else{
                            Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                        }
                    }


                }

            }catch (e:Exception){

            }

        }
    }//
    fun ClickPerfilPaciente(view: View){
        val intent = Intent(this, PacienteActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        intent.putExtra("name",name)
        startActivity(intent)
    }
    fun Back(view: View){
        finish()
    }
    //----------------------------------------------------------------------------------------------Verifica si existe el turno crea una lista y compara la fecha
    fun getTurnoSave(view: View){
        if (odont){
            var JSONArchivo="{\n" +
                    "\"Nombre\": \"ODONTOGRAMA\",\n" +
                    "\"Url\": ${"CODIGO DEL ODONTOGRAMA"}\n" +
                    "}"

            if (JSONAgregadoArchivo==""){
                JSONAgregadoArchivo = JSONArchivo
            }else if (JSONAgregadoArchivo!=""){JSONAgregadoArchivo = JSONAgregadoArchivo+","+JSONArchivo }
            JSONCompletArchivos="{\"Archivos\":[$JSONAgregadoArchivo]}"
        }
    if (ARCHIVONAME !=""){
            var JSONArchivo="{\n" +
                    "\"Nombre\": \"${ARCHIVONAME}\",\n" +
                    "\"Url\": \"http://$url/docs/$ARCHIVONAME\"\n" +
                    "}"

            if (JSONAgregadoArchivo==""){
                JSONAgregadoArchivo = JSONArchivo
            }else if (JSONAgregadoArchivo!=""){JSONAgregadoArchivo = JSONAgregadoArchivo+","+JSONArchivo }
            JSONCompletArchivos="{\"Archivos\":[$JSONAgregadoArchivo]}"

        }
    CoroutineScope(Dispatchers.IO).launch {
            try{
                val call=RetrofitClient.instance.getListTurnodc(dni.toString(),correo.toString())
                val BDatos: List<TurnoRespons>? =call.body()
                if (BDatos != null) {
                    datos=BDatos
                    for (i in 0 until BDatos.size){
                        if(call.isSuccessful){
                                if (id==BDatos[i].ID.toInt()){
                                    if (BDatos[i].archivo1!=""){
                                        postCliente("modificar",JSONAgregadoArchivo,BDatos[i].archivo2,BDatos[i].archivo3,BDatos[i].archivo4,BDatos[i].archivo5,BDatos[i].estado)

                                    }else if (BDatos[i].archivo2!=""){
                                        postCliente("modificar",BDatos[i].archivo1,JSONAgregadoArchivo,BDatos[i].archivo3,BDatos[i].archivo4,BDatos[i].archivo5,BDatos[i].estado)

                                    }else if (BDatos[i].archivo3!=""){
                                        postCliente("modificar",BDatos[i].archivo1,BDatos[i].archivo2,JSONAgregadoArchivo,BDatos[i].archivo4,BDatos[i].archivo5,BDatos[i].estado)

                                    }else if (BDatos[i].archivo4!=""){
                                        postCliente("modificar",BDatos[i].archivo1,BDatos[i].archivo2,BDatos[i].archivo3,JSONAgregadoArchivo,BDatos[i].archivo5,BDatos[i].estado)

                                    }else if (BDatos[i].archivo5!=""){
                                        postCliente("modificar",BDatos[i].archivo1,BDatos[i].archivo2!!,BDatos[i].archivo3,BDatos[i].archivo4,JSONAgregadoArchivo,BDatos[i].estado)

                                    }
                                    //postCliente("modificar",JSONCompletArchivos,estado)



                                }else{
                                    postCliente("insertar","","","","","","E")

                                }

                        }else{
                            postCliente("insertar","","","","","","E")
                            //postCliente("insertar","","E")
                        }
                    }
                }


            }catch (e: java.lang.Exception){
                postCliente("insertar","","","","","","E")
                //postCliente("insertar","","E")

            }




        }
    }//
    //----------------------------------------------------------------------------------------------Guarda los datos del Turno
    private fun postCliente(accion:String,archivo1:String,archivo2:String,archivo3:String,archivo4:String,archivo5:String,estado:String) {

        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postTurno(correo.toString(),name.toString(),txteprofesional.text.toString(),txtespecialidad.text.toString(),txteprestacion.text.toString(),dni.toString(),fecha.toString(),"${txthora.text.toString()}",txtcomentario.text.toString(),estado,IMG.toString(),archivo1,archivo2,archivo3,archivo4,archivo5,accion,accion)
            //val call=RetrofitClient.instance.postTurno(correo.toString(),name.toString(),txteprofesional.text.toString(),txtespecialidad.text.toString(),txteprestacion.text.toString(),dni.toString(),fecha.toString(),"${txthora.text.toString()}",txtcomentario.text.toString(),estado,IMG.toString(),JSONCompletArchivos,accion,accion)
            call.enqueue(object : Callback<TurnoRespons> {
                override fun onFailure(call: Call<TurnoRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<TurnoRespons>, response: retrofit2.Response<TurnoRespons>) {
                    Toast.makeText(applicationContext,"El Turno se $accion ", Toast.LENGTH_LONG).show()
                    arraylisP.clear()
                    arraylisPres.clear()
                    if (id==0){
                        finish()
                    }

                }
            })



        }
    }

    /*
        fun ClickRefresh(){
            val queue = Volley.newRequestQueue(this)
            val url3 = "http://$url/turno.php?dni=${dni}&correoprofesional=${correo}"
            val jsonObjectRequest= JsonObjectRequest(
                Request.Method.GET,url3,null,
                { response ->
                    try {
                        val name = response.getString("nombreprofesional").toString()
                        val email = response.getString("correoprofesional").toString()
                        val coment = response.getString("comentario").toString()
                        val espc = response.getString("especialidad").toString()
                        val prest= response.getString("prestacion").toString()
                        val img= response.getString("img").toString()
                        val namep= response.getString("nombrepaciente").toString()
                        txthora.text=response.getString("hora").toString()
                        txtdia.text=response.getString("fecha").toString()
                        txtespecialidad?.setText(espc)
                        if(espc=="ODONTOLOGIA"){
                            btnodontologia.visibility=View.VISIBLE
                        }
                        txtepaciente?.setText(namep)
                        txteprofesional?.setText(name)
                        txteprestacion?.setText(prest)
                        Glide.with(this)
                            .load(img)
                            .centerCrop()
                            .into(viewimageperfilc)
                        txtcomentario?.text = coment

                        ESPECIALIDAD=espc

                        Historial(response.getString("fecha").toString())


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, { error ->
                }
            )
            queue.add(jsonObjectRequest)





        }
        fun Historial(fecha:String){
            val queue2 = Volley.newRequestQueue(this)
            val url2 = "http://$url/enlace.php?pacientedni=${dni}&profecionalemail=${correo}"
            val jsonObjectRequest2 = JsonObjectRequest(
                Request.Method.GET, url2, null,
                { response2 ->
                    try {
                        val hist = response2.getString("historial").toString()
                        if(hist!=""){
                            JSONCompletHistorial=hist
                            val jsonvalor = JSONObject(JSONCompletHistorial)
                            val HistJSONArray=jsonvalor.getJSONArray("Historial")
                            for (i in 0 until HistJSONArray.length()){
                                var jsonObject2= HistJSONArray.getJSONObject(i)

                                if(jsonObject2.getString("Fecha")==fecha){
                                    var jsonArray3 = jsonObject2.getJSONArray("Archivos")

                                    if(jsonArray3.length()>0){
                                        for (i in 0 until jsonArray3.length()){
                                            var jsonObject3= jsonArray3.getJSONObject(i)
                                            var JSONArchivo=""

                                            if(jsonObject3.getString("Nombre")=="ODONTOGRAMA"){


                                                JSONODONTOGRAMA= JSONObject(jsonObject3.getString("Url"))
                                                for (i in 1 until 32){
                                                    var JSON="\"d$i\": ${JSONODONTOGRAMA?.getString("d$i").toString()}\n"


                                                    if (JSONAgregadoOdontograma==""){
                                                        JSONAgregadoOdontograma = JSON
                                                    }else if (JSONAgregadoOdontograma!=""){JSONAgregadoOdontograma = JSONAgregadoOdontograma+","+JSON }

                                                }
                                                JSONCOMPLETODONTOGRAMA="{\n" +
                                                        "\"codigoprofesional\": \"${JSONODONTOGRAMA?.getString("codigoprofesional").toString()}\",\n" +
                                                        "\"dni\": \"${JSONODONTOGRAMA?.getString("dni").toString()}\",\n" +
                                                        JSONAgregadoOdontograma +
                                                        "}"
                                                 JSONArchivo="{\n" +
                                                        "\"Nombre\": \"ODONTOGRAMA\",\n" +
                                                        "\"Url\": ${JSONCOMPLETODONTOGRAMA}\n" +
                                                        "}"

                                            }else{
                                                 JSONArchivo="{\n" +
                                                        "\"Nombre\": \"${jsonObject3.getString("Nombre")}\",\n" +
                                                        "\"Url\": \"${jsonObject3.getString("Url")}\"\n" +
                                                        "}"
                                            }


                                            if (JSONAgregadoArchivo==""){
                                                JSONAgregadoArchivo = JSONArchivo
                                            }else if (JSONAgregadoArchivo!=""){
                                                JSONAgregadoArchivo = JSONAgregadoArchivo+","+JSONArchivo
                                            }

                                        }
                                    }


                                    JSONCompletArchivos="{\"Archivos\":[$JSONAgregadoArchivo]}"
                                    //ClickAgregarJson(View(applicationContext))
                                }
                            }

                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }


                }, { error ->  }
            )
            queue2.add(jsonObjectRequest2)
        }//DESCARGO EL HISTORIAL, Paso el codigo a retrofit lo comento y lo devuelvo cuando tenga MONGODB Y NODEJS*/
    /*
        fun SaveHistorial(){
            JsonHistorial()
            //Primero Verifico si esta registrado el TOKEN ID
            val queue = Volley.newRequestQueue(this)
            val url2 = "http://$url/odontograma.php?pacientedni=${dni}&profecionalemail=${correo}"
            val jsonObjectRequest= JsonObjectRequest(
                Request.Method.GET,url2,null,
                { response ->

                    val url = "http://$url/enlace.php"
                    val queue3= Volley.newRequestQueue(this)
                    //con este parametro aplico el metodo POST
                    var resultadoPost = object : StringRequest(
                        Method.POST,url,
                        Response.Listener<String> { response ->

                        }, Response.ErrorListener { error ->
                            Toast.makeText(this,"ERROR al modificar $error", Toast.LENGTH_LONG).show()
                        }){
                        override fun getParams(): MutableMap<String, String>? {
                            val parametros = HashMap<String,String>()
                            // Key y value
                            parametros.put("historial",JSONHistorial)
                            parametros.put("estadoprofecional","OCUPADO")
                            parametros.put("especialidad",ESPECIALIDAD)
                            parametros.put("nombreprofesional",name.toString())
                            parametros.put("profecionalemail",correo.toString())
                            parametros.put("pacientedni",dni.toString())
                            parametros.put("modificar","modificar")
                            return parametros
                        }
                    }
                    // con esto envio o SEND todo
                    queue3.add(resultadoPost)


                }, { error ->
                    val url = "http://$url/enlace.php"
                    val queue3= Volley.newRequestQueue(this)
                    //con este parametro aplico el metodo POST
                    var resultadoPost = object : StringRequest(
                        Method.POST,url,
                        Response.Listener<String> { response ->

                        }, Response.ErrorListener { error ->
                            Toast.makeText(this,"ERROR al insertar $error", Toast.LENGTH_LONG).show()
                        }){
                        override fun getParams(): MutableMap<String, String>? {
                            val parametros = HashMap<String,String>()
                            // Key y value
                            parametros.put("historial",JSONHistorial)
                            parametros.put("estadoprofecional","OCUPADO")
                            parametros.put("especialidad",ESPECIALIDAD)
                            parametros.put("nombreprofesional",name.toString())
                            parametros.put("profecionalemail",correo.toString())
                            parametros.put("pacientedni",dni.toString())
                            parametros.put("insertar","insertar")
                            return parametros
                        }
                    }
                    // con esto envio o SEND todo
                    queue3.add(resultadoPost)
                }
            )
            queue.add(jsonObjectRequest)
        }//Guarda los datos del Historial medico
    */
    /* fun ClickSave(view: View){

        //Primero Verifico si esta registrado el TOKEN ID
        val queue = Volley.newRequestQueue(this)
        val url2 = "http://$url/clientes.php?dni=${dni}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url2,null,
            { response ->
                //Toast.makeText(this,"$JSONCompletHistorial",Toast.LENGTH_SHORT).show()
                val jsonArray = response.getJSONArray("data")
                val jsonObject = jsonArray.getJSONObject(0)
                val url = "http://$url/clientes.php"
                val queue= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                var resultadoPost = object : StringRequest(
                    Method.POST,url,
                    Response.Listener<String> { response ->

                        SaveHistorial()
                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        parametros.put("correo",jsonObject.getString("correo").toString())
                        parametros.put("direccion",jsonObject.getString("direccion").toString())
                        parametros.put("nombreapellido",jsonObject.getString("nombreapellido").toString())
                        parametros.put("celular",jsonObject.getString("celular").toString())
                        parametros.put("dni",jsonObject.getString("dni").toString())
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

    }//Guarda los datos del Turno y LLama a Save Historial*/
    /*    fun JsonHistorial(){

           var JSONCompletArchivosd="\"Archivos\":[$JSONAgregadoArchivo]"
                if (JSONAgregadoArchivo==""){
                }else if (JSONAgregadoArchivo!=""){
                    JSONCompletHistorial="{\n" +
                            "    \"DNIprofecional\": \"${correo}\",\n" +
                            "    \"DNIpaciente\": \"${dni}\",\n" +
                            "    \"Prestacion\": \"${txteprestacion.text.toString()}\",\n" +
                            "    \"Especialidad\": \"${txtespecialidad.text.toString()}\",\n" +
                            "    \"Comentario\": \"${txtcomentario.text.toString()}\",\n" +
                            "    \"Fecha\": \"${txtdia.text.toString()}\",\n" +
                            "    \"Hora\": \"${txthora.text.toString()}\",\n" +
                            "    ${JSONCompletArchivosd}\n}"


                    JSONHistorial="{\"Historial\":[$JSONCompletHistorial]}"
                    Toast.makeText(this,"Archivos para el Historial agregado", Toast.LENGTH_SHORT).show()
                }


        }*/
    /*
        fun DeletJson(nombre:String){
            JSONAgregadoArchivo=""
            try{
                    val jsonvalor = JSONObject(JSONCompletArchivos)
                    var jsonArray = jsonvalor.getJSONArray("Archivos")

                    if(jsonArray.length()>0){
                        for (i in 0 until jsonArray.length()){
                            var jsonObject= jsonArray.getJSONObject(i)

                            if(jsonObject.getString("Nombre").toString()==nombre){
                                deleteArchivo(nombre)
                                getTurnoSave(View(applicationContext))
                            }else if(jsonObject.getString("Nombre").toString()!=nombre){
                                var JSONArchivo="{\n" +
                                        "\"Nombre\": \"${jsonObject.getString("Nombre")}\",\n" +
                                        "\"Url\": \"${jsonObject.getString("Url")}\"\n" +
                                        "}"

                                if (JSONAgregadoArchivo==""){
                                    JSONAgregadoArchivo = JSONArchivo
                                }else if (JSONAgregadoArchivo!=""){
                                    JSONAgregadoArchivo = JSONAgregadoArchivo+","+JSONArchivo
                                }
                            }else {
                                Toast.makeText(this,"No se pudo Borrar", Toast.LENGTH_SHORT).show()}
                        }
                    }

                    JSONCompletArchivos="{\"Archivos\":[$JSONAgregadoArchivo]}"


                val jsonvalor2 = JSONObject(JSONCompletArchivos)
                var jsonArray3 = jsonvalor2.getJSONArray("Archivos")

                    val displayListC=ArrayList<Archivos>()
                    if (jsonArray3.length()>0){
                        for (i in 0 until jsonArray3.length()){
                            val jsonObject= jsonArray3.getJSONObject(i)
                            val url = jsonObject.getString("Url").toString()
                            val name = jsonObject.getString("Nombre").toString()
                            displayListC.add(Archivos(name, url))
                        }
                    }else{
                        rviewcliente.visibility=View.GONE
                    }
                    val adapterarchivo = AdapterArchivo(displayListC, this, this)
                    rviewcliente?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    rviewcliente?.adapter = adapterarchivo
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

        } //BORRO UN ARCHIVO DEL JSON, DE LA LISTA Y DEL SERVIDOR
    */
    /*
        fun ClickAgregarJson(view: View){
            try{
                if(ARCHIVONAME !=""){


                    var JSONArchivo="{\n" +
                            "\"Nombre\": \"${ARCHIVONAME}\",\n" +
                            "\"Url\": \"http://$url/docs/$ARCHIVONAME\"\n" +
                            "}"

                    if (JSONAgregadoArchivo==""){
                        JSONAgregadoArchivo = JSONArchivo
                    }else if (JSONAgregadoArchivo!=""){JSONAgregadoArchivo = JSONAgregadoArchivo+","+JSONArchivo }
                    JSONCompletArchivos="{\"Archivos\":[$JSONAgregadoArchivo]}"


                }else if (odont){
                    var JSONArchivo="{\n" +
                            "\"Nombre\": \"ODONTOGRAMA\",\n" +
                            "\"Url\": ${JSONCOMPLETODONTOGRAMA}\n" +
                            "}"

                    if (JSONAgregadoArchivo==""){
                        JSONAgregadoArchivo = JSONArchivo
                    }else if (JSONAgregadoArchivo!=""){JSONAgregadoArchivo = JSONAgregadoArchivo+","+JSONArchivo }
                    JSONCompletArchivos="{\"Archivos\":[$JSONAgregadoArchivo]}"

                }

                val displayListC=ArrayList<Archivos>()
                val response=JSONObject(JSONCompletArchivos)
                val jsonArray = response.getJSONArray("Archivos")
                for (i in 0 until jsonArray.length()){
                    val jsonObject= jsonArray.getJSONObject(i)
                    val url = jsonObject.getString("Url").toString()
                    val name = jsonObject.getString("Nombre").toString()
                    displayListC.add(Archivos(name, url))
                }

                val adapterarchivo = AdapterArchivo(displayListC, this, this)
                rviewcliente?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                rviewcliente?.adapter = adapterarchivo
                rviewcliente.visibility=View.VISIBLE
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    */



    private fun deleteArchivo(archivoname:String){
        val queue= Volley.newRequestQueue(this)
        //con este parametro aplico el metodo POST
        val postURL = "http://$url/acceso.php"
        var resultadoPost = object : StringRequest(Method.POST,postURL,
            Response.Listener<String> { response ->

                Toast.makeText(this,"ARCHIVO BORRADO!", Toast.LENGTH_LONG).show()
            }, Response.ErrorListener { error ->
                Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()
                // Key y value
                parametros.put("usuario","lsuRb/c6GKDIG7lt")
                parametros.put("password","SS4WJOA58mkhWFkLLzC3LA==")
                parametros.put("borrar",archivoname)
                return parametros

            }
        }
        // con esto envio
        queue.add(resultadoPost)


    }


    fun clickSelect(view: View){
        ArchivoController.selectArchivo(this, SELECT_ACTIVITY)
        a=1
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == SELECT_ACTIVITY) {
            val uri = data?.data
            if (uri != null) {

                createImageData(uri)
                dumpImageMetaData(uri)

            }
        }

    }
    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }

    }
    @SuppressLint("Range")
    fun dumpImageMetaData(uri: Uri) {
        val cursor: Cursor? = contentResolver.query(
            uri, null, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName: String =
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.i(ContentValues.TAG, "Display Name: $displayName")
                ARCHIVONAME=displayName
                //ClickAgregarJson(View(applicationContext))
                uploadImage()
            }
        }
    }
    private fun uploadImage() {

        imageData?: return
        val url = "http://$url/acceso.php"
        val request = object : VolleyFileUploadRequest( Method.POST, url,
            Response.Listener {
                Toast.makeText(this,"ARCHIVO SUBIDO",Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener {

                Toast.makeText(this,"ERROR $it", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()
                // Key y value
                parametros.put("usuario","lsuRb/c6GKDIG7lt")
                parametros.put("password","SS4WJOA58mkhWFkLLzC3LA==")
                return parametros

            }
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["docs"] = FileDataPart(ARCHIVONAME.toString(), imageData!!, "file")

                return params
            }



        }
        Volley.newRequestQueue(this).add(request)

    }


    override fun onArchivoItemClick(link: String,nombre:String,download:Boolean,ver:Boolean) {
        //val jsonvalor = JSONObject(JSONCompletArchivos)
        //var jsonArray = jsonvalor.getJSONArray("Archivos")

        if (download){
            val requst= DownloadManager.Request(Uri.parse(link))
                .setTitle(nombre)
                .setDescription("Descargando....")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
            val da = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            da.enqueue(requst)

        }
        if ("ODONTOGRAMA"==nombre){
            ClickOdontograma(View(applicationContext),link)
        }else{
            if (ver){
                val builder= AlertDialog.Builder(this)
                val view=layoutInflater.inflate(R.layout.dialog_archivo,null)
                val name=view.findViewById<TextView>(R.id.arcname)
                val url=view.findViewById<TextView>(R.id.arcurl)
                name.setText(nombre)
                url.visibility=View.GONE
                builder.setView(view)
                builder.setPositiveButton("Descargar", DialogInterface.OnClickListener { dialogInterface, i ->
                    val requst= DownloadManager.Request(Uri.parse(link))
                        .setTitle(nombre)
                        .setDescription("Downloading....")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setAllowedOverMetered(true)
                    val da = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    da.enqueue(requst)

                })

                builder.setNeutralButton("Borrar", DialogInterface.OnClickListener { dialogInterface, j ->
                    // DeletJson(namel)
                    // DeletJson(namel)
                    if (nombre!="ODONTOGRAMA"){
                        deleteArchivo(nombre)
                    }else if (nombre=="ODONTOGRAMA"){

                        //BORRO EL ODONTOGRAMA
                    }


                    for (i in 0 until datos.size){
                        if (datos[i].archivo1!=""){
                            postCliente("modificar","",datos[i].archivo2,datos[i].archivo3,datos[i].archivo4,datos[i].archivo5,datos[i].estado)

                        }else if (datos[i].archivo2!=""){
                            postCliente("modificar",datos[i].archivo1,"",datos[i].archivo3,datos[i].archivo4,datos[i].archivo5,datos[i].estado)

                        }else if (datos[i].archivo3!=""){
                            postCliente("modificar",datos[i].archivo1,datos[i].archivo2,"",datos[i].archivo4,datos[i].archivo5,datos[i].estado)

                        }else if (datos[i].archivo4!=""){
                            postCliente("modificar",datos[i].archivo1,datos[i].archivo2,datos[i].archivo3,"",datos[i].archivo5,datos[i].estado)

                        }else if (datos[i].archivo5!=""){
                            postCliente("modificar",datos[i].archivo1,datos[i].archivo2!!,datos[i].archivo3,datos[i].archivo4,"",datos[i].estado)

                        }
                    }
                })
                builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                builder.show()
            }
        }



    }
    fun ClickCancelar(view: View){
        for (i in 0 until datos.size){
            if (id==datos[i].ID.toInt()){
                postCliente("modificar",datos[i].archivo1,datos[i].archivo2,datos[i].archivo3,datos[i].archivo4,datos[i].archivo5,"C")

            }
        }


    }
}