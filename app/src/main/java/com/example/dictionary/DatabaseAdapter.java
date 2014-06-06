package com.example.dictionary;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("SdCardPath")
public class DatabaseAdapter {
	
	//
	//defining Key values for controlling database
	//
	private static String DB_PATH = "/data/data/com.example.dictionary/databases/";//database dir in phone
	static final String DATABASE_NAME = "Dictionary.db";
	static final int DATABASE_VERSION = 1;
    static final String TAG = "ZIT_Dictionary_DatabaseAdapter";//tags created for debug purposes
    
    static final String DATABASE_HOMESCREEN_TABLE = "fullwords";//table name
    static final String FullWord_Field_id = "_id";
    static final String FullWord_Field_src = "src";
    static final String FullWord_Field_dest = "dest";
    
    static final String DATABASE_FAVORITE_TABLE="favorite";
    static final String Favorite_Field_id="_id";
    static final String Favorite_Field_src="src";
    static final String Favorite_Field_dest="dest";
    
    static final String DATABASE_FLASHCARD_TABLE="flashcard";
    static final String FlashCard_Field_id="_id";
    static final String FlashCard_Field_src="src";
    static final String FlashCard_Field_dest="dest";
    static final String FlashCard_Field_Correct="correct";
    static final String FlashCard_Field_Wrong="wrong";

    static final String DATABASE_TEMPORARY_TABLE="temptable";

    //columns name
    static final String[] FullWords_Columns={FullWord_Field_id,FullWord_Field_src,FullWord_Field_dest};
    static final String[] Favorite_Columns={Favorite_Field_id,Favorite_Field_src,Favorite_Field_dest};
    static final String[] FlashCard_Columns={FlashCard_Field_id,FlashCard_Field_src,FlashCard_Field_dest,FlashCard_Field_Correct,FlashCard_Field_Wrong};
    
    //SQLITE code for creating databases
    static final String CREATE_FAVORITE_TABLE =
            "create table " + DATABASE_FAVORITE_TABLE  +"("+Favorite_Field_id+ " integer primary key autoincrement, "
            + Favorite_Field_src+ " text not null,"+ Favorite_Field_dest+" text not null);";
    
    static final String CREATE_FLASHCARD_TABLE =
            "create table " + DATABASE_FLASHCARD_TABLE  +"("+FlashCard_Field_id+ " integer primary key autoincrement, "
            + FlashCard_Field_src+ " text not null,"+ FlashCard_Field_dest+" text not null,"+ FlashCard_Field_Correct+" integer not null,"+ FlashCard_Field_Wrong+" integer not null);";
    
    static final String CREATE_TEMPORARY_TABLE =            
            "create table " + DATABASE_TEMPORARY_TABLE  +"("+FlashCard_Field_id+ " integer primary key autoincrement, "
            + FlashCard_Field_src+ " text not null,"+ FlashCard_Field_dest+" text not null,"+ FlashCard_Field_Correct+" integer not null,"+ FlashCard_Field_Wrong+" integer not null);";
   
    final Context context;//context of current code

    DatabaseHelper DBHelper;//an object of inner class
    SQLiteDatabase db;
	
