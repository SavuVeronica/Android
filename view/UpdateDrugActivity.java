package com.example.lab1.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab1.R;

public class UpdateDrugActivity extends Activity {


    private Intent intent;

    private static final int REQUEST_CODE_UPDATE = 2;
    private static final int REQUEST_CODE_BACK = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_drug);

        intent = getIntent();
        setData();
    }

    public void setData()
    {
        final TextView nameField = findViewById(R.id.DrugName);
        String name = intent.getStringExtra("NAME");
        nameField.setText(name);

        final TextView compositionField = findViewById(R.id.DrugComposition);
        String composition = intent.getStringExtra("COMPOSITION");
        compositionField.setText(composition);

        final TextView useField = findViewById(R.id.DrugUse);
        String use = intent.getStringExtra("USE");
        useField.setText(use);

        final TextView quantityField = findViewById(R.id.DrugQuantity);
        int quantity = intent.getIntExtra("QUANTITY",0);
        quantityField.setText(Integer.toString(quantity));

        final CheckBox checkBox = findViewById(R.id.DrugRecipe);
        boolean recipe = intent.getBooleanExtra("RECIPE",false);
        checkBox.setChecked(recipe);

        final TextView priceField = findViewById(R.id.DrugPrice);
        double price = intent.getDoubleExtra("PRICE",0.0d);
        priceField.setText(Double.toString(price));

    }

    public void onAddQuantity(View view)
    {
        // Increase quantity of drug
        final TextView quantity = findViewById(R.id.newQuantity);
        if(quantity.getText().toString().isEmpty())
        {
            quantity.setError("Quantity must be specified");
            return;
        }
        int addQuantity = Integer.parseInt(quantity.getText().toString());

        final TextView quantity_old = findViewById(R.id.DrugQuantity);
        int oldQuantity = Integer.parseInt(quantity_old.getText().toString());

        intent.putExtra("NEW_QUANTITY",oldQuantity+addQuantity);

        setResult(REQUEST_CODE_UPDATE, intent);
        finish();

    }

    public void onSellPressed(View view)
    {
        // Decrease quantity of drug
        final TextView quantity = findViewById(R.id.newQuantity);
        if(quantity.getText().toString().isEmpty())
        {
            quantity.setError("Quantity must be specified");
            return;
        }
        int subQuantity = Integer.parseInt(quantity.getText().toString());

        final TextView quantity_old = findViewById(R.id.DrugQuantity);
        int oldQuantity = Integer.parseInt(quantity_old.getText().toString());

        if(oldQuantity>=subQuantity) {
            intent.putExtra("NEW_QUANTITY", oldQuantity - subQuantity);

            setResult(REQUEST_CODE_UPDATE, intent);
            finish();
        }
        else {
            quantity.setError("Quantity can't be greater then available one");
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent newintent = new Intent();
        setResult(REQUEST_CODE_BACK, newintent);
        finish();
    }

    public void onCancelPressed(View view)
    {
        Intent newintent = new Intent();
        setResult(REQUEST_CODE_BACK, newintent);
        finish();
    }
}
