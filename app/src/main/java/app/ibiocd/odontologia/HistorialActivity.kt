package app.ibiocd.odontologia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.lavanderia.Adapter.Historial
import app.ibiocd.odontologia.Adapter.AdapterClienteA
import app.ibiocd.odontologia.Adapter.AdapterHistorial
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_paciente.*
import kotlinx.android.synthetic.main.activity_turno.*
import org.json.JSONException
import org.json.JSONObject

class HistorialActivity : AppCompatActivity(), AdapterHistorial.onHistorialItemClick {
    var JSONCompletHistorial:String?=""
    var JSONHistorial:String?=""
    var url:String?=""
    var correo:String?=""
    var dni:String?=""
    var HISTORIAL:String?=""
    val arraylisH= ArrayList<Historial>()
    var rcviewH: RecyclerView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)
        rcviewH=findViewById(R.id.rviewcliente)
        if(intent.extras !=null){
            dni = intent.getStringExtra("dni")
            correo = intent.getStringExtra("correo")
            url = intent.getStringExtra("url")
        }
        SearchHistorial()

    }
    fun ListHistorial(historial:String){
        arraylisH.clear()

        if ( correo != ""){
            val jsonvalor = JSONObject(historial)
            val HistJSONArray=jsonvalor.getJSONArray("Historial")
            for (i in 0 until HistJSONArray.length()){
                var jsonObject2= HistJSONArray.getJSONObject(i)
                arraylisH.add(Historial(jsonObject2.getString("Prestacion"),jsonObject2.getString("Especialidad"),jsonObject2.getString("Fecha")))

            }

        }

        val adapterhistorial = AdapterHistorial(arraylisH, this, this)
        rcviewH?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcviewH?.adapter = adapterhistorial



    }
    fun Back(view:View){
        finish()
    }
    fun SearchHistorial(){
        val queue = Volley.newRequestQueue(this)
        val url = "http://$url/enlace.php?pacientedni=${dni}&profecionalemail=${correo}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
                ListHistorial(response.getString("historial").toString())
                HISTORIAL=response.getString("historial").toString()
            }, { error ->
            }
        )
        queue.add(jsonObjectRequest)
    }

    override fun onHistorialItemClick(fecha: String) {
        val intent = Intent(this, DetallesActivity::class.java)
         intent.putExtra("historial",HISTORIAL)
         intent.putExtra("fecha",fecha)

        startActivity(intent)
    }

}