package e14.gpsapp.meterdown;
import e14.gpsapp.meterdown.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class settings extends Activity{
		
	private String settingsTAB = "ApplicationSettings";
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
       // Typeface fa=Typeface.createFromAsset(getAssets(),"fonts/ABEAKRG.TTF");
		prefs = getSharedPreferences(settingsTAB, 0);
		

		TextView edtBaseKm = (TextView)findViewById(R.id.textViewBaseKm);
		TextView edtBaseFare = (TextView)findViewById(R.id.textviewBaseFare);
		TextView edtPerKm = (TextView)findViewById(R.id.textViewPerKm);
		TextView edtCity = (TextView)findViewById(R.id.textViewCity);
		//edtCity.setTypeface(fa);
		//edtBaseKm.setTypeface(fa);
		//edtBaseFare.setTypeface(fa);
		//edtPerKm.setTypeface(fa);
		
		final EditText editText1 = (EditText)findViewById(R.id.editText1);
		final EditText editText2 = (EditText)findViewById(R.id.editText2);
		final EditText EditText01 = (EditText)findViewById(R.id.EditText01);
		final EditText EditText02 = (EditText)findViewById(R.id.EditText02);
		
		editText1.setText(prefs.getString("city", "Mumbai"));
		editText2.setText(prefs.getString("basefare", "15"));
		EditText01.setText(prefs.getString("basekm", "1.6"));
		EditText02.setText(prefs.getString("rate", "9.87"));
		//editText1.setTypeface(fa);
		//editText2.setTypeface(fa);
		//EditText01.setTypeface(fa);
		//EditText02.setTypeface(fa);
		
	    Button btnSave = (Button)findViewById(R.id.buttonSave);
	    btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//save the settings

				Editor editor = prefs.edit();
				String city = editText1.getText().toString();
				String basefare = editText2.getText().toString();
				String basekm = EditText01.getText().toString();
				String rate = EditText02.getText().toString();
				
				editor.putString("city",city);
				editor.putString("basefare", basefare);
				editor.putString("basekm", basekm);
				editor.putString("rate", rate);
				editor.commit();
				finish();
			}
		});
		
	}


}
