package com.example.dictionary;


import java.io.IOException;

import com.example.dictionary.JazzyViewPager.TransitionEffect;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FlashCardTrain extends AppCompatActivity{
	
	JazzyViewPager mJazzy;//making jazziness for displaying like a page
	DatabaseAdapter db;
	
	private int count;//number of words to be read
	private String transition;//transition name 
	private static boolean[] cardViewed;//determines which page has been read

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_card_train);
		
		//this tow method are for suppress application name at the top of the screen
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
		count=getIntent().getExtras().getInt("word count");//receiving data from previous activity
		cardViewed=new boolean[count+1];//giving size for array
		for (int i=0;i<cardViewed.length;i++) {
			cardViewed[i]=false;
		}
		transition=getIntent().getExtras().getString("transition");//receiving data from previous activity
		
		//preparing database
		db=new DatabaseAdapter(this);
		try {
			db.CreateDB();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.open();
		
		db.GetRandomRows(count);//making random and temporary table from falshCard
		mJazzy=new JazzyViewPager(this);
		TransitionEffect eff=TransitionEffect.valueOf(transition);//giving Jazziness transition
		setupJazziness(eff);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		//add menu items in activity
		menu.add("Toggle Fade");
		String[] effects = this.getResources().getStringArray(R.array.jazzy_effects);
		for (String effect : effects)
			menu.add(effect);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equals("Toggle Fade")) {
			mJazzy.setFadeEnabled(!mJazzy.getFadeEnabled());
		} else {
			TransitionEffect effect = TransitionEffect.valueOf(item.getTitle().toString());
			setupJazziness(effect);
		}
		return true;
	}

	private void setupJazziness(TransitionEffect effect) {
		mJazzy = (JazzyViewPager) findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(effect);
		mJazzy.setAdapter(new MainAdapter());
		mJazzy.setPageMargin(30);
	}

	
	private class MainAdapter extends PagerAdapter {
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			
			LinearLayout lyMajor=MakeView(position);//Main layout of pages
			container.addView(lyMajor, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);//adding layout to the container in order to instantiate in the page
			mJazzy.setObjectForPosition(lyMajor, position);
			return lyMajor;
		}
		
		//after page turns over all previous items will be destroyed
		@Override
		public void destroyItem(ViewGroup container, int position, Object obj) {
			container.removeView(mJazzy.findViewFromObject(position));
		}
		@Override
		public int getCount() {
			return count+1;//number of pages
		}
		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof OutlineContainer) {
				return ((OutlineContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}	
		
		//making view for each page
		public LinearLayout MakeView(final int position)
		{
			int bg = Color.rgb((int) Math.floor(Math.random()*128)+64, 
					(int) Math.floor(Math.random()*128)+64,
					(int) Math.floor(Math.random()*128)+64);//giving random color for each page
			
			final LinearLayout lyMajor=new LinearLayout(FlashCardTrain.this);
			lyMajor.setGravity(Gravity.CENTER);
			lyMajor.setOrientation(LinearLayout.VERTICAL);
			lyMajor.setBackgroundColor(bg);
			
			if(position!=count)
			{
				int pos=position+1;//because pages starts from 0 we add one to it
				final Cursor c=db.GetSearchRecord(DatabaseAdapter.DATABASE_TEMPORARY_TABLE,pos);
				c.moveToFirst();//moving cursor at first position of it
				
				TextView source = new TextView(FlashCardTrain.this);
				source.setGravity(Gravity.CENTER);
				source.setTextSize(30);
				source.setTextColor(Color.WHITE);
				source.setText(c.getString(1));
				source.setPadding(30, 30, 30, 30);
				lyMajor.addView(source);//adding Child for Main linear layout
				
				Button btnShow=new Button(FlashCardTrain.this);
				btnShow.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams buttonShowparams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				btnShow.setLayoutParams(buttonShowparams);
				btnShow.setText("نمایش ترجمه");
				btnShow.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//checking if the page has viewed doesn't show the buttons
						if(!cardViewed[position])
						{
							cardViewed[position]=true;//after page has been views
							TextView tvTranslation=new TextView(FlashCardTrain.this);
							tvTranslation.setText(c.getString(2));
							tvTranslation.setGravity(Gravity.CENTER);
							
							LinearLayout lyChildTransl=new LinearLayout(FlashCardTrain.this);//inner layout
							LinearLayout.LayoutParams lyChildTranslparams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							lyChildTransl.setLayoutParams(lyChildTranslparams);
							lyChildTransl.setPadding(10, 40, 10, 40);
							
							tvTranslation.setLayoutParams(lyChildTranslparams);
							tvTranslation.setTextColor(Color.WHITE);
							tvTranslation.setTextSize(20);
							tvTranslation.setPadding(10, 10, 10, 10);
							lyChildTransl.addView(tvTranslation);
							lyMajor.addView(lyChildTransl);
							
							LinearLayout lyChild=new LinearLayout(FlashCardTrain.this);
							lyChild.setOrientation(LinearLayout.HORIZONTAL);
							lyChild.setGravity(Gravity.CENTER);
							
							final Button btnCorrect=new Button(FlashCardTrain.this);
							btnCorrect.setGravity(Gravity.CENTER);
							btnCorrect.setPadding(0, 0, 10, 0);
							LinearLayout.LayoutParams buttonCorrectparams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							btnCorrect.setLayoutParams(buttonCorrectparams);
							btnCorrect.setText("صحیح");
							final Button btnWrong=new Button(FlashCardTrain.this);
							btnWrong.setGravity(Gravity.CENTER);
							btnWrong.setPadding(10, 0, 0, 0);
							LinearLayout.LayoutParams buttonWrongparams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							btnWrong.setLayoutParams(buttonWrongparams);
							btnWrong.setText("نادرست");
							
							btnCorrect.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									db.Insert_FlashCard_result(c.getString(1), true);//commit the progress
									//making tow buttons invisible 
									btnCorrect.setVisibility(View.GONE);
									btnWrong.setVisibility(View.GONE);
									cardViewed[position]=true;
								}
							});
							btnWrong.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									db.Insert_FlashCard_result(c.getString(1), false);
									btnCorrect.setVisibility(View.GONE);
									btnWrong.setVisibility(View.GONE);
									cardViewed[position]=true;
								}
							});
							
							lyChild.addView(btnCorrect);
							lyChild.addView(btnWrong);
							lyMajor.addView(lyChild);
						}
						else
						{
							Toast.makeText(FlashCardTrain.this, "فقط یک بار میتوانید مشاهده کنید", Toast.LENGTH_SHORT).show();
						}
					}
				});
				lyMajor.addView(btnShow);			

			}
			else
			{
				Button btnEnd=new Button(FlashCardTrain.this);
				btnEnd.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams buttonCorrectparams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				btnEnd.setLayoutParams(buttonCorrectparams);
				btnEnd.setText("پایان");
				btnEnd.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						finish();//finishing this activity
					}
				});
				
				lyMajor.addView(btnEnd);
			}

			return lyMajor;
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.DeleteTemporaryTable();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		db.DeleteTemporaryTable();
	}	
	
}

