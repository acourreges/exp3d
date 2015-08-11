package com.breakingbyte.game.level;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.entity.enemy.Byrol;
import com.breakingbyte.game.entity.fire.FireCyclic;
import com.breakingbyte.game.entity.fire.Fire.Type;
import com.breakingbyte.game.entity.move.LocalMoveDefault;
import com.breakingbyte.game.entity.move.WorldMoveSequence;
import com.breakingbyte.game.entity.move.WorldMoveSmooth;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;

public class ShowCatalog {
    
    public static Byrol byrolVerticalWaitFireGoDown(float mainX, float mainY)
    {
        Byrol e = Byrol.spawn(); LocalMoveDefault.applyTo(e);
        WorldMoveSmooth firstMove = WorldMoveSmooth.newInstance().speed(1.2f).pt0(mainX, Screen.ARENA_HEIGHT + 10f).pt1(mainX, mainY);
        firstMove.smoother.setInterpolator(Interpolator.BACK_END).setBack(3.0f);
        WorldMoveSequence.applyTo(e)
        .append( firstMove )
        .append( WorldMoveSmooth.newInstance().speed(1.0f).pt0(mainX, mainY).pt1(mainX, -10f).interp(Interpolator.QUADRATIC_START) );
        FireCyclic.applyTo(e).delay(0.5f).shoot(6, 0.04f, 9f).speed(90f).angle(0, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
        FireCyclic.applyTo(e).delay(0.5f).shoot(6, 0.04f, 9f).speed(90f).angle(0, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
        return e;
    }

}
