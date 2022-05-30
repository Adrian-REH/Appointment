package app.ibiocd.odontologia.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.ibiocd.lavanderia.Adapter.Archivos
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.lavanderia.Adapter.Historial
import app.ibiocd.lavanderia.Adapter.Horario
import app.ibiocd.odontologia.PerfilActivity
import app.ibiocd.odontologia.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_archivo.view.*
import kotlinx.android.synthetic.main.list_horarios.view.*
import kotlinx.android.synthetic.main.list_paciente_a.view.*
import kotlinx.android.synthetic.main.list_paciente_a.view.txtname

class AdapterArchivo(val arrayList: ArrayList<Archivos>, val context: Context, val itemClickListener: onArchivoItemClick):
    RecyclerView.Adapter<AdapterArchivo.ViewHolder>(){

    interface onArchivoItemClick{
        fun onArchivoItemClick(dato: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.list_archivo,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bintItem(arrayList[position],context)
        holder.itemView.setOnClickListener {
            val model=arrayList.get(position)


            var gdia:String=model.name
            var gcorreo:String=model.url





            val intent= Intent(context, PerfilActivity::class.java)
            intent.putExtra("correo",gcorreo)

            intent.putExtra("dia",gdia)



        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bintItem(model: Archivos, context: Context){


            itemView.txtdwn.setOnClickListener { itemClickListener.onArchivoItemClick(model.url)}
            itemView.txtnamearc.setOnClickListener { itemClickListener.onArchivoItemClick(model.name)}
            itemView.txtnamearc.text=model.name




        }
    }

}