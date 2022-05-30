package app.ibiocd.odontologia.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.lavanderia.Adapter.Historial
import app.ibiocd.lavanderia.Adapter.Prestacion
import app.ibiocd.odontologia.PerfilActivity
import app.ibiocd.odontologia.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_historial_medico.view.*
import kotlinx.android.synthetic.main.list_paciente_a.view.*
import kotlinx.android.synthetic.main.list_prestacion.view.*

class AdapterPrestacion(val arrayList: ArrayList<Prestacion>, val context: Context, val itemClickListener: onPrestacionItemClick):
    RecyclerView.Adapter<AdapterPrestacion.ViewHolder>(){

    interface onPrestacionItemClick{
        fun onPrestacionItemClick(nombre: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.list_prestacion,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bintItem(arrayList[position],context)
        holder.itemView.setOnClickListener {
            val model=arrayList.get(position)

            var gname:String=model.name
            var gdet:String=model.detalle




            val intent= Intent(context, PerfilActivity::class.java)
            intent.putExtra("nombre",gname)
            intent.putExtra("detalle",gdet)




        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bintItem(model: Prestacion, context: Context){
            itemView.cardprest.setOnClickListener { itemClickListener.onPrestacionItemClick(model.name)}
            itemView.txtsrv.text=model.name
            itemView.txtdesc.text=model.detalle
        }
    }

}