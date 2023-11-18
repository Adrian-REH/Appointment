# Appointment(a bit of everything)
</br>
   Esta app se especializa unicamente en Odontologia, queremso facilitar la organizacion de turnos para los profesionales, asi tambien aplicaremos distintas funcionalidades como:
   - Visualizar, gestionar y diferenciar por calificacion las distintas Tarifas o aplicacion de Descuentos a sus clientes
   - Generar, limitar y combinar distintos Tickets temporales para regalar a sus clientes por bondad del profesional.
   - Organizar los distintos clientes y beneficiarlos con me gusta, una ESTRELLA!, descuentos,  tickets tambien se podra descalificar con un Bloqueo
   - Crear cuentas a distintos clientes para facilitarles el acceso
   - Organizar sus turnos por orden de llegada, brindarles el tiempo de espera y entretenerlos con los datos del Wifi del local asi como las redes sociales del profesional, asi como tambien
   notificar a sus clientes de Repentinos cambios de horarios o deshabilitacion de turnos 
   - Tambien podra ver los clientes que quieren sacar turnos para el profesional.
   - P

   
## Diagrama de estado
<img src="https://github.com/Adrian-REH/Appointment/blob/main/Diagrama%20sin%20título.drawio.png" align="center" width="520"/>


This is one App of **Appointment Medical**, in that the patient can **search favorite Medicals** in GoogleMaps whithin app and **Add this Medical to list of favorite** for after **create appointments** selecting specialty and profession of the medical and, date and hour of appointment.
 After create appointment the Patient can **search record medical** and **view different type of files that medical upload** in referently to the appointment, only some can be see by the patient and all will be editable for the medical.
 By different method the patient **can see personal information of the medical and for know if that his tuition is verified**.
  ### Descarga
