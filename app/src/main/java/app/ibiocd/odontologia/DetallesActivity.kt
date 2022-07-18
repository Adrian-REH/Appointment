package app.ibiocd.odontologia

import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.ibiocd.lavanderia.Adapter.Archivos
import app.ibiocd.lavanderia.Adapter.Historial
import app.ibiocd.lavanderia.Adapter.TurnoRespons
import app.ibiocd.odontologia.Adapter.AdapterArchivo
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detalles.*
import kotlinx.android.synthetic.main.activity_detalles.rviewcliente
import kotlinx.android.synthetic.main.activity_paciente.*
import kotlinx.android.synthetic.main.activity_turno.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class DetallesActivity : AppCompatActivity(), AdapterArchivo.onArchivoItemClick {
    var HISTORIAL:String?=""
    var FECHA:String?=""
    var NAME:String?=""
    var ID:String?=""
    var URL:String?=""
    var DNI:String?=""
    var codigo=0

    //var CORREO:String?=""
    var BACKTXT:String?=""
    var IDP:String?=""
    var JSONAgregadoArchivo:String?=""
    var JSONCompletArchivos:String?=""
    val arraylisH= ArrayList<Historial>()
    var rcviewH: RecyclerView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)
        if(intent.extras !=null){
            IDP = intent.getStringExtra("IDP")
            URL = intent.getStringExtra("url")
            FECHA = intent.getStringExtra("fecha")
            DNI = intent.getStringExtra("dni")

            BACKTXT=intent.getStringExtra("back")
            detbacktext.setText("$BACKTXT")
        }
        getListTurnos()

