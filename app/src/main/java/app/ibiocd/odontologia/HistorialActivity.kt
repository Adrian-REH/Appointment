package app.ibiocd.odontologia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.lavanderia.Adapter.EspecialidadesRespons
import app.ibiocd.lavanderia.Adapter.Historial
import app.ibiocd.lavanderia.Adapter.TurnoRespons
import app.ibiocd.odontologia.Adapter.AdapterClienteA
import app.ibiocd.odontologia.Adapter.AdapterHistorial
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_historial.*
import kotlinx.android.synthetic.main.activity_paciente.*
import kotlinx.android.synthetic.main.activity_turno.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class HistorialActivity : AppCompatActivity(), AdapterHistorial.onHistorialItemClick {

    var url:String?=""
    var correo:String?=""
    var dni:String?=""
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

        if ( correo != ""){
            getListHistorialturnos("",intent.getStringExtra("especialidad").toString())
            edtxtsearchhistorial.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val search=s.toString()

                    getListHistorialturnos(search,intent.getStringExtra("especialidad").toString())


                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                }
            })
        }


    }

    fun Back(view: View) {

        finish()
    }
    //----------------------------------------------------------------------------------------------GET el historial de atencion
    fun getListHistorialturnos(search:String,espec:String){
        arraylisH.clear()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call=RetrofitClient.instance.getListturno(dni.toString())
                runOnUiThread{
                    val datos: List<TurnoRespons>? = call.body()
                    for (i in 0 until datos?.size!!){
                        val prestacion = datos[i].prestacion
                        val fecha = datos[i].fecha
                        val especialidad = datos[i].especialidad
                        val estado = datos[i].estado
                        if ( espec == especialidad){
                            Toast.makeText(applicationContext,"dd",Toast.LENGTH_LONG).show()
                            if (search==fecha){
                                if (estado!="E"){ arraylisH.add(Historial(prestacion,especialidad,fecha)) }
                                arraylisH.add(Historial(prestacion,especialidad,fecha))
                            }else if (search==""){

                                arraylisH.add(Historial(prestacion,especialidad,fecha))

                            }
                        }else{
                            Toast.makeText(applicationContext,"dd",Toast.LENGTH_LONG).show()

                        }

                    }
                    val adapterhistorial = AdapterHistorial(arraylisH, applicationContext, this@HistorialActivity)
                    rcviewH?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    rcviewH?.adapter = adapterhistorial





                }
            }catch (e:Exception){

            }

        }
    }//

    override fun onHistorialItemClick(fecha: String) {
        val intent = Intent(this, DetallesActivity::class.java)
         intent.putExtra("fecha",fecha)
         intent.putExtra("dni",dni)
         intent.putExtra("url",url)
         intent.putExtra("correo",correo)
        intent.putExtra("back","Historial")

        startActivity(intent)
    }

}