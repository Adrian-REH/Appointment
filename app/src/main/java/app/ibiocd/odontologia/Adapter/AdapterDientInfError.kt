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
import app.ibiocd.lavanderia.Adapter.Odontograma
import app.ibiocd.odontologia.PerfilActivity
import app.ibiocd.odontologia.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_dienteinf.view.*
import kotlinx.android.synthetic.main.list_dientesup.view.*
import kotlinx.android.synthetic.main.list_horarios.view.*
import kotlinx.android.synthetic.main.list_paciente_a.view.*

class AdapterDientInfError(val arrayList: ArrayList<Odontograma>, val context: Context, val itemClickListener: onDientInfItemClick):
    RecyclerView.Adapter<AdapterDientInfError.ViewHolder>(){

    interface onDientInfItemClick{
        fun onDientInfItemClick(dia: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.list_dienteinf,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bintItem(arrayList[position],context)
        holder.itemView.setOnClickListener {
            val model=arrayList.get(position)

            var url1:String=model.url1

            val intent= Intent(context, PerfilActivity::class.java)
            intent.putExtra("correo",url1)




        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bintItem(model: Odontograma, context: Context){

            itemView.carddientedent.setOnClickListener { itemClickListener.onDientInfItemClick(model.url1)}
            Glide.with(context)
                .load(model.url1)
                .centerCrop()
                .into(itemView.dienteinf)

        }
    }

}