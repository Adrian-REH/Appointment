# Appointment
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white&labelColor=7F52FF)]()
[![Android_Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=flat-square&logo=android-studio&logoColor=black&labelColor=3DDC84)]()
[![php](https://img.shields.io/badge/php-F7DF1E?style=flat-square&logo=php&logoColor=black&labelColor=F7DF1E)]()
[![MySQL](https://img.shields.io/badge/MySQL-279FDF?style=flat-square&logo=mysql&logoColor=white&labelColor=279FDF)]()
</br>
   Nos especializamos unicamente en ODONTOLOGIA, queremso facilitar la organizacion de turnos para los profesionales, asi tambien aplicaremos distintas funcionalidades como:
   - Visualizar, gestionar y diferenciar por calificacion las distintas Tarifas o aplicacion de Descuentos a sus clientes
   - Generar, limitar y combinar distintos Tickets temporales para regalar a sus clientes por bondad del profesional.
   - Organizar los distintos clientes y beneficiarlos con me gusta, una ESTRELLA!, descuentos,  tickets tambien se podra descalificar con un Bloqueo
   - Crear cuentas a distintos clientes para facilitarles el acceso
   - Organizar sus turnos por orden de llegada, brindarles el tiempo de espera y entretenerlos con los datos del Wifi del local asi como las redes sociales del profesional, asi como tambien
   notificar a sus clientes de Repentinos cambios de horarios o deshabilitacion de turnos 
   - Tambien podra ver los clientes que quieren sacar turnos para el profesional.
   - P
## Diagrama de estado
![diagrama](https://github.com/Adrian-REH/Appointment/blob/main/Diagrama%20sin%20título.drawio.png)
## Descarga
[![Google Play](https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=googleplay&logoColor=white&labelColor=414141)]() Proximamente
## Vista previa y descripcion de actividad
### Principal
  - **Turnos( en espera y proximos a atender) y Descuentos, limite de tickets**
    1. Seran dos listas uno que son los turnos que se atienden en la fecha, los que estan en espera para confirmar y un BOTON que de un calendario de todos los turnos que 
  sacaron los clientes en general.
    2. Los turnos tendran descuentos personalizados para cada clientes que podra cambiarlos entrando a su perfil, su entra a configuracion puede agregar mas descuentos o quitar
    algunos
    3. Podra ver la lista de Tickets usados o sin usar con un boton podra entrar y regalar mas tickets o podra simplemente anularos notificando a su cliente.
    4. al Ingresar a al Turno podra cancelarlo o cambiar su hora.
  
  
  - **Lista de clientes con me gusta y estrellas**
    1.Habra una lista inmensa de clientes que podra ver con me gusta, estrella o simplemente entrando a su perfil podra bloquearlos, tambien regalarle Ticket o darle descuentos
    2.La lista de clientes tiene que tener un orden de, clientes que le dio una estrella o los que simplemente no, tambien podra ver el numero de turnos que saco en general.
    
  - **Configuracion y Boton de datos personales**
    1. Aqui tendra acceso a sus datos personales presionando un boton. podra cambiarlos instantaneamente excepto los que piden verificacion, Tambien se podra aplicar descuentos
    y ticket para los clientes dependiendo el numero de turnos si quiere que se aplique de forma automatica o por estrella o un simple me gusta.
    2. Tambien podra configurar:
     - El acceso
     - Crear nuevos Clientes 
     - Codigo de Recomendacion
     - Estilo de la vista e Idioma
     - Tambien podra ver la lista de bloqueados y desbloquearlos
     - Podra desHabilitar los turnos para todos los clientes o solo algunos.

 <p align="center"><img height="800" src=""> </p>

### Actividad de Turnos
### Actividad de Clientes
### Actividad de Profesional
### Actividad de Configuracion



## Librerias o repositorios
Api
  - retrofit
  - okhttp
  - gson

Testing
  - Junit
  - Mokito
  - Expresso(Vista)

UI
  - JetPack Compose

Data Base Offline
  - Room

Dependencias Injection
  - Hilt
