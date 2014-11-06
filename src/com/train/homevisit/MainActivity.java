package com.train.homevisit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	Button btn_add, btn_setdb;
	TextView tv1;
	ListView listView1;

	private Context context;

	SQLiteDatabase db = null;

	private ArrayAdapter<String> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.context = getApplicationContext();
		// สร้างไฟล์ temp.db เพื่อให้ android สร้างไดเรคทอรี database
		db = context.openOrCreateDatabase("temp1.db",
				SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.close();

		ArrayList<String> listItem = new ArrayList<String>();
		listAdapter = new ArrayAdapter<String>(context, R.layout.simplerow,
				listItem);

		// init widget
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_add.setVisibility(View.GONE);

		btn_setdb = (Button) findViewById(R.id.btn_setdb);
		btn_setdb.setVisibility(View.GONE);

		listView1 = (ListView) findViewById(R.id.listView1);

		// -init widget

		File dbFile = context.getDatabasePath("home.db");
		if (dbFile.exists()) {
			// showData();
			btn_add.setVisibility(View.VISIBLE);

		} else {
			btn_setdb.setVisibility(View.VISIBLE);
		}

		btn_setdb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String mPackage = getApplicationContext().getPackageName();
				String DB_PATH = "/data/data/" + mPackage
						+ "/databases/home.db";

				OutputStream myOutput = null;
				InputStream myInput = null;
				try {
					myInput = context.getAssets().open("home.db");

					myOutput = new FileOutputStream(DB_PATH);
					copyFile(myInput, myOutput);

					Log.d(context.getPackageName(), "สร้าง DB สำเร็จ");
					showData();
					btn_add.setVisibility(View.VISIBLE);
					btn_setdb.setVisibility(View.GONE);

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

		btn_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent itn = new Intent(MainActivity.this, AddActivity.class);
				startActivity(itn);				

			}
		});
		
		
		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String selected = ((TextView) view
						.findViewById(R.id.rowTextView)).getText().toString();

				// Log.d(context.getPackageName(), selected);

				Intent itn = new Intent(MainActivity.this, AddActivity.class);
				itn.putExtra("fullname", selected);
				startActivity(itn);

			}
		});
		

	}// end onCreate

	// copyFile Method
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		out.close();
		out.flush();
		in.close();
	}// End copyFile Method

	private void showData() {
		listAdapter.clear();
		db = context.openOrCreateDatabase("home.db",
				SQLiteDatabase.OPEN_READWRITE, null);
		db.setLocale(Locale.getDefault());
		SQLiteCursor cur = (SQLiteCursor) db.rawQuery(
				"select fullname from visit", null);
		
		if(cur.getCount()>0){
			
			cur.moveToFirst();
			do {
				String res = cur.getString(0);
	
				listAdapter.add(res);
	
			} while (cur.moveToNext());
			
		}

		listAdapter.notifyDataSetChanged();
		listView1.setAdapter(listAdapter);
		listView1.setFastScrollEnabled(true);

		cur.close();
		db.close();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_settings) {

			return true;
		} 
		
		if (id == R.id.action_export) {
			
			
			String mPackage = getApplicationContext().getPackageName();
			String DB_PATH = "/data/data/" + mPackage
					+ "/databases/home.db";
			
			String SD_CARD = "/mnt/sdcard/home_export.db";

			OutputStream myOutput = null;
			InputStream myInput = null;
			try {
				myInput = new FileInputStream(DB_PATH);

				myOutput = new FileOutputStream(SD_CARD);
				copyFile(myInput, myOutput);
				
				Log.d(getPackageName(),"Export OK");
				Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();

				

			} catch (IOException e) {
				e.printStackTrace();
			}
			

			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public void onResume() {

		File dbFile = context.getDatabasePath("home.db");
		if (dbFile.exists()) {

			showData();

		}

		super.onResume();
	}

	

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(this.getPackageName(), "onDestroy");
	}
}// จบ class
