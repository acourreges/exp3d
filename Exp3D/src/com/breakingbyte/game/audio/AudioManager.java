package com.breakingbyte.game.audio;

import java.util.ArrayList;

import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.wrap.Audio;

/**
 * Manages the different sound effects and music.
 * Can play a specific sound from its ID, a track or stop all sound effects.
 */

public class AudioManager {

    public enum SoundId {
        NONE,
        EXPLOSION_1,
        EXPLOSION_2,
        BEEP_1,
        CHIME_1,
        RESPAWN,
        ALARM,
        BULLET_IMPACT,
        
        SPECIAL_WEAPON,
        
        BONUS_TIME,
        BONUS_SUPER_SHIELD,
        BONUS_HELLFIRE,
        
        BGM_M_A,
        BGM_F_L,
        BGM_I_F,
        BGM_P_P,
        BGM_F_T_B_R,
        BGM_S_B_S_W,
        BGM_GAME_OVER
    }
    
    public static ArrayList<SoundDescriptor> allSounds;
    public static ArrayList<SoundDescriptor> allMusics;
    
    private static Audio audio;
    
    public static boolean uniqueSoundPerFrame = true;
    
    private static ArrayList<SoundId> soundsPlayedThisFrame;
    
    private static SoundPool soundPool;
    
    private static String defaultFormat = ".ogg";
    
    //private static MediaPlayer player;
    
    public static void initialize() {
        audio = new Audio();
        soundPool = new SoundPool(audio, 20);
        soundsPlayedThisFrame = new ArrayList<AudioManager.SoundId>();
        allSounds = new ArrayList<SoundDescriptor>();
        allMusics = new ArrayList<SoundDescriptor>();
        
        //---------------------------------
        // Sounds
        //---------------------------------
        
        SoundDescriptor sound = createSoundDescriptor(SoundId.EXPLOSION_1);
        sound.path = "snd/explosion1" + defaultFormat;
        sound.duration = 510;
        sound.poolCapacity = 10;
        
        sound = createSoundDescriptor(SoundId.EXPLOSION_2);
        sound.path = "snd/explosion2" + defaultFormat;
        sound.duration = 810;
        sound.poolCapacity = 10;
        
        sound = createSoundDescriptor(SoundId.BEEP_1);
        sound.path = "snd/beep1" + defaultFormat;
        sound.duration = 290;
        sound.poolCapacity = 3;
        
        sound = createSoundDescriptor(SoundId.CHIME_1);
        sound.path = "snd/chime1" + defaultFormat;
        sound.duration = 175;
        sound.poolCapacity = 5;
        
        sound = createSoundDescriptor(SoundId.RESPAWN);
        sound.path = "snd/respawn" + defaultFormat;
        sound.duration = 285;
        sound.poolCapacity = 2;
        
        sound = createSoundDescriptor(SoundId.ALARM);
        sound.path = "snd/alarm" + defaultFormat;
        sound.duration = 460;
        sound.poolCapacity = 1;
        
        sound = createSoundDescriptor(SoundId.BONUS_TIME);
        sound.path = "snd/bonus_time" + defaultFormat;
        sound.duration = 690;
        sound.poolCapacity = 2;
        
        sound = createSoundDescriptor(SoundId.BONUS_SUPER_SHIELD);
        sound.path = "snd/bonus_super_shield" + defaultFormat;
        sound.duration = 175;
        sound.poolCapacity = 2;
        
        sound = createSoundDescriptor(SoundId.BONUS_HELLFIRE);
        sound.path = "snd/bonus_hellfire" + defaultFormat;
        sound.duration = 230;
        sound.poolCapacity = 2;
        
        sound = createSoundDescriptor(SoundId.SPECIAL_WEAPON);
        sound.path = "snd/special_weapon" + defaultFormat;
        sound.duration = 337;
        sound.poolCapacity = 2;
        
        sound = createSoundDescriptor(SoundId.BULLET_IMPACT);
        sound.path = "snd/bullet_impact" + defaultFormat;
        sound.duration = 495;
        sound.poolCapacity = 20;
        
        
        //---------------------------------
        // Musics
        //---------------------------------
        sound = createMusic(SoundId.BGM_M_A);
        sound.path = "snd/bgm_ma" + defaultFormat;
        
        sound = createMusic(SoundId.BGM_F_L);
        sound.path = "snd/bgm_fl" + defaultFormat;
        
        sound = createMusic(SoundId.BGM_I_F);
        sound.path = "snd/bgm_if" + defaultFormat;
        
        sound = createMusic(SoundId.BGM_P_P);
        sound.path = "snd/bgm_pp" + defaultFormat;
        
        sound = createMusic(SoundId.BGM_F_T_B_R);
        sound.path = "snd/bgm_ftbr" + defaultFormat;
        
        sound = createMusic(SoundId.BGM_S_B_S_W);
        sound.path = "snd/bgm_sbsw" + defaultFormat;
        
        sound = createMusic(SoundId.BGM_GAME_OVER);
        sound.path = "snd/bgm_game_over" + defaultFormat;
        
        registerAll();
    }
    
