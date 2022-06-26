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
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_odontograma.*
import kotlinx.android.synthetic.main.activity_odontograma.view.*
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_turno.*
import kotlinx.android.synthetic.main.list_dientesup.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException
import java.lang.Error
import kotlin.Exception
import kotlinx.android.synthetic.main.activity_turno.txteprestacion as txteprestacion1

class OdontogramaActivity : AppCompatActivity(),AdapterDientInfError.onDientInfItemClick,AdapterDientSupError.onDientSupItemClick,AdapterView.OnItemClickListener {

    var name:String?=""
    var url:String?=""
    var correo:String?=""
    var dni:String?=""
    var Ndient:Int=0
    var fecha:Int=0
    var JSONDiente:String?=""
    var JSONDienteT:String?=""
    var Diente:String?=""
    var id:Int=0

    var codigo:Int=0
    var imghelp:Boolean=true

    var JSONODONTOGRAMA: JSONObject? =null
    var JSONArrayODONTOGRAMA: JSONArray? =null

    val arraylistOdnt= ArrayList<Odontograma>()
    val arraylisError= ArrayList<Odontograma>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_odontograma)
        lindienteinf.visibility=View.GONE
        viewimghelp.visibility=View.GONE

        for (j in 1 until 15) {
            arraylisError.add(Odontograma("http://$url/docs/dientes/inf$j.png","http://$url/docs/dientes/sup$j.png"))

        }

        val arrayList32=ArrayList<String>()

        for (i in 0 until 32) {
            arrayList32.add("${i+1}")
        }

        if(intent.extras !=null){
            dni = intent.getStringExtra("dni")
            correo = intent.getStringExtra("correo")
            url = intent.getStringExtra("url")
            name = intent.getStringExtra("name")
            fecha = intent.getStringExtra("fecha").toString().toInt()
            id = intent.getStringExtra("id").toString().toInt()
            codigo = intent.getStringExtra("codigo")?.toInt() ?: 0

        }


        try {
            if (codigo==0){
                for (j in 0 until 33) {

                    var Diente= ("{\n" +
                            " \"d${j+1}\": ${"{\n" +
                                    " \"url1\": \"http://23herrera.xyz:81/appointment/docs/dientes/inf${j+1}.png\",\n" +
                                    " \"url2\": \"http://23herrera.xyz:81/appointment/docs/dientes/sup${j+1}.png\"\n" +
                                    " }"}\n" +
                            " }")

                    if (JSONDiente==""){
                        JSONDiente = Diente
                    }else if (JSONDiente!=""){
                        JSONDiente = JSONDiente+","+Diente
                    }

                }
                JSONDienteT="{\"data\":[$JSONDiente]}"
                JSONODONTOGRAMA= JSONObject(JSONDienteT)
                JSONArrayODONTOGRAMA= JSONODONTOGRAMA!!.getJSONArray("data")

            }
            else if (codigo!=0){
                getOdontogramaDatos(View(applicationContext),false)

            }
        }catch (e:Exception){
            Toast.makeText(this,"$e",Toast.LENGTH_LONG).show()
        }


        val arrayAdapter= ArrayAdapter(this,R.layout.dropdown_item,arrayList32)
        with(txteprestacion){
            setAdapter(arrayAdapter)
            onItemClickListener = this@OdontogramaActivity

        }
    }

    fun getOdontogramaDatos(view: View,back:Boolean){

        CoroutineScope(Dispatchers.IO).launch {
        try {
            val call=RetrofitClient.instance.getOdontograma(dni.toString(),correo.toString(),fecha.toString())
            val datos: OdontogramaRespons? =call.body()
            runOnUiThread{
                if(call.isSuccessful){
                    Toast.makeText(applicationContext,"Se encontro un odontograma",Toast.LENGTH_LONG).show()
                    if(datos != null){
                        if(codigo==datos.ID){

                            JSONDienteT="{\"data\":[${datos.dientes}]}"
                            JSONODONTOGRAMA= JSONObject(JSONDienteT)
                            JSONArrayODONTOGRAMA= JSONODONTOGRAMA!!.getJSONArray("data")

                            for (j in 0 until 32) {
                                val jsonObject= JSONArrayODONTOGRAMA!!.getJSONObject(j)
                                val JsonOdont=JSONObject(jsonObject?.getString("d${j+1}").toString())
                                arraylistOdnt.add(Odontograma(JsonOdont.getString("url1").toString(),JsonOdont.getString("url2").toString()))
                            }



                        }
                    }
                }
                else{
                    Toast.makeText(applicationContext,"ODONTOGRAMA no encontrado",Toast.LENGTH_LONG).show()
                }
            }

        }catch (e:Exception){

        }

        }
    }

    fun getOdontogramaSave(view: View,back: Boolean){

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val call=RetrofitClient.instance.getOdontograma(dni.toString(),correo.toString(),fecha.toString())
                val datos: OdontogramaRespons? =call.body()
                runOnUiThread{
                if (datos != null) {
                    if(call.isSuccessful){

                        if (codigo==0){
                            Toast.makeText(applicationContext,"Se encontro un Odontograma pero necesitas uno nuevo",Toast.LENGTH_LONG).show()
                            postOdontograma("insertar",back)
                        }else{
                            Toast.makeText(applicationContext,"Se encontro un Odontograma",Toast.LENGTH_LONG).show()
                            postOdontograma("modificar",back)
                        }
                    }else{
                        Toast.makeText(applicationContext,"No se encontro un odontograma o hay un error del srv",Toast.LENGTH_LONG).show()
                       // postOdontograma("insertar",back)
                    }
                }
                }
            }catch (e:Exception){
                if (codigo==0){
                    postOdontograma("insertar",back)
                }
            }
        }
    }

    private fun postOdontograma(accion:String,back: Boolean) {
        JSONDiente=""

        for (j in 0 until JSONArrayODONTOGRAMA!!.length()) {
                val jsonObject= JSONArrayODONTOGRAMA!!.getJSONObject(j)
                val JsonOdont=JSONObject(jsonObject?.getString("d${j+1}").toString())
                if(Ndient-1== j){
                    if (JSONDiente==""){
                        JSONDiente = Diente
                    }else if (JSONDiente!=""){
                        JSONDiente = JSONDiente+","+Diente
                    }

                }else
                {
                    var Diente= ("{\n" +
                            " \"d${j+1}\": ${"{\n" +
                                    " \"url1\": \"${JsonOdont.getString("url1")}\",\n" +
                                    " \"url2\": \"${JsonOdont.getString("url2")}\"\n" +
                                    " }"}\n" +
                            " }")
                    if (JSONDiente==""){
                        JSONDiente = Diente
                    }else if (JSONDiente!=""){
                        JSONDiente = JSONDiente+","+Diente
                    }
                }
            }







        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postOdontograma(JSONDiente.toString(),codigo.toInt(),"${correo}","${dni}",fecha.toString(),accion,accion)
            call.enqueue(object : Callback<OdontogramaRespons> {
                override fun onFailure(call: Call<OdontogramaRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<OdontogramaRespons>, response: retrofit2.Response<OdontogramaRespons>) {
                    Toast.makeText(applicationContext,"Odontograma guardado",Toast.LENGTH_LONG).show()
                    codigo=response.body()!!.ID
                    if (back){
                        val intent = Intent(applicationContext, TurnoActivity::class.java)
                        intent.putExtra("url",url)
                        intent.putExtra("correo",correo)
                        intent.putExtra("dni",dni)
                        intent.putExtra("name",name)
                        intent.putExtra("fecha",fecha.toString())
                        intent.putExtra("id",id.toString())
                        intent.putExtra("codigo",response.body()!!.ID.toString())
                        startActivity(intent)
                        finish()

                    }



                }
            })



        }
    }

    fun dialog2(view: View){
        if (Ndient!=0){
            arraylisError.clear()

            btnhelp.visibility=View.GONE
            carddientfuera.visibility=View.GONE
            lindientesup.visibility=View.VISIBLE
            arraylisError.clear()
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))

            val adaptercliente = AdapterDientSupError(arraylisError, this, this)
            rcydientesup?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

            rcydientesup?.adapter = adaptercliente
        }else{
            Toast.makeText(this,"Por favor selecciona un numero de Diente",Toast.LENGTH_SHORT).show()

        }

    }

    fun dialog1(view: View){
        if (Ndient!=0){
            arraylisError.clear()
            btnhelp.visibility=View.GONE
            carddientedent.visibility=View.GONE
            lindienteinf.visibility=View.VISIBLE
            arraylisError.clear()
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))
            arraylisError.add(Odontograma("https://cdn-icons-png.flaticon.com/512/91/91160.png","https://cdn-icons-png.flaticon.com/512/91/91160.png"))

            val adaptercliente = AdapterDientInfError(arraylisError, this, this)
            rcydienteinf?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rcydienteinf?.adapter = adaptercliente

        }else{
            Toast.makeText(this,"Por favor selecciona un numero de Diente",Toast.LENGTH_SHORT).show()
        }


    }

    fun Back(view: View){
            getOdontogramaSave(View(applicationContext),true)

    }

    fun help(view: View){

        if (imghelp){
            imghelp=false
            viewimghelp.visibility=View.VISIBLE
            carddientedent.visibility=View.GONE
            //lindienteinf.visibility=View.GONE
            carddientfuera.visibility=View.GONE
           // lindientesup.visibility=View.GONE
        }else if (!imghelp) {
            imghelp=true
            viewimghelp.visibility=View.GONE
            carddientedent.visibility=View.VISIBLE
          //  lindienteinf.visibility=View.VISIBLE
            carddientfuera.visibility=View.VISIBLE
        //    lindientesup.visibility=View.VISIBLE
        }


    }

    override fun onDientInfItemClick(url1: String) {
        btnhelp.visibility=View.VISIBLE
        carddientedent.visibility=View.VISIBLE
        lindienteinf.visibility=View.GONE
        var dientn= Ndient!!.toInt()-1
        val jsonObject= JSONArrayODONTOGRAMA!!.getJSONObject(dientn!!.toInt())
        val JsonOdont=JSONObject(jsonObject?.getString("d$Ndient").toString())

        Diente= ("{\n" +
                " \"d$Ndient\": ${"{\n" +
                        " \"url1\": \"${url1}\",\n" +
                        " \"url2\": \"${JsonOdont.getString("url2")}\"\n" +
                        " }"}\n" +
                " }")
        Glide.with(this)
            .load(url1)
            .into(dienteinf)
    }

    override fun onDientSupItemClick(url2: String) {
        btnhelp.visibility=View.VISIBLE

        carddientfuera.visibility=View.VISIBLE
        lindientesup.visibility=View.GONE
        var dientn= Ndient!!.toInt()-1
        val jsonObject= JSONArrayODONTOGRAMA!!.getJSONObject(dientn!!.toInt())
        val JsonOdont=JSONObject(jsonObject?.getString("d$Ndient").toString())


        Diente= ("{\n" +
                " \"d$Ndient\": ${"{\n" +
                        " \"url1\": \"${JsonOdont.getString("url1")}\",\n" +
                        " \"url2\": \"${url2}\"\n" +
                        " }"}\n" +
                " }")
        Glide.with(this)
            .load(url2)
            .into(dientesup)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Ndient = parent?.getItemAtPosition(position).toString().toInt()
        var dientn= Ndient!!.toInt()-1
        val jsonObject= JSONArrayODONTOGRAMA!!.getJSONObject(dientn!!.toInt())

        val JsonOdont=JSONObject(jsonObject?.getString("d$Ndient").toString())
            Glide.with(this)
                .load(JsonOdont.getString("url1").toString())
                .into(dienteinf)

            Glide.with(this)
                .load(JsonOdont.getString("url2").toString())
                .into(dientesup)

    }

}

