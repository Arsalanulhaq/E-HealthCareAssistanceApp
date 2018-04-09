
package com.example.k1420.fireemps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG_KEY";
    final HashMap<String, String> all_symptom = new HashMap<>();
    final ArrayList <String> selected_symptoms = new ArrayList<>();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

 //      TextView text1 = (TextView) findViewById(R.id.textView1);
//        TextView text2 = (TextView) findViewById(R.id.textView2);
//        TextView text3 = (TextView) findViewById(R.id.textView3);
//        TextView text4 = (TextView) findViewById(R.id.textView4);

//        final List <String> sym_arr = new ArrayList<>();
//        sym_arr.add("2");
//        sym_arr.add("3");
//        sym_arr.add("6");
//        sym_arr.add("7");
//        getDiseasesofSymptom(sym_arr);
        getAllSymptoms();

    }


    /* Function-1 retrieving all symptoms with their id's and saving them symptom Hashmap.
        Then calling getAllDiseases Function*/

    private void getAllSymptoms(){
        Log.d(TAG, "---- Start-getAllSymptoms() ----");

        final int[] symptom_count = {0};

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("symptoms");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Log.d(TAG, "id : " + snap.getKey() + ", value : " + snap.getValue());
                    all_symptom.put(snap.getKey(), snap.getValue().toString());
                    symptom_count[0]++;
                }
                Log.d(TAG, "Total Symptoms Count : " + symptom_count[0]);
