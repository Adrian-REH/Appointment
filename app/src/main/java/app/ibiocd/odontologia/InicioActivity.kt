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
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList

class InicioActivity : AppCompatActivity(), AdapterClienteA.onClienteItemClick, AdapterClienteB.onClienteItemClick {

    var clientes:String?=""
    var name:String?=""
    var IDP:String?=""
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
            url = intent.getStringExtra("url")
            IDP = intent.getStringExtra("idprofesional")
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
            try {
                val call=RetrofitClient.instance.getProfesional("$IDP")

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
                        IDP = datos?.IDP ?: ""
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
            }catch (e:Exception){

            }

        }
    }

    private fun postProfesional(datos:ProfesionalRespons,token:String) {

        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postProfesional("${datos.nameprof}","${datos.col}","${datos.especialidad}","${datos.celular}","${datos.direccion}","${datos.correo}","${datos.horarios}","${datos.prestacion}","${datos.verificar}","${datos.img}","${datos.matricula}","$token","$IDP","","modificar")
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
                    val call=RetrofitClient.instance.getAllEnlace(IDP.toString())//turno.php

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
                val call=RetrofitClient.instance.getListTurno(IDP.toString())//turno.php
                runOnUiThread{
                    val datos: List<TurnoRespons>? = call.body()

                    if (datos!=null){
                        swipe_container.isRefreshing=false

                        val c= Calendar.getInstance()
                        val hour:Int=c.get(Calendar.HOUR_OF_DAY)
                        val min:Int=c.get(Calendar.MINUTE)
                        val dia:Int=c.get(Calendar.DAY_OF_MONTH)
                        val mes:Int=c.get(Calendar.MONTH)
                        val anno:Int=c.get(Calendar.YEAR)

                        val listturnhora= arrayOf("09:00","09:15","09:30","09:45","10:00","10:15","10:30","10:45","11:00","11:25","11:45","12:00","12:15","12:30","12:45","13:00","13:15","13:30","13:45","14:00","14:15","14:30","14:45","15:00","15:15","15:30","15:45","16:00","16:15","16:30","16:45","17:00","17:15","17:30","17:45","18:00","18:15","18:30","18:45")
                        val array= ArrayList<String>()
                        array.addAll(listturnhora)
                        if (hour<10){
                            array.removeIf {  it < ("0$hour:$min")}

                        }else{
                            array.removeIf {  it < ("$hour:$min")}

                        }
                        for (i in 0 until datos.size){


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

                                for (j in listturnhora){
                                    if (datos[i].hora==j ){
                                        lisTurno.add(datos[i])
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
        intent.putExtra("IDP",IDP)
        intent.putExtra("url",url)
        intent.putExtra("DATOS",DATO)
        intent.putExtra("back","Inicio")

        startActivity(intent)
    }
    fun ClickCalendar(view: View){
        val intent = Intent(this, CalendarActivity::class.java)
        intent.putExtra("IDP",IDP)
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
        intent.putExtra("IDP",IDP)
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
        intent.putExtra("IDP",IDP)
        intent.putExtra("dni",dni)
        intent.putExtra("especialidad",especialidad)
        intent.putExtra("back","Inicio")

        startActivity(intent)
    }
    fun AddAppointment(view: View){
        val intent = Intent(this, TurnoActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("especialidad",especialidad)
        intent.putExtra("name",name)
        intent.putExtra("IDP",IDP)
        intent.putExtra("dni","patient")
        intent.putExtra("fecha","0")
        intent.putExtra("id","0")
        intent.putExtra("codigo","0")
        intent.putExtra("JSONODONT","")

        intent.putExtra("back","Inicio")
        startActivity(intent)
    }
    fun AddPatient(view: View){
        val intent = Intent(this, SignActivity::class.java)
        intent.putExtra("back","Inicio")
        intent.putExtra("quien","paciente")

        startActivity(intent)
    }

}