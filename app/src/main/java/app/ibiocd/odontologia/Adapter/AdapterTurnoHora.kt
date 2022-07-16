package app.ibiocd.odontologia.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import app.ibiocd.lavanderia.Adapter.HoraTurno
import app.ibiocd.lavanderia.Adapter.Horario
import app.ibiocd.lavanderia.Adapter.TurnoRespons
import app.ibiocd.odontologia.R
import app.ibiocd.odontologia.TurnoActivity
import kotlinx.android.synthetic.main.list_hora_turno.view.*


class AdapterTurnoHora(val arrayList: ArrayList<HoraTurno>, val context: Context, val itemClickListener: onHorarioItemClick):
        RecyclerView.Adapter<AdapterTurnoHora.ViewHolder>(){

        interface onHorarioItemClick{
            fun onHorarioItemClick(hora: String)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v= LayoutInflater.from(parent.context).inflate(R.layout.list_hora_turno,parent,false)
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
                var gfcha:String=model.fecha


                val intent= Intent(context, TurnoActivity::class.java)
                intent.putExtra("hora",ghora)
                intent.putExtra("fecha",gfcha)



            }
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            fun bintItem(model: HoraTurno, context: Context){


                itemView.cardhorariosd.setOnClickListener { itemClickListener.onHorarioItemClick(model.hora)

                }
                itemView.txthora.text=model.hora

                if(model.selector)
                {
                    itemView.selector.setCardBackgroundColor(Color.GREEN)
                }else{
                    itemView.selector.setCardBackgroundColor(Color.WHITE)

                }


            }
        }

    }
