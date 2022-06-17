package app.ibiocd.odontologia

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.odontologia.Adapter.AdapterClienteA
import app.ibiocd.odontologia.Adapter.AdapterClienteB
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_inicio.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_perfil.*
import org.json.JSONException

class InicioActivity : AppCompatActivity(), AdapterClienteA.onClienteItemClick, AdapterClienteB.onClienteItemClick {
    var correo:String?=""
    var clientes:String?=""
    var name:String?=""
    var url:String?=""
    var DATOS:String?=""

    lateinit var swipeContainer: SwipeRefreshLayout

     val arraylisConexion= ArrayList<String>()
     val displayListConexion= ArrayList<String>()

     val arraylisTurno= ArrayList<String>()
     val displayListTurno= ArrayList<String>()

     val arraylisC= ArrayList<Clientes>()
     val displayListC= ArrayList<Clientes>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        arraylisC.clear()
        arraylisTurno.clear()
        arraylisConexion.clear()
        displayListTurno.clear()
        displayListConexion.clear()
        cvverifI.visibility=View.GONE

        if(intent.extras !=null){
            correo = intent.getStringExtra("correo")
            url = intent.getStringExtra("url")
            Refresh()
        }

        swipe_container.setOnRefreshListener  {
            buscarlistadeClientes()
            buscarlistadeClientesenlace()
            ComprobarDatos()


        }

        buscarlistadeClientes()
        buscarlistadeClientesenlace()
        ComprobarDatos()

        clickperfil.setOnClickListener { ClickPerfil("") }
        edtxtsearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val search=s.toString()

