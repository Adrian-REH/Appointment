package app.ibiocd.lavanderia.Adapter

class Clientes(val nombre:String,val celular:String,val dni:String,val historial:String,val image:String,val direccion:String,val correo:String,val img:String) {
}
class Historial(val especialidad:String,val prestacion:String,val fecha:String) {
}
class Horario(val dia:String, val hora:String, val correo:String) {
}
class Odontograma(val url1:String, val url2:String) {
}
class Archivos(val name:String, val url:String) {
}
class Prestacion(val name:String, val detalle:String) {
}

