package telran.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

record MonthYear(int month, int year, int firstDay) {}

public class Main {
    public static void main(String[] args) {
        try {
            MonthYear monthYear = getMonthYear(args); //if no arguments current year and month should be applied
            printCalendar(monthYear);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printCalendar(MonthYear monthYear) {
        printTitle(monthYear);
        printWeekDays(monthYear);
        printDates(monthYear);
    }

    private static void printDates(MonthYear monthYear) {
        Main current = new Main();
        int firstWeekDay = current.getFirstDayOfWeek(monthYear);
        int lastDayOfMonth = current.getLastDayOfMonth(monthYear);
        int j = firstWeekDay;
        for (int i = 1; i <= lastDayOfMonth; i++) {
            if (i == 1) {
                System.out.printf("%" + current.getOffset(firstWeekDay) + "d", i);    
            } else if (j == 1) {
                System.out.printf("%3d", i);
            } else {
                System.out.printf("%4d", i);
            }
            if (j == 7) {
                System.out.print("\n");
                j = 0;
            }
            j++;
        }
    }

    private static void printWeekDays(MonthYear monthYear) {
        DayOfWeek firstDay = DayOfWeek.of(monthYear.firstDay());
        System.out.print(firstDay.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        for (int i = 1; i < 7; i++) {
            System.out.print(" " + firstDay.plus(i).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));    
        }
        System.out.print("\n");
    }

    private static void printTitle(MonthYear monthYear) {
        String str = LocalDate.of(monthYear.year(), monthYear.month(), 1).format(DateTimeFormatter.ofPattern("LLLL", Locale.ENGLISH));
        System.out.printf("%12d, %s\n", monthYear.year(), str);
    }

    private static MonthYear getMonthYear(String[] args) throws Exception {
        LocalDate localDate;
        MonthYear monthYear;

        if (args.length == 0) {
            localDate = LocalDate.now();
            monthYear = new MonthYear(localDate.get(ChronoField.MONTH_OF_YEAR), localDate.get(ChronoField.YEAR), 1);
        } else if (args.length == 1) {
            throw new Exception("Only one argument. Must be two or three");  
        } else if (args.length == 2 && checkArgs(args)) {
            monthYear = createMonthYear(args);  
        } else if (args.length > 3) {
            throw new Exception("More than three arguments. Must be two or three");
        } else if (checkArgs(args)) {
            monthYear = createMonthYear(args);
        } else {
            throw new Exception("Undefined error");
        }
        return monthYear;
    }

    private static boolean checkArgs(String[] args) throws Exception {
        boolean res = true;
        String strError = "";

        if (!args[0].matches("[1-9]|[1][0-2]|[0][1-9]")) {
            strError = "Incorrect month format. Can be used only numbers 1-12.";        
        }
        if (!args[1].matches("[0-9]{3}[1-9]")) {
            strError = strError + (strError.equals("") ? "" : "\n") + "Incorrect year format. Can be used only numbers 0001-9999";        
        }
        if (args.length == 3 && !args[2].matches("[1-7]")) {
            strError = strError + (strError.equals("") ? "" : "\n") + "Incorrect day of week format. Can be used only numbers 1-7";    
        }

        if (!strError.equals("")) {
            throw new Exception(strError);    
        }

        return res;
    }

    private static MonthYear createMonthYear(String[] args) {
        int month = Integer.parseInt(args[0].trim());
        int year = Integer.parseInt(args[1].trim());
        int firstDay = 1;
        if (args.length == 3) {
            firstDay = Integer.parseInt(args[2].trim());    
        }
        return new MonthYear(month, year, firstDay);
    }

    private int getFirstDayOfWeek(MonthYear monthYear) {
        return LocalDate.of(monthYear.year(), monthYear.month(), 1).getDayOfWeek().plus(8 - monthYear.firstDay()).getValue();
    }

    private int getOffset(int firstWeekDay) {
        return firstWeekDay * 3 + firstWeekDay - 1;
    }

    private int getLastDayOfMonth(MonthYear monthYear) {
        return LocalDate.of(monthYear.year(), monthYear.month(), 1).lengthOfMonth();
    }
}