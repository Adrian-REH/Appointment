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
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import app.ibiocd.lavanderia.Adapter.Archivos
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.lavanderia.FileDataPart
import app.ibiocd.lavanderia.VolleyFileUploadRequest
import app.ibiocd.odontologia.Adapter.AdapterArchivo
import app.ibiocd.odontologia.Adapter.AdapterClienteA
import app.ibiocd.odontologia.Adapter.AdapterClienteB
import app.ibiocd.odontologia.Adapter.ArchivoController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_inicio.*
import kotlinx.android.synthetic.main.activity_paciente.*
import kotlinx.android.synthetic.main.activity_paciente.edtxtdirec
import kotlinx.android.synthetic.main.activity_paciente.edtxtemail
import kotlinx.android.synthetic.main.activity_paciente.edtxtnombre
import kotlinx.android.synthetic.main.activity_paciente.edtxttelefono
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_turno.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class TurnoActivity : AppCompatActivity(), AdapterArchivo.onArchivoItemClick {
    private val SELECT_ACTIVITY=50
    private var ARCHIVONAME = ""
    private var ESPECIALIDAD = ""
    var a:Int=0
    var JSONAgregadoArchivo:String=""
    var JSONCompletHistorial:String=""
    var JSONHistorial:String=""
    var JSONCompletArchivos:String=""


    private var imageData: ByteArray? = null

    var url:String?=""
    var correo:String?=""
    var dni:String?=""
    var ARCHIVOS:String?=""
    val listhora=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turno)
        if(intent.extras !=null){
            dni = intent.getStringExtra("dni")
            correo = intent.getStringExtra("correo")
            url = intent.getStringExtra("url")

        }
        ClickRefresh()

    }
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
                    val fecha= response.getString("fecha").toString()
                    val prest= response.getString("prestacion").toString()
                    val img= response.getString("img").toString()
                    val namep= response.getString("nombrepaciente").toString()
                    val horario = "{\n" +
                            " \"hora\": \"15:25\",\n" +
                            " \"fecha\": \"${fecha}\"\n" +
                            " }"
                    txtespecialidad?.setText(espc)
                    txtepaciente?.setText(namep)
                    txteprofesional?.setText(name)
                    txteprestacion?.setText(prest)
                    Glide.with(this)
                        .load(img)
                        .centerCrop()
                        .into(viewimageperfilc)
                    txtcomentario?.text = coment
                    if (horario!="null"){
                        val JsonHora=JSONObject(horario)
                        txthora.text=JsonHora.getString("hora").toString()
                        txtdia.text=JsonHora.getString("fecha").toString()
                        Historial(JsonHora.getString("fecha").toString())
                    }
                    ESPECIALIDAD=espc

                    /*
                    listesp.add("adrian1")
                    val especialidad = listesp
                    val arrayAdapter = ArrayAdapter(this,R.layout.dropdown_item,especialidad)
                    txtespecialidad.setAdapter(arrayAdapter) */



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

                                    var JSONArchivo="{\n" +
                                            "\"Nombre\": \"${jsonObject3.getString("Nombre")}\",\n" +
                                            "\"Url\": \"${jsonObject3.getString("Url")}\"\n" +
                                            "}"

                                    if (JSONAgregadoArchivo==""){
                                        JSONAgregadoArchivo = JSONArchivo
                                    }else if (JSONAgregadoArchivo!=""){
                                        JSONAgregadoArchivo = JSONAgregadoArchivo+","+JSONArchivo
                                    }

                                }
                            }


                            JSONCompletArchivos="{\"Archivos\":[$JSONAgregadoArchivo]}"
                            ClickAgregarJson(View(applicationContext))
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
    
    
            }, { error ->  }
        )
        queue2.add(jsonObjectRequest2)
    }//DESCARGO EL HISTORIAL
    fun ClickPerfilPaciente(view: View){
        val intent = Intent(this, PacienteActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        startActivity(intent)
    }
    fun Back(view: View){
        finish()
    }
    fun ClickArchivos(view: View){
        clickSelect(View(applicationContext))
    }
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
    fun SaveHistorial(){
        JsonHistorial()
        //Primero Verifico si esta registrado el TOKEN ID
        val queue = Volley.newRequestQueue(this)
        val url2 = "http://$url/enlace.php?pacientedni=${dni}&profecionalemail=${correo}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url2,null,
            { response ->
                val hist = response.getString("historial").toString()
                if ( hist != ""){
                    val jsonvalor = JSONObject(hist)
                    val HistJSONArray=jsonvalor.getJSONArray("Historial")
                    for (i in 0 until HistJSONArray.length()){
                        var jsonObject2= HistJSONArray.getJSONObject(i)

                        if(jsonObject2.getString("Fecha")==txtdia.text.toString()){

                        }else{
                            var JSONArchivo="{\n" +
                                    "\"DNIprofecional\": \"${jsonObject2.getString("DNIprofecional")}\",\n" +
                                    "\"DNIpaciente\": \" ${jsonObject2.getString("DNIpaciente")}\",\n" +
                                    "\"Prestacion\": \"${jsonObject2.getString("Prestacion")}\",\n" +
                                    "\"Especialidad\": \"${jsonObject2.getString("Especialidad")}\",\n" +
                                    "\"Comentario\": \"${jsonObject2.getString("Comentario")}\",\n" +
                                    "\"Fecha\": \"${jsonObject2.getString("Fecha")}\",\n" +
                                    "\"Hora\": \"${jsonObject2.getString("Hora")}\",\n" +
                                    "\"Archivos\": \"${jsonObject2.getString("Archivos")}\"\n" +
                                    "}"
                            if (JSONCompletHistorial==""){
                                JSONCompletHistorial = JSONArchivo
                            }else if (JSONCompletHistorial!=""){
                                JSONCompletHistorial = JSONCompletHistorial+","+JSONArchivo
                            }
                        }


                    }
                    JSONHistorial="{\"Historial\":[$JSONCompletHistorial]}"
                }

                val url = "http://$url/enlace.php"
                val queue= Volley.newRequestQueue(this)
                //con este parametro aplico el metodo POST
                var resultadoPost = object : StringRequest(
                    Method.POST,url,
                    Response.Listener<String> { response ->

                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        parametros.put("historial",JSONHistorial)
                        parametros.put("estadoprofecional","OCUPADO")
                        parametros.put("especialidad",ESPECIALIDAD)
                        parametros.put("profecionalemail",correo.toString())
                        parametros.put("pacientedni",dni.toString())
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

                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"ERROR $error", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        // Key y value
                        parametros.put("historial",JSONHistorial)

                        parametros.put("profecionalemail",correo.toString())
                        parametros.put("pacientedni",dni.toString())
                        parametros.put("insertar","insertar")
                        return parametros
                    }
                }
                // con esto envio o SEND todo
                queue.add(resultadoPost)
            }
        )
        queue.add(jsonObjectRequest)
    }//Guarda los datos del Historial medico
    fun ClickSave(view: View){

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

    }//Guarda los datos del Turno y LLama a Save Historial
    fun JsonHistorial(){

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
    }
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
                            ClickSave(View(applicationContext))
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
    private fun uploadImage() {

        imageData?: return
        val url = "http://$url/acceso.php"
        val request = object : VolleyFileUploadRequest( Method.POST, url,
            Response.Listener {

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
                ClickAgregarJson(View(applicationContext))
                uploadImage()
            }
        }
    }
    override fun onArchivoItemClick(dato: String) {

        var a=false
        var namel=""
        var uril=""
        val jsonvalor = JSONObject(JSONCompletArchivos)
        var jsonArray = jsonvalor.getJSONArray("Archivos")
        for (i in 0 until jsonArray.length()){
            var jsonObject= jsonArray.getJSONObject(i)
            if (jsonObject.getString("Url")==dato){
                val requst= DownloadManager.Request(Uri.parse(jsonObject.getString("Url")))
                    .setTitle(jsonObject.getString("Nombre"))
                    .setDescription("Downloading....")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)
                val da = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                da.enqueue(requst)
            }
            if (jsonObject.getString("Nombre")==dato){
                namel=jsonObject.getString("Nombre")
                uril=jsonObject.getString("Url")
                a=true
            }

        }
        if (a){
            val builder= AlertDialog.Builder(this)
            val view=layoutInflater.inflate(R.layout.dialog_archivo,null)
            val name=view.findViewById<TextView>(R.id.arcname)
            val url=view.findViewById<TextView>(R.id.arcurl)
            name.setText(namel)
            url.visibility=View.GONE
            builder.setView(view)
            builder.setPositiveButton("Descargar", DialogInterface.OnClickListener { dialogInterface, i ->
                val requst= DownloadManager.Request(Uri.parse(uril))
                    .setTitle(namel)
                    .setDescription("Downloading....")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)
                val da = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                da.enqueue(requst)

            })

            builder.setNeutralButton("Borrar", DialogInterface.OnClickListener { dialogInterface, i ->
                DeletJson(namel)
            })
            builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            builder.show()
        }
    }
    fun ClickCancelar(view: View){
        val url = "http://$url/turno.php?dni=${dni}&correoprofesional=${correo}"

        val queue = Volley.newRequestQueue(this)

        var resultadoDelete = object : StringRequest(
            Request.Method.DELETE,url,
            Response.Listener { response ->
                Toast.makeText(this,"El usuario se borro de forma exitosa", Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"El usuario se borro de forma exitosa", Toast.LENGTH_LONG).show()
            }
        ){

        }
        queue.add(resultadoDelete)
    }
}