package com.example.screenshotstar.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.screenshotstar.app.Consts.ADDED_ALL_IMAGES_MSG;
import static com.example.screenshotstar.app.Consts.BITMAP_SCREEN_SIZE;
import static com.example.screenshotstar.app.Consts.BODY;
import static com.example.screenshotstar.app.Consts.DEFAULT_NUMBER_TO_SEND;
import static com.example.screenshotstar.app.Consts.EMAIL_PREFS_KEY;
import static com.example.screenshotstar.app.Consts.EMAIL_REQUIRED_MSG;
import static com.example.screenshotstar.app.Consts.EMAIL_REQUIRED_TITLE;
import static com.example.screenshotstar.app.Consts.INTENT_TYPE;
import static com.example.screenshotstar.app.Consts.NO_MORE_IMAGES_TITLE;
import static com.example.screenshotstar.app.Consts.NO_SCREENSHOTS_MSG;
import static com.example.screenshotstar.app.Consts.NO_SCREENSHOTS_TITLE;
import static com.example.screenshotstar.app.Consts.OK_LABEL;
import static com.example.screenshotstar.app.Consts.PACKAGE_NAME;
import static com.example.screenshotstar.app.Consts.PATH_TO_SCREENSHOTS_FOLDER;
import static com.example.screenshotstar.app.Consts.REMOVED_ALL_IMAGES_MSG;
import static com.example.screenshotstar.app.Consts.SUBJ;

public class MainActivity extends Activity {
	
	Bitmap bitmap;
	EditText etEmail;
	SharedPreferences prefs;
	
	int maxNumber; 
	int numberToSend = DEFAULT_NUMBER_TO_SEND;
	Intent intent;
	ImageView iv;
	LinearLayout entriesConatainer;
	ArrayList<Uri> urisToSend;
	ArrayList<Uri> urisAllPossible;
	File folder;
	ScrollView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		folder = new File(PATH_TO_SCREENSHOTS_FOLDER);
		
		listView = (ScrollView) findViewById(R.id.listView1);
		entriesConatainer = new LinearLayout(this);
		entriesConatainer.setOrientation(LinearLayout.VERTICAL);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateImages();
	}

	protected void updateImages() { 
		
		updateUrisToSend();
		removeAllImagesFromScreen();
		
		for (Uri uri : urisToSend) {
			addImageView(uri);
		}
				
		listView.addView(entriesConatainer);
		updateEmailFromPrefs();
	}

	private void updateEmailFromPrefs() {
		etEmail = (EditText) findViewById(R.id.etEmail);
		prefs = this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
		String emailPrefsValue = prefs.getString(EMAIL_PREFS_KEY, "");
		etEmail.setText(emailPrefsValue);
	}
	
	private void updateUrisToSend() {
		File[] listOfFiles = folder.listFiles();
		maxNumber = listOfFiles.length;
		urisAllPossible = new ArrayList<Uri>();
		
		for (File file : listOfFiles) {
			urisAllPossible.add(Uri.fromFile(file));
		}
		
		if (urisAllPossible.size() == 0) {
			showAlert(NO_SCREENSHOTS_TITLE, NO_SCREENSHOTS_MSG, true);
		}
		
		if (urisAllPossible.size() < numberToSend) {
			numberToSend = urisAllPossible.size(); 
		}
			
		List<Uri> subsetUriToSend = urisAllPossible.subList(urisAllPossible.size()  - numberToSend, urisAllPossible.size());
		
		urisToSend = new ArrayList<Uri> ();
		for (int index = subsetUriToSend.size() - 1; index >= 0; index--) {
			urisToSend.add(subsetUriToSend.get(index)); 
		}
	}
	
	private void removeAllImagesFromScreen() {
		if (listView.getChildCount() != 0) {
			listView.removeAllViews();
		}

		if (entriesConatainer.getChildCount() != 0) {
			entriesConatainer.removeAllViews();
		}
	}
	
	private void addImageView(Uri uri) {
		
		try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			iv = new ImageView(this);
			iv.setId(entriesConatainer.getChildCount() + 1);
            if (bitmap == null) {
                showAlert("Image corrupted", "Sorry, seems some recent image is corrupted", false);
                return;
            }
			iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, BITMAP_SCREEN_SIZE, BITMAP_SCREEN_SIZE, false));
			LayoutParams entry = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			iv.setLayoutParams(entry);
			iv.setLayoutParams(entry);
			entriesConatainer.addView(iv);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onSendBtnClicked(View view) {
		
		String email = etEmail.getText().toString();
		if (email.equals("")) {
			showAlert(EMAIL_REQUIRED_TITLE, EMAIL_REQUIRED_MSG, false);
		} else {
			
			prefs.edit().putString(EMAIL_PREFS_KEY, email).commit();
			
			intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
			intent.setType(INTENT_TYPE);
			intent.putExtra(Intent.EXTRA_SUBJECT, SUBJ);
			intent.putExtra(Intent.EXTRA_TEXT, BODY);
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
			intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,  urisToSend);
			startActivity(intent);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onMinusBtnClicked (View view) {
		
		try {
			entriesConatainer.removeView(entriesConatainer.getChildAt(entriesConatainer.getChildCount() - 1));
			urisToSend.remove(urisToSend.size() - 1);
		} catch (IndexOutOfBoundsException e) {
			showAlert(NO_MORE_IMAGES_TITLE, REMOVED_ALL_IMAGES_MSG, false);
		}
	}
	
	private int getIndexToAdd() {
			return urisAllPossible.size() - entriesConatainer.getChildCount() - 1;
	}
	
	public void onPlusBtnClicked (View view) {

		try {
			Uri uriToAdd = urisAllPossible.get(getIndexToAdd());
			urisToSend.add(uriToAdd);
			addImageView(uriToAdd);
		} catch (IndexOutOfBoundsException e) {
			showAlert(NO_MORE_IMAGES_TITLE, ADDED_ALL_IMAGES_MSG, false);
		}
	}
	
	private void showAlert(String title, String message, boolean isFinishRequired) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
	    builder.setMessage(message);
	    
	    if (isFinishRequired) {
	    	builder.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			})
		    .setPositiveButton(OK_LABEL, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	finish();
		        }
		     });
	    } else {
	    	builder.setPositiveButton(OK_LABEL, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	dialog.cancel();
		        }
		     });
	    }
	    
	    builder.show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.exit:
			finish();
			
		case R.id.remove_all:
			removeAllScreenShotFiles();
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void removeAllScreenShotFiles() {
		for(File file : folder.listFiles()) {
			file.delete();
		}
		showAlert(NO_SCREENSHOTS_TITLE, NO_SCREENSHOTS_MSG, true);
		
	}
	
	
}