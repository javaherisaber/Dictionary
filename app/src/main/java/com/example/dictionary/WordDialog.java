package com.example.dictionary;

import java.io.IOException;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class WordDialog extends Activity implements OnClickListener {
	
	//define views for global usage
	TextView tv_src,tv_dest;
	ImageView iv_right,iv_left;
	ViewSwitcher dest_switcher;
	EditText dest_ed;
	private DatabaseAdapter db;
	Toast t1,t2,t3,t4,t5;
	static final String TAG = "ZIT_Dictionary_WordDialog";//tag for debug the application
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_dialog);
		
		//initializing views
		iv_right=(ImageView) findViewById(R.id.iv_rightbutton_dialog);
		iv_left=(ImageView) findViewById(R.id.iv_leftbutton_dialog);
		tv_src=(TextView) findViewById(R.id.tv_src_dialog);
		dest_switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
	    tv_dest=(TextView) dest_switcher.findViewById(R.id.tv_dest_dialog);
		iv_right.setOnClickListener(this);
		iv_left.setOnClickListener(this);
		
		//initializing Toasts
		t1=new Toast(this);
		t2=new Toast(this);
		t3=new Toast(this);
		t4=new Toast(this);
		t5=new Toast(this);
		
		//giving data from previous activity
		String src=getIntent().getExtras().getString("src_word");
		String dest=getIntent().getExtras().getString("dest_word");
		int WhichActivity=getIntent().getExtras().getInt("which_activity");
		
		tv_src.setText(src);
		tv_dest.setText(dest);
		
		//preparing database
		db=new DatabaseAdapter(this);
		try {
			db.CreateDB();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.open();	

		if(WhichActivity==1)
		{
			boolean existance=db.CheckIsDataAlreadyInDBorNot(DatabaseAdapter.DATABASE_FAVORITE_TABLE, src);
			
			if(!existance)
			{
				//set Images resource
				iv_right.setImageResource(R.drawable.favorite_empty_word_dialog);
				iv_right.setTag(R.drawable.favorite_empty_word_dialog);
			}
			else
			{
				//set Images resource
				iv_right.setImageResource(R.drawable.favorite_full_word_dialog);	
				iv_right.setTag(R.drawable.favorite_full_word_dialog);
			}

			boolean existance2=db.CheckIsDataAlreadyInDBorNot(DatabaseAdapter.DATABASE_FLASHCARD_TABLE, src);
			
			if(!existance2)
			{
				//set Images resource
				iv_left.setImageResource(R.drawable.flashcard_add_word_dialog);
				iv_left.setTag(R.drawable.flashcard_add_word_dialog);
			}
			else
			{
				//set Images resource
				iv_left.setImageResource(R.drawable.flashcard_add_word_pressed);
				iv_left.setTag(R.drawable.flashcard_add_word_pressed);
			}
		}
		else
		{
			//set Images resource
			iv_right.setImageResource(R.drawable.editbtn_favorite_dialog_selector_action);
			iv_right.setTag(R.drawable.editbtn_favorite_dialog_selector_action);
			iv_left.setImageResource(R.drawable.deletebtn_favorite_dialog_selector_action);
			iv_left.setTag(R.drawable.deletebtn_favorite_dialog_selector_action);
		}
			
	}

	@Override
	public void onClick(View v) {
		
		//give data from previous activity
		final String src=getIntent().getExtras().getString("src_word");
		final String dest=getIntent().getExtras().getString("dest_word");
		final int WhichActivity=getIntent().getExtras().getInt("which_activity");
		
		//handling clicke's in main activity
		if(WhichActivity==1)
		{
			switch (v.getId()) {
			case R.id.iv_rightbutton_dialog:
				if(getimage_rsc_id(iv_right)==R.drawable.favorite_empty_word_dialog)
				{
					db.Insert_Term(src, dest, DatabaseAdapter.DATABASE_FAVORITE_TABLE,false);
					t1=Toast.makeText(this, "لغت مورد نظر با موفقیت به علاقه مندی ها افزوده شد", Toast.LENGTH_SHORT);
					t2.cancel();
					t3.cancel();
					t4.cancel();
					t1.show();
					
					//swapping images
					iv_right.setImageResource(R.drawable.favorite_full_word_dialog);
					iv_right.setTag(R.drawable.favorite_full_word_dialog);
				}
				else
				{
					db.Delete_Term(src, DatabaseAdapter.DATABASE_FAVORITE_TABLE);
					t2=Toast.makeText(this, "لغت مورد نظر با موفقیت  از علاقه مندی ها حذف شد", Toast.LENGTH_SHORT);
					t1.cancel();
					t3.cancel();
					t4.cancel();
					t2.show();
					
					//swapping images
					iv_right.setImageResource(R.drawable.favorite_empty_word_dialog);
					iv_right.setTag(R.drawable.favorite_empty_word_dialog);
				}			
				break;
			case R.id.iv_leftbutton_dialog:
				if(getimage_rsc_id(iv_left)==R.drawable.flashcard_add_word_dialog)
				{
					db.Insert_Term(src, dest, DatabaseAdapter.DATABASE_FLASHCARD_TABLE,true);
					t3=Toast.makeText(this, "لغت مورد نظر با موفقیت به لیست فلش کارت ها افزوده شد", Toast.LENGTH_SHORT);
					t1.cancel();
					t2.cancel();
					t4.cancel();
					t3.show();
					
					//swapping images
					iv_left.setImageResource(R.drawable.flashcard_add_word_pressed);
					iv_left.setTag(R.drawable.flashcard_add_word_pressed);
				}
				else
				{
					t4=Toast.makeText(this, "لغات فلش کارت را از اینجا نمی توانید حذف کنید", Toast.LENGTH_SHORT);
					t1.cancel();
					t2.cancel();
					t3.cancel();
					t4.show();
				}
				break;
			}
		}
		else//handling clicke's for favorite and flashCard activities
		{
			String table;
			if(WhichActivity==2)
			{
				table=DatabaseAdapter.DATABASE_FAVORITE_TABLE;
			}
			else
				table=DatabaseAdapter.DATABASE_FLASHCARD_TABLE;
			switch (v.getId()) {
			case R.id.iv_rightbutton_dialog:
				if(getimage_rsc_id(iv_right)==R.drawable.acceptbtn_favorite_dialog_edit_selector_action)
				{
					//view switcher is for switching between tow View
					dest_switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
					dest_ed=(EditText) dest_switcher.findViewById(R.id.tv_dest_dialog_ed);
					if(db.Updatedatabase(table, src, dest_ed.getText().toString()))
					{
						t5=Toast.makeText(this, "لغت مورد نظر با موفقیت به روز رسانی شد", Toast.LENGTH_SHORT);
						t1.cancel();
						t2.cancel();
						t3.cancel();
						t4.cancel();
						t5.show();
					}
					
					//swapping images
					iv_right.setImageResource(R.drawable.editbtn_favorite_dialog_selector_action);
					iv_right.setTag(R.drawable.editbtn_favorite_dialog_selector_action);
					iv_left.setImageResource(R.drawable.deletebtn_favorite_dialog_selector_action);
					iv_left.setTag(R.drawable.deletebtn_favorite_dialog_selector_action);
					this.finish();
				}
				else {
					dest_switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
					dest_switcher.showNext(); //or switcher.showPrevious();
				    dest_ed=(EditText) dest_switcher.findViewById(R.id.tv_dest_dialog_ed);
				    dest_ed.setText(dest);
				    
				  //swapping images
					iv_right.setImageResource(R.drawable.acceptbtn_favorite_dialog_edit_selector_action);
					iv_right.setTag(R.drawable.acceptbtn_favorite_dialog_edit_selector_action);
					iv_left.setImageResource(R.drawable.cancelbtn_favorite_dialog_edit_selector_action);
					iv_left.setTag(R.drawable.cancelbtn_favorite_dialog_edit_selector_action);
				}

				break;
			case R.id.iv_leftbutton_dialog:
				if(getimage_rsc_id(iv_left)==R.drawable.cancelbtn_favorite_dialog_edit_selector_action)
				{
					dest_switcher.showNext(); //or switcher.showPrevious();
					
					//swapping images
					iv_right.setImageResource(R.drawable.editbtn_favorite_dialog_selector_action);
					iv_right.setTag(R.drawable.editbtn_favorite_dialog_selector_action);
					iv_left.setImageResource(R.drawable.deletebtn_favorite_dialog_selector_action);
					iv_left.setTag(R.drawable.deletebtn_favorite_dialog_selector_action);
				}
				else
				{
					//dialog for choosing Yes or No
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					        switch (which){
					        case DialogInterface.BUTTON_POSITIVE:
					            //Yes button clicked
								String table;
								if(WhichActivity==2)
								{
									table=DatabaseAdapter.DATABASE_FAVORITE_TABLE;
								}
								else
									table=DatabaseAdapter.DATABASE_FLASHCARD_TABLE;
					        	boolean DeleteStatus=db.Delete_Term(src,table);
					        	ShowToastDelete(DeleteStatus);
					            break;
					        case DialogInterface.BUTTON_NEGATIVE:
					            //No button clicked
					            break;
					        }
					    }
					};
					
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("مطمئنی که میخوای حذف کنی ؟")
					.setPositiveButton("بله", dialogClickListener)
					.setNegativeButton("خیر", dialogClickListener)
					.show();
				}

				break;
			}
		}
		

	}
	
	//this method is for inside of dialog
	public void ShowToastDelete(boolean DeleteStatus)
	{
		if(DeleteStatus)
		{
			t5=Toast.makeText(this, "لغت مورد نظر با موفقیت حذف شد", Toast.LENGTH_SHORT);
			t1.cancel();
			t2.cancel();
			t3.cancel();
			t4.cancel();
			t5.show();
			this.finish();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		t1.cancel();t2.cancel();t3.cancel();t4.cancel();
		Log.d(TAG, "word dialog took to background");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		t1.cancel();t2.cancel();t3.cancel();t4.cancel();
		Log.d(TAG, "word dialog has been killed");
	}
	
	//get image resource id from tag
	private int getimage_rsc_id(ImageView iv) {
        return (Integer) iv.getTag();
    }
}
