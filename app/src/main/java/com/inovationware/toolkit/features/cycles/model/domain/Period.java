package com.inovationware.toolkit.features.cycles.model.domain;


import com.inovationware.toolkit.features.code.domain.Language;

public enum Period {
    First,
    Second,
    Third,
    Fourth,
    Fifth,
    Sixth,
    Seventh;

    public static String toCanonicalString(int i){
        switch (i){
            case 1:
                return First.name();
            case 2:
                return Second.name();
            case 3:
                return Third.name();
            case 4:
                return Fourth.name();
            case 5:
                return Fifth.name();
            case 6:
                return Sixth.name();
            case 7:
                return Seventh.name();
        }
        throw new IllegalArgumentException("Invalid information!");
    }

    public static String to(Period period, Cycle cycle, Language language) {
        switch (language){
            case English:
                return period.name() + " period of the " + cycle.name() + " Cycle";
            case Yoruba:
                return nameToYoruba(period) + " akoko ti awọn " + cycleToYoruba(cycle) + " Yiyipo";
            /*case Bulgarian:
                return nameToBulgarian(period) + " период на " + cycleToBulgaria(cycle) + " Цикъл";*/
        }
        throw new IllegalArgumentException("Invalid information!");
    }

    private static String cycleToBulgaria(Cycle cycle){
        switch (cycle){
            case Health:
                return "здраве";
            case Personal:
                return "Лична";
            case Business:
                return "Бизнес";
            case Soul:
                return "Душа";
        }
        throw new IllegalArgumentException("Invalid Period!");
    }
    private static String cycleToYoruba(Cycle cycle){
        switch (cycle){
            case Health:
                return "Ilera";
            case Personal:
                return "Ti ara ẹni";
            case Business:
                return "Iṣowo";
            case Soul:
                return "Ọkàn";
        }
        throw new IllegalArgumentException("Invalid Period!");
    }

    private static String nameToBulgarian(Period period){
        switch (period){
            case First:
                return "Първо";
            case Second:
                return "Второ";
            case Third:
                return "трето";
            case Fourth:
                return "Четвърто";
            case Fifth:
                return "Пето";
            case Sixth:
                return "Шесто";
            case Seventh:
                return "Седмо";
        }
        throw new IllegalArgumentException("Invalid Period!");
    }

    private static String nameToYoruba(Period period){
        switch (period){
            case First:
                return "Akọkọ";
            case Second:
                return "Keji";
            case Third:
                return "Kẹta";
            case Fourth:
                return "Ẹkẹrin";
            case Fifth:
                return "Karun";
            case Sixth:
                return "Ẹkẹfa";
            case Seventh:
                return "Keje";
        }
        throw new IllegalArgumentException("Invalid Period!");
    }

    public static Period from(int period){
        switch (period){
            case 1:
                return Period.First;
            case 2:
                return Period.Second;
            case 3:
                return Period.Third;
            case 4:
                return Period.Fourth;
            case 5:
                return Period.Fifth;
            case 6:
                return Period.Sixth;
            case 7:
                return Period.Seventh;
        }
        throw new IllegalArgumentException("Invalid Period!");
    }
}
