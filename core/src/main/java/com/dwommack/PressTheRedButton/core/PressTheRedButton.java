package com.dwommack.PressTheRedButton.core;

import react.Slot;
import playn.core.*;
import playn.core.Clock;
import playn.core.Image;
import playn.core.Platform;
import playn.scene.Pointer;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.SceneGame;
import playn.scene.SceneGame;
import playn.scene.Mouse;
import playn.core.Canvas;
import playn.scene.CanvasLayer;
import playn.core.TextBlock;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.TextWrap;


import static java.lang.Math.random;
import static java.lang.Math.round;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class PressTheRedButton extends SceneGame {
   
  private static float width, height;
  //public final Pointer pointer;

  //Game States
  private final int TITLE = 0;
  private final int PLAYING = 1;
  private final int GAMEOVER = 2;
  private int gameState;
  
  //The purpose in playing - try to better it each time
  int score;
  
  //To prevent the player from 'cheating' the game by ensuring they make an attempt to press the red button when they can
  int strikeCounter;
  
  //The speed the buttons flash
  int gameCounter;
  
  //A speed that increases as the player accumulates a higher score
  int gameCounterDifficulty;
  
  //Not sure what this was for or if it works
  int delayCounter = 0;
  
  //To randomly determine which 1 of 3 buttons will turn red
  Random randomButtonSelection = new Random(); 

  //To store the number selected - may not be needed
  int randomButtonNumber;
  
  //Whether the button was pressed or not - used for strike counter
  boolean buttonPressed;

  Image gameOverImage = plat.assets().getImage("images/gameOver2.png");
  
  //A method to paint the screen with different layers when needed  
  public void populateScreen(ImageLayer bgLayer, GroupLayer layer){
    ImageLayer gameOverLayer = new ImageLayer(gameOverImage);
	
    rootLayer.removeAll();
    bgLayer.setSize(plat.graphics().viewSize);
	
    //Game just started or were redirected to the title screen - paints title screen
    if (gameState == TITLE){
      rootLayer.add(bgLayer);
      rootLayer.add(layer);
    }
	
	  //You're playing the game - paints playing the game images
	  if (gameState == PLAYING){
      //resets these values when the player begins a new game
      score = 0;
      strikeCounter = 0;
      gameCounter = 0;
      gameCounterDifficulty = 0;
		  rootLayer.add(bgLayer);
		  rootLayer.add(layer);
	  }
	  //Player lost the game by either pressing a non-red button or missing a total of 3 presses in their game
	  if (gameState == GAMEOVER){
      rootLayer.add(layer);
      rootLayer.addFloorAt(gameOverLayer,50, 10);
	  }
  }
	
	public void updateScore(StringBuilder scoreDisplay, CanvasLayer scoreBoardLayer, Canvas canvas){
    scoreDisplay.delete(0,scoreDisplay.length());
    scoreDisplay.append("Score \n"+ score);
    scoreBoardLayer.begin();
    TextBlock block = new TextBlock(plat.graphics().layoutText(
	     			scoreDisplay.toString(), 
						new TextFormat(new Font("Helvetica", Font.Style.BOLD, 20)),
						TextWrap.MANUAL));
    canvas.clear();
    canvas.setFillColor(0xFFFFFFFF);
    block.fill(canvas, TextBlock.Align.LEFT, 2, 2);
            rootLayer.addFloorAt(scoreBoardLayer, 20, 20);  
            scoreBoardLayer.end();
		}

	public void updateStrikeCounter(StringBuilder strikeCounterDisplay, CanvasLayer strikeLayer, Canvas canvas2){
    strikeCounterDisplay.delete(0, strikeCounterDisplay.length());
    strikeCounterDisplay.append("Strike Counter: \n" + strikeCounter);
    strikeLayer.begin();
    TextBlock block2 = new TextBlock(plat.graphics().layoutText(
						strikeCounterDisplay.toString(), 
						new TextFormat(new Font("Helvetica", Font.Style.BOLD, 20)),
						TextWrap.MANUAL));
    canvas2.clear();
    canvas2.setFillColor(0xFFFFFFFF);
    block2.fill(canvas2, TextBlock.Align.LEFT, 2, 2);
    rootLayer.addFloorAt(strikeLayer, 600, 20);
    strikeLayer.end();	
	}
    
	/*The main feature of the game. 
	An interactive button that turns red when on, green when off, and blue when pressed
	*/
  public class Button {
        
		Image[] images = {
			plat.assets().getImage("images/red-button.png"),
			plat.assets().getImage("images/inactive-button.png"),
			plat.assets().getImage("images/blueButton2.png")
		};
		int buttonColor;
		boolean isRed;
		ImageLayer layer = new ImageLayer(images[0]);
		
		public Button(final GroupLayer buttonLayer, float x, float y, int color) {
			this.layer.setOrigin(ImageLayer.Origin.CENTER);
			buttonLayer.addAt(layer, x, y);
			this.setColor(color);
		}
    public void setBlue(){
      this.layer.setSource(images[2]);
      this.buttonColor = 2;
      this.isRed = false;
    }
    public void setRed(){
      this.layer.setSource(images[0]);
      this.isRed = true;
      buttonColor = 0;
    }
    public void setInactive(){
      this.layer.setSource(images[1]);
      this.isRed = false;
      this.buttonColor = 1;
    }
    public void setColor(int color){
      this.layer.setSource(images[color]);
			if (color == 0){ this.isRed = true;}
			else{this.isRed = false;}
		}
  }

  public final Pointer pointer;

  public PressTheRedButton(final Platform plat) {
    super(plat, 200); 
    width = plat.graphics().viewSize.width();
    height = plat.graphics().viewSize.height();

    //Mouse and/or touch screen stuff
    //pointer = new Pointer(plat, rootlayer, false);
    //plat.input().mouseEvents.connect(new Mouse.dispatcher(rootLayer, false)); 
    
	
	  //Initialize in the title state
	  gameState = TITLE;
	
	  //Start on easiest difficulty
	  gameCounterDifficulty = 0;
	
	  //Background image for game
	  Image bgImage = plat.assets().getImage("images/blackbg.jpg");
  	final ImageLayer bgLayer = new ImageLayer(bgImage);
	
  	//Display for top-left score counter
    final StringBuilder scoreDisplay = new StringBuilder();
  	scoreDisplay.append("Score \n"+ score);
  	TextBlock block = new TextBlock(plat.graphics().layoutText(
				scoreDisplay.toString(), 
				new TextFormat(new Font("Helvetica", Font.Style.BOLD, 20)),
				TextWrap.MANUAL));
  	final Canvas canvas = plat.graphics().createCanvas(block.bounds.width()+8, block.bounds.height()+4);
    canvas.setFillColor(0xFFFFFFFF);
    block.fill(canvas, TextBlock.Align.LEFT, 2, 2);
    final CanvasLayer scoreBoardLayer = new CanvasLayer(plat.graphics(), canvas);

  	//Display for top-right strike counter
    final StringBuilder strikeCounterDisplay = new StringBuilder();
    strikeCounterDisplay.append("Strike Counter: \n" + strikeCounter);
    TextBlock block2 = new TextBlock(plat.graphics().layoutText(
        strikeCounterDisplay.toString(),
        new TextFormat(new Font("Helvetica", Font.Style.BOLD, 20)),
				TextWrap.MANUAL));
    final Canvas canvas2 = plat.graphics().createCanvas(block2.bounds.width()+16, block2.bounds.height()+8);
    canvas2.setFillColor(0xFFFFFFFF);
    block2.fill(canvas2, TextBlock.Align.LEFT, 2, 2);
    final CanvasLayer strikeLayer = new CanvasLayer(plat.graphics(), canvas2);
			
  	//Layer to place title images in
  	final GroupLayer titleLayer = new GroupLayer();
	
  	//Place start button in the center
  	final Button startButton = new Button(titleLayer, width/2, height/2, 0);

  	//Title image
    Image titleImage = plat.assets().getImage("images/PushTheRedButton.png");
    final ImageLayer titleImageLayer = new ImageLayer(titleImage);
  	titleLayer.addFloorAt(titleImageLayer, 150,0); 
	
  	final GroupLayer buttonLayer = new GroupLayer();
  	//Left button
  	final Button buttonOne = new Button(buttonLayer, width/6, height/2, 0);
  	//Middle button
  	final Button buttonTwo = new Button(buttonLayer, width/2, height/2, 0);
  	//Right button
  	final Button buttonThree = new Button(buttonLayer, width*5/6, height/2, 0);
    
  	//Paint screen with the title layer and background layer
    populateScreen(bgLayer, titleLayer);
     
    update.connect(new Slot<Clock>() {
      @Override public void onEmit (Clock clock) {
        if (gameState == PLAYING) {
			    //Setting the gameCounterDifficulty higher makes the delay between the button changes shorter
          if (score >= 2) {
            gameCounterDifficulty = 1;
          }
          if (score >= 5){
              gameCounterDifficulty = 2;
          }
          if (score >= 7){
              gameCounterDifficulty = 3;
          }
        
			    //The gameCounter is set equal to the gameCounterDifficulty after calculating the player's score
          if (++gameCounter == 10){
            if (buttonPressed == false && gameState == PLAYING){
              strikeCounter++;
              updateStrikeCounter(strikeCounterDisplay, strikeLayer, canvas2);
              //You missed three sequences to press the button
              if (strikeCounter == 3){
                gameState = GAMEOVER;
                populateScreen(bgLayer,titleLayer);
                updateScore(scoreDisplay, scoreBoardLayer, canvas);
                updateStrikeCounter(strikeCounterDisplay, strikeLayer, canvas2);
              }
            }
            buttonPressed = false;
            buttonOne.setInactive();
            buttonTwo.setInactive();
            buttonThree.setInactive();
            randomButtonNumber = randomButtonSelection.nextInt(3);
            
            //Light up 1 of 3 random chosen buttons
            if (randomButtonNumber == 0){
            	buttonOne.setRed();
            }
            if (randomButtonNumber == 1){
              buttonTwo.setRed();
            }
            if (randomButtonNumber == 2){
              buttonThree.setRed();
            }
            gameCounter = gameCounterDifficulty;
          }
        }
      }
    });
      
       pointer = new Pointer(plat, rootLayer, false);
       plat.input().mouseEvents.connect(new Mouse.Dispatcher(rootLayer, false));

         pointer.events.connect(new Slot<Pointer.Event>() {
         @Override public void onEmit (Pointer.Event event) {
                 if (gameState == TITLE){
                        buttonPressed = true;
						gameState = PLAYING;
						populateScreen(bgLayer, buttonLayer);
						updateScore(scoreDisplay, scoreBoardLayer, canvas);
                        updateStrikeCounter(strikeCounterDisplay, strikeLayer, canvas2);
					}
                if(gameState == PLAYING && buttonOne.isRed == true){
						buttonOne.setBlue();
                        buttonPressed = true;
                        score++;
                        updateScore(scoreDisplay, scoreBoardLayer, canvas);
					}
                if (gameState == PLAYING && buttonTwo.isRed){
                    buttonTwo.setBlue();
                    buttonPressed = true;
                    score++;
                    updateScore(scoreDisplay, scoreBoardLayer, canvas);
                }
                if (gameState == PLAYING && buttonThree.isRed == true){
                        buttonThree.setBlue();
						score++;
                        buttonPressed = true;
                        updateScore(scoreDisplay, scoreBoardLayer, canvas);
					}
        }
      });
        
		// when the keyboard button is pressed, do a thing
		plat.input().keyboardEvents.connect(new Keyboard.KeySlot(){
		@Override public void onEmit (Keyboard.KeyEvent event) {
			switch (event.key){
			// Case for down keyboard arrow key
				case DOWN:
					if (gameState == TITLE && event.down){
            buttonPressed = true;
						gameState = PLAYING;
						populateScreen(bgLayer, buttonLayer);
						updateScore(scoreDisplay, scoreBoardLayer, canvas);
            updateStrikeCounter(strikeCounterDisplay, strikeLayer, canvas2);
						break;
					} 
          if (gameState == GAMEOVER && event.down){
            gameState = PLAYING;
            populateScreen(bgLayer, buttonLayer);
            updateScore(scoreDisplay, scoreBoardLayer, canvas);
						updateStrikeCounter(strikeCounterDisplay, strikeLayer, canvas2);
            break;
          }
					if (gameState == PLAYING && event.down && buttonTwo.isRed == true){
            score++;
            buttonPressed = true;
            updateScore(scoreDisplay, scoreBoardLayer, canvas);
						buttonTwo.setBlue();
						break;
          }
					if (gameState == PLAYING && event.down && buttonTwo.isRed == false){
						gameState = GAMEOVER;
						populateScreen(bgLayer, titleLayer);
            updateScore(scoreDisplay, scoreBoardLayer, canvas);
            updateStrikeCounter(strikeCounterDisplay, strikeLayer, canvas2);
						break;
          }
				// Case for Left keyboard arrow key
        case LEFT:
					if(gameState == PLAYING && event.down && buttonOne.isRed == true){
						buttonOne.setBlue();
            buttonPressed = true;
            score++;
            updateScore(scoreDisplay, scoreBoardLayer, canvas);
						break;
					}
					if (gameState == PLAYING && event.down && buttonOne.isRed == false){
						gameState = GAMEOVER;
						populateScreen(bgLayer, titleLayer);
            updateScore(scoreDisplay, scoreBoardLayer, canvas);
            updateStrikeCounter(strikeCounterDisplay, strikeLayer, canvas2);
						break;
					}	
				// Case for right keyboard arrow key
				case RIGHT:
					if (gameState == PLAYING && event.down && buttonThree.isRed == true){
            buttonThree.setBlue();
						score++;
            buttonPressed = true;
            updateScore(scoreDisplay, scoreBoardLayer, canvas);
						break;
					}
					if (gameState == PLAYING && event.down && buttonThree.isRed == false){
						gameState = GAMEOVER;
						populateScreen(bgLayer, titleLayer);
            updateScore(scoreDisplay, scoreBoardLayer, canvas);
            updateStrikeCounter(strikeCounterDisplay, strikeLayer, canvas2);
						break;
					}
				default:
					break;
			} 
		}
    });
  }
}