	public DatabaseAdapter(Context ctx)
	{
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
    {
		private final Context mContext;
		
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext=context;
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(CREATE_FAVORITE_TABLE);//create table
                db.execSQL(CREATE_FLASHCARD_TABLE);//create table
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        	 Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            //this codes drops the tables if exists in newer version of database
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_HOMESCREEN_TABLE);  
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_FAVORITE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_FLASHCARD_TABLE);
            onCreate(db);          
        }
        
    	private boolean checkDataBase() {
    	    try {
    	        final String DbPath = DB_PATH + DATABASE_NAME;
    	        final File file = new File(DbPath);//file for checking our database existence 
    	        if (file.exists())
    	        {
    	        	Log.d(TAG, "Database olready exist");
    	            return true;
    	        }
    	        else
    	        {
    	            return false;	            
    	        }
    	    } catch (SQLiteException e) {
    	        e.printStackTrace();
    	        return false;
    	    }
    	}
    	
    	public void createDataBase() throws IOException {
    	    boolean mDataBaseExist = checkDataBase();
    	    if (!mDataBaseExist) {
    	        this.getReadableDatabase();//makes our database readable
    	        try {
    	            copyDataBase();//copy database from assets folder
    	        } catch (IOException mIOException) {
    	            mIOException.printStackTrace();
    	            throw new Error("Error copying database");
    	        } finally {
    	            this.close();//closing this IO operation after accomplishing it
    	        }
    	    }
    	}
    	
    	private void copyDataBase() throws IOException {
    	    try {
    	    	//using an inputstram to open database from assets
    	        InputStream mInputStream = mContext.getAssets().open(DATABASE_NAME);
    	        String outFileName = DB_PATH + DATABASE_NAME;//output file dir 
    	        OutputStream mOutputStream = new FileOutputStream(outFileName);//outputstream to make new database
    	        byte[] buffer = new byte[1024];//buffer size for write operation
    	        int length;
    	        while ((length = mInputStream.read(buffer)) > 0) {
    	            mOutputStream.write(buffer, 0, length);//writing output byte by byte
    	        }
    	        mOutputStream.flush();//flushes the stream
    	        mOutputStream.close();
    	        mInputStream.close();
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	    }
    	}
    }
	
    public long Insert_Term(String src, String dest ,String table,boolean isflashcard) 
    {
        ContentValues initialValues = new ContentValues();//a storage for set of data that contentResolver can handle it
        initialValues.put(Favorite_Field_src, src);
        initialValues.put(Favorite_Field_dest, dest);
        if(isflashcard)
        {
        	initialValues.put(FlashCard_Field_Correct, 0);
        	initialValues.put(FlashCard_Field_Wrong, 0);
        }
        return db.insert(table, null, initialValues);
    }
    
    public boolean Insert_FlashCard_result(String src,boolean WrongOrCorrect)//true for correct and false for wrong
    {
    	ContentValues initialValues=new ContentValues();//a storage for set of data that contentResolver can handle it
    	Cursor c=GetExactSearch(DATABASE_FLASHCARD_TABLE, FlashCard_Columns, src);//taking word information
    	c.moveToFirst();//going on the first Item of cursor
    	if(!WrongOrCorrect)
    	{
    		int wrong=c.getInt(4);
    		wrong++;
    		initialValues.put(FlashCard_Field_Wrong, wrong);
    	}
    	else
    	{
    		int correct=c.getInt(3);
    		correct++;
    		initialValues.put(FlashCard_Field_Correct, correct);
    	}
    	return db.update(DATABASE_FLASHCARD_TABLE, initialValues, DatabaseAdapter.FullWord_Field_src + " = "+"\""+src+"\"", null) > 0;
    }
    
    public boolean Delete_Term(String src,String table) 
    {
    	return db.delete(table, FullWord_Field_src + " LIKE '" + src + "'" , null) > 0;	
    }
	
	public void CreateDB() throws IOException
	{
		DBHelper.createDataBase();
	}
	
	
    //---opens the database---
    public DatabaseAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();//making database Writable
        return this;
    }

    //---closes the database---
    public synchronized void close() 
    {
        DBHelper.close();//Closing database
    }
    
    public  Cursor GetAllRecords(String table,String[] columns)
    {
    	//Querying from table and returning all record of that table
    	Cursor cursor = db.query(table,columns, null, null, null, null, null);	
        return cursor;
    }
    
    public Cursor GetSearchRecords(String table,String[] columns,String word)
    {
    	Cursor cursor = db.query(table,columns, FullWord_Field_src + " LIKE '" + word + "%' ", null, null, null, null);
    	return cursor;
    }
    
    public Cursor GetSearchRecord(String table,int position)
    {
    	String sql="Select * from "+table+" where "+FullWord_Field_id+" = "+position;
    	Cursor cursor = db.rawQuery(sql, null);//making Query
    	return cursor;
    }
    
    public Cursor GetExactSearch(String table, String[] columns, String word)
    {
    	Cursor cursor = db.query(table,columns, FullWord_Field_src + " = " + "\"" + word + "\"" , null, null, null, null);
    	return cursor;
    }
    
    public boolean CheckIsDataAlreadyInDBorNot(String TableName, String fieldValue) {
        String Query = "Select * from " + TableName + " where " + FullWord_Field_src + " = " + "\"" + fieldValue +"\"";
        Cursor cursor = db.rawQuery(Query, null);
            if(cursor.getCount() <= 0){
                cursor.close();
                return false;
            }
        cursor.close();
        return true;
    }
    
    public boolean Updatedatabase(String table,String src,String dest	)
    {
        ContentValues values = new ContentValues();//a storage for set of data that contentResolver can handle it
        values.put(FullWord_Field_src, src);
        values.put(FullWord_Field_dest, dest);
        // updating row
       return db.update(table, values, DatabaseAdapter.FullWord_Field_src + " = "+"\""+src+"\"",null) > 0;
    }
    
    public int GetTableCount(String table)
    {
    	String command="Select *"+" from "+table;//SQlite command
    	Cursor c=db.rawQuery(command, null);
    	return c.getCount();
    }
    
    public void GetRandomRows(int limit)
    {
    	db.execSQL("drop table if exists "+DATABASE_TEMPORARY_TABLE);
    	db.execSQL(CREATE_TEMPORARY_TABLE);//creating temp database
    	String sql="insert into "+DATABASE_TEMPORARY_TABLE+"(src,dest,correct,wrong) SELECT src,dest,correct,wrong FROM "+DATABASE_FLASHCARD_TABLE+" ORDER BY RANDOM() LIMIT "+limit;
    	db.execSQL(sql);//Executing SQlite command
    }
    
    public void DeleteTemporaryTable()
    {
    	db.execSQL("drop table if exists "+DATABASE_TEMPORARY_TABLE);//Executing SQlite command
    }
}