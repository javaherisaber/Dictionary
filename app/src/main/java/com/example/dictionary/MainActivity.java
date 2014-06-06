package com.example.dictionary;


import java.io.IOException;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
	//
	//define variables for global usage
	//
		static MyCursorAdapter cursor_adapter;//this is adapter for our list view
		Cursor get_cursor;//cursor is a container for contain a query 
		DatabaseAdapter db;//our database interactor
		
		//our Views
		TextView tv;
		ListView list;
		EditText ed_search;
		Button bt_clean_search;
		
    	@Override
    	protected void onCreate(Bundle savedInstanceState) {
    		
    		super.onCreate(savedInstanceState);
    		setContentView(R.layout.activity_main);
    		
    		//this tow method are for suppress application name at the top of the screen
    		getSupportActionBar().setDisplayShowTitleEnabled(false);
    		getSupportActionBar().setDisplayShowHomeEnabled(false);
    		
    		//bounding our Views
    		list=(ListView) findViewById(android.R.id.list);
    		ed_search=(EditText) findViewById(R.id.search_edit_text);
    		bt_clean_search=(Button) findViewById(R.id.clear_edittext);
    		tv=(TextView) findViewById(android.R.id.empty);
    		list.setEmptyView(tv);//set empty state for list view which displays a text instead of listview
    		
			db = new DatabaseAdapter(this);
			try {
				db.CreateDB();//create our database
			} catch (IOException e) {
				e.printStackTrace();
			}
			db.open();     //opening our database in order to make it usable
			
			new GetRecords().execute("all"); //processing main cursor in new THREAD
			
			//set button clickable
			bt_clean_search.setOnClickListener(new OnClickListener() { 
				
				@Override
				public void onClick(View v) { 
					ed_search.setText("");
				}
			});
			
			ed_search.addTextChangedListener(new TextWatcher() { 
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count)
				{	
					new GetRecords().execute("search");//processing search cursor in new THREAD	
	    		}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			//when user touch an item of our listview this method will be called
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

					String src,dest;
					src=get_cursor.getString(1);//taking English word from Cursor
					dest=get_cursor.getString(2);//taking Persian Translation from Cursor
					Intent it_word=new Intent(MainActivity.this,WordDialog.class);//making a bridge from this activity to another
					//sending some values from this activity to another
					it_word.putExtra("src_word", src);
					it_word.putExtra("dest_word", dest);
					it_word.putExtra("which_activity", 1);
					
					startActivity(it_word);
				}
				
			});
    		
    	}
    	
    	//Asynchronous method for making new Thread to handle process
    	private class GetRecords extends AsyncTask<String , Void, String>
    	{

    		//this implemented method is for handling most processing works
			@Override
			protected String doInBackground(String... params) {
				if(params[0]=="all")
				{
					//making query from fullwords table
					get_cursor=db.GetAllRecords(DatabaseAdapter.DATABASE_HOMESCREEN_TABLE,DatabaseAdapter.FullWords_Columns);
					
					//4th argument is for distinguish between activities . 1 for main and 2 for favorite and 3 for flashcard
					cursor_adapter=new MyCursorAdapter(MainActivity.this, get_cursor, 0,1);
				}
				else
				{
					//making query from fullwords table
					get_cursor=db.GetSearchRecords(DatabaseAdapter.DATABASE_HOMESCREEN_TABLE,DatabaseAdapter.FullWords_Columns, ed_search.getText().toString());
		    		
					//4th argument is for distinguish between activities . 1 for main and 2 for favorite and 3 for flashcard
					cursor_adapter=new MyCursorAdapter(MainActivity.this, get_cursor, 0,1);
				}
				return params[0];
			}
			
			//this implemented method is for handling most Ui based works
			@Override
		    protected void onPostExecute(String result) {
				list.setAdapter(cursor_adapter);
			}
    	}
    	
    	@Override
    	public boolean onCreateOptionsMenu(Menu menu) {
    	    MenuInflater inflater = getMenuInflater();
    	    inflater.inflate(R.menu.main_activity_menu, menu);//inflating our menu items into ActionBar
    	return super.onCreateOptionsMenu(menu);
    	}
    	
    	@Override
    	public boolean onOptionsItemSelected(MenuItem item) {
    	    // Handle presses on the action bar items
    		Intent it;
    	    switch (item.getItemId()) {
    	        case R.id.action_favorite:
    	            it=new Intent(MainActivity.this,Favorite.class);
    	            startActivity(it);
    	            break;
    	        case R.id.action_flashcard:
    	            it=new Intent(MainActivity.this,FlashCardActivity.class);
    	            startActivity(it);
    	            break;
    	        case R.id.review_flash_cards:
    				it=new Intent(MainActivity.this,FlashCardDialog.class);
    				int words_count=db.GetTableCount(DatabaseAdapter.DATABASE_FLASHCARD_TABLE);
    				it.putExtra("words count", words_count);
    				startActivity(it);
    	        	break;
    	    }
    	    return super.onOptionsItemSelected(item);
    	}
    	
    	//when this activity pause 
    	@Override
    	protected void onPause() {
    	super.onPause();
	    	db.close();//closing database
    	}
    	
    	//when this activity resume's
    	@Override
    	protected void onResume() {
    	super.onResume();
	    	db.open();//opening database again
    	}
    	
    	//when this activity has been killed
    	@Override
    	protected void onDestroy() {
    	super.onDestroy();
	    	db.close();
    	}
    	
        
}