package com.dwommack.PressTheRedButton.html;

import com.google.gwt.core.client.EntryPoint;
import playn.html.HtmlPlatform;
import com.dwommack.PressTheRedButton.core.PressTheRedButton;

public class PressTheRedButtonHtml implements EntryPoint {

  @Override public void onModuleLoad () {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    // use config to customize the HTML platform, if needed
    HtmlPlatform plat = new HtmlPlatform(config);
    plat.assets().setPathPrefix("presstheredbutton/");
    new PressTheRedButton(plat);
    plat.start();
  }
}
