package com.example.quickbuy.di

import com.example.quickbuy.repository.ProductRepository
import com.example.quickbuy.repository.ProductRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirestoreDb() : FirebaseFirestore{
        return Firebase.firestore
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideProductFirebaseRepository(
        firestore: FirebaseFirestore
    ): ProductRepository {
        return ProductRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideUserId():String = provideFirebaseAuth().currentUser?.uid?:""

}