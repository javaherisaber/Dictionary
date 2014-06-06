package com.example.dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FlashCardDialog extends Activity {

	//defining variables for global usage
	Spinner sp_transition_effect;
	EditText edt_word_numbers;
	Button btn_start_flashCard;
	
	private int wordsCount;//number of FlashCard Records in it's table
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_card_dialog);
		
		//bounding Views
		sp_transition_effect=(Spinner) findViewById(R.id.spinner_transition_effect);
		edt_word_numbers=(EditText) findViewById(R.id.edt_word_numbers);
		btn_start_flashCard=(Button) findViewById(R.id.btn_start_flashCard);
		
		wordsCount=getIntent().getExtras().getInt("words count");//giving data which sent from flashcard class
		
		//make button clickable
		btn_start_flashCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it=new Intent(FlashCardDialog.this,FlashCardTrain.class);//making a bridge from flashcardDialog to FlashCardTrain
				int number;//number of words to review
					try {
						number=Integer.parseInt(edt_word_numbers.getText().toString());//making string to integer
						if(number<=wordsCount)
						{
							String ef=sp_transition_effect.getSelectedItem().toString();
							it.putExtra("word count", number);//sending data to another activity
							it.putExtra("transition", ef);//sending data to another activity
							startActivity(it);
							finish();
						}
						else
							Toast.makeText(FlashCardDialog.this, "لغات کافی برای مرور وجود ندارد", Toast.LENGTH_SHORT).show();
					} catch (NumberFormatException e) {
						// TODO: handle exception
						Toast.makeText(FlashCardDialog.this, "تعداد لغات نمیتواند خالی باشد" , Toast.LENGTH_SHORT).show();
					}
			}
		});
		
	}

}
