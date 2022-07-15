package com.example.roomexample

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomexample.db.Subscriber
import com.example.roomexample.db.SubscriberRepository
import kotlinx.coroutines.launch

class SubscriberViewModel (private val repository: SubscriberRepository) : ViewModel(){

    private var isUpdateorDelete = false
    private lateinit var subscribertosaveorupdate:Subscriber
    val subscribers=repository.subscribers
    val inputName=MutableLiveData<String>()
    val inputEmail=MutableLiveData<String>()

    private val statusmessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusmessage

    val saveorupdatebuttontext=MutableLiveData<String>()
    val clearallordeletebuttontext=MutableLiveData<String>()
    init {
        saveorupdatebuttontext.value="Save"
        clearallordeletebuttontext.value="Clear All"
    }
    fun saveorupdate(){

        if(inputName.value==null){
            statusmessage.value = Event("Please Enter Subscriber's Name")

        }
        else if(inputEmail.value==null){
            statusmessage.value = Event("Please Enter Subscriber's Email")

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches())
        {
            statusmessage.value = Event("Please Enter correct Email Address")

        }
        else {
            if (isUpdateorDelete) {
                subscribertosaveorupdate.name = inputName.value!!
                subscribertosaveorupdate.email = inputEmail.value!!
                update(subscribertosaveorupdate)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                //auto generate hojyega count iska
                insert(Subscriber(0, name, email))
                inputName.value = ""
                inputEmail.value = ""
            }
        }
    }
    fun clearallordelete(){
        if(isUpdateorDelete)
        {
            delete(subscribertosaveorupdate)
        }
        else{
            clearAll()
        }
    }
    fun insert(subscriber: Subscriber)=
        viewModelScope.launch {
            val newRowID=repository.insert(subscriber)
            if (newRowID>-1) {
                statusmessage.value = Event("Subscriber Inserted Successfully ${newRowID-60}")
            }
            else{
                statusmessage.value = Event("Error Occured During Insertion Process")
            }
        }
    fun update(subscriber: Subscriber)=viewModelScope.launch {
        val newRowID=repository.update(subscriber)
        if (newRowID>0) {


            inputName.value = ""
            inputEmail.value = ""
            isUpdateorDelete = false
            saveorupdatebuttontext.value = "Save"
            clearallordeletebuttontext.value = "Clear All"
            statusmessage.value = Event("Subscriber Updated Successfully $newRowID")
        }
        else{
            statusmessage.value = Event("Error Occured During updation Process")

        }

    }
    fun delete(subscriber: Subscriber)=viewModelScope.launch {

        val newRowId=repository.delete(subscriber)
        if(newRowId>0) {
            inputName.value = ""
            inputEmail.value = ""
            isUpdateorDelete = false
            saveorupdatebuttontext.value = "Save"
            clearallordeletebuttontext.value = "Clear All"

            statusmessage.value = Event("Subscriber Deleted Successfully")
        }
        else{
            statusmessage.value = Event("Error Occured During Deletion Process")

        }
    }
    fun clearAll()=viewModelScope.launch {
        val noofrowsdeleted=repository.deleteAll()
        if (noofrowsdeleted>0) {
            statusmessage.value = Event("$noofrowsdeleted Subscribers Deleted Successfully")
        }
        else {
            statusmessage.value = Event("Error Occured")

        }

    }
    fun initSaveOrUpdate(subscriber: Subscriber){
        inputName.value=subscriber.name
        inputEmail.value=subscriber.email
        isUpdateorDelete=true
        subscribertosaveorupdate=subscriber
        saveorupdatebuttontext.value="Update"
        clearallordeletebuttontext.value="Delete"
    }
}