package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.AppLib;
import dev.marasmium.kit.assetlib.AssetLib;

public class AppTest {

    static void main() {
        System.out.println("AppTest Dependencies:");
        AssetLib.assetLib();
        AppLib.appLib();
        System.out.println();
    }

}
