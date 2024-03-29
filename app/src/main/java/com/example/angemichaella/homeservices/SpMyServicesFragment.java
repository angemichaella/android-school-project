package com.example.angemichaella.homeservices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpMyServicesFragment extends Fragment {
    String id;
    String username;
    DatabaseReference spNode;   //node in database where this service provider is stored

    private FloatingActionButton addMyServiceBtn;
    protected ArrayList<Service> myServices;

    DatabaseReference databaseServices;
    ListView myServiceListView;
    ServiceAdapter adptr;

    ServiceProvider sp;

    //constructor that allows passing arguments from main activity
    public static SpMyServicesFragment newInstance(String username, String id ) {
        SpMyServicesFragment myFrag = new SpMyServicesFragment();

        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("ID", id);
        myFrag.setArguments(args);

        return myFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            username = getArguments().getString("username");
            id = getArguments().getString("ID");
            spNode = FirebaseDatabase.getInstance().getReference("users").child( id );
            setServiceProvider();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sp_myservices, null);

        //initialize attributes
        addMyServiceBtn = view.findViewById(R.id.addFloatingActionButton);
        myServiceListView = (ListView)view.findViewById(R.id.serviceListView);


        //hold to delete service
        myServiceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Service");
                final Service clickedServ = (Service)myServiceListView.getItemAtPosition(index);
                builder.setMessage("Are you sure you want to delete " + '"'+ clickedServ.name()+ '"' +" from your profile?");


                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){

                        deleteService(clickedServ);
                        dialog.dismiss();//user clicked create

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        return view;
    }


    //onclick of add service button
    private void addServicePopUp(){
        AddServiceDialog d = new AddServiceDialog();
        d.show(getActivity().getSupportFragmentManager(), "add service dialog");
    }


    //deletes a service from the database
    private void deleteService(Service service){

        sp.removeService(service);
        spNode.setValue(sp);
        Toast.makeText(getActivity(), "Service Deleted", Toast.LENGTH_LONG).show();

    }


    //setting up SP object and their servcie list from database
    private void setServiceProvider(){
        spNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                sp = dataSnapshot.getValue(ServiceProvider.class);
                myServices = sp.services;
                addMyServiceBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addServicePopUp();
                    }
                });

                if(sp.hasServices()){
                    adptr = new ServiceAdapter(getActivity(), myServices);
                    myServiceListView.setAdapter(adptr);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    public void addService(Service s){
        boolean alreadyexist = false;

        if(sp.hasServices()) {
            for (Service sv : myServices) {
                if (sv.id().equals(s.id())){
                    alreadyexist = true;
                    Toast.makeText(getActivity(), "Service is already in your profile", Toast.LENGTH_LONG).show();
                }

            }
        }

        if(!alreadyexist){
            sp.addService(s); //adds the service
            spNode.setValue(sp); //updates service provider in database
            Toast.makeText(getActivity(), "Service '"+ s.name()+ "' added", Toast.LENGTH_LONG).show();
        }

    }

    /*
     * For real time updating my service list
     */
    @Override
    public void onStart(){
        super.onStart();
        spNode.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                sp = dataSnapshot.getValue(ServiceProvider.class);
                if(sp.hasServices()){
                    if(getActivity()!= null) {
                        myServices = sp.services;
                        adptr = new ServiceAdapter(getActivity(), myServices);
                        myServiceListView.setAdapter(adptr);
                    }
                }else{
                    myServiceListView.setAdapter(null);
                }

            }
            public void onCancelled(DatabaseError databaseError){

            }
        });
    }

}

