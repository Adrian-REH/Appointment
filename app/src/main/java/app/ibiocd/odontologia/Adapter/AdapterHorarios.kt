package app.ibiocd.odontologia.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.lavanderia.Adapter.Historial
import app.ibiocd.lavanderia.Adapter.Horario
import app.ibiocd.odontologia.PerfilActivity
import app.ibiocd.odontologia.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_horarios.view.*

class AdapterHorarios(val arrayList: ArrayList<Horario>, val context: Context, val itemClickListener: onHorarioItemClick):
    RecyclerView.Adapter<AdapterHorarios.ViewHolder>(){

    interface onHorarioItemClick{
        fun onHorarioItemClick(dia: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.list_horarios,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bintItem(arrayList[position],context)
        holder.itemView.setOnClickListener {
            val model=arrayList.get(position)

            var ghora:String=model.hora
            var gdia:String=model.dia
            var gcorreo:String=model.correo




            val intent= Intent(context, PerfilActivity::class.java)
            intent.putExtra("correo",gcorreo)
            intent.putExtra("hora",ghora)
            intent.putExtra("dia",gdia)



        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bintItem(model: Horario, context: Context){


            itemView.cardhorariosd.setOnClickListener { itemClickListener.onHorarioItemClick(model.dia)}
            itemView.txtdia.text=model.dia
            itemView.txthora.text = model.dia


        }
    }

}