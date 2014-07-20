package com.prach.mashup.sma;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
 
public class SMA extends Activity { 
	//private Button barcode,gps,finish;
	private final int REQ_CODE = 0x111;
	private Button finish;
	public Vector<String> ResultNameVector = new Vector<String>();
	public Vector<String> ResultStringVector = new Vector<String>();
	public Vector<String> ResultArrayNameVector = new Vector<String>();
	public Vector<String[]> ResultStringArrayVector = new Vector<String[]>();
	//private final int BARCODE_INTENT = 0;
	//private final int GPS_INTENT = 1;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		finish = (Button) findViewById(R.id.button_finish);

		/*barcode.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		        startActivityForResult(intent, BARCODE_INTENT);
			}
		});*/

		/*gps.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("com.prach.mashup.GPS");
				intent.putExtra("MODE", "ACTIVE");
		        startActivityForResult(intent, GPS_INTENT);
				//showDialog("IntentLauncher","do gps locator");
			}
		});*/

		finish.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent bintent = new Intent("com.prach.mashup.FINISHED");
				ResultStringVector.removeAllElements();
				ResultStringVector.add("RESULT_CANCELED");
				bintent.putExtra("VECTOR", ResultStringVector.toArray());
				sendBroadcast(bintent, "");
				Log.i("SMA.button_finish","RESULT_CANCELED");
				SMA.this.finish(); 
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
	}

	@Override
	protected void onResume(){
		super.onResume();
		Intent intent = getIntent();
		String[] messages = null;
		if(intent!=null)
			messages = intent.getStringArrayExtra("MSG");
		String[] temps = null;

		ResultNameVector.removeAllElements();
		ResultArrayNameVector.removeAllElements();

		if(intent!=null&&messages!=null){
			Intent newIntent = new Intent(messages[0]);
			Log.i("SMA.onResume()","call intent:"+messages[0]);
			for (int i = 1; i < messages.length; i++) {
				if(messages[i].startsWith("RESULT:")){
					temps = messages[i].split(":");
					ResultNameVector.add(temps[1]);
					Log.i("SMA.onResume()","intent result:"+temps[1]);
				}else if(messages[i].startsWith("RESULTS:")){
					temps = messages[i].split(":");
					ResultArrayNameVector.add(temps[1]);
					Log.i("SMA.onResume()","intent results:"+temps[1]);
				}else if(messages[i].startsWith("EXTRA:")){
					temps = messages[i].split(":");
					newIntent.putExtra(temps[1],messages[i+1]);
					Log.i("SMA.onResume()","intent extra:"+temps[1]+"/"+messages[i+1]);
				}else if(messages[i].startsWith("EXTRAS:")){
					temps = messages[i].split(":");
					if(!messages[i+1].equals("null")){
						String[] extraarray = messages[i+1].split("<<>>");
						newIntent.putExtra(temps[1],extraarray);
						StringBuffer extramsg = new StringBuffer();
						extramsg.append("array\n");
						for (int j = 0; j < extraarray.length; j++) {
							extramsg.append("["+j+"]");
							extramsg.append(extraarray[j]);
							extramsg.append("\n");
						}
						Log.i("SMA.onResume()","intent extras:"+temps[1]+"/"+extramsg.toString());
					}
				}else if(messages[i].startsWith("URI:")){
					temps = messages[i].split(":");
					if(!messages[i+1].equals("null")){
						int uri_index = Integer.parseInt(messages[i+1].split("=")[1]);
						Parcelable[] uris = intent.getParcelableArrayExtra("URIS");
						newIntent.putExtra(temps[1],uris[uri_index]);
						Log.i("SMA.onResume()","intent extra(uri):"+uris[uri_index].toString());
					}
				}
			}

			startActivityForResult(newIntent, REQ_CODE);

			/*if(mode!=null){
				if(mode.equals("PASSIVE")&&type==null){
					intent.putExtra("LAT", nf.format(mLatitude));
					intent.putExtra("LNG", nf.format(mLongitude));
					intent.putExtra("PROVIDER", provider);
					this.setResult(Activity.RESULT_OK, intent);
					GPSLocator.this.finish();
				}else if(mode.equals("PASSIVE")&&type.equals("JSON")){
					intent.putExtra("JSON_RESULT", "{\"latitude\":\""+nf.format(mLatitude)+"\",\"longitude\":\""+nf.format(mLongitude)+"\"}");
					this.setResult(Activity.RESULT_OK, intent);
					GPSLocator.this.finish();
				}else if(mode.equals("ACTIVE")){

				}
			}*/
		}else{
			Log.i("SMA.onResume()","intent==null");
		}
		Log.i("SMA.onResume()","intent call finished");
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.i("SMA.onActivityResult","resultCode = "+resultCode);
		ResultStringVector.removeAllElements();
		ResultStringArrayVector.removeAllElements();

		//no-use requestCode
		if(resultCode == RESULT_OK) {
			ResultStringVector.add("RESULT_OK");
			for (int i = 0; i < ResultNameVector.size(); i++) {
				ResultStringVector.add(intent.getStringExtra(ResultNameVector.get(i)));
				Log.i("SMA.onActivityResult()","intent result:"+ResultNameVector.get(i)+"/"+intent.getStringExtra(ResultNameVector.get(i)));
			}
			for (int i = 0; i < ResultArrayNameVector.size(); i++) {
				String[] extraarray = intent.getStringArrayExtra(ResultArrayNameVector.get(i));
				StringBuffer extramsg = new StringBuffer();
				extramsg.append("array\n");
				for (int j = 0; j < extraarray.length; j++) {
					extramsg.append("["+j+"]");
					extramsg.append(extraarray[j]);
					extramsg.append("\n");
				}
				ResultStringArrayVector.add(extraarray);
				Log.i("SMA.onActivityResult()","intent result:"+ResultArrayNameVector.get(i)+"/"+extramsg.toString());
			}

			//String contents = intent.getStringExtra("SCAN_RESULT");
			//String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
			//showDialog("Scan Succeed", "Format: " + format + "\nContents: " + contents);
			Intent bintent = new Intent("com.prach.mashup.FINISHED");

			String[] resultstrings = new String[ResultStringVector.size()];
			for (int i = 0; i < resultstrings.length; i++) {
				resultstrings[i] = ResultStringVector.get(i);
			}
			bintent.putExtra("STRING_VECTOR", resultstrings);

			if(ResultStringArrayVector.size()>0){
				String[][] resultarrays = new String[ResultStringArrayVector.size()][];
				for (int i = 0; i < resultarrays.length; i++) {
					resultarrays[i] = ResultStringArrayVector.get(i);
				}
				StringBuffer arraypattern = new StringBuffer();
				int allarraycount = 0;
				for (int i = 0; i < resultarrays.length; i++) {
					arraypattern.append(resultarrays[i].length);
					allarraycount+=resultarrays[i].length;
					if(i!=resultarrays.length-1)
						arraypattern.append(":");
				}
				bintent.putExtra("ARRAY_VECTOR_PATTERN", arraypattern.toString());
				String[] allarray = new String[allarraycount];
				//1 4 5
				int count=0;
				for (int i = 0; i < resultarrays.length; i++)
					for (int j = 0; j < resultarrays[i].length; j++)
						allarray[count++]=resultarrays[i][j];
					
				bintent.putExtra("ARRAY_VECTOR", allarray);
				
			}else{
				bintent.putExtra("ARRAY_VECTOR_PATTERN", "null");
			}
			sendBroadcast(bintent, null);
			SMA.this.finish();
			android.os.Process.killProcess(android.os.Process.myPid());
		}else if (resultCode == RESULT_CANCELED) {
			//ResultStringVector.add("RESULT_CANCELED");
			Log.i("SMA.onActivityResult()","RESULT_CANCELED");
			//showDialog("Scan failed", "some msg");
			Intent bintent = new Intent("com.prach.mashup.FINISHED");
			String[] temparray = {"RESULT_CANCELED"};
			bintent.putExtra("STRING_VECTOR",temparray);
			bintent.putExtra("ARRAY_VECTOR_PATTERN", "null");
			sendBroadcast(bintent, null);
			SMA.this.finish();
		}
	}
}