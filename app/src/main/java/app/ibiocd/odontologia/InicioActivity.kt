package app.ibiocd.odontologia

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.ibiocd.lavanderia.Adapter.*
import app.ibiocd.odontologia.Adapter.AdapterClienteA
import app.ibiocd.odontologia.Adapter.AdapterClienteB
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_inicio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import java.io.EOFException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class InicioActivity : AppCompatActivity(), AdapterClienteA.onClienteItemClick, AdapterClienteB.onClienteItemClick {

    var correo:String?=""
    var clientes:String?=""
    var name:String?=""
    var url:String?=""
    var especialidad:String?=""

    var id:Int=0

     val lisTurno= ArrayList<TurnoRespons>()
     val lisEnlace= ArrayList<EnlaceRespons>()

     val arraylisC= ArrayList<Clientes>()
     val displayListC= ArrayList<Clientes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        arraylisC.clear()
        displayListC.clear()
        lisTurno.clear()
        lisEnlace.clear()
        cvverifI.visibility=View.GONE

        if(intent.extras !=null){
            correo = intent.getStringExtra("correo")
            url = intent.getStringExtra("url")
        }

        swipe_container.setOnRefreshListener  {
            getListTurnos()
            getListEnlace()
            getComprobarCliente()


            Handler(Looper.getMainLooper()).postDelayed({

                swipe_container.isRefreshing=false

            }, 3000)
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


    fun getListClientes(search: String){
        arraylisC.clear()
        displayListC.clear()
        for (j in 0 until lisEnlace.size) {
            var clientes = lisEnlace[j].pacientedni
            CoroutineScope(Dispatchers.IO).launch {
                val call=RetrofitClient.instance.getCliente("$clientes")
                runOnUiThread{

                    if (call.isSuccessful){
                        val datos: ClienteRespons? = call.body()
                        //swipe_container.isRefreshing=false

                        val name =  datos?.name ?: ""
                        val direc =  datos?.direccion ?: ""
                        val telefono =  datos?.cel ?: ""
                        val email =  datos?.correo ?: ""
                        val image=  datos?.img ?: ""
                        val dni=  datos?.dni ?: ""

                        if (search.equals(dni) || search.equals(name)){
                            displayListC.add(Clientes(name, telefono, dni,image,direc,email,image,"",0,""))
                        }else if (search==""){
                            displayListC.add(Clientes(name, telefono, dni,image,direc,email,image,"",0,""))
                        }


                        val adapterclienteb = AdapterClienteB(displayListC, applicationContext, this@InicioActivity)
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
        arraylisC.clear()
        if (lisTurno.size>0){
            for (j in 0 until lisTurno.size) {
                var dni = lisTurno[j].dni
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val call=RetrofitClient.instance.getCliente("$dni")
                        runOnUiThread{

                            if (call.isSuccessful){
                                val datos: ClienteRespons? = call.body()
                               // swipe_container.isRefreshing=false

                                val name =  datos?.name ?: ""
                                val direc =  datos?.direccion ?: ""
                                val telefono =  datos?.cel ?: ""
                                val email =  datos?.correo ?: ""
                                val image=  datos?.img ?: ""
                                val dni=  datos?.dni ?: ""

                                if (search.equals(dni) || search.equals(name)){
                                    arraylisC.add(Clientes(name, telefono, dni,image,direc,email,image,lisTurno[j].fecha,lisTurno[j].ID,lisTurno[j].hora))
                                }else if (search==""){
                                    arraylisC.add(Clientes(name, telefono, dni,image,direc,email,image,lisTurno[j].fecha,lisTurno[j].ID,lisTurno[j].hora))
                                }

                                val adapterclientea = AdapterClienteA(arraylisC, applicationContext, this@InicioActivity)
                                rviewclientea?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
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


    fun getComprobarCliente(){
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.getProfesional("$correo")

            runOnUiThread{
                swipe_container.isRefreshing=false

                val datos: ProfesionalRespons? = call.body()
                val nombre = datos?.nameprof ?: ""
                val verif = datos?.verificar ?: ""
                especialidad= datos?.especialidad ?: ""
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

                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }
                    val token = task.result
                    if (datos?.TID!=token){

                        postProfesional(datos!!,token)
                    }

                })

                //ENVIO LOS DATOS


            }
        }
    }

    private fun postProfesional(datos:ProfesionalRespons,token:String) {

        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postProfesional("${datos.nameprof}","${datos.col}","${datos.especialidad}","${datos.celular}","${datos.direccion}","${correo}","${datos.horarios}","${datos.prestacion}","${datos.verificar}","${datos.img}","${datos.matricula}","$token","","modificar")
            call.enqueue(object : Callback<ProfesionalRespons> {
                override fun onFailure(call: Call<ProfesionalRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<ProfesionalRespons>, response: retrofit2.Response<ProfesionalRespons>) {

                }
            })



        }
    }

    fun getListEnlace(){

        lisEnlace.clear()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val call=RetrofitClient.instance.getAllEnlace(correo.toString())//turno.php

                    runOnUiThread{
                        swipe_container.isRefreshing=false

                        val datos: List<EnlaceRespons>? = call.body()
                        if (datos!=null){
                            for (i in 0 until datos?.size!!){
                                val dni = datos[i].pacientedni
                                val estp = datos[i].estado
                                if (estp=="OCUPADO"){
                                    lisEnlace.add(datos[i])

                                }
                            }
                            getListClientes("")


                        }

                    }
                }catch (e: EOFException){
                    //Toast.makeText(applicationContext,"Error: $e",Toast.LENGTH_LONG).show()

                }

            }



    }
    fun getListTurnos(){

        lisTurno.clear()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call=RetrofitClient.instance.getListTurno(correo.toString())//turno.php
                runOnUiThread{
                    val datos: List<TurnoRespons>? = call.body()

                    if (datos!=null){
                        swipe_container.isRefreshing=false

                        for (i in 0 until datos.size){
                            val listturnhora= arrayOf("10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00")
                            val c= Calendar.getInstance()
                            val hour:Int=c.get(Calendar.HOUR_OF_DAY)
                            val dia:Int=c.get(Calendar.DAY_OF_MONTH)
                            val mes:Int=c.get(Calendar.MONTH)
                            val anno:Int=c.get(Calendar.YEAR)
                            var fechahoy=0
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
                            if (datos[i].fecha==fechahoy.toString()){

                                for (j in listturnhora.indices){
                                    if (datos[i].hora==listturnhora[j]){
                                        Toast.makeText(applicationContext,"${datos[i].hora}" +"=="+ "${listturnhora[j]}",Toast.LENGTH_SHORT).show()

                                        if (j+10>=hour){
                                            lisTurno.add(datos[i])

                                        }
                                    }
                                }
                            }
                            //TEST



                        }


                        getListClientesTurnos("")


                    }

                }
            }catch (e: EOFException){
//                Toast.makeText(applicationContext,"Error: $e",Toast.LENGTH_LONG).show()
            }

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

        intent.putExtra("back","Inicio")

        startActivity(intent)
    }
    fun ClickCalendar(view: View){
        val intent = Intent(this, CalendarActivity::class.java)
        intent.putExtra("correo",correo)
        intent.putExtra("url",url)
        intent.putExtra("especialidad",especialidad)
        intent.putExtra("name",name)
        intent.putExtra("back","Inicio")

        startActivity(intent)
    }

    override fun onClienteAItemClick(dni: String,fecha:String,ID:Int) {//Ingresa al perfil del paciente sabiendo que tiene turno el mismo dia
        val intent = Intent(this, TurnoActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("especialidad",especialidad)
        intent.putExtra("name",name)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        intent.putExtra("fecha",fecha)
        intent.putExtra("id","$ID")
        intent.putExtra("codigo","0")
        intent.putExtra("JSONODONT","")
        intent.putExtra("back","Inicio")

        startActivity(intent)
    }
    override fun onClienteBItemClick(dni: String) {//Ingresa al perfil del paciente sin saber que tiene turno el mismo dia
        val intent = Intent(this, PacienteActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        intent.putExtra("especialidad",especialidad)
        intent.putExtra("back","Inicio")

        startActivity(intent)
    }

}