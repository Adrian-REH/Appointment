package app.ibiocd.odontologia

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import app.ibiocd.lavanderia.Adapter.ClienteRespons
import app.ibiocd.lavanderia.Adapter.EspecialidadesRespons
import app.ibiocd.lavanderia.Adapter.Horario
import app.ibiocd.lavanderia.Adapter.ProfesionalRespons
import app.ibiocd.lavanderia.FileDataPart
import app.ibiocd.lavanderia.VolleyFileUploadRequest
import app.ibiocd.odontologia.Adapter.AdapterHorarios
import app.ibiocd.odontologia.Adapter.ArchivoController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_detalles.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_perfil.txtcorreo
import kotlinx.android.synthetic.main.list_paciente_a.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException

class PerfilActivity : AppCompatActivity(), AdapterHorarios.onHorarioItemClick {
    //var correo:String?=""
    var url:String?=""
    var BACKTXT:String?=""
    var CLAVE:String?=""
    var IDP:String?=""
    var snaph:Boolean=false

    private var imageData: ByteArray? = null
    private val SELECT_ACTIVITY=50
    private var IMAGENAME = ""
    private var PRESTACION = ""
    //var a:Int=0


    var JSONHORA:String?="{\n" +
            "\"Domingo\": \"${"10 a 15"}\",\n" +
            "\"Lunes\": \"${"10 a 15"}\",\n" +
            "\"Martes\": \"${"10 a 15"}\",\n" +
            "\"Miercoles\": \"${"10 a 15"}\",\n" +
            "\"Jueves\": \"${"10 a 15"}\",\n" +
            "\"Viernes\": \"${"10 a 15"}\",\n" +
            "\"Sabado\": \"${"10 a 15"}\"\n" +
            "}"
    var textname: TextInputLayout?=null
    var textcel: TextInputLayout?=null
    var textemail: TextInputLayout?=null
    var textdire: TextInputLayout?=null
    var sw: Boolean=true
    var swe: Boolean=true
    val arraylisH= ArrayList<Horario>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        LinearMisDatos.visibility=View.GONE
        lishorario.visibility=View.GONE

        if(intent.extras !=null){
            //correo = intent.getStringExtra("correo")
            IDP = intent.getStringExtra("IDP")
            CLAVE = intent.getStringExtra("CLAVE")

            var DATOS = intent.getStringExtra("DATOS")
            if (DATOS=="LLENARPERFIL"){
                ClickEditar(View(applicationContext))
                Toast.makeText(this,"Ingrese sus datos", Toast.LENGTH_SHORT).show()

            }
            url = intent.getStringExtra("url")


            BACKTXT=intent.getStringExtra("back")
            perfbacktext.setText("$BACKTXT")
        }
        //txtcorreo.setText(correo)

