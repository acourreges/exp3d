package com.breakingbyte.game.util;

import java.util.ArrayList;

public class ModelManager {

    public static ArrayList<Model> allModels;
    
    public static Model ship, 
                        astrol,
                        byrol,
                        crystol,
                        drakol,
                        
                        bossa,
                        bossb,
                        bossb_arm,
                        bossc,
                        bossd,

                        tunnel,
                        tunnel_lm;
    
    static {
        init();
    }
    
    public static void init() {
        
        allModels = new ArrayList<Model>();
        
        ship = createModel("mdl/ship.obj");        
        astrol = createModel("mdl/astrol.obj");
        byrol = createModel("mdl/byrol.obj");
        crystol = createModel("mdl/crystol.obj");
        drakol = createModel("mdl/drakol.obj");
        
        bossa = createModel("mdl/bossa.obj");
        bossb = createModel("mdl/bossb.obj");
        bossb_arm = createModel("mdl/bossb_arm.obj");
        bossc = createModel("mdl/bossc.obj");
        bossd = createModel("mdl/bossd.obj");
        
        tunnel = createModel("mdl/tunnel.obj");
        tunnel_lm = createModel("mdl/tunnel_lm.obj");
        
    }
    
    public static Model createModel(String filePath) {
        Model result = new Model(filePath);
        allModels.add(result);
        return result;
    }
    
    public static void trashAll() {
        for (int i = 0; i < allModels.size(); i++) {
            Model model = allModels.get(i);
                model.isLoaded = false;
        }
    }
    
    public static boolean loadModels(int level) {
        
        boolean allLoaded = true;
        
        for (int i = 0; i < allModels.size(); i++) {
            Model model = allModels.get(i);
            if (model.level <= level && !model.isLoaded) {
                model.load();
                allLoaded = false;
            }
        }
        return allLoaded;
    }
    
    public static float getProgress(int level) {
        int total = 0;
        int done = 0;
        
        for (int i = 0; i < allModels.size(); i++) {
            Model model = allModels.get(i);
            if (model.level <= level) {
                total++;
                if (model.isLoaded) done++;
            }
        }
        
        if (total == 0) return 1f;
        return (float)done / (float)total;
    }
    
    public static boolean isAllLoaded(int level) {
        for (int i = 0; i < allModels.size(); i++) {
            Model model = allModels.get(i);
            if (model.level <= level) {
                if (!model.isLoaded) return false;
            }
        }
        return true;
    }
    
    public static boolean allLoaded() {
        boolean allLoaded = true;
        for (int i = 0; i < allModels.size(); i++) {
            Model model = allModels.get(i);
            if (!model.isLoaded) {
                model.load();
                allLoaded = false;
            }
        }
        return allLoaded;
    }
    
}
