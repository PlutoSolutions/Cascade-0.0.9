/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package cascade.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Random;
import java.util.regex.Pattern;

public class TextUtil {
    public static final String BLACK = String.valueOf((Object)ChatFormatting.BLACK);
    public static final String DARK_BLUE = String.valueOf((Object)ChatFormatting.DARK_BLUE);
    public static final String DARK_GREEN = String.valueOf((Object)ChatFormatting.DARK_GREEN);
    public static final String DARK_AQUA = String.valueOf((Object)ChatFormatting.DARK_AQUA);
    public static final String DARK_RED = String.valueOf((Object)ChatFormatting.DARK_RED);
    public static final String DARK_PURPLE = String.valueOf((Object)ChatFormatting.DARK_PURPLE);
    public static final String GOLD = String.valueOf((Object)ChatFormatting.GOLD);
    public static final String GRAY = String.valueOf((Object)ChatFormatting.GRAY);
    public static final String DARK_GRAY = String.valueOf((Object)ChatFormatting.DARK_GRAY);
    public static final String BLUE = String.valueOf((Object)ChatFormatting.BLUE);
    public static final String GREEN = String.valueOf((Object)ChatFormatting.GREEN);
    public static final String AQUA = String.valueOf((Object)ChatFormatting.AQUA);
    public static final String RED = String.valueOf((Object)ChatFormatting.RED);
    public static final String LIGHT_PURPLE = String.valueOf((Object)ChatFormatting.LIGHT_PURPLE);
    public static final String YELLOW = String.valueOf((Object)ChatFormatting.YELLOW);
    public static final String WHITE = String.valueOf((Object)ChatFormatting.WHITE);
    public static final String OBFUSCATED = String.valueOf((Object)ChatFormatting.OBFUSCATED);
    public static final String BOLD = String.valueOf((Object)ChatFormatting.BOLD);
    public static final String STRIKE = String.valueOf((Object)ChatFormatting.STRIKETHROUGH);
    public static final String UNDERLINE = String.valueOf((Object)ChatFormatting.UNDERLINE);
    public static final String ITALIC = String.valueOf((Object)ChatFormatting.ITALIC);
    public static final String RESET = String.valueOf((Object)ChatFormatting.RESET);
    private static final Random rand = new Random();

    public static String stripColor(String input) {
        if (input != null) {
            return Pattern.compile("(?i)\u00a7[0-9A-FK-OR]").matcher(input).replaceAll("");
        }
        return "";
    }

    public static String coloredString(String string, Color color) {
        String coloredString = string;
        switch (color) {
            case AQUA: {
                coloredString = (Object)ChatFormatting.AQUA + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case WHITE: {
                coloredString = (Object)ChatFormatting.WHITE + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case BLACK: {
                coloredString = (Object)ChatFormatting.BLACK + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case DARK_BLUE: {
                coloredString = (Object)ChatFormatting.DARK_BLUE + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case DARK_GREEN: {
                coloredString = (Object)ChatFormatting.DARK_GREEN + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case DARK_AQUA: {
                coloredString = (Object)ChatFormatting.DARK_AQUA + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case DARK_RED: {
                coloredString = (Object)ChatFormatting.DARK_RED + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case DARK_PURPLE: {
                coloredString = (Object)ChatFormatting.DARK_PURPLE + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case GOLD: {
                coloredString = (Object)ChatFormatting.GOLD + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case DARK_GRAY: {
                coloredString = (Object)ChatFormatting.DARK_GRAY + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case GRAY: {
                coloredString = (Object)ChatFormatting.GRAY + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case BLUE: {
                coloredString = (Object)ChatFormatting.BLUE + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case RED: {
                coloredString = (Object)ChatFormatting.RED + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case GREEN: {
                coloredString = (Object)ChatFormatting.GREEN + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case LIGHT_PURPLE: {
                coloredString = (Object)ChatFormatting.LIGHT_PURPLE + coloredString + (Object)ChatFormatting.RESET;
                break;
            }
            case YELLOW: {
                coloredString = (Object)ChatFormatting.YELLOW + coloredString + (Object)ChatFormatting.RESET;
            }
        }
        return coloredString;
    }

    public static String cropMaxLengthMessage(String s, int i) {
        String output = "";
        if (s.length() >= 256 - i) {
            output = s.substring(0, 256 - i);
        }
        return output;
    }

    public static enum Color {
        NONE,
        WHITE,
        BLACK,
        DARK_BLUE,
        DARK_GREEN,
        DARK_AQUA,
        DARK_RED,
        DARK_PURPLE,
        GOLD,
        GRAY,
        DARK_GRAY,
        BLUE,
        GREEN,
        AQUA,
        RED,
        LIGHT_PURPLE,
        YELLOW;

    }
}

