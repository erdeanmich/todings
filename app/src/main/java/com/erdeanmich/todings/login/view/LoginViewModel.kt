package com.erdeanmich.todings.login.view

import android.app.Application
import android.util.Patterns
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.erdeanmich.todings.database.DatabaseProvider
import com.erdeanmich.todings.database.ToDoItemDAO
import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.web.model.ApiTransformer
import com.erdeanmich.todings.web.model.UserApiModel
import com.erdeanmich.todings.web.service.ToDoService
import com.erdeanmich.todings.web.service.UserService
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val toDoItemDao: ToDoItemDAO by lazy { DatabaseProvider.getToDoItemDAO(application) }
    private val passwordValid: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mailValid: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mailText: MutableLiveData<String> = MutableLiveData("")
    private val passwordText: MutableLiveData<String> = MutableLiveData("")
    private val allValid: MutableLiveData<Boolean> = MutableLiveData(false)
    private val transformer = ApiTransformer()

    fun getMailValidStatus(): LiveData<Boolean> {
        return mailValid
    }

    fun getPasswordValidStatus(): LiveData<Boolean> {
        return passwordValid
    }

    fun getValidStatus(): LiveData<Boolean> {
        return allValid
    }

    fun setPassword(password: String) {
        passwordText.value = password
        validatePassword()
    }

    fun setMail(mail: String) {
        mailText.value = mail
        validateMail()
    }

    suspend fun getRemoteToDos() {
        val response = ToDoService.getService().getAllToDos()
        if (!response.isSuccessful) {
            return
        }

        val items = response.body()?.map { transformer.apiToLocal(it) } ?: emptyList<ToDoItem>()
        toDoItemDao.deleteAll()
        items.forEach { toDoItemDao.insert(it) }
    }

    suspend fun syncLocalToDosToRemote() {
        val service = ToDoService.getService()
        val deleteResponse = service.deleteAllToDos()
        if (!deleteResponse.isSuccessful) {
            return
        }

        toDoItemDao.getAll()
            .map { transformer.localToApi(it) }
            .forEach {
                service.createToDo(it)
            }

    }

    suspend fun getToDosInDB(): List<ToDoItem> {
        return toDoItemDao.getAll()
    }

    suspend fun login(): Response<Boolean> {
        return UserService.getService()
            .authenticate(UserApiModel(passwordText.value!!, mailText.value!!))
    }

    private fun validatePassword() {
        val password = passwordText.value
        val isValid = !password.isNullOrEmpty() && password.isDigitsOnly() && password.length == 6
        passwordValid.value = isValid
        validateAll()

    }

    private fun validateMail() {
        val mail = mailText.value
        val isValid = !mail.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()
        mailValid.value = isValid
        validateAll()
    }

    private fun validateAll() {
        allValid.value = (passwordValid.value ?: false) && (mailValid.value ?: false)
    }
}