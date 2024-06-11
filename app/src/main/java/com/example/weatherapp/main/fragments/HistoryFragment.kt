package com.example.weatherapp.main.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.DatabaseHelper
import com.example.weatherapp.R
import com.example.weatherapp.adapter.HistoryAdapter
import com.example.weatherapp.databinding.FragmentHistoryBinding
import com.example.weatherapp.models.FinalForecastModel
import com.example.weatherapp.viewModel.DbViewModel


class HistoryFragment : Fragment() {

    lateinit var binding:FragmentHistoryBinding
    lateinit var adapter: HistoryAdapter

    lateinit var helper: DatabaseHelper
    val list= mutableListOf<FinalForecastModel>()
    private lateinit var dbViewModel: DbViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentHistoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        helper= DatabaseHelper(requireContext())
        dbViewModel = ViewModelProvider(this)[DbViewModel::class.java]
        dbViewModel.dbLiveData.observe(viewLifecycleOwner){
            binding.runCatching {
                list.clear()
                list.addAll(it)
                rvHistory.layoutManager=LinearLayoutManager(requireContext())
                adapter=HistoryAdapter(list,{
                    val intent=Intent(requireActivity(),ForecastDetailsActivity::class.java)
                    intent.putExtra("data",list[it])
                    requireActivity().startActivity(intent)
                },{
                    val data=list[it]
                    val deleteList=helper.getAllEntriesWithDate(data.time.toString())
                    deleteList.forEach {
                        helper.deleteForecast(it.id?:0)
                    }
                    list.remove(data)
                    adapter.notifyDataSetChanged()
                })
                rvHistory.adapter=adapter

            }


        }
    }

    override fun onResume() {
        super.onResume()
        dbViewModel.getAllData()
    }
}