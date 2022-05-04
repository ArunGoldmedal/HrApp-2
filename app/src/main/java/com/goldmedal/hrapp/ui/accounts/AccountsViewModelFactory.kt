package com.goldmedal.hrapp.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.hrapp.data.repositories.AccountsRepository


@Suppress("UNCHECKED_CAST")
class AccountsViewModelFactory(
    private val repository: AccountsRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AccountsViewModel(repository) as T
    }
}