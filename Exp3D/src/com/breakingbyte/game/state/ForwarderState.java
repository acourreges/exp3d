package com.breakingbyte.game.state;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.content.Article;
import com.breakingbyte.game.content.ShopContent;
import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.ui.ImageWidget;
import com.breakingbyte.game.ui.dialog.Dialog;
import com.breakingbyte.game.ui.dialog.DialogButton;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class ForwarderState extends State {
    
    public static ForwarderState instance = new ForwarderState();

    public State getInstance() { return instance; }

    public State nextState;
    private float red, green, blue, duration;
    
    private static CannonDialog cannonDialog;
    private static CongratulationDialog congratulationDialog;
    private static DialogEnum displayDialog = DialogEnum.NONE;
    
    public static void init() {
        cannonDialog = new CannonDialog();
        congratulationDialog = new CongratulationDialog();
    }
    
    public static enum DialogEnum {
        NONE,
        EXTRA_CANNON,
        GAME_CLEARED
    }
    
    public void setNextState(State state, float red, float green, float blue, float duration, DialogEnum dialogToDisplay) {
        this.nextState = state;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.duration = duration;
        ForwarderState.displayDialog = dialogToDisplay;
    }
    
    @Override
    public void updateImpl() {

        if (displayDialog == DialogEnum.EXTRA_CANNON) {
            cannonDialog.setSize(90, 103);
            cannonDialog.update();
            if (Controller.hasEvent) {
                cannonDialog.handleTouch(Controller.touchX, Controller.touchY, Controller.touchAction);
            }
        } 
        else if (displayDialog == DialogEnum.GAME_CLEARED) {
            congratulationDialog.setSize(90, 80);
            congratulationDialog.update();
            if (Controller.hasEvent) {
                congratulationDialog.handleTouch(Controller.touchX, Controller.touchY, Controller.touchAction);
            }
        } 
        else {
            if (!transitionInProgress) switchToStateWithColor(nextState, red, green, blue, 0.01f, 0f, duration);
        } 
    }
    
    @Override
    public void renderAlways() {
        
        Renderer.DepthTest.disable();
        Renderer.Blending.enable();
        Renderer.Blending.resetMode();
        Renderer.clear(false, true);
        
        Renderer.loadOrtho();
    }
    
    @Override
    public void renderImpl() {
        if (displayDialog == DialogEnum.EXTRA_CANNON) cannonDialog.render();
        if (displayDialog == DialogEnum.GAME_CLEARED) congratulationDialog.render();
    }

    @Override
    public void onEnterImpl(boolean isResume) {
        if (displayDialog == DialogEnum.EXTRA_CANNON && !isResume) {
            cannonDialog.setValuesFromArticle(ShopContent.primaryCannonArticle);
            cannonDialog.setSize(90, 110);
            cannonDialog.show();
        }
        else if (displayDialog == DialogEnum.GAME_CLEARED && !isResume) {
            congratulationDialog.setSize(90, 110);
            congratulationDialog.show();
        }
    }

    @Override
    public void onLeaveCompleted() {

    }

    //A bit dirty: we use this state to display the dialog that tells the player he unlocked
    //an additional cannon at the end of level 1
    
    public static class CannonDialog extends Dialog {
        
        public ImageWidget icon;

        public DynamicText 
            description,
            shop,
            price,
            headerText;
        
        public DialogButton okButton;
        
        public Article article;
        
        private float elapsed = 0f;
        
        public CannonDialog() {
            icon = new ImageWidget();
            price = new DynamicText(16);
            description = new DynamicText(64);
            shop = new DynamicText(64);
            
            okButton = new DialogButton(8);
            okButton.text.setAlignment(Align.CENTER);
            okButton.setOnClickListener( new TouchListener() { 
                public void riseEvent(float x, float y) { 
                    AudioManager.playClickSound();
                    hide();
                    ForwarderState.instance.switchToStateWithColor(TitleState.instance, 0f, 0f, 0f, 1f, 0f);
                } 
                } );

            headerText = new DynamicText(16);
            addChild(icon);
            addChild(description);
            addChild(shop);
            addChild(headerText);
            
            addChild(okButton);
        }
        
        public void buyButtonClicked() {
            ShopState.articleBuyButtonClicked(article);
        }
        
        @Override
        public void update() {
            super.update();
            elapsed += Timer.delta;
        }
        
        public void setValuesFromArticle(Article article) {    
            
            this.article = article;
            
            //boolean maxReached = article.isAtMaxLevel();
            
            title.reset().printString("Ship Upgraded").updateBuffers();
            
            this.icon.setTexture(article.getIcon());
            
            description.setAlignment(Align.CENTER);
            description.reset();
            description.printString("An extra cannon has").newLine()
                       .printString("been installed.");
            description.updateBuffers();
            
            shop.setAlignment(Align.CENTER);
            shop.reset();
            shop.printString("More upgrades are").newLine()
                .printString("available in the shop!");
            shop.updateBuffers();
            
            headerText.setAlignment(Align.CENTER);
            headerText.reset();
            headerText.reset().printString("Congratulations!").updateBuffers();
            
            okButton.glowing = true;
            okButton.text.reset().printString("Ok").updateBuffers();
        }

        public void setSize(float width, float height) {
            super.setSize(width, height);
            layoutChildren(width, height);
        }
        
        public void layoutChildren(float width, float height) {
            super.layoutChildren(width, height);
            
            panel.setAlpha(1f);
            //panel.setColor(0.3f, 0.6f, 1f);
            
            float offsetY = height * 0.5f - 25f;

            icon.setAlpha(0.8f);
            icon.setSize(22, 22);
            icon.setPosition(0, offsetY);
            
            offsetY -= 11f;
            
            float textSize = 10f;
            offsetY -= 0f;
            headerText.textSize = textSize;
            headerText.setAlpha(0.7f);
            headerText.setPosition(0f, offsetY);
            

            
            textSize = 7.0f;
            
            offsetY -= 9f;
            description.setAlpha(0.7f);
            description.textSize = textSize;
            description.setPosY(offsetY);
            
            float modifier =  (float)Math.sin(3f * elapsed);
            textSize += 1f + 0.5f * modifier;
            offsetY -= 19f;
            shop.setAlpha(0.7f);
            shop.textSize = textSize;
            shop.setPosY(offsetY);
            shop.setColor( (0.2f - 0.2f * modifier), 0.9f + 0.1f * modifier, 1f);
            
            
            float buttonYOffset = height * -0.5f + 12f;
            float buttonXOffset = 0;
            float buttonWidth = 38f, buttonHeight = 15f;
            float buttonTextSize = 9f, buttonTextY = 4.5f;
            
            okButton.text.textSize = buttonTextSize;
            okButton.text.setPosY(buttonTextY);
            okButton.setSize(buttonWidth, buttonHeight);
            okButton.setPosition(-buttonXOffset, buttonYOffset);
            
        }
    }
    
    public static class CongratulationDialog extends Dialog {
        
        public DynamicText 
            description,
            headerText;
        
        public DialogButton okButton;

        public CongratulationDialog() {
            description = new DynamicText(64);
            
            okButton = new DialogButton(8);
            okButton.text.setAlignment(Align.CENTER);
            okButton.setOnClickListener( new TouchListener() { 
                public void riseEvent(float x, float y) { 
                    AudioManager.playClickSound();
                    hide();
                    ForwarderState.instance.switchToStateWithColor(TitleState.instance, 0f, 0f, 0f, 1f, 0f);
                } 
                } );

            headerText = new DynamicText(16);
            addChild(description);
            addChild(headerText);
            
            addChild(okButton);
            setValues();
        }

        
        public void setValues() {    

            //boolean maxReached = article.isAtMaxLevel();
            
            title.reset().printString("Game Cleared").updateBuffers();
            
            description.setAlignment(Align.CENTER);
            description.reset();
            description.printString("Thank you for").newLine()
                       .printString("playing Exp3D!").newLine().newLine()
                       .printString("We hope you").newLine()
                       .printString("enjoyed the game.").newLine();
            description.updateBuffers();
            
            headerText.setAlignment(Align.CENTER);
            headerText.reset();
            headerText.reset().printString("Congratulations!").updateBuffers();
            
            okButton.glowing = true;
            okButton.text.reset().printString("Ok").updateBuffers();
        }

        public void setSize(float width, float height) {
            super.setSize(width, height);
            layoutChildren(width, height);
        }
        
        public void layoutChildren(float width, float height) {
            super.layoutChildren(width, height);
            
            panel.setAlpha(1f);
            //panel.setColor(0.3f, 0.6f, 1f);
            
            float offsetY = height * 0.5f - 17f;

            
            float textSize = 10f;
            offsetY -= 0f;
            headerText.textSize = textSize;
            headerText.setAlpha(0.7f);
            headerText.setPosition(0f, offsetY);
            

            
            textSize = 7.0f;
            
            offsetY -= 13f;
            description.setAlpha(0.7f);
            description.textSize = textSize;
            description.setPosY(offsetY);
            
            float buttonYOffset = height * -0.5f + 12f;
            float buttonXOffset = 0;
            float buttonWidth = 38f, buttonHeight = 15f;
            float buttonTextSize = 9f, buttonTextY = 4.5f;
            
            okButton.text.textSize = buttonTextSize;
            okButton.text.setPosY(buttonTextY);
            okButton.setSize(buttonWidth, buttonHeight);
            okButton.setPosition(-buttonXOffset, buttonYOffset);
            
        }
    }


}
