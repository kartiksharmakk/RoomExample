package com.example.roomexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomexample.databinding.ActivityMainBinding
import com.example.roomexample.db.Subscriber
import com.example.roomexample.db.SubscriberDatabase
import com.example.roomexample.db.SubscriberRepository
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
         val dao=SubscriberDatabase.getInstance(application).subscriberDAO
        val repository=SubscriberRepository(dao)
        val Factory= SubscriberViewModelFactory(repository)
        subscriberViewModel=ViewModelProvider(this,Factory).get(SubscriberViewModel::class.java)
        binding.myViewModel=subscriberViewModel
        binding.lifecycleOwner=this
        initRecyclerView()

        subscriberViewModel.message.observe(this, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let{
                Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun initRecyclerView(){
        binding.subscriberRecyclerView.layoutManager=LinearLayoutManager(this)
        adapter=MyRecyclerViewAdapter(
            {selectedItem:Subscriber->listItemClicked(selectedItem)})
        binding.subscriberRecyclerView.adapter=adapter
        displaySubscribersList()
    }
    fun getSavedSubscribers() = liveData {
        subscriberViewModel.subscribers.collect {
            emit(it)
        }
    }
    private fun displaySubscribersList(){
        getSavedSubscribers().observe(this, androidx.lifecycle.Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }
    private fun listItemClicked(subscriber:Subscriber){
        //Toast.makeText(this,"Selected name is ${subscriber.name}",Toast.LENGTH_LONG).show()
        subscriberViewModel.initSaveOrUpdate(subscriber)
    }
}