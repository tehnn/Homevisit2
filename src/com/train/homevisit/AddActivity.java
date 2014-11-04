package com.train.homevisit;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends ActionBarActivity {

	SQLiteDatabase db;

	Button btn_save, btn_cancel, btn_remove, btn_sync;

	EditText edt_fullname, edt_age, edt_bps, edt_bpd, edt_bw;

	Boolean isNewRecord = false;
	
	String fullname="";

	void bindWidget() {

		edt_fullname = (EditText) findViewById(R.id.editText1);
		edt_age = (EditText) findViewById(R.id.editText2);
		edt_bps = (EditText) findViewById(R.id.editText3);
		edt_bpd = (EditText) findViewById(R.id.editText4);
		edt_bw = (EditText) findViewById(R.id.editText5);
		btn_save = (Button) findViewById(R.id.button1);
		btn_cancel = (Button) findViewById(R.id.button2);
		btn_remove = (Button) findViewById(R.id.button3);
		btn_sync = (Button) findViewById(R.id.button4);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		bindWidget();

		Bundle extras = getIntent().getExtras();
		

		if (extras != null) {
			fullname = extras.getString("fullname");
		}

		if (fullname.equals("")) {
			isNewRecord = true;
		} else {
			isNewRecord = false;
			db = openOrCreateDatabase("home.db", SQLiteDatabase.OPEN_READWRITE,
					null);
			db.setLocale(Locale.getDefault());

			SQLiteCursor cur = (SQLiteCursor) db.rawQuery(
					"select fullname,age,bps,bpd,bw from visit where fullname='"
							+ fullname + "'", null);
			cur.moveToFirst();

			edt_fullname.setText(cur.getString(0));
			edt_age.setText(String.valueOf(cur.getInt(1)));
			edt_bps.setText(String.valueOf(cur.getInt(2)));
			edt_bpd.setText(String.valueOf(cur.getInt(3)));
			edt_bw.setText(String.valueOf(cur.getDouble(4)));

			cur.close();

			db.close();

		}

		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		}); // ®∫ ªÿË¡ cancel

		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String fname = edt_fullname.getText().toString();
				int age = Integer.parseInt(edt_age.getText().toString());
				int bps = Integer.parseInt(edt_bps.getText().toString());
				int bpd = Integer.parseInt(edt_bpd.getText().toString());
				double bw = Double.parseDouble(edt_bw.getText().toString());

				db = openOrCreateDatabase("home.db",
						SQLiteDatabase.OPEN_READWRITE, null);
				db.setLocale(Locale.getDefault());

				String SQL;
				SQL = "INSERT INTO visit (fullname, age,bps,bpd,bw) VALUES "
						+ "('" + fname + "'," + age + "," + bps + "," + bpd
						+ "," + bw + ")";

				if (!isNewRecord) {
					
					
					
					SQL = "update visit set fullname='" + fname + "',age="
							+ age + ",bps=" + bps + ",bpd=" + bpd + ",bw=" + bw
							+ " where fullname='" + fullname + "'";
				}

				db.execSQL(SQL);
				db.close();
				finish();

			}
		});

		btn_remove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				db = openOrCreateDatabase("home.db",
						SQLiteDatabase.OPEN_READWRITE, null);
				db.setLocale(Locale.getDefault());

				String fullname = edt_fullname.getText().toString();
				String SQL = "delete from visit where fullname='" + fullname
						+ "'";
				db.execSQL(SQL);
				db.close();
				finish();

			}
		});
		
btn_sync.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
				Thread thread = new Thread(new Runnable(){
				    @Override
				    public void run() {
				        try {				            

							// Create a new HttpClient and Post Header
					        HttpClient httpclient = new DefaultHttpClient();
					        HttpPost httppost = new HttpPost("http://61.19.22.108/train/add_visit.php");  
					 
					        try {
					            // Add your data
					            List nameValuePairs = new ArrayList(1);
					            
					            
					            nameValuePairs.add(new BasicNameValuePair("fullname", edt_fullname.getText().toString()));
					            nameValuePairs.add(new BasicNameValuePair("age",edt_age.getText().toString()));
					            nameValuePairs.add(new BasicNameValuePair("bps", edt_bps.getText().toString()));
					            nameValuePairs.add(new BasicNameValuePair("bpd",edt_bpd.getText().toString()));
					            nameValuePairs.add(new BasicNameValuePair("bw",edt_bw.getText().toString()));
					            
					            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
					            
					            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
					 
					            // Execute HTTP Post Request
					            HttpResponse response = httpclient.execute(httppost);
					 
					            InputStream is = response.getEntity().getContent();
					            BufferedInputStream bis = new BufferedInputStream(is);
					            ByteArrayBuffer baf = new ByteArrayBuffer(20);
					 
					            int current = 0;
					             
					            while((current = bis.read()) != -1){
					                baf.append((byte)current);
					            }  
					 
					            /* Convert the Bytes read to a String. */
					            final String text = new String(baf.toByteArray());
					            
					            Log.d("mysql", text);
					            
					            runOnUiThread(new Runnable() {
					                public void run() {
					                    Toast.makeText(getApplicationContext(),text, Toast.LENGTH_SHORT).show();
					                }
					            });
					            
					        } catch (ClientProtocolException e) {
					            // TODO Auto-generated catch block
					        } catch (IOException e) {
					            // TODO Auto-generated catch block
					        }
							
				        	
				        	
				        } catch (Exception e) {
				            e.printStackTrace();
				        }
				    }
				});

				thread.start(); 
				
				
				
				
			}
		});

		
		
		

	} // end OnCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
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
		return super.onOptionsItemSelected(item);
	}
}
