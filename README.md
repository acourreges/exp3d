# Exp3D

[![banner](/media/thumbnail2.png)](http://www.breakingbyte.com/exp3d/)

[![WebGL Live Demo](/media/play-webgl.png)](http://www.breakingbyte.com/exp3d/demo/) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
[![Google Play](/media/get-it-on-google-play.png)](https://play.google.com/store/apps/details?id=com.breakingbyte.exp3d) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
[![Website](/media/breaking-byte-site.png)](http://www.breakingbyte.com/exp3d/)

This is the source code of the game **[Exp3D](http://www.breakingbyte.com/exp3d/)**, a space shooter I released on Android in 2013.  
It started out as an Android-only game I began writing from scratch back in 2011, in my free time. 
During the development, desktop support (*Windows/Linux/Mac*) was added, followed by a *WebGL* port by transcompiling to pure Javascript. 

Here is the complete source code of the game, including the assets (with the exception of some music due to licensing issues and some in-app purchase/analytics code left out). 

## License

All the program source code is released under GPL.  

But the game assets (sprites, images, sound effects...) remain proprietary, they are included here as a courtesy, for educational purposes only.  
Note that due to restrictive licensing issues, I cannot myself redistribute the original BGM songs so they are replaced with placeholder files.  
In short: ***you are free to study and play with the source code but you are not allowed to redistribute a recompiled version of Exp3D***. 

## Details

All the code of Exp3D was written from scratch. The game runs on top of a minimalistic multiplatform framework 
I wrote. The source is entirely in Java, there is no "native" code included. The main loop doesn't perform any dynamic allocation to 
maintain a constant 60FPS with 0 GC pauses, this was achieved by relying heavily on object pooling.  
95% of the code is written on top of this framework and thus is shared between the different platforms. 

The rendering engine can run on either a fixed-pipeline (OpenGL ES 1.1) or a programmable-pipeline (OpenGL ES 2.0, WebGL). 

![architecture](/media/exp3d-architecture-diagram.png)

## Compiling

There are 3 Eclipse projects in the repository:

- **Exp3D** *for Android*
- **Exp3DDesktop** *for the desktop (Windows/Linux/Mac)*
- **Exp3DGWT** *for the Javascript + WebGL version*

### Android

The project **Exp3D** is self-contained and doesn't require any external code or library.  
If you have the Android SDK installed with Eclipse, simply open the project in Eclipse and do `Run As Android Application`.

Note: you need an Android SDK with API level 17 (Android 4.2) or newer to compile the game.  
The APK generated is still compatible down to Android 2.2 thanks to version check at runtime. (The game could actually run on an old Android 1.6 with a bit of tuning.) 

### Desktop

First you need to make Eclipse aware of the [JOGL library](http://jogamp.org/jogl/www/), it is the only external library required to make Exp3D run.  
Download the JOGL version corresponding to your OS and add it to Eclipse as a user library under the name `jogl`. ([more details here](http://jogamp.org/wiki/index.php/Setting_up_a_JogAmp_project_in_your_favorite_IDE))

The **Exp3D** Android project is referenced by the desktop project so make sure you imported it in Eclipse first.

Then import the **Exp3DDesktop** project in Eclipse and simply run the class `com.breakingbyte.wrap.DesktopApplication` to start the game.

### Web

The Java code is actually transcompiled to Javascript using [GWT](http://www.gwtproject.org/). 

First install the GWT plugin for Eclipse (it now appears to be contained into the *[Google Plugin](https://developers.google.com/eclipse/)*).  
The version supported by the current code is GWT 2.6.0. 

Make sure the the **Exp3DDesktop** project is open in Eclipse, even if not all of its dependencies are installed.  
Run once the class `com.breakingbyte.wrap.SynchronizeGWT`, this copies and pre-processes some assets to optimize them for the web. 

Import the **Exp3DGWT** project into Eclipse and do `Google → GWT Compile`. You can now open the web page `war/Exp3DGWT.html` and the game will start.