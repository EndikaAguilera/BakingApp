# Baking App

- Project for [Android Developer Nanodregree](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801) by [Udacity](https://www.udacity.com/).

- Your task is to create a Android Baking App that will allow Udacity’s resident baker-in-chief, Miriam, to share her recipes with the world. You will create an app that will allow a user to select a recipe and see video-guided steps for how to complete it.

- [The recipe listing is located here](https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json)

- The JSON file contains the recipes' instructions, ingredients, videos and images you will need to complete this project. Don’t assume that all steps of the recipe have a video. Some may have a video, an image, or no visual media at all.

- One of the skills you will demonstrate in this project is how to handle unexpected input in your data -- professional developers often cannot expect polished JSON data when building an app.

# Navigate

  - [Baking App](#baking-app)
  
  - [What is in this project](#what-is-in-this-project)
  
  - [Project Meets Required Specifications](#project-meets-required-specifications)
    - [General App Usage](#general-app-usage)
    - [Components and Libraries](#components-and-libraries)
    - [Homescreen Widget](#homescreen-widget)
    
 - [Libraries](#libraries)
 
 - [Art](#art)
    - [Nexus 5 Screens](#nexus-5-screens)
    - [Nexus 7 Screens](#nexus-7-screens)
    - [Pixel C Screens](#pixel-c-screens)
    
 - [License](#license)

## What is in this project

  - Use MediaPlayer/Exoplayer to display videos.
  - Handle error cases in Android.
  - Add a widget to your app experience.
  - Leverage a third-party library in your app.
  - Use Fragments to create a responsive design that works on phones and tablets.


# Project Meets Required Specifications

## General App Usage

  - [x] App should display recipes from provided network resource.
  - [x] App should allow navigation between individual recipes and recipe steps.
  - [x] App uses RecyclerView and can handle recipe steps that include videos or images.
  - [x] App conforms to common standards found in the Android Nanodegree General Project Guidelines.

## Components and Libraries

  - [x] Application uses Master Detail Flow to display recipe steps and navigation between them.
  - [x] Application uses Exoplayer to display videos.
  - [x] Application properly initializes and releases video assets when appropriate.
  - [x] Application should properly retrieve media assets from the provided network links. It should properly handle network requests.
  - [x] Application makes use of Espresso to test aspects of the UI.
  - [x] Application sensibly utilizes a third-party library to enhance the app's features. That could be helper library to interface with ContentProviders if you choose to store the recipes, a UI binding library to avoid writing findViewById a bunch of times, or something similar.

## Homescreen Widget

  - [x] Application has a companion homescreen widget.
  - [x] Widget displays ingredient list for desired recipe.
  
# Libraries
  - [ExoPlayer](https://github.com/google/ExoPlayer)
  - [Butter Knife](http://jakewharton.github.io/butterknife/)
  - [Volley](https://developer.android.com/training/volley/index.html)
  - [Gson](https://github.com/google/gson)
  - [Espresso](https://developer.android.com/training/testing/espresso/index.html)
  - [Gide](https://github.com/bumptech/glide)    
    
# Art

## Nexus 5 Screens
![screen](../master/art/nexus_5_baking_app_portrait_poster.jpg)
![screen](../master/art/nexus_5_baking_app_landscape_poster.jpg)

## Nexus 7 Screens
![screen](../master/art/nexus_7_baking_app_portrait_poster.jpg)
![screen](../master/art/nexus_7_baking_app_landscape_poster.jpg)

## Pixel C Screens
![screen](../master/art/pixel_C_baking_app_portrait_poster.jpg)
![screen](../master/art/pixel_C_baking_app_landscape_poster.jpg)

# License

```Copyright 2017 Endika Aguilera```

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
