package app.ibiocd.odontologia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import app.ibiocd.lavanderia.Adapter.ClienteRespons
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.lavanderia.Adapter.EnlaceRespons
import app.ibiocd.lavanderia.Adapter.TurnoRespons
import app.ibiocd.odontologia.Adapter.AdapterClienteA
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.EOFException
import java.lang.Exception
import java.util.*

class CalendarActivity : AppCompatActivity(), AdapterClienteA.onClienteItemClick{
    val lisTurno= ArrayList<TurnoRespons>()
    val arraylisC= ArrayList<Clientes>()
    var correo =""
    var url =""
    var name =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        if(intent.extras !=null){
            correo=intent.getStringExtra("correo").toString()
            url = intent.getStringExtra("url").toString()
            name = intent.getStringExtra("name").toString()
            caledbacktext.setText("${intent.getStringExtra("back")}")
        }
        getListTurnos()
        //val c= Calendar.getInstance()
        //calendarView.minDate=c.timeInMillis
        //calendarView.maxDate=c.timeInMillis+(1000*60*60*24*7)
        calendarView
            .setOnDateChangeListener(
                CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
                    val Date = (dayOfMonth.toString() + "-"+ (month + 1) + "-" + year)
                    var fechahoy=0
                    if (month<10){
                        fechahoy = ("$year"+"0"+"$month$dayOfMonth").toInt()
                        if(dayOfMonth<10){
                            fechahoy = ("$year"+"0"+"$month"+"0"+"$dayOfMonth").toInt()
                        }
                    } else if(dayOfMonth<10){
                        fechahoy = ("$year$month"+"0"+"$dayOfMonth").toInt()
                    }else{
                        fechahoy = ("$year$month$dayOfMonth").toInt()
                    }
                    textseleccion.setText("Turnos de la fecha: "+ Date)
                    // set this date in TextView for Display
                    getListClientesTurnos(fechahoy.toString())

                })
    }

    fun getListClientesTurnos(search: String){
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

                                val name =  datos?.name ?: ""
                                val direc =  datos?.direccion ?: ""
                                val telefono =  datos?.cel ?: ""
                                val email =  datos?.correo ?: ""
                                val image=  datos?.img ?: ""

                                if (search.equals(lisTurno[j].fecha)){
                                    arraylisC.add(Clientes(name, telefono, dni,image,direc,email,image,lisTurno[j].fecha,lisTurno[j].ID,lisTurno[j].hora))

                                }else{
                                    textseleccion.setText("Sin turnos en la fecha")

                                }

                                val adapterclientea = AdapterClienteA(arraylisC, applicationContext, this@CalendarActivity)
                                rviewlist?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
                                rviewlist?.adapter = adapterclientea


                            }else{

                            }



                        }
                    }catch (e: Exception){

                    }

                }
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
                        for (i in 0 until datos.size){
                            val listturnhora= arrayOf("10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00")
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
                                        if (j+10>=hour){
                                            lisTurno.add(datos[i])

                                        }
                                    }
                                }
                            }
                            //TEST
                            lisTurno.add(datos[i])



                        }


                        getListClientesTurnos("")


                    }

                }
            }catch (e: EOFException){
                Toast.makeText(applicationContext,"Error: $e",Toast.LENGTH_LONG).show()
            }

        }

    }

    fun Back(view: View){
        finish()
    }

    override fun onClienteAItemClick(dni: String, fecha: String, ID: Int) {
        val intent = Intent(this, TurnoActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("correo",correo)
        intent.putExtra("dni",dni)
        intent.putExtra("fecha",fecha)
        intent.putExtra("name",name)
        intent.putExtra("id","$ID")
        intent.putExtra("codigo","0")
        intent.putExtra("JSONODONT","")
        intent.putExtra("back","Inicio")

        startActivity(intent)
    }
}