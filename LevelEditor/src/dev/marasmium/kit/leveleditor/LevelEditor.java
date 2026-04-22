package dev.marasmium.kit.leveleditor;

import dev.marasmium.kit.applib.AppLib;
import dev.marasmium.kit.assetlib.AssetLib;
import dev.marasmium.kit.levellib.LevelLib;

public class LevelEditor {

    static void main() {
        System.out.println("LevelEditor Dependencies:");
        AssetLib.assetLib();
        AppLib.appLib();
        LevelLib.levelLib();
        System.out.println();
    }

}
