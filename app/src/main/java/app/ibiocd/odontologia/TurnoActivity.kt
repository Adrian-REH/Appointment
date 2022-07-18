package app.ibiocd.odontologia

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import app.ibiocd.lavanderia.Adapter.*
import app.ibiocd.lavanderia.FileDataPart
import app.ibiocd.lavanderia.VolleyFileUploadRequest
import app.ibiocd.odontologia.Adapter.AdapterArchivo
import app.ibiocd.odontologia.Adapter.AdapterTurnoHora
import app.ibiocd.odontologia.Adapter.ArchivoController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_sign.*
import kotlinx.android.synthetic.main.activity_turno.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.io.EOFException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TurnoActivity : AppCompatActivity(), AdapterArchivo.onArchivoItemClick,
    AdapterView.OnItemClickListener, AdapterTurnoHora.onHorarioItemClick {
    val TAG = "MainActivity"

    private val SELECT_ACTIVITY=50
    private var ARCHIVONAME = ""
    private var ESPECIALIDAD = ""

    var a:Int=0
    var fecha=0
    var id=0
    var codigo=0

    var odont:Boolean=false

    var JSONAgregadoArchivo:String=""
    var JSONAgregadoOdontograma:String=""
    var JSONCompletArchivos:String=""

    var JSONODONTOGRAMA: JSONObject? =null

     var date: TurnoRespons?=null

    private var imageData: ByteArray? = null

    var url:String?=""
    var IMG:String?=""
   // var correo:String?=""
    var IDP:String?=""
    var dni:String?=""
    var name:String?=""

    val arraylis=  ArrayList<TurnoRespons>()
    val arraylisP= ArrayList<EnlaceRespons>()
    val arraylisPres= ArrayList<String>()
    val displayListC=ArrayList<Archivos>()
    val arrayturnhora= ArrayList<HoraTurno>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turno)
        btnodontologia.visibility=View.GONE
        btnarchivos.visibility=View.GONE
        floatcanc.visibility=View.GONE
        rviewcliente.visibility=View.GONE
        cardhorariosd.visibility=View.GONE

        if(intent.extras !=null){
            dni = intent.getStringExtra("dni")
           // correo = intent.getStringExtra("correo")
            IDP = intent.getStringExtra("IDP")
            url = intent.getStringExtra("url")
            name = intent.getStringExtra("name")
            fecha= intent.getStringExtra("fecha").toString().toInt()
            codigo= intent.getStringExtra("codigo").toString().toInt()
            id= intent.getStringExtra("id").toString().toInt()

            //Toast.makeText(this,"$codigo",Toast.LENGTH_LONG).show()
            turnobacktext.setText(intent.getStringExtra("back").toString())
            if (codigo!=0 && id!=0){
                    getListTurnos()

                    odont=true
                    displayListC.clear()
                    if (odont){
                        JSONAgregadoArchivo="{\n" +
                                "\"Nombre\": \"ODONTOGRAMA\",\n" +
                                "\"Url\": ${"$codigo"}\n" +
                                "}"
/*
                        if (JSONAgregadoArchivo==""){
                            JSONAgregadoArchivo = JSONArchivo
                        }else if (JSONAgregadoArchivo!=""){JSONAgregadoArchivo = JSONAgregadoArchivo+","+JSONArchivo }*/
                        JSONCompletArchivos="{\"Archivos\":[$JSONAgregadoArchivo]}"
                    }
                    displayListC.add(Archivos("ODONTOGRAMA", "$codigo"))

                    val adapterarchivo = AdapterArchivo(displayListC, applicationContext, this@TurnoActivity)
                    rviewcliente?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
                    rviewcliente?.adapter = adapterarchivo
                    rviewcliente.visibility=View.VISIBLE
                    //ClickAgregarJson(View(applicationContext))
                }

            if(dni=="patient"){
                getListPaciente(intent.getStringExtra("especialidad").toString())

                txteprofesional.setText( intent.getStringExtra("name").toString())
                txtprofesion.setText( intent.getStringExtra("especialidad").toString())
            }

        }



    }
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                // Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                //Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    fun ClickOdontograma(view: View){
        getTurnoSave(View(applicationContext))

        val intent = Intent(this, OdontogramaActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("IDP",IDP)
        intent.putExtra("dni",dni)
        intent.putExtra("name",name)
        intent.putExtra("fecha",fecha.toString())
        intent.putExtra("id",id.toString())
        intent.putExtra("codigo",codigo.toString())
        startActivity(intent)
        finish()

    }

    private fun getListTurnos(){
        displayListC.clear()
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val call=RetrofitClient.instance.getListTurnodc(dni.toString(),IDP.toString())
                var datos: List<TurnoRespons>? =call.body()
                runOnUiThread{

                    for (i in 0 until datos?.size!!){

                        if(call.isSuccessful){
                            if (id==datos[i].ID.toInt()){
                                date=datos[i]
                                val namepac = datos[i].namepac
                                val nameprof = datos[i].nameprof
                                val comentario = datos[i].comentario
                                val img = datos[i].img
                                val espc = datos[i].especialidad
                                val prest = datos[i].prestacion

                                txtnumdia.text=datos[i].hora


                                val fch2= datos[i].fecha

                                var annosigno=""
                                var messigno=""
                                var diasigno=""
                                var character=""

                                for(char in fch2){
                                    character += char
                                    if(character.length==4){

                                        if(annosigno==""){
                                            annosigno = character
                                            character=""
                                        }
                                    }
                                    if(annosigno!=""){
                                        if(character.length==2){
                                            if(messigno==""){
                                                messigno = character
                                                character=""
                                            }else
                                            {
                                                diasigno = character
                                            }
                                        }
                                    }

                                }


                                txtdia.text="$annosigno/$messigno/$diasigno"
                                txtprofesion?.setText(espc)

                                if(espc=="ODONTOLOGIA"){
                                    btnodontologia.visibility=View.VISIBLE
                                }

                                txtepaciente?.setText(namepac)
                                txteprofesional?.setText(nameprof)
                                txtespecialidad?.setText(prest)
                                Glide.with(applicationContext)
                                    .load(img)
                                    .centerCrop()
                                    .into(viewimageperfilc)
                                txtcomentario?.text = comentario

                                IMG=img
                                ESPECIALIDAD=espc

                                if (datos[i].archivo1!=""){
                                    if (JSONObject(datos[i].archivo1).getString("Nombre").toString()=="ODONTOGRAMA"){
                                        if (JSONObject(datos[i].archivo1).getString("Url").toString()!=codigo.toString()){
                                            codigo= JSONObject(datos[i].archivo1).getString("Url").toString().toInt()
                                            displayListC.add(Archivos(JSONObject(datos[i].archivo1).getString("Nombre").toString(), JSONObject(datos[i].archivo1).getString("Url").toString()))

                                        }
                                    }else{
                                        displayListC.add(Archivos(JSONObject(datos[i].archivo1).getString("Nombre").toString(), JSONObject(datos[i].archivo1).getString("Url").toString()))

                                    }
                                }
                                 if (datos[i].archivo2!=""){
                                    if (JSONObject(datos[i].archivo2).getString("Nombre").toString()=="ODONTOGRAMA"){
                                        if (JSONObject(datos[i].archivo2).getString("Url").toString()!=codigo.toString()){
                                            codigo= JSONObject(datos[i].archivo2).getString("Url").toString().toInt()
                                            displayListC.add(Archivos(JSONObject(datos[i].archivo2).getString("Nombre").toString(), JSONObject(datos[i].archivo2).getString("Url").toString()))

                                        }
                                    }else{
                                        displayListC.add(Archivos(JSONObject(datos[i].archivo2).getString("Nombre").toString(), JSONObject(datos[i].archivo2).getString("Url").toString()))

                                    }
                                }
                                 if (datos[i].archivo3!=""){

                                    if (JSONObject(datos[i].archivo3).getString("Nombre").toString()=="ODONTOGRAMA"){
                                        if (JSONObject(datos[i].archivo3).getString("Url").toString()!=codigo.toString()){
                                            codigo= JSONObject(datos[i].archivo3).getString("Url").toString().toInt()
                                            displayListC.add(Archivos(JSONObject(datos[i].archivo3).getString("Nombre").toString(), JSONObject(datos[i].archivo3).getString("Url").toString()))

                                        }
                                    }else{
                                        displayListC.add(Archivos(JSONObject(datos[i].archivo3).getString("Nombre").toString(), JSONObject(datos[i].archivo3).getString("Url").toString()))

                                    }
                                }
                                 if (datos[i].archivo4!=""){

                                    if (JSONObject(datos[i].archivo4).getString("Nombre").toString()=="ODONTOGRAMA"){
                                        if (JSONObject(datos[i].archivo4).getString("Url").toString()!=codigo.toString()){
                                            codigo= JSONObject(datos[i].archivo4).getString("Url").toString().toInt()
                                            displayListC.add(Archivos(JSONObject(datos[i].archivo4).getString("Nombre").toString(), JSONObject(datos[i].archivo4).getString("Url").toString()))

                                        }
                                    }else{
                                        displayListC.add(Archivos(JSONObject(datos[i].archivo4).getString("Nombre").toString(), JSONObject(datos[i].archivo4).getString("Url").toString()))

                                    }
                                }
                                 if (datos[i].archivo5!=""){
                                    if (JSONObject(datos[i].archivo5).getString("Nombre").toString()=="ODONTOGRAMA"){
                                        if (JSONObject(datos[i].archivo5).getString("Url").toString()!=codigo.toString()){
                                            codigo= JSONObject(datos[i].archivo5).getString("Url").toString().toInt()
                                            displayListC.add(Archivos(JSONObject(datos[i].archivo5).getString("Nombre").toString(), JSONObject(datos[i].archivo5).getString("Url").toString()))

                                        }
                                    }else{
                                        displayListC.add(Archivos(JSONObject(datos[i].archivo5).getString("Nombre").toString(), JSONObject(datos[i].archivo5).getString("Url").toString()))

                                    }
                                }
                                if (datos[i].estado=="C"){
                                    floatcanc?.visibility=View.GONE
                                }
                                if (displayListC.size>0 && displayListC.size<6){
                                    rviewcliente.visibility=View.VISIBLE

                                }
                                if (displayListC.size==5){
                                    btnodontologia.visibility=View.GONE
                                    btnarchivos.visibility=View.GONE
                                }


                                val adapterarchivo = AdapterArchivo(displayListC, applicationContext, this@TurnoActivity)
                                rviewcliente?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
                                rviewcliente?.adapter = adapterarchivo
                                rviewcliente.visibility=View.VISIBLE


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
        intent.putExtra("IDP",IDP)
        intent.putExtra("dni",dni)
        intent.putExtra("name",name)
        intent.putExtra("back","Turno")
        startActivity(intent)
    }

    fun Back(view: View){
        if(dni!="patient" && fecha!=0 && txtnumdia.text.toString()!=""){
            getTurnoSave(View(applicationContext))
        }
        finish()
    }

    private fun getClientDatos(dni:String,accion: String,name:String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call=RetrofitClient.instance.getCliente(dni.toString())
                val datos: ClienteRespons? =call.body()
                runOnUiThread{
                    if(call.isSuccessful){
                        if (datos != null) {

                            if (accion=="modificar"){
                                sendNotification(PushNotification(NotificationData("Turno cambiado ","$name modifico el turno del $fecha"),"${datos.TID}"))

                            }else if (accion=="insertar"){
                                sendNotification(PushNotification(NotificationData("Turno creado ","$name saco un turno para el $fecha"),"${datos.TID}"))

                            }else if (accion=="borrar"){
                                Toast.makeText(applicationContext,"El Turno se ${datos.TID} ", Toast.LENGTH_SHORT).show()

                                sendNotification(PushNotification(NotificationData("Turno cancelado ","$name cancelo el turno de la fecha: $fecha"),"${datos.TID}"))

                            }

                        }

                    }else{
                        Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                    }
                }
            }catch (e: java.lang.Exception){

            }


        }

    }//


    //----------------------------------------------------------------------------------------------Verifica si existe el turno crea una lista y compara la fecha
    fun getTurnoSave(view: View){

        if (ARCHIVONAME !=""){
            Toast.makeText(this,"$ARCHIVONAME",Toast.LENGTH_SHORT).show()

            displayListC.add(Archivos(ARCHIVONAME, "http://$url/docs/$ARCHIVONAME"))
            val adapterarchivo = AdapterArchivo(displayListC, applicationContext, this@TurnoActivity)
            rviewcliente?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
            rviewcliente?.adapter = adapterarchivo

            if (displayListC.size==5){
                btnodontologia.visibility=View.GONE
                btnarchivos.visibility=View.GONE
            }

            JSONAgregadoArchivo="{\n" +
                        "\"Nombre\": \"${ARCHIVONAME}\",\n" +
                        "\"Url\": \"http://$url/docs/$ARCHIVONAME\"\n" +
                        "}"/*

                if (JSONAgregadoArchivo==""){
                    JSONAgregadoArchivo = JSONArchivo
                }else if (JSONAgregadoArchivo!=""){JSONAgregadoArchivo = JSONAgregadoArchivo+","+JSONArchivo }*/
                JSONCompletArchivos="{\"Archivos\":[$JSONAgregadoArchivo]}"
            }


        CoroutineScope(Dispatchers.IO).launch {
                try{
                    val call=RetrofitClient.instance.getListTurnodc(dni.toString(),IDP.toString())
                    val BDatos: List<TurnoRespons>? =call.body()
                    if (BDatos != null) {
                        for (i in 0 until BDatos.size){
                            if(call.isSuccessful){

                                if (id==BDatos[i].ID.toInt()){
                                        date=BDatos[i]

                                        if (BDatos[i].archivo1==""){
                                                postCliente("modificar",JSONAgregadoArchivo,BDatos[i].archivo2,BDatos[i].archivo3,BDatos[i].archivo4,BDatos[i].archivo5,BDatos[i].estado)

                                        }else if (BDatos[i].archivo1!=""){
                                            if (JSONObject(date!!.archivo1).getString("Url").toString()==JSONObject(JSONAgregadoArchivo).getString("Url")){
                                                postCliente("modificar",JSONAgregadoArchivo,BDatos[i].archivo2,BDatos[i].archivo3,BDatos[i].archivo4,BDatos[i].archivo5,BDatos[i].estado)

                                            }
                                        }

                                        if (BDatos[i].archivo2==""){
                                            postCliente("modificar",BDatos[i].archivo1,JSONAgregadoArchivo,BDatos[i].archivo3,BDatos[i].archivo4,BDatos[i].archivo5,BDatos[i].estado)

                                        }else if (BDatos[i].archivo2!=""){
                                            if (JSONObject(date!!.archivo2).getString("Url").toString()==JSONObject(JSONAgregadoArchivo).getString("Url")){
                                                postCliente("modificar",BDatos[i].archivo1,JSONAgregadoArchivo,BDatos[i].archivo3,BDatos[i].archivo4,BDatos[i].archivo5,BDatos[i].estado)

                                            }
                                        }

                                        if (BDatos[i].archivo3==""){
                                            postCliente("modificar",BDatos[i].archivo1,BDatos[i].archivo2,JSONAgregadoArchivo,BDatos[i].archivo4,BDatos[i].archivo5,BDatos[i].estado)

                                        }else if (BDatos[i].archivo3!=""){
                                            if (JSONObject(date!!.archivo3).getString("Url").toString()==JSONObject(JSONAgregadoArchivo).getString("Url")){
                                                postCliente("modificar",BDatos[i].archivo1,BDatos[i].archivo2,JSONAgregadoArchivo,BDatos[i].archivo4,BDatos[i].archivo5,BDatos[i].estado)

                                            }
                                        }
                                        if (BDatos[i].archivo4==""){
                                            postCliente("modificar",BDatos[i].archivo1,BDatos[i].archivo2,BDatos[i].archivo3,JSONAgregadoArchivo,BDatos[i].archivo5,BDatos[i].estado)

                                        }else if (BDatos[i].archivo4!=""){
                                            if (JSONObject(date!!.archivo4).getString("Url").toString()==JSONObject(JSONAgregadoArchivo).getString("Url")){
                                                postCliente("modificar",BDatos[i].archivo1,BDatos[i].archivo2,BDatos[i].archivo3,JSONAgregadoArchivo,BDatos[i].archivo5,BDatos[i].estado)

                                            }
                                        }
                                        if (BDatos[i].archivo5==""){
                                            postCliente("modificar",BDatos[i].archivo1,BDatos[i].archivo2!!,BDatos[i].archivo3,BDatos[i].archivo4,JSONAgregadoArchivo,BDatos[i].estado)

                                        }else if (BDatos[i].archivo5!=""){
                                            if (JSONObject(date!!.archivo5).getString("Url").toString()==JSONObject(JSONAgregadoArchivo).getString("Url")){
                                                postCliente("modificar",BDatos[i].archivo1,BDatos[i].archivo2!!,BDatos[i].archivo3,BDatos[i].archivo4,JSONAgregadoArchivo,BDatos[i].estado)

                                            }
                                        }
                                        //postCliente("modificar",JSONCompletArchivos,estado)
                                    }

                                else if(BDatos[i].hora!=txtnumdia.text.toString()){
                                    postCliente("insertar","","","","","","E")
                                    }

                            }else{
                               // postCliente("insertar","","","","","","E")
                                //postCliente("insertar","","E")
                            }
                        }
                    }


                }catch (e: Exception){
                    postCliente("insertar","","","","","","E")
                }
            }
    }//

    //----------------------------------------------------------------------------------------------Guarda los datos del Turno
    private fun postCliente(accion:String,archivo1:String,archivo2:String,archivo3:String,archivo4:String,archivo5:String,estado:String) {

        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postTurno(IDP.toString(),txtepaciente.text.toString(),name.toString(),txtprofesion.text.toString(),txtespecialidad.text.toString(),dni.toString(),fecha.toString(),"${txtnumdia.text.toString()}",txtcomentario.text.toString(),estado,IMG.toString(),archivo1,archivo2,archivo3,archivo4,archivo5,accion,accion)
            //val call=RetrofitClient.instance.postTurno(correo.toString(),name.toString(),txteprofesional.text.toString(),txtespecialidad.text.toString(),txteprestacion.text.toString(),dni.toString(),fecha.toString(),"${txthora.text.toString()}",txtcomentario.text.toString(),estado,IMG.toString(),JSONCompletArchivos,accion,accion)
            call.enqueue(object : Callback<TurnoRespons> {
                override fun onFailure(call: Call<TurnoRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<TurnoRespons>, response: retrofit2.Response<TurnoRespons>) {
                    arraylisP.clear()
                    arraylisPres.clear()
                    if (estado!="C"){

                        getClientDatos(dni.toString(),accion,txtepaciente.text.toString())

                    }else{

                        getClientDatos(dni.toString(),"borrar",txtepaciente.text.toString())

                    }

                    if (id==0){
                        finish()
                    }

                }
            })



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
            val intent = Intent(this, OdontogramaActivity::class.java)
            intent.putExtra("codigo",link)
            intent.putExtra("url",url)
            intent.putExtra("IDP",IDP)
            intent.putExtra("dni",dni)
            intent.putExtra("name",name)
            intent.putExtra("fecha",fecha.toString())
            intent.putExtra("id",id.toString())
            startActivity(intent)
            finish()

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
                        displayListC.clear()

                        if (date!!.archivo1!=""){
                            if (JSONObject(date!!.archivo1).getString("Nombre").toString()=="$nombre"){
                                postCliente("modificar","",date!!.archivo2,date!!.archivo3,date!!.archivo4,date!!.archivo5,date!!.estado)

                            }else{
                                displayListC.add(Archivos(JSONObject(date!!.archivo1).getString("Nombre").toString(), JSONObject(date!!.archivo1).getString("Url").toString()))

                            }


                        }
                    if (date!!.archivo2!=""){
                            if (JSONObject(date!!.archivo2).getString("Nombre").toString()=="$nombre"){
                                postCliente("modificar",date!!.archivo1,"",date!!.archivo3,date!!.archivo4,date!!.archivo5,date!!.estado)

                            }else{
                                displayListC.add(Archivos(JSONObject(date!!.archivo2).getString("Nombre").toString(), JSONObject(date!!.archivo2).getString("Url").toString()))

                            }


                        }
                    if (date!!.archivo3!=""){
                            if (JSONObject(date!!.archivo3).getString("Nombre").toString()=="$nombre"){
                                postCliente("modificar",date!!.archivo1,date!!.archivo2,"",date!!.archivo4,date!!.archivo5,date!!.estado)

                            }else{
                                displayListC.add(Archivos(JSONObject(date!!.archivo3).getString("Nombre").toString(), JSONObject(date!!.archivo3).getString("Url").toString()))

                            }

                        }
                    if (date!!.archivo4!=""){
                            if (JSONObject(date!!.archivo4).getString("Nombre").toString()=="$nombre"){
                                postCliente("modificar",date!!.archivo1,date!!.archivo2,date!!.archivo3,"",date!!.archivo5,date!!.estado)

                            }else{
                                displayListC.add(Archivos(JSONObject(date!!.archivo4).getString("Nombre").toString(), JSONObject(date!!.archivo4).getString("Url").toString()))

                            }

                        }
                    if (date!!.archivo5!=""){
                            if (JSONObject(date!!.archivo5).getString("Nombre").toString()=="$nombre"){
                                postCliente("modificar",date!!.archivo1,date!!.archivo2!!,date!!.archivo3,date!!.archivo4,"",date!!.estado)

                            }else{
                                displayListC.add(Archivos(JSONObject(date!!.archivo5).getString("Nombre").toString(), JSONObject(date!!.archivo5).getString("Url").toString()))

                            }

                        }
                    val adapterarchivo = AdapterArchivo(displayListC, applicationContext, this@TurnoActivity)
                    rviewcliente?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
                    rviewcliente?.adapter = adapterarchivo

                })
                builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                builder.show()
            }
        }



    }

    fun ClickCancelar(view: View){
            if (id==date!!.ID.toInt()){
                postCliente("modificar",date!!.archivo1,date!!.archivo2,date!!.archivo3,date!!.archivo4,date!!.archivo5,"C")

            }



    }



    fun getListPaciente(search: String){
        arraylisP.clear()
        val list=ArrayList<String>()
        CoroutineScope(Dispatchers.IO).launch {

            val call=RetrofitClient.instance.getAllEnlace(IDP.toString())
            val datos: List<EnlaceRespons>? =call.body()
            runOnUiThread{

                if (datos != null) {
                    for (i in 0 until datos.size) {

                        if (search.equals(datos[i].especialidad)){


                            arraylisP.add(datos[i])
                            list.add(datos[i].nameclient)



                        }

                    }
                    val arrayAdapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,list)//
                    with(txtepaciente){
                        setAdapter(arrayAdapter)
                        onItemClickListener = this@TurnoActivity
                    }

                }

            }


        }

    }//
    fun getListProfesional(search: String){
        CoroutineScope(Dispatchers.IO).launch {

            val call=RetrofitClient.instance.getAllListProf("")
            val datos: List<ProfesionalRespons>? =call.body()
            runOnUiThread{

                if (datos != null) {
                    for (i in 0 until datos.size) {

                        if (search.equals(datos[i].IDP)){
                            IDP = datos[i].IDP
                            if(datos[i].img !="null"){
                                Glide.with(applicationContext)
                                    .load(datos[i].img )
                                    .centerCrop()
                                    .into(viewimageperfilc)
                                IMG= datos[i].img
                            }

                            arraylisPres.clear()
                            getTurnoHoraOcupada(IDP.toString())

                            val jsonvalor= JSONObject(datos[i].prestacion)
                            val HistJSONArray=jsonvalor.getJSONArray("dato")

                            if (HistJSONArray.length()>0){
                                for (i in 0 until HistJSONArray.length()){
                                    val jsonObject2= HistJSONArray.getJSONObject(i)
                                    arraylisPres.add(jsonObject2.getString("Prestacion"))
                                }

                            }else{
                                arraylisPres.clear()
                            }

                            val arrayAdapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,arraylisPres)//
                            with(txtespecialidad){
                                setAdapter(arrayAdapter)
                                onItemClickListener = this@TurnoActivity
                            }

                        }


                    }

                }

            }


        }

    }//

    //----------------------------------------------------------------------------------------------Busca los horarios y fecha ocupada del profesional
    private fun getTurnoHoraOcupada(Search:String){
        arraylis.clear()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call=RetrofitClient.instance.getListTurno(IDP.toString())

                val datos: List<TurnoRespons>? =call.body()
                runOnUiThread{
                    if(call.isSuccessful){
                        for (i in 0 until datos?.size!!){
                                arraylis.add(datos[i])
                        }

                    }else{
                        Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                    }
                }
            }catch (e: EOFException){

            }
        }



    }//
    fun  onDateSelected(day:Int,month:Int,year:Int){
        rviewcliente.visibility=View.VISIBLE
        arrayturnhora.clear()
        txtdia.setText("$day/$month/$year")

        val c= Calendar.getInstance()
        val hour:Int=c.get(Calendar.HOUR_OF_DAY)
        val min:Int=c.get(Calendar.MINUTE)

        val dia:Int=c.get(Calendar.DAY_OF_MONTH)
        val mes:Int=c.get(Calendar.MONTH)
        val anno:Int=c.get(Calendar.YEAR)
        var fechahoy=1
        if (month<10){
            fecha = ("$year"+"0"+"$month$day").toInt()
            if(day<10){
                fecha = ("$year"+"0"+"$month"+"0"+"$day").toInt()
            }
        } else if(day<10){
            fecha = ("$year$month"+"0"+"$day").toInt()
        }else{
            fecha = ("$year$month$day").toInt()
        } //Codigo para saber si esta bien la fecha porque el mes se cuenta desde cero

        if (mes<10){
            fechahoy = ("$anno"+"0"+"$mes$dia").toInt()
            if(dia<10){
                fechahoy = ("$anno"+"0"+"$mes"+"0"+"$dia").toInt()
            }
        } else if(dia<10){
            fechahoy = ("$anno$mes"+"0"+"$dia").toInt()
        }else{
            fechahoy = ("$anno$mes$dia").toInt()
        }


//Es utilizable para reducir el codigo de fechas pero nose si sirbe para cuando el mes es menor a 10 o para cuando el dia es menor a 10
        if ("$day/$month/$year">"$dia/$mes/$anno"){

            Toast.makeText(applicationContext, "$day/$month/$year\">\"$dia/$mes/$anno", Toast.LENGTH_LONG).show()

        }
        val listturnhora= arrayOf("09:00","09:15","09:30","09:45","10:00","10:15","10:30","10:45","11:00","11:25","11:45","12:00","12:15","12:30","12:45","13:00","13:15","13:30","13:45","14:00","14:15","14:30","14:45","15:00","15:15","15:30","15:45","16:00","16:15","16:30","16:45","17:00","17:15","17:30","17:45","18:00","18:15","18:30","18:45")

        val array= ArrayList<String>()
        array.addAll(listturnhora)

        if(fecha!=fechahoy){
            if (arraylis.size>0) {
                for ((j,value) in arraylis.withIndex()){
                        if (value.fecha==fecha.toString()){
                            array.removeIf { it==value.hora}
                        }
                }
            }
        }
        else{
            if (arraylis.size>0){
                for ((j,value) in arraylis.withIndex()){
                    if (value.fecha==fecha.toString()){
                        array.removeIf { it==value.hora }
                        array.removeIf {  it < ("0$hour:$min")}

                    }else{
                        array.removeIf {  it < ("0$hour:$min")}

                    }
                }
            }
        }


        for ((i,hora) in array.distinct().withIndex()){

            arrayturnhora.add(HoraTurno(hora.toString(),"$fecha",false))

        }




            val adapterturnhora = AdapterTurnoHora(arrayturnhora, this, this)
        rviewcliente?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rviewcliente?.adapter = adapterturnhora


    }
    fun clickdia(view: View){
        val datePicker = DatePickerFragment{day,month,year -> onDateSelected(day,month,year)}
        datePicker.show(supportFragmentManager,"datePicker")

        getTurnoHoraOcupada(IDP.toString())
    }
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString()

        for (i in 0 until arraylisP.size) {

            if (item == arraylisP[i].nameclient) {
                dni =  arraylisP[i].pacientedni
                getListProfesional(IDP.toString())
                cardhorariosd.visibility=View.VISIBLE
            }
        }
    }

    override fun onHorarioItemClick(hora: String) {
        txtnumdia.setText(hora)
        val listhoraturn= ArrayList<HoraTurno>()

        for (i in 0 until arrayturnhora.size){
            if (arrayturnhora[i].hora==hora){
                listhoraturn.add(HoraTurno(arrayturnhora[i].hora,arrayturnhora[i].fecha,true))

            }else{
                listhoraturn.add(HoraTurno(arrayturnhora[i].hora,arrayturnhora[i].fecha,false))

            }

        }
        val adapterturnhora = AdapterTurnoHora(listhoraturn, this, this)
        rviewcliente?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rviewcliente?.adapter = adapterturnhora
    }

}