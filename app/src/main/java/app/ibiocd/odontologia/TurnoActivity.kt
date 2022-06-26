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
import kotlinx.android.synthetic.main.activity_sign.*
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
    var correo:String?=""
    var dni:String?=""
    var name:String?=""

    val arraylisP= ArrayList<String>()
    val arraylisPres= ArrayList<String>()
    val displayListC=ArrayList<Archivos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turno)
        btnodontologia.visibility=View.GONE
        rviewcliente.visibility=View.GONE
        if(intent.extras !=null){
            dni = intent.getStringExtra("dni")
            correo = intent.getStringExtra("correo")
            url = intent.getStringExtra("url")
            name = intent.getStringExtra("name")
            fecha= intent.getStringExtra("fecha").toString().toInt()

            codigo= intent.getStringExtra("codigo").toString().toInt()

            id= intent.getStringExtra("id").toString().toInt()
            //Toast.makeText(this,"$codigo",Toast.LENGTH_LONG).show()
            turnobacktext.setText(intent.getStringExtra("back").toString())
            if (id != 0){
                getListTurnos()

                if (codigo!=0){
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


            }
        }
    }

    fun ClickOdontograma(view: View){
        getTurnoSave(View(applicationContext))

        val intent = Intent(this, OdontogramaActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("correo",correo)
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
                val call=RetrofitClient.instance.getListTurnodc(dni.toString(),correo.toString())
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


                                txtdia.text="$annosigno/$messigno/$diasigno"
                                txtespecialidad?.setText(espc)

                                if(espc=="ODONTOLOGIA"){
                                    btnodontologia.visibility=View.VISIBLE
                                }

                                txtepaciente?.setText(namepac)
                                txteprofesional?.setText(nameprof)
                                txteprestacion?.setText(prest)
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
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        intent.putExtra("name",name)
        startActivity(intent)
    }

    fun Back(view: View){
        getTurnoSave(View(applicationContext))
        finish()
    }

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

                    val call=RetrofitClient.instance.getListTurnodc(dni.toString(),correo.toString())
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
                                    }else{
                                        //postCliente("insertar","","","","","","E")

                                    }

                            }else{
                               // postCliente("insertar","","","","","","E")
                                //postCliente("insertar","","E")
                            }
                        }
                    }


                }catch (e: Exception){
                   // postCliente("insertar","","","","","","E")
                }
            }
    }//

    //----------------------------------------------------------------------------------------------Guarda los datos del Turno
    private fun postCliente(accion:String,archivo1:String,archivo2:String,archivo3:String,archivo4:String,archivo5:String,estado:String) {

        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postTurno(correo.toString(),txtepaciente.text.toString(),name.toString(),txtespecialidad.text.toString(),txteprestacion.text.toString(),dni.toString(),fecha.toString(),"${txtnumdia.text.toString()}",txtcomentario.text.toString(),estado,IMG.toString(),archivo1,archivo2,archivo3,archivo4,archivo5,accion,accion)
            //val call=RetrofitClient.instance.postTurno(correo.toString(),name.toString(),txteprofesional.text.toString(),txtespecialidad.text.toString(),txteprestacion.text.toString(),dni.toString(),fecha.toString(),"${txthora.text.toString()}",txtcomentario.text.toString(),estado,IMG.toString(),JSONCompletArchivos,accion,accion)
            call.enqueue(object : Callback<TurnoRespons> {
                override fun onFailure(call: Call<TurnoRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<TurnoRespons>, response: retrofit2.Response<TurnoRespons>) {
                    Toast.makeText(applicationContext,"El Turno se $accion ", Toast.LENGTH_SHORT).show()
                    arraylisP.clear()
                    arraylisPres.clear()
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
            intent.putExtra("correo",correo)
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
}