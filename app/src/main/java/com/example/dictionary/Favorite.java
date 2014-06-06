package com.example.dictionary;
 

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Favorite extends Activity {
	
	//
	//define variables for global usage
	//
		MyCursorAdapter cursor_adapter;//this is adapter for our list view
		Cursor get_cursor;//cursor is a container for contain a query 
		DatabaseAdapter db;//our database interactor
	
	ListView list;
	EditText ed_search;
	Button bt_clean_search;
	TextView tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		db = new DatabaseAdapter(this);
		try {
			db.CreateDB();	//create our database		
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.open();//opening our database in order to make it usable
		get_cursor=db.GetAllRecords(DatabaseAdapter.DATABASE_FAVORITE_TABLE,DatabaseAdapter.Favorite_Columns);
		
	
                //bounding our Views
		list=(ListView) findViewById(android.R.id.list);
		cursor_adapter=new MyCursorAdapter(this, get_cursor, 0,2);
		list.setAdapter(cursor_adapter);
		tv=(TextView) findViewById(android.R.id.empty);
		list.setEmptyView(tv);//set empty state for list view which displays a text instead of listview
		
		ed_search=(EditText) findViewById(R.id.search_edit_text);
		bt_clean_search=(Button) findViewById(R.id.clear_edittext);
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
				get_cursor=db.GetSearchRecords(DatabaseAdapter.DATABASE_FAVORITE_TABLE,DatabaseAdapter.Favorite_Columns, ed_search.getText().toString());
	    		cursor_adapter=new MyCursorAdapter(Favorite.this, get_cursor, 0,2);
	    		list.setAdapter(cursor_adapter);	
    		}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		//when user touch an item of our listview this method will be called
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String src,dest;
				src=get_cursor.getString(1);//taking English word from Cursor
				dest=get_cursor.getString(2);//taking Persian Translation from Cursor
				Intent it_word=new Intent(Favorite.this,WordDialog.class);//making a bridge from this activity to another
	//sending some values from this activity to another
				it_word.putExtra("src_word", src);
				it_word.putExtra("dest_word", dest);
				it_word.putExtra("which_activity", 2);
				startActivity(it_word);
			}
			
		});
		
	}
	
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
		get_cursor=db.GetAllRecords(DatabaseAdapter.DATABASE_FAVORITE_TABLE,DatabaseAdapter.Favorite_Columns);
		list=(ListView) findViewById(android.R.id.list);
		cursor_adapter=new MyCursorAdapter(this, get_cursor, 0,2);
		list.setAdapter(cursor_adapter);
		tv=(TextView) findViewById(android.R.id.empty);
		list.setEmptyView(tv);
	}
	
    	//when this activity has been killed
	@Override
	protected void onDestroy() {
	super.onDestroy();
    	db.close();
	}
}
