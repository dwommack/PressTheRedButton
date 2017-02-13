package com.dwommack.PressTheRedButton.java;

import playn.java.LWJGLPlatform;

import com.dwommack.PressTheRedButton.core.PressTheRedButton;

public class PressTheRedButtonJava {

  public static void main (String[] args) {
    LWJGLPlatform.Config config = new LWJGLPlatform.Config();
    // use config to customize the Java platform, if needed
    config.width=800;
    config.height=480;
    LWJGLPlatform plat = new LWJGLPlatform(config);
    new PressTheRedButton(plat);
    plat.start();
  }
}
