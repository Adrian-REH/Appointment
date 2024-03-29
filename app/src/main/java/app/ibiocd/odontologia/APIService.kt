package app.ibiocd.odontologia

import app.ibiocd.lavanderia.Adapter.*
import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @GET("/appointment/especialidad.php?")
    suspend fun getEspecialidades(@Query("todos") todos:String):Response<List<EspecialidadesRespons>>

    @GET("/appointment/odontograma.php?")
    suspend fun getListOdontograma(@Query("dni") dni:String,@Query("codigoprofesional") matricula:String):Response<List<OdontogramaRespons>>

    @GET("/appointment/odontograma.php?")
    suspend fun getOdontograma(@Query("dni") dni:String,@Query("codigoprofesional") matricula:String,@Query("fecha") fecha:String):Response<OdontogramaRespons>

    @FormUrlEncoded
    @POST("/appointment/odontograma.php")
    fun postOdontograma(
        @Field("dientes")  dientes:String,
        @Field("odontogramaid")  ID:Int,
        @Field("codigoprofesional")  idprofesional:String,
        @Field("dni")  dni:String,
        @Field("fecha")  fecha:String,
        @Field("insertar")  insertar:String,
        @Field("modificar")  modificar:String
    ): Call<OdontogramaRespons>



    //PARA LOS PROFESIONALES
    @DELETE("/appointment/cuentas.php?")
    suspend fun deleteProfesional(@Query("correo") correo:String):Response<ProfesionalRespons>
    @GET("/appointment/cuentas.php?")
    suspend fun getcorreoProfesional(@Query("correo") correo:String):Response<ProfesionalRespons>
    @GET("/appointment/cuentas.php?")
    suspend fun getProfesional(@Query("idprofesional") idprofesional:String):Response<ProfesionalRespons>
    @GET("/appointment/cuentas.php?")
    suspend fun getAllListProf(@Query("todos") todos:String):Response<List<ProfesionalRespons>>


    @Headers("Authorization: key=${Constants.SERVER_KEY}", "Content-Type:${Constants.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/appointment/cuentas.php")
    fun postProfesional(
        @Field("nombreapellido") name:String,
        @Field("profesionalcol") col:String,
        @Field("especialidad") especialidad:String,
        @Field("celular") cel:String,
        @Field("direccion") dire:String,
        @Field("correo") correo:String,
        @Field("horarios") horararios:String,
        @Field("prestaciones") prestacion:String,
        @Field("verificar") verificar:String,
        @Field("img") img:String,
        @Field("matricula") matricula:String,
        @Field("TokenID") TID:String,
        @Field("idprofesional") IDP:String,
        @Field("dni") dni:String,
        @Field("insertar") insertar:String,
        @Field("modificar") modificar:String
    ): Call<ProfesionalRespons>

    //PARA LOS CLIENTES
    @GET("/appointment/clientes.php?")
    suspend fun getCliente(@Query("idclientes") dni:String):Response<ClienteRespons>
    @GET("/appointment/clientes.php?")
    suspend fun getClienteCorreo(@Query("correo") correo:String):Response<ClienteRespons>

    @FormUrlEncoded
    @POST("/appointment/clientes.php")
    fun postCliente(
        @Field("correo") correo:String,
        @Field("direccion") direccion:String,
        @Field("nombreapellido") nombreapellido:String,
        @Field("img") img:String,
        @Field("celular") celular:String,
        @Field("dni") dni:String,
        @Field("idclientes") IDC:String,
        @Field("TokenID") TID:String,
        @Field("insertar") insertar:String,
        @Field("modificar") modificar:String
    ): Call<ClienteRespons>

    //PARA LOS TURNOS
    @GET("/appointment/turno.php?")
    suspend fun getListTurnodc(@Query("dni") pacientedni:String,@Query("correoprofesional") profecionalemail:String):Response<List<TurnoRespons>>
    @GET("/appointment/turno.php?")
    suspend fun getListTurno(@Query("correoprofesional") correo:String):Response<List<TurnoRespons>>
    @GET("/appointment/turno.php?")
    suspend fun getListturno(@Query("dni") dni:String):Response<List<TurnoRespons>>
    @FormUrlEncoded
    @POST("/appointment/turno.php")
    fun postTurno(
        @Field("correoprofesional") correoprof:String,
        @Field("nombrepaciente") namepac:String,
        @Field("nombreprofesional") nameprof:String,
        @Field("especialidad") especialidad:String,
        @Field("prestacion") prestacion:String,
        @Field("dni") dni:String,
        @Field("fecha") fecha:String,
        @Field("hora") hora:String,
        @Field("comentario") comentario:String,
        @Field("estado") estado:String,
        @Field("img") img:String,
        @Field("archivo1") archivo1:String,
        @Field("archivo2") archivo2:String,
        @Field("archivo3") archivo3:String,
        @Field("archivo4") archivo4:String,
        @Field("archivo5") archivo5:String,
        @Field("insertar") insertar:String,
        @Field("modificar") modificar:String
    ): Call<TurnoRespons>

   //PARA LA CONEXION ENTRE CLIENTE Y PROFESIONAL
    @GET("/appointment/enlace.php?")
    suspend fun getEnlace(@Query("pacientedni") dni:String,@Query("profecionalemail") correo:String):Response<EnlaceRespons>
   //PARA LA CONEXION ENTRE CLIENTE Y PROFESIONAL
    @GET("/appointment/enlace.php?")
    suspend fun getAllEnlace(@Query("profecionalemail") correo:String):Response<List<EnlaceRespons>>
    @FormUrlEncoded
    @POST("/appointment/enlace.php")
    fun postEnlace(
        @Field("profecionalemail") profecionalemail:String,
        @Field("pacientedni") pacientedni:String,
        @Field("especialidad") especialidad:String,
        @Field("estadoprofecional") estadoprofecional:String,
        @Field("nombreprofesional") nombreprofesional:String,
        @Field("nombrecliente") nombrecliente:String,
        @Field("insertar") insertar:String,
        @Field("modificar") modificar:String

    ): Call<EnlaceRespons>
}
