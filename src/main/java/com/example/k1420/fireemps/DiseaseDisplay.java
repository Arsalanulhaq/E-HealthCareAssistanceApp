package com.example.k1420.fireemps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DiseaseDisplay extends AppCompatActivity {

    HashMap<String,String> allSymp = new HashMap<>();
    ArrayList <String> selectedSympListIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_display);

        TextView diseaseText = findViewById(R.id.diseaseName);
        Intent i = getIntent();
        Bundle b = i.getBundleExtra("myBundle");
        selectedSympListIds = b.getStringArrayList("selectedSymList");
        ArrayList <String> selectedSympListNames = new ArrayList<>();

                allSymp = (HashMap<String, String>) b.getSerializable("allSymptoms");
        final String disease_name = b.getString("diseaseName");

        diseaseText.setText(disease_name);

        for(String symp : selectedSympListIds){
            Log.d("2nd activity: ","selectedSymptoms : " + symp);
            selectedSympListNames.add(allSymp.get(symp));
        }
//        Log.d("2nd activity", "New List ---");
//        for(String symp : selectedSympListNames){
//            Log.d("2nd activity: ","selectedSymptoms : " + symp);
//        }

        ListView sym_listView = findViewById(R.id.symList);

        /* ListView Start */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, selectedSympListNames);
        sym_listView.setAdapter(adapter);
        /* ListView End */

        TextView moreSymptoms = (TextView) findViewById(R.id.moreSymptoms);
        moreSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreSymptoms(disease_name);
            }
        });

    }



    public void showMoreSymptoms(String disease_name){
        final ArrayList <String> moreSymps = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("mapping/" + disease_name);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Log.d("TAGGG", "id : " + snap.getKey() + ", name : " + allSymp.get(snap.getKey()));
                    if(!selectedSympListIds.contains(snap.getKey())){
                        moreSymps.add(allSymp.get(snap.getKey()));
                    }
                }
                showMoreSymps(moreSymps);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showMoreSymps(ArrayList<String> moreSymps){

        ListView sym_listView = findViewById(R.id.moreSymList);
        /* ListView Start */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, moreSymps);
        sym_listView.setAdapter(adapter);
        /* ListView End */
    }

}
