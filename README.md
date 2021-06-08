Android Covid Detector
======================

[![toharifqi](https://circleci.com/gh/toharifqi/CovidDetector.svg?style=svg)](https://circleci.com/gh/toharifqi/CovidDetector)

A Covid Detector App illustrating Android development practices with Android Jetpack.

The main purpose of this application is to identify if user has infected with Covid-19. With this application we can detect if a person gets infected by covid-19 using cough sound. Hence, we can anticipate the infection of covid-19 before it gets worse.  

Android Covid Detector is currently under development. Some changes will be updated (such as database schema modifications).
  
App Screenshots 
---------------

![Authentication](screenshots/login.jpg "User Authentication.")
![Home Screen](screenshots/home.jpg "Home Screen - Contain: main feature, sympthoms list, preventions list.")
![User Profile](screenshots/profile.jpg "User Profile.")
  
Main Feature Screenshots
------------------------

![Main Feature](screenshots/main1.jpg "Main Feature.")
![Cough Analyzation](screenshots/main3.jpg "Analyzing User's Cough.")
![Classification Result](screenshots/main4.jpg "Cough Classification Result.")

Introduction
------------

We developed our app using Android Jetpack guideline. Android Jetpack is a set of components, tools and guidance to make great Android apps. They bring together the existing Support Library and Architecture Components and arrange them into four categories:

![jetpack](https://storage.googleapis.com/kotakode-prod-public/images/59ec86ea-09db-4a7f-b870-488633d70583-1_FB931aBGoALv3OLY5LSRGg.png)

Android Covid Detector demonstrates utilizing these components to create a simple detect covid using sound of cough.
  
Latest Updated Branch
---------------------
* master (Added main feature: Covid Detection with tensorflow lite)
* development-1.0 (Added main feature: Covid Detection with tensorflow lite)
  
Getting Started
---------------

This project uses the Gradle build system. To build this project, use the
`gradlew build` command or use "Import Project" in Android Studio.

There are two Gradle tasks for testing the project:
* `connectedAndroidTest` - for running Espresso on a connected device
* `test` - for running unit tests

For more resources on learning Android development, visit the
[Developer Guides](https://developer.android.com/guide/) at
[developer.android.com](https://developer.android.com).

Libraries Used
--------------

- Foundation 
  - AppCompat
  - Android KTX 
  - Test
- Architecture
  - Data Binding
  - Lifecycles
  - LiveData
  - ViewModel
  - Navigation 
  - Room (Local Database)
  - Firebase real-time database (Remote Database)
  - Firebase Storage (Remote Storage)
- UI
  - Fragment
  - Layout
- Third party and miscellaneous libraries
  - Glide for image loading
  - CircleImage
  - dmax Spot Dialog
  - TensorFlow Lite (Machine Learning)
 
Continous Integration Tools
---------------------------
Circle CI [![toharifqi](https://circleci.com/gh/toharifqi/CovidDetector.svg?style=shield)](https://circleci.com/gh/toharifqi/CovidDetector)
  
Upcoming features
-----------------
Updates will include news feature using API to get latest information about COVID-19 for users.
  
Machine Learning Documentation
------------------------------
- Download Dataset
- Preprocess Dataset
- Make model and training dataset 
- Create Prediction
- Result Prediction
- Convert keras model to tflite model
  
Cloud Computing Documentation
-----------------------------
- Create VM Instance for running Jupyter Notebooks, Tensorflow, Keras.
- Create firebase to store user data
- Create custom model tflite on firebase
