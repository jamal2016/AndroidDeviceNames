/*
 * Copyright (C) 2017 Jared Rummler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jaredrummler.android.devicenames;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.android.device.DeviceName;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private EditText editTextCodename;
  private EditText editTextModel;
  private TextView result;
  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    setDeviceNameText();

    editTextCodename = findViewById(R.id.input_codename);
    editTextModel = findViewById(R.id.input_model);
    result = findViewById(R.id.result);

    editTextCodename.setText(Build.DEVICE);
    editTextModel.setText(Build.MODEL);

    findViewById(R.id.btn).setOnClickListener(this);
    findViewById(R.id.btn2).setOnClickListener(this);
  }

  @Override
  public void onClick(final View v) {

    switch (v.getId()) {
      case R.id.btn: {


        String codename = editTextCodename.getText().toString();
        String model = editTextModel.getText().toString();

        if (TextUtils.isEmpty(codename)) {
          Snackbar.make(findViewById(R.id.main), "Please enter a codename", Snackbar.LENGTH_LONG)
                  .show();
          return;
        }

        // // To View Data On Main View
        DeviceName.Request request = DeviceName.with(this).setCodename(codename);
        if (!TextUtils.isEmpty(model)) {
          request.setModel(model);
        }

        request.request(new DeviceName.Callback() {

          @Override
          public void onFinished(DeviceName.DeviceInfo info, Exception error) {
            if (error != null) {
              result.setText(error.getLocalizedMessage());
              return;
            }

            result.setText(Html.fromHtml(
                    "<b>Codename</b>: " + info.codename + "<br>"
                            + "<b>DeviceName</b>: " + info.getName() + "<br>"
                            + "<b>Model</b>: " + info.model + "<br>"
                            + "<b>Manufacturer</b>: " + info.manufacturer + "<br>"
                            + "<b>Name</b>: " + info.getName()));
          }
        });


        break;
      }

      case R.id.btn2: {

        // To View Data On Log For Testing
        DeviceName.with(MainActivity.this).request(new DeviceName.Callback() {

          @Override
          public void onFinished(DeviceName.DeviceInfo info, Exception error) {
            String manufacturer = info.manufacturer;
            String name = info.marketName;
            String model = info.model;
            String codename = info.codename;
            String deviceName = info.getName();

            Log.d(TAG, "manufacturer: " + manufacturer);
            Log.d(TAG, "name: " + name);
            Log.d(TAG, "model: " + model);
            Log.d(TAG, "codename: " + codename);
            Log.d(TAG, "deviceName: " + deviceName);
          }
        });


        Toast.makeText(MainActivity.this,"Done Print On Log",Toast.LENGTH_LONG).show();

        break;
      }

    }

  }

  private void setDeviceNameText() {
    final TextView textView = findViewById(R.id.my_device);

    String deviceName = DeviceName.getDeviceName();
    if (deviceName != null) {
      textView.setText(Html.fromHtml("<b>THIS DEVICE</b>: " + deviceName));
      return;
    }

    // This device is not in the popular device list. Request the device info:
    DeviceName.with(this).request(new DeviceName.Callback() {

      @Override
      public void onFinished(DeviceName.DeviceInfo info, Exception error) {
        textView.setText(Html.fromHtml("<b>THIS DEVICE</b>: " + info.getName()));
      }
    });
  }

}
