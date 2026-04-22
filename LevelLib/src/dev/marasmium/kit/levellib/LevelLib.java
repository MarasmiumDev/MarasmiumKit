package dev.marasmium.kit.levellib;

import dev.marasmium.kit.applib.AppLib;
import dev.marasmium.kit.assetlib.AssetLib;

public class LevelLib {

    public static void levelLib() {
        System.out.println("LevelLib Dependencies:");
        AssetLib.assetLib();
        AppLib.appLib();
        System.out.println();
    }

}
