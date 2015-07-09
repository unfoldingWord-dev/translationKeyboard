/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.distantshoresmedia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.keyboard.InputLanguageSelection;
import org.distantshoresmedia.translationkeyboard20.NetWorkUtil;
import org.distantshoresmedia.translationkeyboard20.R;
import org.distantshoresmedia.translationkeyboard20.UpdateFragment;
import org.distantshoresmedia.translationkeyboard20.UpdateService;

public class Main extends FragmentActivity implements UpdateFragment.OnFragmentInteractionListener {

    private static final String TAG = "Main";
    private static Context context;
    public static Context getAppContext() {
        return Main.context;
    }

    private final static String MARKET_URI = "market://search?q=pub:\"Klaus Weidner\"";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Main.context = getApplicationContext();

        KeyboardDatabaseHandler.initializeDatabaseIfNecessary(this.getApplicationContext());

        setContentView(R.layout.main);
        String html = getString(R.string.main_body);
        //html += "<p><i>Version: " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + "</i></p>";
        Spanned content = Html.fromHtml(html);
        TextView description = (TextView) findViewById(R.id.main_description);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        description.setText(content, BufferType.SPANNABLE);


        final Button setup1 = (Button) findViewById(R.id.main_setup_btn_configure_imes);
        setup1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
            }
        });

        final Button setup2 = (Button) findViewById(R.id.main_setup_btn_set_ime);
        setup2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showInputMethodPicker();
            }
        });
        
        final Activity that = this;

        final Button setup4 = (Button) findViewById(R.id.main_setup_btn_input_lang);
        setup4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(that, InputLanguageSelection.class), 0);
            }
        });


        final Button setup5 = (Button) findViewById(R.id.main_setup_btn_update_keyboards);
        setup5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateKeyboards();
            }
        });

        final Button setup6 = (Button) findViewById(R.id.main_setup_btn_keyboard_sharing);
        setup6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sharePressed();
            }
        });
    }

    private void sharePressed(){

        startActivity(new Intent(getApplicationContext(), ShareActivity.class));
    }

    private void updateKeyboards() {

            UpdateFragment updateFragment = UpdateFragment.getSharedInstance();

            if (!updateFragment.isShowing()) {


                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();


                transaction.add(R.id.updating_layout_id, updateFragment);
                transaction.commit();
                updateFragment.setProgress(5, "Initializing");
            }
        if (!NetWorkUtil.isConnected(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Unable to perform update at this time");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        } else {
            // to handle new data from network
            startService(new Intent(this, UpdateService.class));
        }
    }

    @Override
    public void endUpdate() {

        Log.i(TAG, "Fragment Closed");

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.remove(UpdateFragment.getSharedInstance());
            transaction.commit();
    }
}
