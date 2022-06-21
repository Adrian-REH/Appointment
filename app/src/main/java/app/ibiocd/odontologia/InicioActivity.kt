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
import app.ibiocd.lavanderia.Adapter.*
import app.ibiocd.odontologia.Adapter.AdapterClienteA
import app.ibiocd.odontologia.Adapter.AdapterClienteB
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_inicio.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.EOFException
import java.lang.Exception

class InicioActivity : AppCompatActivity(), AdapterClienteA.onClienteItemClick, AdapterClienteB.onClienteItemClick {
    var correo:String?=""
    var clientes:String?=""
    var name:String?=""
    var url:String?=""

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
        displayListC.clear()
        arraylisTurno.clear()
        arraylisConexion.clear()
        displayListTurno.clear()
        displayListConexion.clear()
        cvverifI.visibility=View.GONE

        if(intent.extras !=null){
            correo = intent.getStringExtra("correo")
            url = intent.getStringExtra("url")
        }

        swipe_container.setOnRefreshListener  {
            getListTurnos()
            getListEnlace()
            getComprobarCliente()


        }

        getListTurnos()
        getListEnlace()
        getComprobarCliente()

        clickperfil.setOnClickListener { ClickPerfil("") }
        edtxtsearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val search=s.toString()

                getListClientesTurnos(search)
                getListClientes(search)


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
        })


    }

/*
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
                                displayListC.add(Clientes(name, telefono, dni,image,direc,email,image,""))
                            }else if (search==""){
                                displayListC.add(Clientes(name, telefono, dni,image,direc,email,image,""))
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
*/

    fun getListClientes(search: String){
        for (j in 0 until displayListConexion.size) {
            var clientes = displayListConexion[j]
            CoroutineScope(Dispatchers.IO).launch {
                val call=RetrofitClient.instance.getCliente("$clientes")
                runOnUiThread{

                    if (call.isSuccessful){
                        val datos: ClienteRespons? = call.body()
                        swipe_container.isRefreshing=false

                        val name =  datos?.name ?: ""
                        val direc =  datos?.direccion ?: ""
                        val telefono =  datos?.cel ?: ""
                        val email =  datos?.correo ?: ""
                        val image=  datos?.img ?: ""
                        val dni=  datos?.dni ?: ""

                        if (search.equals(dni) || search.equals(name)){
                            arraylisC.add(Clientes(name, telefono, dni,image,direc,email,image,""))
                        }else if (search==""){
                            arraylisC.add(Clientes(name, telefono, dni,image,direc,email,image,""))
                        }


                        val adapterclienteb = AdapterClienteB(arraylisC, applicationContext, this@InicioActivity)
                        rviewclienteb?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                        rviewclienteb?.adapter = adapterclienteb

                    }else
                    {

                    }



                }
            }

        }

    }
    fun getListClientesTurnos(search: String){

        displayListC.clear()
        if (displayListTurno.size>0){
            for (j in 0 until displayListTurno.size) {
                var dni = displayListTurno[j]
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val call=RetrofitClient.instance.getCliente("$dni")
                        runOnUiThread{

                            if (call.isSuccessful){
                                val datos: ClienteRespons? = call.body()
                                swipe_container.isRefreshing=false

                                val name =  datos?.name ?: ""
                                val direc =  datos?.direccion ?: ""
                                val telefono =  datos?.cel ?: ""
                                val email =  datos?.correo ?: ""
                                val image=  datos?.img ?: ""
                                val dni=  datos?.dni ?: ""

                                if (search.equals(dni) || search.equals(name)){
                                    arraylisC.add(Clientes(name, telefono, dni,image,direc,email,image,arraylisTurno[j] ))
                                }else if (search==""){
                                    arraylisC.add(Clientes(name, telefono, dni,image,direc,email,image,arraylisTurno[j] ))
                                }

                                val adapterclientea = AdapterClienteA(arraylisC, applicationContext, this@InicioActivity)
                                rviewclientea?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                                rviewclientea?.adapter = adapterclientea


                            }else{

                            }



                        }
                    }catch (e:Exception){

                    }

                }
            }
        }
    }

/*
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
                            arraylisC.add(Clientes(name, telefono, dni,image,direc,email,image,""))
                        }else if (search==""){
                            arraylisC.add(Clientes(name, telefono, dni,image,direc,email,image,"" ))
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
*/

    fun getComprobarCliente(){
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.getProfesional("$correo")
            runOnUiThread{
                val datos: ProfesionalRespons? = call.body()
                val nombre = datos?.nameprof ?: ""
                val verif = datos?.verificar ?: ""
                if (verif=="V"){
                    cvverifI.visibility=View.VISIBLE

                }
                if (nombre=="null" || nombre==""){
                    ClickPerfil("LLENARPERFIL")
                }else{
                    name = nombre
                    correo = datos?.correo ?: ""
                    Glide.with(this@InicioActivity)
                        .load(datos?.img ?: "")
                        .centerCrop()
                        .into(viewminiperfil)

                }


            }
        }
    }

/*
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
*/
    fun getListEnlace(){
        arraylisConexion.clear()
        displayListConexion.clear()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val call=RetrofitClient.instance.getAllEnlace(correo.toString())//turno.php

                    runOnUiThread{
                        val datos: List<EnlaceRespons>? = call.body()
                        if (datos!=null){
                            for (i in 0 until datos?.size!!){
                                val dni = datos[i].pacientedni
                                val estp = datos[i].estado
                                if (estp=="OCUPADO"){
                                    arraylisConexion.add(dni)
                                }
                            }

                            displayListConexion.addAll(arraylisConexion)
                            getListClientes("")


                        }

                    }
                }catch (e: EOFException){
                    Toast.makeText(applicationContext,"Error: $e",Toast.LENGTH_LONG).show()

                }

            }



    }
    fun getListTurnos(){

        arraylisTurno.clear()
        displayListTurno.clear()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call=RetrofitClient.instance.getListTurno(correo.toString())//turno.php
                runOnUiThread{
                    val datos: List<TurnoRespons>? = call.body()
                    if (datos!=null){
                        for (i in 0 until datos.size){
                            arraylisTurno.add(datos[i].fecha)
                            displayListTurno.add(datos[i].dni)

                        }


                        getListClientesTurnos("")


                    }

                }
            }catch (e: EOFException){
                Toast.makeText(applicationContext,"Error: $e",Toast.LENGTH_LONG).show()
            }

        }

    }

/*
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
*/




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
    override fun onClienteAItemClick(dni: String,fecha:String) {//Ingresa al perfil del paciente sabiendo que tiene turno el mismo dia
        val intent = Intent(this, TurnoActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        intent.putExtra("fecha",fecha)
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
}