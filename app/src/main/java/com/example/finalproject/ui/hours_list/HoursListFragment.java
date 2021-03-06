package com.example.finalproject.ui.hours_list;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.finalproject.MyApplication;
import com.example.finalproject.R;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;


public class HoursListFragment extends Fragment {

    View view;
    hoursListViewModel hoursListViewModel;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView queueList;
    String barbershopId;
    MyAdapter adapter;
    String fullDate;
    TextView dateTv;
    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialise Params
        view = inflater.inflate(R.layout.fragment_hours_list, container, false);
        barbershopId = HoursListFragmentArgs.fromBundle(getArguments()).getBarbershopId();
        swipeRefreshLayout = view.findViewById(R.id.hoursList_swipeRefreshLayout);
        fullDate = HoursListFragmentArgs.fromBundle(getArguments()).getDate();
        dateTv = view.findViewById(R.id.hoursList_selectedDate_tv);
        pb = view.findViewById(R.id.hoursList_progressBar);
        swipeRefreshLayout.setRefreshing(false);
        pb.setVisibility(View.GONE);
        dateTv.setText(fullDate);

        //ViewModel
        hoursListViewModel  = new ViewModelProvider(this).get(hoursListViewModel.class);
        hoursListViewModel.getData().observe(getViewLifecycleOwner(), (data)->{
                    hoursListViewModel.getFilterData(fullDate,barbershopId);
                    adapter.notifyDataSetChanged();
        });


        //RecyclerView
        queueList = view.findViewById(R.id.hoursList_RecyclerView);
        queueList.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(MyApplication.context);
        queueList.setLayoutManager(manager);
        adapter = new MyAdapter();
        queueList.setAdapter(adapter);

        //Listeners:
        adapter.setOnClickListener((position)-> {

            Queue queue = hoursListViewModel.list.get(position);
            if(queue.isQueueAvailable)
            {
                queue.setQueueAvailable(false);
                queue.userId= Model.instance.getUser().getId();
                Model.instance.saveQueue(queue,()->{
                    Navigation.findNavController(view).navigate(R.id.nav_queues_list_Fragment);
                });
            }
        });
        swipeRefreshLayout.setOnRefreshListener(()->hoursListViewModel.refresh());
        setUpProgressListener();

        return view;
    }

    private void setUpProgressListener() {
        Model.instance.loadingState.observe(getViewLifecycleOwner(),(state)->{
            switch(state){
                case loaded:
                    pb.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case loading:
                    pb.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(true);
                    break;
                case error:
                    //...
            }
        });
    }


    static class MyViewHolder extends RecyclerView.ViewHolder{
        OnItemClickListener listener;
        TextView timeTv;
        TextView isQueueAvailableTv;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.hoursListRow_hour_tv);
            isQueueAvailableTv = itemView.findViewById(R.id.hoursListRow_isAvailable_tv);
            //After we created the listener we connect it to this row:
            this.listener=listener;
            itemView.setOnClickListener(v -> {
                if(listener!=null){
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onClick(position);
                    }
                }
            });
        }

        public void bind(Queue queue)
        {
            timeTv.setText(queue.getQueueTime());
            if(!queue.isQueueAvailable)
                isQueueAvailableTv.setText("Not Available");
            else
                isQueueAvailableTv.setText("");
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        OnItemClickListener listener;

        public void setOnClickListener(OnItemClickListener listener){
            this.listener=listener;
        }
        //Create a viewHolder for the view:
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= getLayoutInflater().inflate(R.layout.hours_list_row,parent,false);
            MyViewHolder holder= new MyViewHolder(view,listener);
            return holder;
        }
        // make the variables bind to the created view from "onCreateViewHolder" function:
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Queue queue = hoursListViewModel.list.get(position);
            holder.bind(queue);
        }
        //Give me the items count:
        @Override
        public int getItemCount() {
            return hoursListViewModel.list.size();
        }

    }
}