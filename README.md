# Appointment Medical
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white&labelColor=7F52FF)]()
[![Android_Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=flat-square&logo=android-studio&logoColor=black&labelColor=3DDC84)]()
[![php](https://img.shields.io/badge/php-F7DF1E?style=flat-square&logo=php&logoColor=black&labelColor=F7DF1E)]()
[![MySQL](https://img.shields.io/badge/MySQL-279FDF?style=flat-square&logo=mysql&logoColor=white&labelColor=279FDF)]()
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

## Descarga
[![Google Play](https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=googleplay&logoColor=white&labelColor=414141)]() Proximamente


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
