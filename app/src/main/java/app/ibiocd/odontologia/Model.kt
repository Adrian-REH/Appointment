package app.ibiocd.lavanderia.Adapter

import com.google.gson.annotations.SerializedName

class Clientes(val nombre:String,val celular:String,val dni:String,val image:String,val direccion:String,val correo:String,val img:String,val fecha: String,val ID: Int,val hora:String) {
}
class Historial(val especialidad:String,val prestacion:String,val fecha:String) {
}
class Horario(val dia:String, val hora:String, val correo:String) {
}
class Odontograma(val url1:String, val url2:String) {
}
class HoraTurno(val hora:String, val fecha:String,val selector:Boolean) {
}
class Archivos(val name:String, val url:String) {
}
class Prestacion(val name:String, val detalle:String) {
}
data class OdontogramaRespons(
    @SerializedName("dientes") var dientes:String,
    @SerializedName("odontogramaid") var ID:Int,
    @SerializedName("codigoprofesional") var matricula:String,
    @SerializedName("fecha") var fecha: String,
    @SerializedName("dni") var dni:String
)
data class ClienteRespons(
    @SerializedName("direccion") var direccion:String,
    @SerializedName("nombreapellido") var name:String,
    @SerializedName("correo") var correo:String,
    @SerializedName("celular") var cel:String,
    @SerializedName("dni") var dni:String,
    @SerializedName("TokenID") var TID:String,
    @SerializedName("img") var img:String
)
data class EspecialidadesRespons(
    @SerializedName("especialidad") var especialidad:String,
    @SerializedName("img") var img:String
)
data class NotificacionRespons(
    @SerializedName("multicast_id") var multicast_id:String,
    @SerializedName("success") var success:String,
    @SerializedName("failure") var failure:String,
    @SerializedName("canonical_ids") var canonical_ids:String,
    @SerializedName("results") var data:String
)
data class PushNotification(
    val data: NotificationData,
    val to: String
)
data class NotificationData(
    val title: String,
    val message: String
)
data class ProfesionalRespons(
    @SerializedName("nombreapellido") var nameprof:String,
    @SerializedName("profesionalcol") var col:String,
    @SerializedName("especialidad") var especialidad:String,
    @SerializedName("celular") var celular:String,
    @SerializedName("direccion") var direccion:String,
    @SerializedName("correo") var correo:String,
    @SerializedName("horarios") var horarios:String,
    @SerializedName("prestaciones") var prestacion:String,
    @SerializedName("verificar") var verificar:String,
    @SerializedName("img") var img:String,
    @SerializedName("TokenID") var TID:String,
    @SerializedName("matricula") var matricula:String
)
data class TurnoRespons(
    @SerializedName("correoprofesional") var correopr:String,
    @SerializedName("nombrepaciente") var namepac:String,
    @SerializedName("nombreprofesional") var nameprof:String,
    @SerializedName("especialidad") var especialidad:String,
    @SerializedName("prestacion") var prestacion:String,
    @SerializedName("dni") var dni:String,
    @SerializedName("fecha") var fecha:String,
    @SerializedName("hora") var hora:String,
    @SerializedName("comentario") var comentario:String,
    @SerializedName("img") var img:String,
    @SerializedName("imgprof") var imgprof:String,
    @SerializedName("estado") var estado:String,
    @SerializedName("idturno") var ID:Int,
    @SerializedName("archivo1") var archivo1:String,
    @SerializedName("archivo2") var archivo2:String,
    @SerializedName("archivo3") var archivo3:String,
    @SerializedName("archivo4") var archivo4:String,
    @SerializedName("archivo5") var archivo5:String
)
data class EnlaceRespons(
    @SerializedName("profecionalemail") var correo:String,
    @SerializedName("pacientedni") var pacientedni:String,
    @SerializedName("especialidad") var especialidad:String,
    @SerializedName("estadoprofecional") var estado:String,
    @SerializedName("nombreprofesional") var nameprof:String,
    @SerializedName("nombrecliente") var nameclient:String
)

