 package com.example.dictionary;

 

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class MyCursorAdapter extends CursorAdapter {
	
	Context c;//current context of our application
	int WhichActivity;//determines which activity we should set row view
	
	//Constructor
    public MyCursorAdapter(Context context, Cursor c,int flags,int WhichActivity) {
        super(context, c,0);
        this.c=context;//setting current context from the constructor argument
        this.WhichActivity=WhichActivity;//setting activity number
    }
    
    //binds each View inside the layout
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    	TextView src=null;
    	TextView dest=null;
    	TextView wrong=null;
    	switch (WhichActivity) {
		case 1://this case for mainActivity
    		src= (TextView) view.findViewById(R.id.en_textview);
        	dest =(TextView) view.findViewById(R.id.per_textview);
			break;
		case 2 ://this case for favorite activity
    		src= (TextView) view.findViewById(R.id.en_textview);
        	dest =(TextView) view.findViewById(R.id.per_textview);
    		src.setTextColor(Color.parseColor("#c5053c"));//at parsecolor method you could place every color you want to choose 
			break;
		case 3 ://this case for flashCard activity
			src= (TextView) view.findViewById(R.id.tv_english);
			dest =(TextView) view.findViewById(R.id.tv_correct_number);
			wrong=(TextView) view.findViewById(R.id.tv_wrong_number);
    		src.setTextColor(Color.parseColor("#009f16"));//at parsecolor method you could place every color you want to choose
			break;
		}

    	if(WhichActivity!=3)
    	{
        	src.setText(cursor.getString(1));
        	dest.setText(cursor.getString(2));
    	}
    	else
    	{
        	src.setText(cursor.getString(1));
        	dest.setText(cursor.getString(3));
        	wrong.setText(cursor.getString(4));
    	}

    }

    //inflating our row inside the context
	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate your view here.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View newView;
        if(WhichActivity!=3)
	        newView = inflater.inflate(R.layout.row_common, parent,false);
        else
        	newView = inflater.inflate(R.layout.row_flash_card, parent,false);
        return newView;
    }

	
}