        getProfesionalDatos()
    }
    fun ClickEditar(view: View){
    if(swe){
        LinearMisDatos?.visibility = View.VISIBLE
        btnimgedit?.setImageDrawable(getDrawable(R.drawable.ic_cancel))
        btnimgedit?.imageTintList = resources.getColorStateList(R.color.black)
        swe=false
    }else if(!swe){
        LinearMisDatos?.visibility = View.GONE

        btnimgedit?.setImageDrawable(getDrawable(R.drawable.ic_edit))
        btnimgedit?.imageTintList = resources.getColorStateList(R.color.black)
        swe=true
    }


    }



    private fun getProfesionalDatos(){
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val call=RetrofitClient.instance.getProfesional(IDP.toString())
                val datos: ProfesionalRespons? =call.body()
                runOnUiThread{
                    if(call.isSuccessful){
                        val nombre = datos?.nameprof ?: ""
                        val dnid = datos?.DNI ?: ""
                        val direc = datos?.direccion ?: ""
                        val celul = datos?.celular ?: ""
                        val matr= datos?.matricula ?: ""
                        val espec= datos?.especialidad ?: ""
                        val hora= datos?.horarios ?: ""
                        val verf= datos?.verificar ?: ""
                        val img= datos?.img ?: ""
                        val prest= datos?.prestacion ?: ""
                        val correo= datos?.correo ?: ""

                        if (hora!=""){
                            JSONHORA=hora
                        }

                        txtmatricula?.setText(matr)
                        txtcorreo?.setText(correo)
                        txtnombre?.setText(nombre)
                        txtcel?.setText(celul)
                        txtdirec?.setText(direc)
                        txtesp?.setText(espec)
                        txtdni.setText(dnid)
                        if(verf=="V"){
                            cvverifperf.visibility=View.VISIBLE

                        }
                        if(img!="null"){
                            Glide.with(applicationContext)
                                .load(img)
                                .centerCrop()
                                .into(viewimageperfil)

                        }


                        PRESTACION=prest
                    }else{
                        Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                    }
                }
            }catch (e:Exception){

            }


        }
    }//


    fun getProfesionalSave(view: View){
        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.getProfesional(IDP.toString())
            val datos: ProfesionalRespons? =call.body()
            runOnUiThread{
                var Imagen=""
                if(call.isSuccessful){
                    if (IMAGENAME==""){
                        if (datos != null) {
                            postProfesional(datos.img,datos.verificar,datos.prestacion,datos.TID,datos)
                        }
                    }else{
                        if (datos != null) {
                            postProfesional("http://${url}/docs/$IMAGENAME",datos.verificar,datos.prestacion,datos.TID,datos)
                        }
                    }
                }
            }
        }
    }//
    private fun postProfesional(imagen:String,verificar:String,prestacion:String,token:String,datos:ProfesionalRespons) {

        CoroutineScope(Dispatchers.IO).launch {
            val call=RetrofitClient.instance.postProfesional("${datos.nameprof}","${datos.col}","${datos.especialidad}","${datos.celular}","${datos.direccion}","${datos.correo}","${JSONHORA}",prestacion,verificar,imagen,"${txtmatricula.text}","$token","$IDP","${datos.DNI}","modificar","modificar")
            call.enqueue(object : Callback<ProfesionalRespons> {
                override fun onFailure(call: Call<ProfesionalRespons>, t: Throwable) {
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<ProfesionalRespons>, response: retrofit2.Response<ProfesionalRespons>) {

                    uploadImage()

                }
            })



        }
    }




    fun Back(view: View){
        finish()
    }
    fun ClickHorario(view: View){
        if(sw){
            arraylisH.clear()
            val nombredias = arrayOf("Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado")
            val jsonobject=JSONObject(JSONHORA!!)
            for (i in nombredias.indices){
                val hora = jsonobject.getString(nombredias[i])
                val dia = nombredias[i]
                arraylisH.add(Horario(dia,hora,IDP!!))
            }


            val adaptercliente = AdapterHorarios(arraylisH, this, this)
            lishorario?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            if (!snaph){
                val snapHelper: SnapHelper =LinearSnapHelper()
                snapHelper.attachToRecyclerView(lishorario)
            }

            lishorario?.adapter = adaptercliente
            lishorario?.visibility = View.VISIBLE



            snaph=true
            sw=false
        }else if(!sw){
            arraylisH.clear()
            lishorario?.visibility = View.GONE
            sw=true
        }

    }
    fun ClickPrestaciones(view: View){
        val intent = Intent(this, PrestacionesActivity::class.java)
        intent.putExtra("prestacion",PRESTACION)
        intent.putExtra("url",url)
        intent.putExtra("IDP",IDP)
        intent.putExtra("back",titleperfil.text.toString())

        startActivity(intent)
    }
    fun clickSelect(view: View){
        ArchivoController.selectArchivo(this, SELECT_ACTIVITY)
        viewimageperfil?.setImageResource(R.drawable.blank_profile)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == SELECT_ACTIVITY) {
            val uri = data?.data
            if (uri != null) {

                createImageData(uri)
                dumpImageMetaData(uri)

            }
        }

    }
    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()

            viewimageperfil?.setImageURI(uri)
        }

    }
    @SuppressLint("Range")
    fun dumpImageMetaData(uri: Uri) {
        val cursor: Cursor? = contentResolver.query(
            uri, null, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName: String =
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.i(ContentValues.TAG, "Display Name: $displayName")
                IMAGENAME=displayName

            }
        }
    }
    private fun uploadImage() {
        imageData?: return
        val url = "http://$url/acceso.php"
        val request = object : VolleyFileUploadRequest( Method.POST, url,
            Response.Listener {

            },
            Response.ErrorListener {

                Toast.makeText(this,"ERROR $it", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()
                // Key y value
                parametros.put("usuario","lsuRb/c6GKDIG7lt")
                parametros.put("password","SS4WJOA58mkhWFkLLzC3LA==")
                return parametros

            }
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["docs"] = FileDataPart(IMAGENAME.toString(), imageData!!, "file")

                return params
            }



        }
        Volley.newRequestQueue(this).add(request)

    }
    override fun onHorarioItemClick(dia: String) {

        val builder= AlertDialog.Builder(this)
        builder.setTitle("Horario del $dia")
        val view=layoutInflater.inflate(R.layout.dialog_horario,null)
        val horarioini=view.findViewById<EditText>(R.id.horaini)
        val horariofin=view.findViewById<EditText>(R.id.horafin)
        val solodetalle=view.findViewById<TextView>(R.id.solodetalle)
        val jsonObject=JSONObject(JSONHORA)
        solodetalle.setText(jsonObject.getString(dia))

        horariofin.setOnClickListener {
            val timePicker = TimePickerFragment{horariofin?.setText("$it")}
            timePicker.show(supportFragmentManager,"time") }
        horarioini.setOnClickListener {
            val timePicker = TimePickerFragment{horarioini?.setText("$it")}
            timePicker.show(supportFragmentManager,"time") }
        builder.setView(view)

        builder.setPositiveButton("Guardar", DialogInterface.OnClickListener { dialogInterface, i ->
            sw=true
            val dias= arrayOf("Domingo","Lunes","Martes","Miercoles","Jueves","Viernes","Sabado")
            val dias2= ArrayList<String>()
            val diashora= ArrayList<String>()
            for (j in dias.indices){
                if (dia==dias[j]){
                    diashora.add("${horarioini.text} a ${horariofin.text}")
                    dias2.add(dias[j])
                }else{
                    dias2.add(dias[j])
                    diashora.add(jsonObject.getString(dias[j]))
                }
            }

            JSONHORA="{\n" +
                    "\"${dias2[0]}\": \"${diashora[0]}\",\n" +
                    "\"${dias2[1]}\": \"${diashora[1]}\",\n" +
                    "\"${dias2[2]}\": \"${diashora[2]}\",\n" +
                    "\"${dias2[3]}\": \"${diashora[3]}\",\n" +
                    "\"${dias2[4]}\": \"${diashora[4]}\",\n" +
                    "\"${dias2[5]}\": \"${diashora[5]}\",\n" +
                    "\"${dias2[6]}\": \"${diashora[6]}\"\n" +
                    "}"

            ClickHorario(View(applicationContext))
        })
        builder.setNegativeButton("Close", DialogInterface.OnClickListener { dialogInterface, i ->

        })
        builder.show()
    }