[![Google Play](https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=googleplay&logoColor=white&labelColor=414141)](https://play.google.com/store/apps/details?id=app.ibiocd.appointment) 

  ### Skill
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white&labelColor=7F52FF)]()
[![Android_Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=flat-square&logo=android-studio&logoColor=black&labelColor=3DDC84)]()
[![Node.js](https://img.shields.io/badge/Node.js-F7DF1E?style=flat-square&logo=node&logoColor=black&labelColor=F7DF1E)]()
[![MongoDB](https://img.shields.io/badge/MongoDB-3DDC84?style=flat-square&logo=mongodb&logoColor=black&labelColor=3DDC84)]()
</br>
  

 ## Activity and functions:
 
 *I'm create four activitys with function specify*
 
 
 ### Home Activity
 *The patient and Medical can search Appointment, see History medical, navegation to the different screens*
  
- **HOME**: You Can see Appointment of the week and the of Today view is autoselect also can create new Appointment and search Medicals
    
- **FAVORITE**: Here search Medicals and add or delete of Favorite list
    
- **CALENDAR**: Here you can selected date and search appointment list of that day
    
- **FILES**: Here you can see list of Record of appointment, summarized for type of files, 
    
- **MENU**: You can navegation by the different Screen, exit app, view Profile and edit security
    

 ### Profile Activity
  *Here the Medical and Patient see Resumen and Edit the view with on Click*

- **RESUME**: The resume of medical and patient they haven't much different, also is possible see view of the Card Hour and List Card Specialty
  
- **EDITABLE_RESUME**: Is editable all information of the users, also there is CRUD for list card selected Hour and card specialty
 
 ### Appointment Activity
  *you have two shapes for create appointment and See resume of each one*

- **CREATE**:The patient or Medical can create appointment simple medical or for one laboratory for one patient, selecting **_Name_** medical or Lab, **_specialty_** or type of **_analysis_**, **_profession_** or **_direction_** of Labs, select **_Hour_** and **_Date_** and then Confirm. Very Simple .

- **RESUME**: View of the appointment detailed and download files upload for the medical and if open view medical can upload new file of this type: **_Odontogram_**, **_prescription_**, **_forms_**, **_studies_**, etc. and the patient can see those files.

 ### Session Activity
  *Can you login and check in*
  
 - **CHECK-IN**: You can check in with email and create password, also can create different account for patient and medical, if email is checked in account patient, can create and validate Tuition in medical account. Is required **_name_** and **_last name_**, **_email_**, **_dni_** and **_password_**

 - **LOGIN**: If you created medical or patient acount can login with email and password
  
## SUPOSSED CODE:
 I'm supossed code of DATA petition using mvvm, hilt, retrofit, room and Clean Architecture
 
-  function in file ViewModel __DataViewModel.kt__
```kotlin

@HiltViewModel
class DataViewModel @Inject constructor(
    private val dataRepo: DataRepository
) : ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    val data: LiveData<List<Data>> by lazy {
        userRepo.getAllData()
    }
    fun deleteData(toDelete: Data) {
        if (_isLoading.value == false)
            viewModelScope.launch(Dispatchers.IO) {
                _isLoading.postValue(true)
                dataRepo.deleteData(toDelete)
                _isLoading.postValue(false)
            }
    }
    fun loadData() {
        if (_isLoading.value == false)
            viewModelScope.launch(Dispatchers.IO) {
                _isLoading.postValue(true)
                dataRepo.getDate()
                _isLoading.postValue(false)
            }
    }
    fun uploadData(data: Data) {
        if (_isLoading.value == false)
            viewModelScope.launch(Dispatchers.IO) {
                _isLoadingUp.postValue(true)
                dataRepo.putData(data)
                _isLoadingUp.postValue(false)

            }
    }
    fun addData(data: Data) {
        if (_isLoading.value == false)
            viewModelScope.launch(Dispatchers.IO) {
                _isLoading.postValue(true)
                dataRepo.postData(data)
                _isLoading.postValue(false)
            }
    }
...

```
 
 -  __DataRepository.kt__
```kotlin
interface DataRepository {

    suspend fun getData(): ArrayList<Data>
    fun getAllDate(): LiveData<List<Data>>
    suspend fun deleteData(toDelete: Data)
    suspend fun putData(data: Data)
    suspend fun postData(data:Data)
}

class DataRepositoryImp @Inject constructor(
    private val dataDao: DataDao,

) : DataRepository {

    override suspend fun deleteData(toDelete:Data) {
        val call=dataSource.deleteData(toDelete._id)
        dataDao.delete(toDelete)
    }
    
    override suspend fun getData(): ArrayList<Data> {
        delay(3000)
        val listDatas= ArrayList<Data>()
        for (i in 0 until dataSource.getData().result.size) {
            val _id =dataSource.getData().result[i]._id
            val file =dataSource.getData().result[i].file

            val  data= Data(_id,file,)
            listDatas.add(data)
            dataDao.insert(data)
        }
        return listDatas
    }
    
    override fun getAllData() = dataDao.getAllData()
    override suspend fun putData(data:  Data) {
        delay(3000)

        val call = dataSource.putData(data.file)
        call.enqueue(object : Callback<UpdateResponse> {
            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
            }
            override fun onResponse(call: Call<UpdateResponse>, response: retrofit2.Response<UpdateResponse>) {
            }
        })
        dataDao.insert(data)
        return ID
    }
    
    override suspend fun postData(data:Data) {
        delay(3000)
        //DESCARGO LOS DATOS
        var  ID= ""
        val call = dataSource.postData(data.file)
        call.enqueue(object : Callback<DateResponse> {
            override fun onFailure(call: Call<DateResponse>, t: Throwable) {
            }
            override fun onResponse(call: Call<DateResponse>, response: retrofit2.Response<DateResponse>) {

            }
        })
    }


}
```
 - Model directory  __Data.kt__

```kotlin
@Entity(tableName = "datas", indices = [Index(value = ["_id"], unique = true)])
data class Data(
    @ColumnInfo(name = "_id") val _id: String,
    @ColumnInfo(name = "file") val file: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)

@Dao
interface DateDao {

    @Query("SELECT * FROM datas ORDER BY id DESC")
    fun getAllData(): LiveData<List<Data>>
    @Delete
    fun delete(data: Data)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Data)
}
```


- di directory
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun dataRepository(repo: DataRepositoryImp): DataRepository
}
```

```kotlin

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {

    @Singleton
    @Provides
    @Named("BaseUrl")
    fun provideBaseUrl() = "https://data.net/api/"

    @Singleton
    @Provides
    fun provideRetrofit(@Named("BaseUrl") baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(cliente())
            .baseUrl(baseUrl)
            .build()
    }

    @Singleton
    @Provides
    fun cliente(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(100,TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    fun restDataSource(retrofit: Retrofit): RestDataSource =
        retrofit.create(RestDataSource::class.java)

    @Singleton
    @Provides
    fun dbDataSource(@ApplicationContext context: Context): DbDataSource {
        return Room.databaseBuilder(context, DbDataSource::class.java, "data_database")
            .fallbackToDestructiveMigration()
            .build()
    }
    @Singleton
    @Provides
    fun dateDao(db: DbDataSource): DateDao = db.dateDao()

}

```

- DataSource directory
```kotlin
interface RestDataSource {

    @GET("data")
    suspend fun  getDate(): ApiDate

    @DELETE("data/{id}")
    suspend fun  deleteData(@Path("id") id:String): Response<DeleteResponse>

    @FormUrlEncoded
    @PUT("data/{id}")
    fun putDate(
        @Path("id") id:String,
        @Field("file") file:String
    ): Call<UpdateResponse>

    @FormUrlEncoded
    @POST("data")
    fun postData(
        @Field("file") file:String
    ): Call<DataResponse>
    ...
```
```kotlin
@Database(entities = [ Data::class], version =1)
abstract class DbDataSource : RoomDatabase() {

    abstract fun dataDao(): DataDao
}
```








## SUPOSSED CODE-TEST:

 ```kotlin
 @Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
class FakeRepositoryModule {

    @Provides
    @Singleton
    fun appointmentRepository(): AppointmetRepository =
        object : AppointmetRepository {

            private val appointmets = MutableLiveData<List<Appointment>>(listOf());



            override suspend fun deleteAppointment(toDelete: Appointment) {
                appointmets.postValue(appointmets.value?.toMutableList()?.apply { remove(toDelete) })
            }

            override fun getAllAppointment(): LiveData<List<Appointment>> {

                val listAppointment= ArrayList<Appointment>()
                val NewAppointment = Appointment("IDa ${appointmets.value!!.size}", "FECHA", "11:00", "COMENT","IDs 1","IDp 1","IDm 1","PROFF","IDf 1","100")

                listAppointment.add(NewAppointment)
                appointmets.postValue(appointmets.value?.toMutableList()?.apply { add(NewAppointment) })

                return  appointmets
            }


            override suspend fun getPatientAppointment(patient: String): ArrayList<Appointment> {
                val listAppointment= ArrayList<Appointment>()
                val NewAppointment = Appointment("IDa ${appointmets.value!!.size}", "FECHA", "11:00", "COMENT","IDs 1","IDp 1","IDm 1","PROFF","IDf 1","100")

                listAppointment.add(NewAppointment)
                appointmets.postValue(appointmets.value?.toMutableList()?.apply { add(NewAppointment) })

                return listAppointment
            }

            override suspend fun getMedicalAppointment(medical:String): ArrayList<Appointment> {

                val listAppointment= ArrayList<Appointment>()
                 val NewAppointment = Appointment("IDa ${appointmets.value!!.size}", "FECHA", "11:00", "COMENT","IDs 1","IDp 1","IDm 1","PROFF","IDf 1","100")

                listAppointment.add(NewAppointment)
                appointmets.postValue(appointmets.value?.toMutableList()?.apply { add(NewAppointment) })

                return listAppointment
            }


            override suspend fun deleteAllAppointment() {
                appointmets.postValue(appointmets.value?.toMutableList()?.apply { removeAll(appointmets.value!!) })

            }

            override suspend fun postAppointment(appointment: Appointment): String {
                appointmets.postValue(appointmets.value?.toMutableList()?.apply { remove(appointment) })
                return ""
            }
        }
 }
 ```
 
  ```kotlin
  
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AppointmentInstrumentedTest {

    @get:Rule(order  =1)
    var hiltTestRule=HiltAndroidRule(this)

    @get:Rule(order  =2)
    var composeTestRule= createAndroidComposeRule<AppointmentActivity>()
    lateinit var navController: TestNavHostController



    @Test
    fun AppointmentNavHost_Medical_AddNewItem(){
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            AppointmentNavHost(true,"IDm 0","appointmentID","HomeSelect/?newText=",navController,onDownload={

            })
        }

        composeTestRule.onNodeWithText("MEDICAL").performClick()
        composeTestRule.onNodeWithContentDescription("MedicalList").performClick()
        composeTestRule.onNodeWithText("Name 0 Last 0").performClick()
        composeTestRule.onNodeWithContentDescription("ProfessionList").performClick()
        composeTestRule.onNodeWithText("PROFF").performClick()
        composeTestRule.onNodeWithContentDescription("SpecialtyList").performClick()
        composeTestRule.onNodeWithText("TITLE").performClick()
        composeTestRule.onNodeWithContentDescription("PatientList").performClick()
        composeTestRule.onNodeWithText("NAME 0 LAST 0").performClick()
        composeTestRule.onNodeWithContentDescription("CalendarSee").performClick()
        composeTestRule.onNodeWithText("Search").performClick()
        composeTestRule.onNodeWithText("10:00").performClick()
        composeTestRule.onNodeWithText("CONFIRMAR").performClick()

    }
    ...
  ```

  ## Gradle Implementation:
  - **_Jetpack Compose_**
  - **_ROOM_** 
  - **_Retrofit_** 
  - **_Dogger - Hilt - testing_**
  - **_Junit and Espresso_** 
  - **_mockwebserver_**
  - **_Firebase_**
  - **_Maps_**
  - **_Volley_**
  
  ## Necessary data for the DB:
  > _Document of identity, Direction of the Medical, Tuition of Medical, Phone, Email, Name and Last name, Specialty his price and Offer, of medical, Hour of activity_
  > 
  >  **Upload File**: _Odontograms, recipe, forms, result of the Laboratory, studies of medical_



## Librerias 
- Minimum SDK level 23
- [Kotlin](https://kotlinlang.org/) based, [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous.
- Jetpack
  - Lifecycle: Observe Android lifecycles and handle UI states upon the lifecycle changes.
  - ViewModel: Manages UI-related data holder and lifecycle aware. Allows data to survive configuration changes such as screen rotations.
  - DataBinding: Binds UI components in your layouts to data sources in your app using a declarative format rather than programmatically.
  - Room: Constructs Database by providing an abstraction layer over SQLite to allow fluent database access.
  - [Hilt](https://dagger.dev/hilt/): for dependency injection.
- Architecture
  - MVVM Architecture (View - DataBinding - ViewModel - Model)
  - [Bindables](https://github.com/skydoves/bindables): Android DataBinding kit for notifying data changes to UI layers.
  - Repository Pattern
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit): Construct the REST APIs and paging network data.
- [WhatIf](https://github.com/skydoves/whatif): Check nullable objects and empty collections more fluently.
- [Bundler](https://github.com/skydoves/bundler): Android Intent & Bundle extensions, which insert and retrieve values elegantly.
- [ksp](https://github.com/google/ksp): Kotlin Symbol Processing API.
- [Turbine](https://github.com/cashapp/turbine): A small testing library for kotlinx.coroutines Flow.
- [Material-Components](https://github.com/material-components/material-components-android): Material design components for building ripple animation, and CardView.
- [Glide](https://github.com/bumptech/glide), [GlidePalette](https://github.com/florent37/GlidePalette): Loading images from network.
- [TransformationLayout](https://github.com/skydoves/transformationlayout): Implementing transformation motion animations.
- Custom Views
  - [Rainbow](https://github.com/skydoves/rainbow): An easy way to apply gradations and tinting for Android.
  - [AndroidRibbon](https://github.com/skydoves/androidribbon): A simple way to implement a  beautiful ribbon with the shimmering on Android.
  - [ProgressView](https://github.com/skydoves/progressview): A polished and flexible ProgressView, fully customizable with animations.
- [Timber](https://github.com/JakeWharton/timber): A logger with a small, extensible API.

# Licencia
```xml
Designed and developed by 2022 adrian-reh (Adrian Ramon Elias Herrera)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