//                for(Map.Entry<String, String> entry : symptom.entrySet()){
//                    Log.d(TAG, "KEYY: " + entry.getKey() + " VAL: " + entry.getValue());
//                }

                getAllDiseases();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "---- End-getAllSymptoms() ----");
    }




    /* Function-2 retrieving all diseases and passing them to getSymptomsofDisease Function */
    private void getAllDiseases(){

        Log.d(TAG, "---- Start-getAllDiseases() ----");

        final int[] disease_count = {0};
        final List <String> _diseases = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("diseases");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Log.d(TAG, snap.getValue() + "");
                    _diseases.add(snap.getValue().toString());
                    disease_count[0]++;
                }
                Log.d(TAG, "Total Diseases Count : " + disease_count[0]);

                getSymptomsofDiseases(_diseases);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "---- End-getAllDiseases() ----");
    } /* End_of_Function -- getAllDiseases() */




    /* Function-3 retrieving all symptoms of array of diseases, counting their occurences then
        passing them to symptomSortFrequency */
    private void getSymptomsofDiseases(List<String> diseases){

        Log.d(TAG, "---- Start-getSymptomsofDiseases() ----");

        final HashMap<String, String> _map = new HashMap<>();
        final int[] count = {0};
        final int dis_count = diseases.size();

        for (String _disease : diseases){
            Log.d(TAG, "Disease: " + _disease);
            DatabaseReference diseaseRef = FirebaseDatabase.getInstance().getReference("mapping/" + _disease);
            diseaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        Log.d(TAG, "Key: " + snap.getKey() + " Value: " + snap.getValue());

                        if(_map.containsKey(snap.getKey())){
//                            Log.d(TAG,"ToRemove: "
//                                    +snap.getKey().toString()
//                                    +" Value "+Integer.parseInt( _map.get(snap.getKey().toString())));

                            int var = Integer.parseInt( _map.get(snap.getKey())) + 1; // snap's value & _map's key
                            _map.put(snap.getKey(), String.valueOf(var));
//                            Log.d(TAG,"Key: "+snap.getKey().toString()+" NewValue: "+ String.valueOf(var));
                        }
                        else{
                            _map.put(snap.getKey(), "1");
//                            Log.d(TAG,"DONE");
                        }
                    } /* Datasnapshot foreach loop for single disease */
                    Log.d(TAG, "---- " + _map.size() + " ---");
                    count[0]++;
                    Log.d(TAG, "disease: " + count[0] + " end");
                    if(count[0] == dis_count){
                        Log.d(TAG, "Diseases symptoms count : " + _map.size() + " ---");
                        sortSymptomFrequency(_map);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } /* disease foreach loop */

        Log.d(TAG, "---- End-getSymptomsofDiseases() ----");
    } /* End_of_Function -- getSymptomsofDiseases() */




    /* Function-4 retrieving frequency of symptoms and sorting them wrt to their frequency */
    private void sortSymptomFrequency(HashMap<String, String> symptoms){

        Log.d(TAG, "---- Start-sortSymptomFrequency() ----");

        final LinkedHashMap<String, String> _freq = new LinkedHashMap<>();

        Set<Map.Entry<String,String>> set = symptoms.entrySet();
        List<Map.Entry<String,String>> list = new ArrayList<>(set);

        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> t1, Map.Entry<String, String> t2) {
                return (t2.getValue()).compareTo(t1.getValue());
            }
        });

        for(Map.Entry<String, String> entry:list){
            Log.d(TAG, "KEYY: " + entry.getKey() + " VAL: " + entry.getValue());
            _freq.put(entry.getKey(), entry.getValue());
        }

        _freq.keySet().removeAll(selected_symptoms);

        /* remove selected_symptoms from the symptoms */
        Log.d(TAG, "New set of freq");
        for(Map.Entry<String, String> entry:list){
            Log.d(TAG, "KEYY: " + entry.getKey() + " VAL: " + entry.getValue());
            _freq.put(entry.getKey(), entry.getValue());
        }

        Log.d(TAG, "-- Selected Symptoms Start");
        for(int a=0; a<selected_symptoms.size();a++){
            Log.d(TAG, "id : " + selected_symptoms.get(a) + ", name : " + all_symptom.get(selected_symptoms.get(a)));
        }
        Log.d(TAG, "Selected Symptoms end --");

        Log.d(TAG, "---- End-sortSymptomFrequency() ----");
        showup(_freq);
    }




    /* Function-5 Main function to show symptoms from higher to lower frequency */
    private void showup(HashMap<String, String> map){
        Log.d(TAG, "---- Start-showup() ----");

//        for(Map.Entry<String, String> entry : map.entrySet()){
//            Log.d(TAG, "KEYY: " + entry.getKey() + " VAL: " + entry.getValue());
//            text1.setText(entry.getKey());
//        }
        Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
//        while (entries.hasNext()) {
//            Map.Entry<String, String> entry = entries.next();
//            Log.d(TAG, "Key = " + entry.getKey() + ", Value = " + entry.getValue());
//        }

        Log.d(TAG, "Map Size: " + map.size());

        final ListView listView = findViewById(R.id.list_item);
        List <String> values = new ArrayList<>();
        final HashMap<String,String> sym_name_id = new HashMap<>();
        String sym_name;

        for(int i=0; i<4; i+=4){
            values.clear();
            sym_name_id.clear();
            for(int j=0; j<4; j++){
                if(entries.hasNext()){
                    Map.Entry<String,String> entry = entries.next();
                    Log.d(TAG, "Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    sym_name = all_symptom.get(entry.getKey());
                    values.add(sym_name);
                    sym_name_id.put(sym_name, entry.getKey());
                }
            }
            if(!values.isEmpty()){
                /* ListView Start */
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                        android.R.id.text1, values);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        //int itemPosition = position;
                        String itemValue = (String) listView.getItemAtPosition(position);
                        selected_symptoms.add(sym_name_id.get(itemValue));
                        // Show Alert
                        Toast.makeText(getApplicationContext(), "Position : " + position + ", " +
                                "ListItem : " + itemValue, Toast.LENGTH_LONG).show();
                        getDiseasesofSymptom(selected_symptoms);
                    }
                });
                /* ListView End */
            }
        }




        Log.d(TAG, "---- End-showup() ----");
    }




    /* Function-6 retrieving all diseases and passing them to getSymptomsofDisease Function */
    private void getDiseasesofSymptom(List<String> selected_symps){
        Log.d(TAG, "---- Start-getDiseasesofSymptom() ----");

        final List <String> sym_diseases = new ArrayList<>();
        final List <String> sym_arr = selected_symps;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("mapping").orderByChild(sym_arr.get(0)).equalTo(0);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Log.d(TAG,issue.getKey());
//                        boolean i = false;
//                        i = issue.hasChild("3");
//                        Log.d(TAG, String.valueOf(i));
//                        i = issue.hasChild("6");
//                        Log.d(TAG, String.valueOf(i));
                        boolean i;
                        int cnt = 1;
                        for(int k=1; k<sym_arr.size(); k++){
                            Log.d(TAG, "for-s");
                            i = issue.hasChild(sym_arr.get(k));
                            if(i){ // if(i==true)
                                Log.d(TAG,"++");
                                cnt++;
                            }
                        }
                        Log.d(TAG, "for-e");
                        if(cnt == sym_arr.size()){
                            Log.d(TAG, "add");
                            sym_diseases.add(issue.getKey());
                        }
                    }
                    // show sym_diseases
                    for(String dis : sym_diseases){
                        Log.d("Retrieved D: ", "Diseasee : " + dis);
                    }
                    if(sym_diseases.size() > 1){
                        Log.d("Retrieved D: ", "--------------------------");
                        getSymptomsofDiseases(sym_diseases);
                    }
                    else{
                        String dis = sym_diseases.get(0);
                        Log.d("Retrieved D: ", "1111" + " disease: " + dis);
                        for(String sym : selected_symptoms){
                            Log.d("Retrieved D: ","selected_symptoms_ : " + sym);
                        }
                        Intent intent = new Intent(MainActivity.this, DiseaseDisplay.class);
                        //intent.putExtra("DiseaseName", dis);
                        Bundle b = new Bundle();
                        b.putStringArrayList("selectedSymList", selected_symptoms);
                        b.putSerializable("allSymptoms", all_symptom);

                        b.putString("diseaseName", dis);
                        intent.putExtra("myBundle", b);
//                        intent.putStringArrayListExtra("selected_symptoms", selected_symptoms);
                        //Bundle extras = new Bundle();
                        //extras.putStringArrayList("selected_symptoms", selected_symptoms);
                        //extras.putString("possible_disease", dis);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "---- End-getDiseasesofSymptom() ----");
    }




    /* Function-3 retrieving all diseases and passing them to getSymptomsofDisease Function */
//    private void sample(){
//        Log.d(TAG, "---- Start-() ----");
//
//
//
//        Log.d(TAG, "---- End-() ----");
//    }









    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(MainActivity.this , "User is authenticated",Toast.LENGTH_LONG)
                    .show();
        }else{
            Toast.makeText(MainActivity.this , "User is NOT_Authenticated",Toast.LENGTH_LONG)
                    .show();
            logIn();
        }
    }

    private void logIn(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            //FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
