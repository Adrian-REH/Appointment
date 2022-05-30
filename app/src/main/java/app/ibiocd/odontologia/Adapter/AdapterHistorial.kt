package app.ibiocd.odontologia.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.lavanderia.Adapter.Historial
import app.ibiocd.odontologia.PerfilActivity
import app.ibiocd.odontologia.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_historial_medico.view.*
import kotlinx.android.synthetic.main.list_paciente_a.view.*

class AdapterHistorial(val arrayList: ArrayList<Historial>, val context: Context, val itemClickListener: onHistorialItemClick):
    RecyclerView.Adapter<AdapterHistorial.ViewHolder>(){

    interface onHistorialItemClick{
        fun onHistorialItemClick(fecha: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.list_historial_medico,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bintItem(arrayList[position],context)
        holder.itemView.setOnClickListener {
            val model=arrayList.get(position)

            var gespec:String=model.especialidad
            var gprest:String=model.prestacion
            var gfecha:String=model.fecha



            val intent= Intent(context, PerfilActivity::class.java)
            intent.putExtra("especialidad",gespec)
            intent.putExtra("prestacion",gprest)
            intent.putExtra("fecha",gfecha)



        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bintItem(model: Historial, context: Context){


            itemView.servicioprest.setOnClickListener { itemClickListener.onHistorialItemClick(model.fecha)}
            itemView.txttit.text=model.prestacion
            itemView.txtdet.text=model.especialidad
            itemView.txtfecha.text=model.fecha



        }
    }

}