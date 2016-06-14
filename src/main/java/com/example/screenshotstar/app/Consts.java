package com.example.screenshotstar.app;

import android.os.Environment;

public class Consts {
	static final String EMAIL = "sdima2@ya.ru";
	static final String SUBJ = "Nexus 7 screen shots";
	static final String BODY = "See attached screen shots";
	static final String PATH_TO_SCREENSHOTS_FOLDER = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Screenshots/";
	static final String PACKAGE_NAME = "com.example.screenshotsender";
	static final String EMAIL_PREFS_KEY = "com.example.screenshotsender.email";
	static final String NO_SCREENSHOTS_TITLE = "No screen shot taken";
	static final String NO_SCREENSHOTS_MSG = "In order to take screen shot press Power button with Volume down button.";
	static final String EMAIL_REQUIRED_TITLE = "Email required";
	static final String EMAIL_REQUIRED_MSG = "Please enter email!";
	static final String NO_MORE_IMAGES_TITLE = "No more images";
	static final String REMOVED_ALL_IMAGES_MSG = "You've already removed all possible screen shots!";
	static final String ADDED_ALL_IMAGES_MSG = "You've already added all possible screen shots!";
	
	
	static final String INTENT_TYPE = "text/plain";
	static final String OK_LABEL = "OK";
	
	
	
	static final int DEFAULT_NUMBER_TO_SEND = 2;
	static final int BITMAP_SCREEN_SIZE = 800;
}
