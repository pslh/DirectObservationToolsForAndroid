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
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;





public class Occupancy_Selection_Form extends Activity {

	public boolean DEBUG_LOG = false; 

	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 6;

	private String topLevelAttributeDictionary = "DIC_OCCUPANCY";
	private String topLevelAttributeKey = "OCCUPCY";
	
	private String secondLevelAttributeDictionary = "DIC_OCCUPANCY_DETAIL";
	private String secondLevelAttributeKey = "OCCUPCY_DT";	
	
	/*
	private String topLevelAttributeType = "OCC";
	private String secondLevelAttributeType = "OCCD";
	 */

	

	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;


	private ArrayList list;
	public ArrayList<DBRecord> secondLevelAttributesList;


	ListView listview;
	ListView listview2;		 


	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.occupancy_selectable_list);
	}


	@Override
	protected void onResume() {
		super.onResume();
		MainTabActivity a = (MainTabActivity)getParent();		
		surveyDataObject = (GEMSurveyObject)getApplication();
		
		
		if (a.isTabCompleted(tabIndex)) {

		} else {


			mDbHelper = new GemDbAdapter(getBaseContext());        

			mDbHelper.createDatabase();      
			mDbHelper.open();

			Cursor allAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(topLevelAttributeDictionary);     
			ArrayList<DBRecord> topLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesTopLevelCursor);        
			if (DEBUG_LOG) Log.d("IDCT","TYPES: " + topLevelAttributesList.toString());

			Cursor allAttributeTypesSecondLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(secondLevelAttributeDictionary);
			secondLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesSecondLevelCursor);
			
			allAttributeTypesTopLevelCursor.close(); 
			allAttributeTypesSecondLevelCursor.close(); 
			mDbHelper.close();

			selectedAdapter = new SelectedAdapter(this,0,topLevelAttributesList);
			selectedAdapter.setNotifyOnChange(true);

			listview = (ListView) findViewById(R.id.listExample);
			listview.setAdapter(selectedAdapter);        


			selectedAdapter2 = new SelectedAdapter(this,0,secondLevelAttributesList);    		
			selectedAdapter2.setNotifyOnChange(true);		
			listview2 = (ListView) findViewById(R.id.listExample2);
			listview2.setAdapter(selectedAdapter2);                      
			listview2.setVisibility(View.INVISIBLE);
			RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
			relativeLayout.setVisibility(View.INVISIBLE);



			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					selectedAdapter.setSelectedPosition(position);
					selectedAdapter2.setSelectedPosition(-1);			
					surveyDataObject.putData(topLevelAttributeKey, selectedAdapter.getItem(position).getAttributeValue());

					completeThis();

					secondLevelAttributesList.clear();
					mDbHelper.open();			

					Cursor mCursor = mDbHelper.getAttributeValuesByDictionaryTableAndScope(secondLevelAttributeDictionary,selectedAdapter.getItem(position).getJson());
					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {			
						DBRecord o1 = new DBRecord();		

						if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
						//String mTitleRaw = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1)));

						o1.setAttributeDescription(mCursor.getString(0));
						o1.setAttributeValue(mCursor.getString(1));
						o1.setJson(mCursor.getString(2));
						secondLevelAttributesList.add(o1);
						mCursor.moveToNext();
					}		     
					mDbHelper.close();

					
					if (mCursor.getCount() > 0) { 
						listview2.setVisibility(View.VISIBLE);
						RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
						relativeLayout.setVisibility(View.VISIBLE);
					}
					mCursor.close();
					
					selectedAdapter2.notifyDataSetChanged();

				}
			});        

			
			
			
			

			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter2.setSelectedPosition(position);
					surveyDataObject.putData(secondLevelAttributeKey, selectedAdapter2.getItem(position).getAttributeValue());				

				}
			});
			
			boolean result = false;	
			result= selectedAdapter.loadPreviousAtttributes(listview, topLevelAttributeKey,surveyDataObject.getSurveyDataValue(topLevelAttributeKey));
			result= selectedAdapter2.loadPreviousAtttributes(listview2, secondLevelAttributeKey,surveyDataObject.getSurveyDataValue(secondLevelAttributeKey));
			if (result)  {
				listview2.setVisibility(View.VISIBLE);
				findViewById(R.id.rel2).setVisibility(View.VISIBLE);
			}
		}//end tab is compelted check
		surveyDataObject.lastEditedAttribute = "";
	}

	@Override
	public void onBackPressed() {
		if (DEBUG_LOG) Log.d("IDCT","back button pressed");
		MainTabActivity a = (MainTabActivity)getParent();
		a.backButtonPressed();
	}


	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}

}
