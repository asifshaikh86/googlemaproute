# googlemaproute

Draw route between two points

![alt tag](https://drive.google.com/uc?export=view&id=0B4JREw1JZW_cdkF1MmJ5Ujh5WHc)

### Gradle dependency  ```build.gradle```
```
dependencies {
    compile 'com.cs:googlemaproute:1.0.0'
}
```

# Sample usage

First must implement interface

```groovy
implements DrawRoute.onDrawRoute
```

For simple draw line need start loaction(latitude, longitude) and end location(latitude, longitude)

```groovy
DrawRoute.getInstance(this,RouteActivity.this).setFromLatLong(24.905954,67.0803505)
                .setToLatLong(24.9053485,67.079119).setGmapAndKey("MapandroidKey",gMap).run();
```

#Callback listener

This listener return status message and locations, receive from google.

```groovy
@Override
    public void afterDraw(String result) {
    Log.d("response",""+result);
    }
```


#Methods

Method |  Description 
------------ |  -------------
setFromLatLong  |  Need two parameters both are double types, ``Start latitude`` and ``Start longitude``
setToLatLong  | Need two parameters both are double types, ``End latitude`` and ``End longitude``
setGmapAndKey  | Need two parameters ``MapObject`` and string ``Mapkey``
setZoomLevel  | Need one parameter it should be float like ``15.0f``
setColorHash  | Need one parameter it should be string hash code ``"#00ff00"``
setLoader  | Need one parameter it should be bolean ``true`` or ``false``
setLoaderMsg  | Need one parameter it should be string like ``"Draw line, please wait..."``

### Start from 
``minSdkVersion 19 ``


# LICENSE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

##Authors

* [Muhammad Asif Shaikh](https://github.com/asifshaikh86) *original Author*
