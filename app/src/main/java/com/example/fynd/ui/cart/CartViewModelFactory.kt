package com.example.fynd.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fynd.data.repositories.CartRepository
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class CartViewModelFactory(private val repository: CartRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CartViewModel::class.java)){
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}