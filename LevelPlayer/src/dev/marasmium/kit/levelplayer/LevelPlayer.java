package dev.marasmium.kit.levelplayer;

import dev.marasmium.kit.applib.AppLib;
import dev.marasmium.kit.assetlib.AssetLib;
import dev.marasmium.kit.levellib.LevelLib;

public class LevelPlayer {

    static void main() {
        System.out.println("LevelPlayer Dependencies:");
        AssetLib.assetLib();
        AppLib.appLib();
        LevelLib.levelLib();
        System.out.println();
    }

}
