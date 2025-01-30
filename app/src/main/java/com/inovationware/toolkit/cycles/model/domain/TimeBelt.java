package com.inovationware.toolkit.cycles.model.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum TimeBelt {
    Minus12,
    Minus11,
    Minus10,
    Minus9_30,
    Minus9,
    Minus8,
    Minus7,
    Minus6,
    Minus5,
    Minus4,
    Minus3_30,
    Minus3,
    Minus2,
    Minus1,
    Plus0,
    Plus1,
    Plus2,
    Plus3,
    Plus3_30,
    Plus4,
    Plus4_30,
    Plus5,
    Plus5_30,
    Plus5_45,
    Plus6,
    Plus6_30,
    Plus7,
    Plus8,
    Plus8_45,
    Plus9,
    Plus9_30,
    Plus10,
    Plus10_30,
    Plus11,
    Plus12,
    Plus12_45,
    Plus13,
    Plus14;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] listing() {
        return Arrays.stream(TimeBelt.values()).map(belt -> to(belt)).collect(Collectors.toList()).toArray(String[]::new);

        //return Arrays.stream(TimeBelt.values()).map(Enum::name).toArray(String[]::new);
    }

    private static final String plus_string = "Plus";
    private static final String plus_sign = "+";
    private static final String minus_string = "Minus";
    private static final String minus_sign = "-";
    private static final String underscore = "_";
    private static final String space = " ";
    private static final String nothing = "";
    private static final String colon = ":";

    public static String to(TimeBelt belt) {
        if (belt.name().startsWith(plus_string)) {
            String step1 = belt.name().replace(plus_string, plus_sign);
            String step2 = step1.replace(underscore, colon);
            return step2;
        }
        String step1 = belt.name().replace(minus_string, minus_sign);
        String step2 = step1.replace(underscore, colon);
        return step2;

        //return belt.name().startsWith(plus_string) ? belt.name().replace(plus_string, plus_sign + space + (belt.name().replace(plus_string, nothing).replace(underscore, colon))) : belt.name().replace(minus_string, minus_sign + space + (belt.name().replace(minus_string, nothing).replace(underscore, colon)));
    }

    public static TimeBelt from(String value) {
        String step2 = value.replace(colon, underscore);
        return TimeBelt.valueOf(step2.startsWith(plus_sign) ? step2.replace(plus_sign, plus_string) : step2.replace(minus_sign, minus_string));

        //return TimeBelt.valueOf(value.startsWith(plus_sign) ? value.replace(plus_sign, plus_string).replace(colon, underscore) : value.replace(minus_sign, minus_string).replace(colon, underscore));
    }

    public static <T> String informationAboutTimezone(T value) {
        try {

            TimeBelt belt = value instanceof TimeBelt ? (TimeBelt) value : from(String.valueOf(value));

            switch (belt) {
                case Minus12:
                    return "Baker Island, Howland Island";
                case Minus11:
                    return "Jarvis Island, Kingman Reef";
                case Minus10:
                    return "Cook Islands, Hawaii, United States: Andreanof Islands, Islands of Four Mountains, Near Islands";
                case Minus9_30:
                    return "Marquesas Islands";
                case Minus9:
                    return "Gambier Islands, United States: Alaska (most)";
                case Minus8:
                    return "Canada: British Columbia (most)\nMexico: Baja California\nUnited States: California, Idaho (north), Washington";
                case Minus7:
                    return "Canada: British Columbia (northeast), Yukon;\n" +
                            "Mexico: Baja California Sur, Chihuahua (northwest border);\n" +
                            "Canada: Alberta, British Columbia (southeast), Northwest Territories;\n" +
                            "United States: Arizona (most), New Mexico, Texas (west), Utah";
                case Minus6:
                    return "Canada: Manitoba, Ontario (west), Saskatchewan (most);\n" +
                            "Costa Rica\n" +
                            "Ecuador: Galápagos\n" +
                            "Mexico (most, northeast border)\n" +
                            "United States: Florida (northwest), Oklahoma, Texas (most)";
                case Minus5:
                    return "Bahamas\n" +
                            "Brazil: Acre, Amazonas (southwest)\n" +
                            "Canada: Southampton Island, Ontario (most), Quebec (most);\n" +
                            "Jamaica\n" +
                            "Mexico: Quintana Roo\n" +
                            "United States: District of Columbia, Florida (most), Maryland, New York";
                case Minus4:
                    return "Canada: Nova Scotia, Prince Edward Island, Quebec (east);\n" +
                            "Brazil: Amazonas (most)\n" +
                            "Puerto Rico\n" +
                            "Trinidad and Tobago\n" +
                            "Venezuela";
                case Minus3_30:
                    return "Canada: Newfoundland, Labrador (southeast)";
                case Minus3:
                    return "Argentina, Brazil (most), Chile: Magallanes Region; Falkland Islands, Uruguay";
                case Minus2:
                    return "Brazil: Fernando de Noronha;\n" +
                            "South Georgia and the South Sandwich Islands;\n" +
                            "Greenland (most)";
                case Minus1:
                    return "Cape Verde, Portugal: Azores";
                case Plus0:
                    return "Burkina Faso, Gambia, Ghana\n" +
                            "Liberia, Mali\n" +
                            "Mauritania, Sierra Leone\n" +
                            "Spain: Canary Islands\n" +
                            "United Kingdom";
                case Plus1:
                    return "Austria, Belgium, Czech Republic, Denmark,\n" +
                            "France (metropolitan), Germany, Italy,\n" +
                            "Kosovo, Liechtenstein, Luxembourg,\n" +
                            "Malta, Netherlands, Serbia,\n" +
                            "Spain (most), Sweden, Switzerland,\n" +
                            "Central African Republic, Morocco, Niger";
                case Plus2:
                    return "Burundi, Egypt, Finland, Greece, Israel,\n" +
                            "Lebanon, Libya, Palestine, Romania,\n" +
                            "Russia: Kaliningrad, Rwanda,\n" +
                            "South Africa (most), Ukraine (most)";
                case Plus3:
                    return "Bahrain, Belarus, Ethiopia, Iraq,\n" +
                            "Jordan, Kenya, Kuwait, Madagascar,\n" +
                            "Qatar, Russia (most of European part),\n" +
                            "Saudi Arabia, Somalia, Syria, Tanzania,\n" +
                            "Turkey, Uganda, Yemen";
                case Plus3_30:
                    return "Iran";
                case Plus4:
                    return "Armenia, Azerbaijan, Georgia, Oman\n" +
                            "Russia: Astrakhan, Samara, Saratov, Udmurtia, Ulyanovsk;\n" +
                            "Seychelles, UAE";
                case Plus4_30:
                    return "Afghanistan";
                case Plus5:
                    return "Kazakhstan, Maldives, Pakistan,\n" +
                            "Russia: Bashkortostan, Chelyabinsk, Khanty-Mansi;\n" +
                            "Tajikistan, Turkmenistan, Uzbekistan";
                case Plus5_30:
                    return "India, Sri Lanka";
                case Plus5_45:
                    return "Nepal";
                case Plus6:
                    return "Bangladesh, Bhutan, Kyrgyzstan,\n" +
                            "Russia: Omsk";
                case Plus6_30:
                    return "Cocos (Keeling) Islands, Myanmar";
                case Plus7:
                    return "Cambodia, Christmas Island,\n" +
                            "Indonesia: Sumatra, Java, West Kalimantan;\n" +
                            "Laos, Mongolia: Bayan-Ölgii, Khovd, Uvs;\n" +
                            "Russia: Altai Krai, Altai Republic, Kemerovo;\n" +
                            "Thailand, Vietnam";
                case Plus8:
                    return "Australia: Western Australia (most), China,\n" +
                            "Hong Kong, Indonesia: South Kalimantan, East Kalimantan;\n" +
                            "Macau, Malaysia, Mongolia (most), Philippines,\n" +
                            "Russia: Buryatia, Irkutsk; Singapore, Taiwan";
                case Plus8_45:
                    return "Australia: Eucla";
                case Plus9:
                    return "Indonesia: Maluku Islands, Western New Guinea;\n" +
                            "Japan, North Korea, Palau,\n" +
                            "Russia: Amur, Sakha (most), Zabaykalsky;\n" +
                            "South Korea";
                case Plus9_30:
                    return "Australia: Northern Territory, South Australia, Yancowinna County";
                case Plus10:
                    return "Australia: Queensland, Australian Capital Territory,\n" +
                            "Jervis Bay Territory, New South Wales (most),\n" +
                            "Tasmania, Victoria;\n" +
                            "Micronesia: Chuuk, Yap;\n" +
                            "Northern Mariana Islands;\n" +
                            "Papua New Guinea (most);\n" +
                            "Russia: Jewish, Khabarovsk, Primorsky, Sakha (central-east)";
                case Plus10_30:
                    return "Australia: Lord Howe Island";
                case Plus11:
                    return "Micronesia: Kosrae, Pohnpei; New Caledonia,\n" +
                            "Norfolk Island, Papua New Guinea: Bougainville;\n" +
                            "Russia: Magadan, Sakha (east), Sakhalin;\n" +
                            "Solomon Islands, Vanuatu\n";
                case Plus12:
                    return "Fiji, Kiribati: Gilbert Islands;\n" +
                            "Marshall Islands, New Zealand (most),\n" +
                            "Russia: Chukotka, Kamchatka;\n" +
                            "Wake Island, Wallis and Futuna";
                case Plus12_45:
                    return "New Zealand: Chatham Islands";
                case Plus13:
                    return "Kiribati: Phoenix Islands; Samoa,\n" +
                            "Tokelau, Tonga";
                //case Plus14:
                default:
                    return "Kiribati: Line Islands";
            }
        } catch (Exception ignored) {
            return "";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static <T> LocalTime offset(T value) {
        TimeBelt belt = value instanceof TimeBelt ? (TimeBelt) value : from(String.valueOf(value));

        switch (belt) {
            case Minus12:
                return LocalTime.of(-12, 0);
            case Minus11:
                return LocalTime.of(-11, 0);
            case Minus10:
                return LocalTime.of(-10, 0);
            case Minus9_30:
                return LocalTime.of(-9, 30);
            case Minus9:
                return LocalTime.of(-9, 0);
            case Minus8:
                return LocalTime.of(-8, 0);
            case Minus7:
                return LocalTime.of(-7, 0);
            case Minus6:
                return LocalTime.of(-6, 0);
            case Minus5:
                return LocalTime.of(-5, 0);
            case Minus4:
                return LocalTime.of(-4, 0);
            case Minus3_30:
                return LocalTime.of(-3, 30);
            case Minus3:
                return LocalTime.of(-3, 0);
            case Minus2:
                return LocalTime.of(-2, 0);
            case Minus1:
                return LocalTime.of(-1, 0);
            case Plus0:
                return LocalTime.of(0, 0);
            case Plus1:
                return LocalTime.of(1, 0);
            case Plus2:
                return LocalTime.of(2, 0);
            case Plus3:
                return LocalTime.of(3, 0);
            case Plus3_30:
                return LocalTime.of(3, 30);
            case Plus4:
                return LocalTime.of(4, 0);
            case Plus4_30:
                return LocalTime.of(4, 30);
            case Plus5:
                return LocalTime.of(5, 0);
            case Plus5_30:
                return LocalTime.of(5, 30);
            case Plus5_45:
                return LocalTime.of(5, 45);
            case Plus6:
                return LocalTime.of(6, 0);
            case Plus6_30:
                return LocalTime.of(6, 30);
            case Plus7:
                return LocalTime.of(7, 0);
            case Plus8:
                return LocalTime.of(8, 0);
            case Plus8_45:
                return LocalTime.of(8, 45);
            case Plus9:
                return LocalTime.of(9, 0);
            case Plus9_30:
                return LocalTime.of(9, 30);
            case Plus10:
                return LocalTime.of(10, 0);
            case Plus10_30:
                return LocalTime.of(10, 30);
            case Plus11:
                return LocalTime.of(11, 0);
            case Plus12:
                return LocalTime.of(12, 0);
            case Plus12_45:
                return LocalTime.of(12, 45);
            case Plus13:
                return LocalTime.of(13, 0);
            //case Plus14:
            default:
                return LocalTime.of(14, 0);
        }
    }

}
