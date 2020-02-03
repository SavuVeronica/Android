package com.example.lab1.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.lab1.R;


public class DrugDetailsActivity extends Activity {

    private Intent intent;

    private static final int REQUEST_CODE_CREATE = 1;
    private static final int REQUEST_CODE_BACK = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_drug);

        intent = getIntent();
    }

    public void createEvent(View view)
    {
        final EditText nameField = findViewById(R.id.EditTextName);
        if (nameField.getText().toString().isEmpty())
        {
            nameField.setError( "Drug name is required!" );
            return;
        }
        String name = nameField.getText().toString();
        intent.putExtra("NAME", name);

        final EditText compositionField = findViewById(R.id.EditTextComposition);
        String composition="";
        if(!compositionField.getText().toString().isEmpty())
        {
            composition = compositionField.getText().toString();
        }
        intent.putExtra("COMPOSITION", composition);

        final EditText useField = findViewById(R.id.EditTextUse);
        String use ="";
        if(!useField.getText().toString().isEmpty())
        {
            use = useField.getText().toString();
        }
        intent.putExtra("USE", use);

        final EditText quantityField = findViewById(R.id.EditTextQuantity);
        if(quantityField.getText().toString().isEmpty())
        {
            quantityField.setError("Quantity is required!");
            return;
        }
        String quantity = quantityField.getText().toString();
        intent.putExtra("QUANTITY",Integer.parseInt(quantity));

        final CheckBox checkBox = findViewById(R.id.CheckBoxRecipe);
        boolean recipe = checkBox.isChecked();
        intent.putExtra("RECIPE", recipe);

        final EditText priceField = findViewById(R.id.EditTextPrice);
        double price = Double.parseDouble(priceField.getText().toString());
        intent.putExtra("PRICE",price);

        setResult(REQUEST_CODE_CREATE, intent);
        finish();
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