                ListClienteA(search)
                ListClienteB(search)


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
        })


    }

    fun Refresh(){
        val queue = Volley.newRequestQueue(this)
        val url = "http://$url/cuentas.php?correo=${correo}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray("data")
                    val jsonObject = jsonArray.getJSONObject(0)
                    name = jsonObject.getString("nombreapellido").toString()
                    correo = jsonObject.getString("correo").toString()
                    val image= jsonObject.getString("img").toString()
                    val verif= jsonObject.getString("verificar").toString()

                    if (verif=="V"){
                        cvverifI.visibility=View.VISIBLE

                    }
                    Glide.with(this)
                        .load(image)
                        .centerCrop()
                        .into(viewminiperfil)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { error ->
            }
        )
        queue.add(jsonObjectRequest)
    }

    fun ListClienteA(search: String){
        //Limpio
        displayListC.clear()
        if (displayListTurno.size>0){


            //GENERA LA LISTA
            for (j in 0 until displayListTurno.size) {
               clientes = displayListTurno[j]
                val queue = Volley.newRequestQueue(this)
                val url = "http://$url/clientes.php?dni=$clientes"
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    { response ->
                        swipe_container.isRefreshing=false
                        try {
                            val jsonArray = response.getJSONArray("data")
                            val jsonObject = jsonArray.getJSONObject(0)
                            val name = jsonObject.getString("nombreapellido").toString()
                            val direc = jsonObject.getString("direccion").toString()
                            val telefono = jsonObject.getString("celular").toString()
                            val email = jsonObject.getString("correo").toString()
                            val hist = jsonObject.getString("historial").toString()
                            val image= jsonObject.getString("img").toString()
                            val dni= jsonObject.getString("dni").toString()
                            if (search==dni || search==name){
                                displayListC.add(Clientes(name, telefono, dni,hist,image,direc,email,image))
                            }else if (search==""){
                                displayListC.add(Clientes(name, telefono, dni,hist,image,direc,email,image))
                            }

                            val adapterclientea = AdapterClienteA(displayListC, this, this)
                            rviewclientea?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                            rviewclientea?.adapter = adapterclientea
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, { error ->
                        Toast.makeText(this,"Error1: $error", Toast.LENGTH_SHORT).show()
                    }
                )
                queue.add(jsonObjectRequest)
            }


        }else{
            val adapterclienteb = AdapterClienteB(arraylisC, this, this)
            rviewclienteb?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rviewclienteb?.adapter = adapterclienteb

        }

    }
    fun ListClienteB(search: String){
        //Limpio
        arraylisC.clear()
        if ( displayListConexion.size>0){


        //GENERA LA LISTA
        for (j in 0 until displayListConexion.size) {
            clientes = displayListConexion[j]
            val queue = Volley.newRequestQueue(this)
            val url = "http://$url/clientes.php?dni=$clientes"
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    swipe_container.isRefreshing=false
                    try {
                        val jsonArray = response.getJSONArray("data")
                        val jsonObject = jsonArray.getJSONObject(0)
                        val name = jsonObject.getString("nombreapellido").toString()
                        val direc = jsonObject.getString("direccion").toString()
                        val telefono = jsonObject.getString("celular").toString()
                        val email = jsonObject.getString("correo").toString()
                        val hist = jsonObject.getString("historial").toString()
                        val image= jsonObject.getString("img").toString()
                        val dni= jsonObject.getString("dni").toString()
                        if (search.equals(dni) || search.equals(name)){
                            arraylisC.add(Clientes(name, telefono, dni,hist,image,direc,email,image))
                        }else if (search==""){
                            arraylisC.add(Clientes(name, telefono, dni,hist,image,direc,email,image))
                        }

                        val adapterclienteb = AdapterClienteB(arraylisC, this, this)
                        rviewclienteb?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        rviewclienteb?.adapter = adapterclienteb


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }


                }, {
                        error ->  Toast.makeText(this,"Error2: $error", Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(jsonObjectRequest)
        }

        }else{
            val adapterclienteb = AdapterClienteB(arraylisC, this, this)
            rviewclienteb?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rviewclienteb?.adapter = adapterclienteb

        }


    }
    fun ClickBack(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("DATA","A")
        startActivity(intent)
        finish()
    }
    fun ClickPerfil(DATO:String){
        val intent = Intent(this, PerfilActivity::class.java)
        intent.putExtra("correo",correo)
        intent.putExtra("url",url)
        intent.putExtra("DATOS",DATO)
        startActivity(intent)
    }
    fun buscarlistadeClientes(){
        //Limpio
        arraylisC.clear()
        displayListC.clear()
        arraylisTurno.clear()
        displayListTurno.clear()

        //GENERA LA LISTA
        val queue = Volley.newRequestQueue(this)
        val url = "http://$url/turno.php?correoprofesional=$correo"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
                try{
                    val jsonArray = response.getJSONArray("data")

                    for (i in 0 until jsonArray.length()){
                        val jsonObject= jsonArray.getJSONObject(i)
                        val dni= jsonObject.getString("dni").toString()
                        val turnoid= jsonObject.getString("turnoid").toString()
                        val fecha= jsonObject.getString("fecha").toString()
                        //USAR LOS DATOS DE FECHA PARA RESTRINGIR EL LLENADO DEL ARRAYLISTTURNO

                        //condicional de fecha
                        arraylisTurno.add(dni)}


                    displayListTurno.addAll(arraylisTurno)
                    ListClienteA("")
                }catch (e: JSONException){ e.printStackTrace() }



            }, {error ->

            })
        queue.add(jsonObjectRequest)
    }//Busco los clientes que tienen turnos
    fun buscarlistadeClientesenlace(){
        //Limpio
        arraylisC.clear()
        displayListC.clear()
        displayListConexion.clear()
        arraylisConexion.clear()
        //GENERA LA LISTA
        val queue = Volley.newRequestQueue(this)
        val url = "http://$url/enlace.php?profecionalemail=$correo"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
                try{
                    val jsonArray = response.getJSONArray("data")

                    for (i in 0 until jsonArray.length()){
                        val jsonObject= jsonArray.getJSONObject(i)
                        val dni= jsonObject.getString("pacientedni").toString()
                        val estp= jsonObject.getString("estadoprofecional").toString()
                        if (estp=="OCUPADO"){
                            arraylisConexion.add(dni)
                        }
                        //USAR LOS DATOS DE FECHA PARA RESTRINGIR EL LLENADO DEL ARRAYLISTTURNO
                    }

                    displayListConexion.addAll(arraylisConexion)

                    ListClienteB("")
                }catch (e: JSONException){ e.printStackTrace() }



            }, {error ->

            })
        queue.add(jsonObjectRequest)
    }//Busco los  clientes que tuvieron turno o se registraron con el profesional

    override fun onClienteAItemClick(dni: String) {//Ingresa al perfil del paciente sabiendo que tiene turno el mismo dia
        val intent = Intent(this, TurnoActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        intent.putExtra("name",name)
        intent.putExtra("JSONODONT","")
        startActivity(intent)
    }
    override fun onClienteBItemClick(dni: String) {//Ingresa al perfil del paciente sin saber que tiene turno el mismo dia
        val intent = Intent(this, PacienteActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        startActivity(intent)
    }
    fun ComprobarDatos(){
        val queue = Volley.newRequestQueue(this)
        val url = "http://$url/cuentas.php?correo=${correo}"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray("data")
                    val jsonObject = jsonArray.getJSONObject(0)
                    val name = jsonObject.getString("nombreapellido").toString()

                    if (name=="null" || name==""){
                        ClickPerfil("LLENARPERFIL")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { error ->
                Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(jsonObjectRequest)

    }
}