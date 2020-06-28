# Placer

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/devloopsnet/placer.svg)](https://jitpack.io/#devloopsnet/placer)

Placer is an Easy, lightweight Places picker library for Android!
Placer handles permission requests
supports API 19 - 29

### Installing

This library is available through jitpack.io. You can also import this library as a module.

### Add it in your project level build.gradle at the end of repositories:
```
allprojects {
       repositories {
           ...
           maven { url 'https://jitpack.io' }
         }
    }
```    

### Add the dependency
```

dependencies {
     implementation 'com.github.devloopsnet:Placer:$LATEST_VERSION'
}
```

### Add the following to your app level build.gradle file
```groovy
        multiDexEnabled = true
```

Like so...
```
defaultConfig {
    ...
     multiDexEnabled = true
}
```
```
android{
    ...
    compileOptions {
            sourceCompatibility = '1.8'
            targetCompatibility = '1.8'
        }
}
```

## Usage:
* get your google api key (enabled for maps)
* add google api key in strings.xml with name "google_maps_key". example: <string name="google_maps_key">GOOGLE_API_KEY</string>
* Simply call `Placer.withActivity(this).show();` in Activity  `Placer.withFragment(this).show();` in Fragment.     
* receive `address` `LatLng` intent results in onActivityResult.

### Example:  
```java 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Placer.LOCATION_REQUEST_CODE) {
                Bundle intent = Objects.requireNonNull(data).getExtras();
                if (intent != null) {
                    String address = data.getStringExtra("address");
                    LatLng latLng = (LatLng) intent.get("LatLng");
                    if (latLng != null)
                        Logger.i(Placer.TAG, "Address: " + address + "latLng: " 
                                            + latLng.latitude + "," + latLng.longitude);
    
                }
            }
        }
    }
```

## UI Customizations
You can override colors and strings to properly customize your layout.
 
### Customizable Colors: 
add color value in colors.xml file to override

|		Resource name		|		Description		|
|:----------------------|:---------------------:|
|		`header_background_color`			|	Header background color			|
|		`btn_back_arrow_color`		|	back arrow tint color (API < 21)	|
|		`header_text_color`		|	Header text color	|
|		`search_area_background_color`		|	Button clear layout background color	|
|		`search_txt_color`		|	Search phrase text color	|
|		`btn_clear_color`		|	Clear icon tint color (API < 21)	|
|		`color_marker`		|	Map marker tint color (API < 21)	|
|		`color_progress`		|	ProgressBar tint color (API < 21)	|
|		`btn_text_color`		|	Button text color	|
|		`btn_background_color`		|	Button background color	|


### Customizable Strings: 
add string value in strings.xml to override

|		Resource name		|		Description		|
|:----------------------|:---------------------:|
|		`google_maps_key`			|	MAPS_API_KEY (mandatory)			|
|		`msg_no_location`		|	No Location text	|
|		`txt_location`		|	Location text	|
|		`txt_header`		|	Picker Title	|
|		`txt_btn_done`		|	Button text	|
|		`txt_cannot_find_address`		|	Error when cannot find location	|


## Author
[Odey M. Khalaf](https://github.com/OdeyFox)

## License
   Copyright (c) [2020] [Devloops LLC](https://devloops.net/)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
