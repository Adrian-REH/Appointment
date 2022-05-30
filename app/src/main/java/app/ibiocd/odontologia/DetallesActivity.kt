package app.ibiocd.odontologia

import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.ibiocd.lavanderia.Adapter.Archivos
import app.ibiocd.lavanderia.Adapter.Historial
import app.ibiocd.odontologia.Adapter.AdapterArchivo
import kotlinx.android.synthetic.main.activity_detalles.*
import org.json.JSONException
import org.json.JSONObject


class DetallesActivity : AppCompatActivity(), AdapterArchivo.onArchivoItemClick {
    var HISTORIAL:String?=""
    var FECHA:String?=""
    var JSONAgregadoArchivo:String?=""
    var JSONCompletArchivos:String?=""
    val arraylisH= ArrayList<Historial>()
    var rcviewH: RecyclerView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)
        if(intent.extras !=null){
            HISTORIAL = intent.getStringExtra("historial")
            FECHA = intent.getStringExtra("fecha")
        }


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

    override fun onArchivoItemClick(dato: String) {

        val jsonvalor = JSONObject(JSONCompletArchivos)
        var jsonArray = jsonvalor.getJSONArray("Archivos")
        for (i in 0 until jsonArray.length()){
            var jsonObject= jsonArray.getJSONObject(i)
            if (jsonObject.getString("Url")==dato ||jsonObject.getString("Nombre")==dato ){

            val requst=DownloadManager.Request(Uri.parse(jsonObject.getString("Url")))
                .setTitle(jsonObject.getString("Nombre"))
                .setDescription("Downloading....")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
            val da = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            da.enqueue(requst)

            }

        }
    }
}












