/*******************************************************************************
 * Copyright (c) 2010-2012, GEM Foundation.
 * IDCT Android is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IDCT Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IDCT Android.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.globalquakemodel.idctdo.android;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.UUID;
import org.globalquakemodel.idctdo.android.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Exposure_Form extends Activity {

	private ArrayList list;
	public ArrayList<DBRecord> lLrsd;

	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 8;

	ListView listview;
	ListView listview2;

	Button btn_saveObservation;
	Button btn_cancelObservation;

	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	private String topLevelAttributeDictionary = "DIC_CURRENCY";
	private String topLevelAttributeKey = "CURRENCY";

	
	public EditText editTextNumberOfDayOccupants;
	public EditText editTextNumberOfNightOccupants;	
	public EditText editTextNumberOfTransitOccupants;	
	public EditText editTextNumberOfDwellings;
	public EditText editTextPlanArea;
	public EditText editTextReplacementCost;
	public EditText editTextExposureComments;

	
	public Spinner spinnerCurrency;


	private String attributeKey1 = "DAY_OCC";
	private String attributeKey2 = "NIGHT_OCC";
	private String attributeKey3 = "TRANS_OCC";
	private String attributeKey4 = "NUM_DWELL";
	private String attributeKey5 = "PLAN_AREA";
	private String attributeKey6 = "REPLC_COST";
	private String attributeKey7 = "CURRENCY";
	private String attributeKey8 = "COMMENTS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exposure);
		Log.d("IDCT","onCreate exposure form");
		

/*
		spinnerCurrency = (Spinner)  findViewById(R.id.spinnerCurrency);
		*/
	}

	@Override
	public void onBackPressed() {
		Log.d("IDCT","back button pressed");
		MainTabActivity a = (MainTabActivity)getParent();
		a.backButtonPressed();
	}


	@Override
	protected void onResume() {
		super.onResume();

		MainTabActivity a = (MainTabActivity)getParent();
		surveyDataObject = (GEMSurveyObject)getApplication();

		//if (a.isTabCompleted(tabIndex)) {

		//} else {
			Log.d("IDCT","Resuming exposure form.");
			
			
			editTextNumberOfDayOccupants = (EditText) findViewById(R.id.editTextNumberOfDayOccupants);
			editTextNumberOfNightOccupants = (EditText) findViewById(R.id.editTextNumberOfNightOccupants);
			editTextNumberOfTransitOccupants = (EditText) findViewById(R.id.editTextNumberOfTransitOccupants );
			editTextNumberOfDwellings= (EditText) findViewById(R.id.editTextNumberOfDwellings);
			editTextPlanArea= (EditText) findViewById(R.id.editTextPlanArea);
			editTextReplacementCost= (EditText) findViewById(R.id.editTextReplacementCost);
			editTextExposureComments= (EditText) findViewById(R.id.editTextExposureComments);
			
			
			mDbHelper = new GemDbAdapter(getBaseContext());        
			mDbHelper.createDatabase();      
			mDbHelper.open();
			final Cursor allAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(topLevelAttributeDictionary);     
			ArrayList<DBRecord> topLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesTopLevelCursor);        
			Log.d("IDCT","TYPES: " + topLevelAttributesList.toString());			
			allAttributeTypesTopLevelCursor.close();
			mDbHelper.close();
			
			spinnerCurrency = (Spinner) findViewById(R.id.spinnerCurrency);
			ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,topLevelAttributesList);
			spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerCurrency.setAdapter(spinnerArrayAdapter);
			
			spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);
					Log.d("IDCT","spinner selected: " + spinnerCurrency.getSelectedItem().toString());
					Log.d("IDCT","spinner selected pos: " + pos);					

					DBRecord selected = (DBRecord) spinnerCurrency.getSelectedItem();
					Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putGedData(topLevelAttributeKey, selected.getAttributeValue());
//					Log.d("IDCT","surveyDataObject: " + surveyDataObject.getAttributeValue());
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});	


			editTextNumberOfDayOccupants.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putGedData(attributeKey1, editTextNumberOfDayOccupants.getText().toString());
						completeThis();
					}
				}
			});

			editTextNumberOfNightOccupants.setOnFocusChangeListener(new OnFocusChangeListener() { 			

				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putGedData(attributeKey2, editTextNumberOfNightOccupants.getText().toString());
						completeThis();
					}
				}
			});

			
			editTextNumberOfTransitOccupants.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putGedData(attributeKey3, editTextNumberOfTransitOccupants.getText().toString());
						completeThis();
					}
				}
			});


			editTextNumberOfDwellings.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putGedData(attributeKey4, editTextNumberOfDwellings.getText().toString());
						completeThis();
					}
				}
			});		

			editTextPlanArea.setOnFocusChangeListener(new OnFocusChangeListener() {			
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putGedData(attributeKey5 , editTextPlanArea.getText().toString());
						completeThis();
					}
				}
			});


			editTextReplacementCost.setOnFocusChangeListener(new OnFocusChangeListener() {			
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putGedData(attributeKey6 , editTextReplacementCost.getText().toString());
						completeThis();
					}
				}
			});


			editTextExposureComments.setOnFocusChangeListener(new OnFocusChangeListener() {			
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putGedData(attributeKey8 ,editTextExposureComments.getText().toString());
						completeThis();
					}
				}
			});
			
			
			Log.d("IDCT", "getting Ged val for resume: " + surveyDataObject.getGedDataValue(attributeKey1));
			editTextNumberOfDayOccupants.setText(surveyDataObject.getGedDataValue(attributeKey1));
			editTextNumberOfNightOccupants.setText(surveyDataObject.getGedDataValue(attributeKey2));
			editTextNumberOfTransitOccupants.setText(surveyDataObject.getGedDataValue(attributeKey3));
			editTextNumberOfDwellings.setText(surveyDataObject.getGedDataValue(attributeKey4));
			editTextPlanArea.setText(surveyDataObject.getGedDataValue(attributeKey5));
			editTextReplacementCost.setText(surveyDataObject.getGedDataValue(attributeKey6));
			editTextExposureComments.setText(surveyDataObject.getGedDataValue(attributeKey8));
	
					
			boolean result = false;	
			result = GemUtilities.loadPreviousAtttributesSpinner(spinnerCurrency, topLevelAttributesList, topLevelAttributeKey,surveyDataObject.getGedDataValue(topLevelAttributeKey));
				
		//}//End tab completed check
			surveyDataObject.lastEditedAttribute = "";
	}		
	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}


}
