plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fansfun"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fansfun"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true;
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-firestore:24.10.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")

    implementation ("androidx.databinding:databinding-runtime:7.0.3")

    //google palcesAPI
    implementation("com.google.android.libraries.places:places:3.3.0")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")

    //visualizzazioni immagini storage
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    //codice qr
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")

    //viewmodel
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.3.1")


    //mappe
    implementation ("org.osmdroid:osmdroid-android:6.1.10")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //imagepicker
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    //slider images
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    //CardView
    implementation ("androidx.cardview:cardview:1.0.0")

    //per implementare le recycle view
    implementation ("androidx.recyclerview:recyclerview:1.2.1")


}