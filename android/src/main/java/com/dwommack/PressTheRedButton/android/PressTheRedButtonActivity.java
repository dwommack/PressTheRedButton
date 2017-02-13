package com.dwommack.PressTheRedButton.android;

import playn.android.GameActivity;

import com.dwommack.PressTheRedButton.core.PressTheRedButton;

public class PressTheRedButtonActivity extends GameActivity {

  @Override public void main () {
    new PressTheRedButton(platform());
  }
}
