package app.ibiocd.odontologia.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.ibiocd.lavanderia.Adapter.Clientes
import app.ibiocd.odontologia.PerfilActivity
import app.ibiocd.odontologia.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_paciente_a.view.*

class AdapterClienteA(val arrayList: ArrayList<Clientes>, val context: Context, val itemClickListener: onClienteItemClick):
    RecyclerView.Adapter<AdapterClienteA.ViewHolder>(){

    interface onClienteItemClick{
        fun onClienteAItemClick(dni: String,fecha:String,ID:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.list_paciente_a,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bintItem(arrayList[position],context)
        holder.itemView.setOnClickListener {
            val model=arrayList.get(position)

            var gname:String=model.nombre
            var gcel:String=model.celular
            var gcid:String=model.dni
            var gimag:String=model.image
            var gem:String=model.correo
            var dir:String=model.direccion
            var img:String=model.img
            var fcha:String=model.fecha



            val intent= Intent(context, PerfilActivity::class.java)
            intent.putExtra("nombre",gname)
            intent.putExtra("celular",gcel)
            intent.putExtra("dni",gcid)
            intent.putExtra("image",gimag)
            intent.putExtra("correo",gem)
            intent.putExtra("direccion",dir)
            intent.putExtra("img",img)


        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bintItem(model: Clientes, context: Context){


            itemView.view.setOnClickListener { itemClickListener.onClienteAItemClick(model.dni, model.fecha,model.ID)}
            itemView.txtname.text=model.nombre
            Glide.with(context)
                .load(model.image)
                .centerCrop()
                .into(itemView.view)


        }
    }

}