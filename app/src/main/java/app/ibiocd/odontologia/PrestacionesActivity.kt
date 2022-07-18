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

    // val arraylisP= ArrayList<Prestacion>()
    val arraylisPActualizado= ArrayList<Prestacion>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestaciones)
        if(intent.extras !=null){
            arraylisPActualizado.clear()
            JSONCompletPrestacion = intent.getStringExtra("prestacion").toString()
            IDP = intent.getStringExtra("IDP").toString()
            url = intent.getStringExtra("url").toString()

            prestbacktext.setText(intent.getStringExtra("back").toString())

            if (JSONCompletPrestacion!=""){
               // refresh(intent.getStringExtra("prestacion").toString())
                leer(intent.getStringExtra("prestacion").toString())
            }else{



            }
        }

    }

    fun Back(view: View){
        finish()
    }
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
          //  agregarJson(edtxtprest.text.toString(),edtxtdet.text.toString())
            Agregar(edtxtprest.text.toString(),edtxtdet.text.toString())



        })
        builder.setNegativeButton("Close", DialogInterface.OnClickListener { dialogInterface, i ->

        })
        builder.show()
    } //DIALOGO PARA CREAR JSON

   //SUBIR EL ARCHIVO AL SERVIDOR
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
                    Toast.makeText(applicationContext, "Se actualizaron los cambios", Toast.LENGTH_SHORT).show()
                   borrar("S/N")

                }
            })



        }
    }


    /*
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
    */

    //CRUD CON PRESTACION
    fun borrar(nombre:String){

        val array= ArrayList<Prestacion>()
        array.addAll(arraylisPActualizado)
        array.removeIf {  it.name == nombre}
        arraylisPActualizado.clear()

        for ((i,hora) in array.distinct().withIndex()){
            arraylisPActualizado.add(Prestacion(hora.name,hora.detalle))

        }

        //ENLISTO
        val adapterprestacion = AdapterPrestacion(arraylisPActualizado, this, this)
        rviewcliente?.layoutManager = GridLayoutManager(this,2)
        rviewcliente?.adapter = adapterprestacion



    }

    fun Agregar(nombre:String,detalle:String){
        arraylisPActualizado.add(Prestacion(nombre,detalle))

        //ENLISTO
        val adapterprestacion = AdapterPrestacion(arraylisPActualizado, this, this)
        rviewcliente?.layoutManager = GridLayoutManager(this,2)
        rviewcliente?.adapter = adapterprestacion
    }

    fun leer(response:String){
        JSONAgregadoPrestacion=""
        val jsonvalor=JSONObject(response)
        val HistJSONArray=jsonvalor.getJSONArray("dato")

        if (HistJSONArray.length()>0){
            for (i in 0 until HistJSONArray.length()){
                val jsonObject2= HistJSONArray.getJSONObject(i)
                arraylisPActualizado.add(Prestacion(jsonObject2.getString("Prestacion"),jsonObject2.getString("Detalle")))
            }

        }else{
            Toast.makeText(this, "No hay prestaciones ", Toast.LENGTH_SHORT).show()
        }

        //ENLISTO
        val adapterprestacion = AdapterPrestacion(arraylisPActualizado, this, this)
        rviewcliente?.layoutManager = GridLayoutManager(this,2)
        rviewcliente?.adapter = adapterprestacion

    }


    fun Subir(view: View){
        var JSOND=""



        for(dato in arraylisPActualizado){
            var JSON="{\n" +
                    "\"Prestacion\": \"${dato.name}\",\n" +
                    "\"Detalle\": \"${dato.detalle}\"\n" +
                    "}"
            if (JSOND==""){
                JSOND = JSON
            }else if (JSOND!=""){
                JSOND = JSOND+","+JSON
            }
        }
       val JSONC="{\"dato\":[$JSOND]}"

        // CREO EL ARRAY CON LA INFORMACION
        getProfesionalSave(JSONC)
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
            // agregarJson(nombre,edtxtdet.text.toString())
            Agregar(nombre,edtxtdet.text.toString())
        })

        builder.setNegativeButton("Borrar", DialogInterface.OnClickListener { dialogInterface, i ->
            // DeletJson(nombre)
            borrar(nombre)
        })
        builder.setPositiveButton("Close", DialogInterface.OnClickListener { dialogInterface, i ->
            // refresh(JSONCompletPrestacion)
        })

        builder.show()
    }//DIALOGO PARA VER EL JSON
    override fun onPrestacionItemClick(nombre: String) {
        PrestacionDialogSee(nombre,JSONCompletPrestacion)
    } //CLICK EN EL RECYLCER VIEW






}