    public static SoundDescriptor createSoundDescriptor(SoundId id) {
        SoundDescriptor result = new SoundDescriptor();
        result.id = id;
        allSounds.add(result);
        return result;
    }
    
    public static SoundDescriptor createMusic(SoundId id) {
        SoundDescriptor result = new SoundDescriptor();
        result.id = id;
        allMusics.add(result);
        return result;
    }
    
    public static boolean load() {
        //all loading is triggered at the initialization phase
        return getProgress() > 0.999999f;
    }
    
    public static float getProgress() {
        
        if (audio == null) return 0f;
        
        float soundProgress = soundPool.getProgress();
        
        int musicLoaded = 0;
        for (int i = 0; i < allMusics.size(); i++) {
            if (audio.isMusicLoaded(allMusics.get(i).id)) musicLoaded++;
        }
        float musicProgress = allMusics.size() > 0? (float)musicLoaded / (float)allMusics.size() : 0f;
        
        return soundProgress * 0.5f + musicProgress * 0.5f;
    }
    
    private static void registerAll() {
        
        for (int i = 0; i < allSounds.size(); i++) {
            soundPool.registerSound(allSounds.get(i));
        }
        
        for (int i = 0; i < allMusics.size(); i++) {
            audio.registerMusic(allMusics.get(i));
        }
        
    }

    public void beginsNewFrame() {
        soundsPlayedThisFrame.clear();
    }
    
    public static SoundId lastMusicPlayed = SoundId.NONE;
    public static boolean wasPaused = false;
    public static void playMusic(SoundId musicId) {
        if (!EngineState.Settings.musicOn) return;
        
        if (lastMusicPlayed == musicId && wasPaused) {
            audio.resumeMusic();
            wasPaused = false;
            return;
        }
        
        audio.loadMusicToPlay(musicId);
        lastMusicPlayed = musicId;
        audio.playMusic(1f);
        wasPaused = false;
    }
    
    public static void pauseMusic() {
        if (audio == null) return;
        wasPaused = true;
        audio.pauseMusic();
    }
    
    public static void stopMusic() {
        audio.stopMusic();
    }
    
    public static void playSound(SoundId soundId) {
        if (!EngineState.Settings.soundOn) return;
        soundPool.playSound(soundId);
    }
    
    public static void playExplosion() {
        if (!EngineState.Settings.soundOn) return;
        int random = MathUtil.getRandomInt(0, 1);
        if (random == 0) {
            soundPool.playSound(SoundId.EXPLOSION_1);
        } else {
            soundPool.playSound(SoundId.EXPLOSION_2);
        }
    }
    
    public static void playClickSound() {
        playSound(SoundId.BEEP_1);
    }
    
    public static void playTitleMusic() {
        playMusic(SoundId.BGM_M_A);
    }

}