/*
        if ( HISTORIAL != ""){
            val jsonvalorH = JSONObject(HISTORIAL)
            val HistJSONArray=jsonvalorH.getJSONArray("Historial")
            for (i in 0 until HistJSONArray.length()){
                var jsonObject2= HistJSONArray.getJSONObject(i)

                if (FECHA==jsonObject2.getString("Fecha")){
                    fechahist.text= jsonObject2.getString("Fecha")
                    horahist.text= jsonObject2.getString("Hora")
                    presthist.text=jsonObject2.getString("Prestacion")
                    srviciohist.text=jsonObject2.getString("Especialidad")
                    detallhist.text=jsonObject2.getString("Comentario")


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
                    ClickJson(View(applicationContext))



                }



            }

        }
*/



    }
    fun ClickJson(view: View){
        try{

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
            rviewcliente?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rviewcliente?.adapter = adapterarchivo
            rviewcliente.visibility=View.VISIBLE
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
    fun Back(view: View){
        finish()
    }
    private fun getListTurnos(){
        val displayListC=ArrayList<Archivos>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val call=RetrofitClient.instance.getListTurnodc(DNI.toString(),IDP.toString())
                val datos: List<TurnoRespons>? =call.body()
                runOnUiThread{

                    for (i in 0 until datos?.size!!){

                        if(call.isSuccessful){
                            if (FECHA==datos[i].fecha){
                                ID=datos[i].ID.toString()
                                NAME=datos[i].nameprof.toString()
                                fechahist.text= datos[i].fecha
                                horahist.text= datos[i].hora
                                presthist.text=datos[i].prestacion
                                srviciohist.text=datos[i].especialidad
                                detallhist.text=datos[i].comentario


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
                                else if (datos[i].archivo2!=""){
                                    if (JSONObject(datos[i].archivo2).getString("Nombre").toString()=="ODONTOGRAMA"){
                                        if (JSONObject(datos[i].archivo2).getString("Url").toString()!=codigo.toString()){
                                            codigo= JSONObject(datos[i].archivo2).getString("Url").toString().toInt()
                                            displayListC.add(Archivos(JSONObject(datos[i].archivo2).getString("Nombre").toString(), JSONObject(datos[i].archivo2).getString("Url").toString()))

                                        }
                                    }else{
                                        displayListC.add(Archivos(JSONObject(datos[i].archivo2).getString("Nombre").toString(), JSONObject(datos[i].archivo2).getString("Url").toString()))

                                    }
                                }
                                else if (datos[i].archivo3!=""){

                                    if (JSONObject(datos[i].archivo3).getString("Nombre").toString()=="ODONTOGRAMA"){
                                        if (JSONObject(datos[i].archivo3).getString("Url").toString()!=codigo.toString()){
                                            codigo= JSONObject(datos[i].archivo3).getString("Url").toString().toInt()
                                            displayListC.add(Archivos(JSONObject(datos[i].archivo3).getString("Nombre").toString(), JSONObject(datos[i].archivo3).getString("Url").toString()))

                                        }
                                    }else{
                                        displayListC.add(Archivos(JSONObject(datos[i].archivo3).getString("Nombre").toString(), JSONObject(datos[i].archivo3).getString("Url").toString()))

                                    }
                                }
                                else if (datos[i].archivo4!=""){

                                    if (JSONObject(datos[i].archivo4).getString("Nombre").toString()=="ODONTOGRAMA"){
                                        if (JSONObject(datos[i].archivo4).getString("Url").toString()!=codigo.toString()){
                                            codigo= JSONObject(datos[i].archivo4).getString("Url").toString().toInt()
                                            displayListC.add(Archivos(JSONObject(datos[i].archivo4).getString("Nombre").toString(), JSONObject(datos[i].archivo4).getString("Url").toString()))

                                        }
                                    }else{
                                        displayListC.add(Archivos(JSONObject(datos[i].archivo4).getString("Nombre").toString(), JSONObject(datos[i].archivo4).getString("Url").toString()))

                                    }
                                }
                                else if (datos[i].archivo5!=""){
                                    if (JSONObject(datos[i].archivo5).getString("Nombre").toString()=="ODONTOGRAMA"){
                                        if (JSONObject(datos[i].archivo5).getString("Url").toString()!=codigo.toString()){
                                            codigo= JSONObject(datos[i].archivo5).getString("Url").toString().toInt()
                                            displayListC.add(Archivos(JSONObject(datos[i].archivo5).getString("Nombre").toString(), JSONObject(datos[i].archivo5).getString("Url").toString()))

                                        }
                                    }else{
                                        displayListC.add(Archivos(JSONObject(datos[i].archivo5).getString("Nombre").toString(), JSONObject(datos[i].archivo5).getString("Url").toString()))

                                    }
                                }

                                rviewcliente.visibility=View.VISIBLE

                                val adapterarchivo = AdapterArchivo(displayListC, applicationContext, this@DetallesActivity)
                                rviewcliente?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                                rviewcliente?.adapter = adapterarchivo

                                // Historial(response.getString("fecha").toString())
                            }
                        }else{
                            Toast.makeText(applicationContext,"Error", Toast.LENGTH_LONG).show()
                        }
                    }


                }

            }catch (e:Exception){

            }

        }
    }//
    fun ClickOdontograma(view: View){
        val intent = Intent(this, OdontogramaActivity::class.java)
        intent.putExtra("url",URL)
        intent.putExtra("IDP",IDP)
        intent.putExtra("dni",DNI)
        intent.putExtra("name",NAME)
        intent.putExtra("codigo",codigo)
        intent.putExtra("id",ID)
        intent.putExtra("fecha",FECHA)

        intent.putExtra("back",titledetalles.text.toString())
        startActivity(intent)
        finish()

    }
    override fun onArchivoItemClick(url: String, name: String, download: Boolean, ver: Boolean) {

         if (download ){

            val requst=DownloadManager.Request(Uri.parse(url))
                .setTitle(name)
                .setDescription("Downloading....")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
            val da = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            da.enqueue(requst)

         }else if(name=="ODONTOGRAMA"){
                ClickOdontograma(View(applicationContext))
         }


    }


}












