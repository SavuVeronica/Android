package com.example.lab1.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab1.HttpRequests.HttpDeleteRequest;
import com.example.lab1.HttpRequests.HttpGetByIdRequest;
import com.example.lab1.HttpRequests.HttpGetRequest;
import com.example.lab1.HttpRequests.HttpPostRequest;
import com.example.lab1.HttpRequests.HttpPutRequest;
import com.example.lab1.R;
import com.example.lab1.databinding.DrugsListBinding;
import com.example.lab1.model.Drug;
import com.example.lab1.model.ListAdapter;
import com.example.lab1.repository.DBManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DrugListActivity extends AppCompatActivity {
    private ListView list_drugs;
    private List<Drug> drugs;

    private static final int REQUEST_CODE_CREATE = 1;
    private static final int REQUEST_CODE_UPDATE = 2;
    private static final int REQUEST_CODE_BACK = -1;

    private DrugsListBinding binding;

    private DBManager dbManager;
    private Intent intent;
    private boolean online = true;
    private static final String IP = "172.30.113.83";
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
             @Override
             public void onAdLoaded() {
                  // Code to be executed when an ad finishes loading.
                 super.onAdLoaded();
                 mAdView.setVisibility(View.VISIBLE); // banner visible
                 Animation animFadeIn = AnimationUtils.loadAnimation(DrugListActivity.this, R.anim.fade_in);
                 animFadeIn.setRepeatMode(1);
                 mAdView.startAnimation(animFadeIn); // make animation

                 Animation move = AnimationUtils.loadAnimation(DrugListActivity.this,R.anim.move);
                 mAdView.startAnimation(move);
             }
             @Override
             public void onAdFailedToLoad(int errorCode) {
                 new AlertDialog.Builder(DrugListActivity.this)
                         .setTitle("Error")
                         .setMessage(errorCode)
                         .setPositiveButton("Ok", null)
                         .show();
             }
         });

        binding = DrugsListBinding.inflate(getLayoutInflater());
        intent = getIntent();
        int local = intent.getIntExtra("local",1);

        if(local==1)
        {
            findViewById(R.id.ButtonCreateNewDrug).setVisibility(View.GONE);
            binding.setIsVisible(false);
        }
        else {
            binding.setIsVisible(true);
        }

        dbManager = new DBManager(this);
        dbManager.open();

        drugs = new ArrayList<>();
        checkOnlineOffline();
    }

    final Handler handler = new Handler();

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            boolean networkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            online = networkAvailable;
            // Repeat this the same runnable code block again another 5 seconds
            handler.postDelayed(this, 5000);
            if(online) {
                syncroniseServer();
            }
            setList_drugs();
        }
    };

    private void checkOnlineOffline()
    {
        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
    }

    public void setList_drugs() {
        //dbManager.deleteAll();
        drugs.clear();
        if (online) {
            try {
                String url = "http://"+IP+":8080/drugs";

                //String to place our result in
                String result;
                //Instantiate new instance of our class
                HttpGetRequest getRequest = new HttpGetRequest();
                //Perform the doInBackground method, passing in our url
                result = getRequest.execute(url).get();

                JSONArray json = new JSONArray(result);
                dbManager.deleteAll();

                for (int i = 0; i < json.length(); i++) {
                    JSONObject obj = (JSONObject) json.get(i);
                    int id = (int) obj.get("id");
                    String name = (String) obj.get("name");
                    int quantity = (int) obj.get("quantity");
                    Drug drug = new Drug(id, name, quantity);
                    drugs.add(drug);
                    dbManager.insert(drug.getName(),drug.getQuantity(),drug.getComposition(),drug.getUse(),drug.get_Recipe_required(),drug.getPrice(),1);
                }

            } catch (Exception e) {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        }
        else {

            Cursor cursor = dbManager.fetch();
            if (cursor.moveToFirst()) {
                do {

                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                    Drug drug = new Drug(id, name, quantity);
                    drugs.add(drug);
                } while (cursor.moveToNext());
            }

        }

        list_drugs = findViewById(R.id.list_drugs);
        ArrayAdapter listAdapter = new ListAdapter(this, R.layout.drugs_list, drugs);
        list_drugs.setAdapter(listAdapter);
    }

    public void syncroniseServer()
    {
        Cursor cursor = dbManager.notIntoServer();
        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                String composition = cursor.getString(cursor.getColumnIndex("composition"));
                String use = cursor.getString(cursor.getColumnIndex("use"));
                boolean recipe = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("recipe")));
                double price = cursor.getDouble(cursor.getColumnIndex("price"));
                Drug drug = new Drug(id,quantity,name,composition,use,recipe,price);


                String url = "http://"+IP+":8080/drug";

                //String to place our result in
                String result;
                //Instantiate new instance of our class
                HttpPostRequest getRequest = new HttpPostRequest();
                //Perform the doInBackground method, passing in our url
                getRequest.execute(url,String.valueOf(quantity),name,composition,use,String.valueOf(recipe),String.valueOf(price));

                dbManager.intoServerSet(id);

            } while (cursor.moveToNext());
        }
    }


    public void createNewDrug(View view)
    {
        Intent intent = new Intent(DrugListActivity.this,DrugDetailsActivity.class);

        startActivityForResult(intent, REQUEST_CODE_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        switch (resultCode)
        {
            case REQUEST_CODE_CREATE:
                createDrug(dataIntent);
                break;
            case REQUEST_CODE_UPDATE:
                updateData(dataIntent);
                break;
            case REQUEST_CODE_BACK:
                break;
        }
    }

    public void createDrug(Intent intent)
    {
        String name = intent.getStringExtra("NAME");
        String composition = intent.getStringExtra("COMPOSITION");
        String use = intent.getStringExtra("USE");
        int quantity = intent.getIntExtra("QUANTITY",0);
        boolean recipe = intent.getBooleanExtra("RECIPE",false);
        double price = intent.getDoubleExtra("PRICE",0.0d);

        if(online) {
            try {
                    String url = "http://"+IP+":8080/drug";

                    //String to place our result in
                    String result;
                    //Instantiate new instance of our class
                    HttpPostRequest getRequest = new HttpPostRequest();
                    //Perform the doInBackground method, passing in our url
                    result = getRequest.execute(url,String.valueOf(quantity),name,composition,use,String.valueOf(recipe),String.valueOf(price)).get();
                new AlertDialog.Builder(this)
                        .setTitle("Info")
                        .setMessage("Drug added succesfully!")
                        .setPositiveButton("Ok", null)
                        .show();
            } catch (Exception e) {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
//                Toast.makeText(DrugListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        try {
            dbManager.insert(name, quantity, composition, use, recipe, price,0);
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(e.getMessage())
                    .setPositiveButton("Ok", null)
                    .show();
        }
        // refresh list of drugs
        setList_drugs();
    }

    public void deleteDrug(View view)
    {
        if (online)
        {
            final int position = list_drugs.getPositionForView((View) view.getParent());
            Drug d = drugs.get(position);
            try {
                String url = "http://"+IP+":8080/drug/"+d.getId();


                //String to place our result in
                String result;
                //Instantiate new instance of our class
                HttpDeleteRequest getRequest = new HttpDeleteRequest();
                //Perform the doInBackground method, passing in our url
                result = getRequest.execute(url).get();
                dbManager.delete(d.getId());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Drugs can't be deleted in offline mode!")
                    .setPositiveButton("Ok", null)
                    .show();
        }

        setList_drugs();
    }

    public void updateData(Intent intent)
    {
        // Take id and quantity from intent and update the quantity + refresh list of items
        int id = intent.getIntExtra("ID",-1);
        int quantity = intent.getIntExtra("NEW_QUANTITY",0);

        if(online) {
            try {
                String url = "http://"+IP+":8080/drug/"+id+"/"+quantity;


                //String to place our result in
                String result;
                //Instantiate new instance of our class
                HttpPutRequest getRequest = new HttpPutRequest();
                //Perform the doInBackground method, passing in our url
                result = getRequest.execute(url).get();

                dbManager.update(id, quantity);
                new AlertDialog.Builder(this)
                        .setTitle("Info")
                        .setMessage("Drug updated succesfully!")
                        .setPositiveButton("Ok", null)
                        .show();
            } catch (Exception e) {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        }

        setList_drugs();
    }

    public void updateDrug(View view)
    {
        // Set the content of the new view with the data of the element clicked
        Intent intent = new Intent(DrugListActivity.this,UpdateDrugActivity.class);
        final int position = list_drugs.getPositionForView((View) view.getParent());

        if(online) {
            try {
                String url = "http://" + IP + ":8080/drug/" + drugs.get(position).getId();

                //String to place our result in
                String result;
                //Instantiate new instance of our class
                HttpGetByIdRequest getRequest = new HttpGetByIdRequest();
                //Perform the doInBackground method, passing in our url
                result = getRequest.execute(url).get();

                JSONObject obj = new JSONObject(result);
                int id = (int) obj.get("id");
                String name = (String) obj.get("name");
                String composition = (String) obj.get("composition");
                String use = (String) obj.get("use");
                int quantity = (int) obj.get("quantity");
                double price = (double) obj.get("price");
                boolean recipe = (boolean) obj.get("recipe_required");

                intent.putExtra("ID", id);
                intent.putExtra("NAME", name);
                intent.putExtra("COMPOSITION", composition);
                intent.putExtra("USE", use);
                intent.putExtra("QUANTITY", quantity);
                intent.putExtra("RECIPE", recipe);
                intent.putExtra("PRICE", price);

                // Start new intent to see product details and make quantity editable to increase / decrease
                startActivityForResult(intent, REQUEST_CODE_UPDATE);

            } catch (Exception e) {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Drugs can't be updated in offline mode!")
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

}
