/**
 * File:        AppTest.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.21
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's application framework test program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.AppConfig;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vec2D;

public class AppTest {

    public static void TestColour() {
        App.Log.write(Colour.Black, ", ", Colour.Red, ", ", Colour.Green, ", ", Colour.Blue, ", ", Colour.White);
        App.Log.write(Colour.Channels(128, 0, 255, 128));
        App.Log.write(Colour.Bytes(0x0AFF00F0));
        Colour c1 = Colour.Channels(128, 35, 230, 155);
        Colour c2 = Colour.Channels(0, 50, 200, 50);
        Colour c3 = Colour.Channels(200, 45, 30, 255);
        App.Log.write("Blend ", c1, " + ", c2, " -> ", c1.blend(c2));
        App.Log.write("Blend ", c1, " + ", c3, " -> ", c1.blend(c3));
        App.Log.write(c1, ".getRGBA() = ", c1.getRGBA());
        App.Log.write(c1, ".getRed() = ", c1.getRed());
        App.Log.write(c1, ".getGreen() = ", c1.getGreen());
        App.Log.write(c1, ".getBlue() = ", c1.getBlue());
        App.Log.write(c1, ".getAlpha() = ", c1.getAlpha());
        Colour c4 = Colour.Channels(128, 35, 230, 155);
        App.Log.write(c1, " = ", c4, ": ", c1.equals(c4));
        App.Log.write(c2, " = ", c4, ": ", c2.equals(c4));
    }

    public static void TestAngle() {
        App.Log.write(Angle.Radians(Angle.Pi / 4.0d));
        App.Log.write(Angle.Degrees(340.0d));
        App.Log.write(Angle.Gradians(130.0d));
        App.Log.write(Angle.Revolutions(1.05d));
        Angle a1 = Angle.Degrees(390.0d);
        Angle a2 = Angle.Degrees(-450.0d);
        App.Log.write(a1, " + ", a2, " = ", a1.add(a2));
        App.Log.write(a1, " - ", a2, " = ", a1.subtract(a2));
        App.Log.write(a1, " * 3.5 = ", a1.scalarMultiply(3.5d));
        App.Log.write(a1, " / 2.75 = ", a1.scalarDivide(2.75d));
        App.Log.write("Standardize: ", a1, " -> ", a1.standardize());
        App.Log.write("Standardize: ", a2, " -> ", a2.standardize());
        Angle a3 = Angle.Radians((3.0d * Angle.Pi) / 4.0d);
        App.Log.write(a3, " radians -> ", a3.getRadians(), " radians");
        App.Log.write(a3, " radians -> ", a3.getDegrees(), " degrees");
        App.Log.write(a3, " radians -> ", a3.getGradians(), " gradians");
        App.Log.write(a3, " radians -> ", a3.getRevolutions(), " revolutions");
        Angle a4 = Angle.Radians(0.0d);
        App.Log.write(a1, " = 0: ", a1.isZero());
        App.Log.write(a4, " = 0: ", a4.isZero());
        Angle a5 = Angle.Degrees(40.0d);
        Angle a6 = Angle.Degrees(490.0d);
        Angle a7 = Angle.Degrees(240.0d);
        Angle a8 = Angle.Degrees(-370.0d);
        App.Log.write(a5, " in quadrant ", a5.getQuadrant());
        App.Log.write(a6, " in quadrant ", a6.getQuadrant());
        App.Log.write(a7, " in quadrant ", a7.getQuadrant());
        App.Log.write(a8, " in quadrant ", a8.getQuadrant());
        Angle a9 = Angle.Degrees(850.0d);
        App.Log.write(a4, " coterminal to ", a9, ": ", a4.isCoterminalTo(a9));
        App.Log.write(a6, " coterminal to ", a9, ": ", a6.isCoterminalTo(a9));
        App.Log.write(a6, " acute: ", a6.isAcute());
        App.Log.write(a5, " acute: ", a5.isAcute());
        App.Log.write(a7, " right: ", a7.isRight());
        Angle a10 = Angle.Degrees(90.0d);
        App.Log.write(a10, " right: ", a10.isRight());
        App.Log.write(a4, " obtuse: ", a4.isObtuse());
        Angle a11 = Angle.Degrees(130.0d);
        App.Log.write(a11, " obtuse: ", a11.isObtuse());
        Angle a12 = Angle.Degrees(180.0d);
        App.Log.write(a3, " straight: ", a3.isStraight());
        App.Log.write(a12, " straight: ", a12.isStraight());
        App.Log.write(a10, " reflex: ", a10.isReflex());
        App.Log.write(a7, " reflex: ", a7.isReflex());
        App.Log.write(a11, " = ", a12, ": ", a11.equals(a12));
        Angle a13 = Angle.Degrees(130.0d);
        App.Log.write(a11, " = ", a13, ": ", a11.equals(a13));
    }

    public static void TestVec2D() {
        Vec2D v1 = Vec2D.Cartesian(5.0d, 3.0d);
        Vec2D v2 = Vec2D.Polar(7.0d, Angle.Radians(5.0d));
        App.Log.write(v1, ", ", v2);
        App.Log.write(v1, " + ", v2, " = ", v1.add(v2));
        App.Log.write(v1, " - ", v2, " = ", v1.subtract(v2));
        App.Log.write("-", v1, " = ", v1.negate());
        App.Log.write("3.5 * ", v1, " = ", v1.scalarMultiply(3.5d));
        App.Log.write(v1, " / 2.0 = ", v1.scalarDivide(2.0d));
        App.Log.write(v1, " * ", v2, " = ", v1.elementMultiply(v2));
        App.Log.write(v1, " / ", v2, " = ", v1.elementDivide(v2));
        App.Log.write(v1, " . ", v2, " = ", v1.dotMultiply(v2));
        App.Log.write("dist^2(", v1, ", ", v2, ") = ", v1.getDistanceToSquared(v2));
        App.Log.write("dist(", v1, ", ", v2, ") = ", v1.getDistanceTo(v2));
        App.Log.write("normalize(", v1, ") = ", v1.normalize());
        App.Log.write(v1, " x ", v2, " = ", v1.crossMultiply(v2));
        App.Log.write("rotate(", v1, ", Pi/2rad) = ", v1.rotate(Angle.Radians(Angle.Pi / 2.0d)));
        App.Log.write("rotate(", v1, ", Pi/3rad, ", v2, ") = ", v1.rotateAbout(Angle.Radians(Angle.Pi / 3.0d), v2));
        App.Log.write("angle(", v1, ", ", v2, ") = ", v1.getAngleTo(v2));
        App.Log.write("reflect x(", v1, ") = ", v1.reflectHorizontally());
        App.Log.write("reflect y(", v1, ") = ", v1.reflectVertically());
        App.Log.write("interpolate(", v1, ", ", v2, ", 0.75) = ", v1.interpolate(v2, 0.75d));
        App.Log.write("midpoint(", v1, ", ", v2, ") = ", v1.midpoint(v2));
        App.Log.write("floor(", v2, ") = ", v2.floor());
        App.Log.write("ceiling(", v2, ") = ", v2.ceiling());
        App.Log.write(v1, " -> x = ", v1.getX(), ", y = ", v1.getY());
        App.Log.write(v1, " -> element sum = ", v1.getElementSum());
        App.Log.write("length^2(", v1, ") = ", v1.getLengthSquared());
        App.Log.write("length(", v1, ") = ", v1.getLength());
        App.Log.write(v1, " = 0: ", v1.isZero());
        Vec2D v3 = Vec2D.Cartesian(0.0d, 0.0d);
        App.Log.write(v3, " = 0: ", v3.isZero());
        App.Log.write(v2, " normalized: ", v2.isNormalized());
        Vec2D v4 = Vec2D.Polar(1.0d, Angle.Radians(Angle.Pi / 4.0d));
        App.Log.write(v4, " normalized: ", v4.isNormalized());
        App.Log.write(v1, " -> angle: ", v1.getAngle());
        App.Log.write(v1, " || ", v2, ": ", v1.isParallelTo(v2));
        Vec2D v5 = Vec2D.Cartesian(10.0d, 6.0d);
        App.Log.write(v1, " || ", v5, ": ", v1.isParallelTo(v5));
        App.Log.write(v1, " T ", v2, ": ", v1.isPerpendicularTo(v2));
        Vec2D v6 = Vec2D.Cartesian(-3.0d, 5.0d);
        App.Log.write(v1, " T ", v6, ": ", v1.isPerpendicularTo(v6));
        Vec2D v7 = Vec2D.Polar(5.0d, Angle.Radians(2.21429747667595d));
        Vec2D v8 = Vec2D.Cartesian(-3.0d, 4.0d);
        App.Log.write(v1, " = ", v7, ": ", v1.equals(v7));
        App.Log.write(v7, " = ", v8, ": ", v7.equals(v8));
    }

    static void main() {
        AppConfig config = new AppConfig();
        if (!App.Initialize(config)) {
            System.out.println("Failed to initialize app!");
            return;
        }
        App.Log.write("\n\n*****     Colour Tests     *****\n");
        TestColour();
        App.Log.write("\n\n*****     Angle Tests     *****\n");
        TestAngle();
        App.Log.write("\n\n*****     Vec2D Tests     *****\n");
        TestVec2D();
        if (!App.Destroy()) {
            System.out.println("Failed to destroy app!");
        }
    }

}