//EDITAR DATOS PERSONALES
    fun Identidadedit(view: View){
        val intent = Intent(this, UploadPerfilActivity::class.java)
        intent.putExtra("IDP",IDP)
        intent.putExtra("url",url)
        intent.putExtra("CLAVE",CLAVE)
        intent.putExtra("email","false")
        intent.putExtra("ubucacion","false")
        intent.putExtra("telefono","false")
        intent.putExtra("identidad","true")
        intent.putExtra("back","Perfil")
    intent.putExtra("matricula","false")
    intent.putExtra("profesion","false")
    intent.putExtra("seguridad","false")

    startActivity(intent)

    }
    fun Ubicacionedit(view: View){
        val intent = Intent(this, UploadPerfilActivity::class.java)
        intent.putExtra("IDP",IDP)
        intent.putExtra("url",url)
        intent.putExtra("CLAVE",CLAVE)
        intent.putExtra("email","false")
        intent.putExtra("ubicacion","true")
        intent.putExtra("telefono","false")
        intent.putExtra("identidad","false")
        intent.putExtra("back","Perfil")
        intent.putExtra("matricula","false")
        intent.putExtra("profesion","false")
        intent.putExtra("seguridad","false")

        startActivity(intent)

    }
    fun Telefonoedit(view: View){
        val intent = Intent(this, UploadPerfilActivity::class.java)
        intent.putExtra("IDP",IDP)
        intent.putExtra("url",url)
        intent.putExtra("CLAVE",CLAVE)
        intent.putExtra("email","false")
        intent.putExtra("ubucacion","false")
        intent.putExtra("telefono","true")
        intent.putExtra("identidad","false")
        intent.putExtra("back","Perfil")
        intent.putExtra("matricula","false")
        intent.putExtra("profesion","false")
        intent.putExtra("seguridad","false")

        startActivity(intent)

    }
    fun Emailedit(view: View){
        val intent = Intent(this, UploadPerfilActivity::class.java)
        intent.putExtra("IDP",IDP)
        intent.putExtra("url",url)
        intent.putExtra("CLAVE",CLAVE)
        intent.putExtra("email","true")
        intent.putExtra("ubucacion","false")
        intent.putExtra("telefono","false")
        intent.putExtra("identidad","true")
        intent.putExtra("matricula","false")
        intent.putExtra("profesion","false")
        intent.putExtra("seguridad","false")
        intent.putExtra("back","Perfil")

        startActivity(intent)
    }
    fun Matriculaedit(view: View){
        val intent = Intent(this, UploadPerfilActivity::class.java)
        intent.putExtra("IDP",IDP)
        intent.putExtra("url",url)
        intent.putExtra("CLAVE",CLAVE)
        intent.putExtra("email","false")
        intent.putExtra("ubucacion","false")
        intent.putExtra("telefono","false")
        intent.putExtra("identidad","false")
        intent.putExtra("matricula","true")
        intent.putExtra("profesion","false")
        intent.putExtra("seguridad","false")
        intent.putExtra("back","Perfil")
        startActivity(intent)
    }
    fun Profesionedit(view: View){
        val intent = Intent(this, UploadPerfilActivity::class.java)
        intent.putExtra("IDP",IDP)
        intent.putExtra("url",url)
        intent.putExtra("CLAVE",CLAVE)
        intent.putExtra("email","false")
        intent.putExtra("ubucacion","false")
        intent.putExtra("telefono","false")
        intent.putExtra("identidad","false")
        intent.putExtra("matricula","false")
        intent.putExtra("profesion","true")
        intent.putExtra("seguridad","false")
        intent.putExtra("back","Perfil")
        startActivity(intent)
    }
    fun ClickSeguridad(view: View){

        val intent = Intent(this, UploadPerfilActivity::class.java)
        intent.putExtra("IDP",IDP)
        intent.putExtra("url",url)
        intent.putExtra("CLAVE",CLAVE)
        intent.putExtra("email","false")
        intent.putExtra("ubucacion","false")
        intent.putExtra("telefono","false")
        intent.putExtra("identidad","false")
        intent.putExtra("matricula","false")
        intent.putExtra("profesion","false")
        intent.putExtra("seguridad","true")
        intent.putExtra("back","Perfil")
        startActivity(intent)
    }


}