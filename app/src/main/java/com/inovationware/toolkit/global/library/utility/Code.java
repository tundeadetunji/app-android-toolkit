package com.inovationware.toolkit.global.library.utility;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.code.domain.Language;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.inovationware.toolkit.global.domain.DomainObjects.DATA_TRANSFER_OBJECTS;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.TIME_UNITS;

public class Code {
    public static String content(TextView view){
        return view.getText().toString();
    }

    public static boolean content(CheckBox checkBox){
        return checkBox.isChecked();
    }

    public enum IDPattern {
        Short, Short_DateOnly, Short_DateTime, Long, Long_DateTime
    }

    public enum CommitDataTo {
        CSV, Sequel, Access, Excel, Hibernate
    }

    private enum AppendWith {
        Date, DateTime, Nothing
    }

    public enum FormatFor {
        Web, JavaScript, Custom, Nothing
    }

    public enum SideToReturn {
        Left, Right, AsArray, AsListOfString, AsListToString, AsCustomApplicationInfo, AsCustomApplicationInfoDisplayName, AsCustomApplicationInfoProcessName, AsCustomApplicationInfoFilename, AsCustomApplicationInfoInstallLocation
    }

    public enum SearchStringOperator {
        OR_, AND_, NOT_
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh-mm-ss-ms");

    /**
     * Constructs a GUID.
     *
     * @param prefix                  what to precede the final string with
     * @param append_with_date_string add a transformed date string to the final string
     * @param truncate                grabs the first 13 characters only
     * @return string of transformed GUID
     */
    public static String newGUID(String prefix, AppendWith append_with_date_string, boolean truncate) {
        StringBuilder result = new StringBuilder();
        result.append(prefix.length() > 0 ? prefix : "");
        result.append(result.toString().length() > 0 ? "-" : "");
        if (append_with_date_string == AppendWith.DateTime) {
            result.append(DATE_FORMAT.format(new Date(System.currentTimeMillis()))).append("-")
                    .append(TIME_FORMAT.format(new Date(System.currentTimeMillis()))).append("-");
        } else if (append_with_date_string == AppendWith.Date) {
            result.append(DATE_FORMAT.format(new Date(System.currentTimeMillis()))).append("-");
        }

        String id = UUID.randomUUID().toString();
        return result.append(truncate ? id.substring(0, 13) : id).toString();
    }

    /**
     * Constructs a GUID with a given pattern.
     *
     * @param pattern what pattern to construct the final string with
     * @param prefix  what to precede the final string with
     * @return string of transformed GUID
     */
    public static String newGUID(IDPattern pattern, String prefix) {
        String result = "";
        switch (pattern) {
            case Long:
                result = newGUID(prefix, AppendWith.Nothing, false);
                break;
            case Long_DateTime:
                result = newGUID(prefix, AppendWith.DateTime, false);
                break;
            case Short:
                result = newGUID(prefix, AppendWith.Nothing, true);
                break;
            case Short_DateOnly:
                result = newGUID(prefix, AppendWith.Date, true);
                break;
            case Short_DateTime:
                result = newGUID(prefix, AppendWith.DateTime, true);
                break;
        }
        return result;
    }

    /**
     * Constructs a GUID with a given pattern.
     *
     * @param pattern what pattern to construct the final string with
     * @return string of transformed GUID
     */
    public static String newGUID(IDPattern pattern) {
        String result = "";
        switch (pattern) {
            case Long:
                result = newGUID("", AppendWith.Nothing, false);
                break;
            case Long_DateTime:
                result = newGUID("", AppendWith.DateTime, false);
                break;
            case Short:
                result = newGUID("", AppendWith.Nothing, true);
                break;
            case Short_DateOnly:
                result = newGUID("", AppendWith.Date, true);
                break;
            case Short_DateTime:
                result = newGUID("", AppendWith.DateTime, true);
                break;
        }
        return result;
    }

    /**
     * Transforms a string by replacing escape characters (carriage return, tab, etc.) to
     * the appropriate characters depending on the intended output (defaults to web page).
     *
     * @param str_ the string to transform
     * @return string that is safe for direct use for the intended output
     */
    public static String prepareForIo(String str_) {
        return prepareForIO(str_, FormatFor.Web);
    }

    /**
     * Transforms a string by attempting to replace escape characters (carriage return, tab, etc.) to
     * the appropriate characters depending on the intended output specified by output_.
     *
     * @param str_    the string to transform
     * @param output_ the intended output
     * @return string that is safe for direct use for the intended output
     */
    public static String prepareForIO(String str_, FormatFor output_) {

        String trimmed_, CR_less, CRLFless, TABless;
        trimmed_ = str_.trim();
        str_ = trimmed_;

        if (output_ == FormatFor.Web) {
            CRLFless = str_.replace("\n", "<br />");
            str_ = CRLFless;
        } else if (output_ == FormatFor.JavaScript) {
            CRLFless = str_.replace("\n", "\n");
            str_ = CRLFless;
        }

        if (output_ == FormatFor.Web) {
            CR_less = str_.replace("\n", "<br />");
            str_ = CR_less;
        } else if (output_ == FormatFor.JavaScript) {
            CR_less = str_.replace("\n", "\n");
            str_ = CR_less;
        }

        if (output_ == FormatFor.Web) {
            TABless = str_.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
            str_ = TABless;
        } else if (output_ == FormatFor.JavaScript) {
            TABless = str_.replace("\t", "\t");
            str_ = TABless;
        }

        return str_;
    }

    /**
     * Combines a list of strings to form a single string joined with the characters specified by del.
     *
     * @param list the list to join together
     * @param del  what to join the strings with
     * @return a string that combines all the items in the original list
     */
    public static String listToString(List<String> list, String del) {
        assert list != null;
        assert del != null && !del.isEmpty() && !del.isBlank();

        StringBuilder r = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            r.append(list.get(i)).append(i < list.size() - 1 ? del : "");
        }
        return r.toString();
    }

    /**
     * Splits a string into tokens, each of which represents the string split along the characters specified
     * by delimiter.
     *
     * @param string    the string to split into tokens
     * @param delimiter what to split the string along
     * @return a list of strings
     */
    public static List<String> stringToList(String string, String delimiter) {
        return List.of(string.split(delimiter));
    }

    /**
     * Splits a string into tokens, each of which represents the string split along carriage return character.
     *
     * @param string the string to split into tokens
     * @return a list of strings
     */
    public static List<String> stringToList(String string) {
    /*
    M
    * */
        List<String> result = new ArrayList<>();
        String[] tokens = string.split("\n");
        for (String token : tokens) result.add(token);
        return result;
    }

    /*
    M
    * */
    public static List<String> stringToList(String string, int limit) {
        return string.trim().length() > 0 ? List.of(string.split("\n", limit)) : new ArrayList<>();
    }

    /*
    M
    * */
    public static boolean isNothing(String string){
        return string.isEmpty() || string.isBlank();
    }
    /*
     M?
     * */
    public static String clean(String string){
        //return string.replace("\n","").trim();
        return string.strip().trim();
    }



    /**
     * Splits a string into tokens, each of which represents the string split along what is specified from delimiter.
     *
     * @param string    the string to split into tokens
     * @param delimiter what to split the string along
     * @return a list of strings
     */
    public static List<String> splitTextInSplits(String string, String delimiter) {
        return List.of(string.split(delimiter));
    }

    /**
     * Splits a string into tokens, each of which represents the string split along what is specified from delimiter.
     *
     * @param string    the string to split into tokens
     * @param delimiter what to split the string along
     * @param how_many  how many tokens to return
     * @return a list of strings
     */
    public static List<String> splitTextInSplits(String string, String delimiter, int how_many) {
        return List.of(string.split(delimiter, how_many));
    }

    /**
     * Splits a string into 2 tokens, each of which represents the string split along what is specified from delimiter.
     *
     * @param string       the string to split into tokens
     * @param delimiter    what to split the string along
     * @param sideToReturn should it return the first token or the last token
     * @return a string that is the first or last token
     */
    public static String splitTextInTwo(String string, String delimiter, SideToReturn sideToReturn) {
        String[] splits = string.split(delimiter, 2);
        return sideToReturn == SideToReturn.Left ? splits[0] : splits[1];
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");

    /**
     * Checks if string is valid email.
     *
     * @param email the string to check
     * @return true if it's valid or false otherwise
     */
    public static boolean isEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Removes hypertext markup from text.
     *
     * @param text the HTML to strip of markup
     * @return string without HTML
     */
    public static String removeHtmlFromText(String text) {
        return text.replaceAll("(<.*?>)|(&.*?;)|([ ]{2,})", "").replaceAll("(<.*?>)|(&.*?;)", " ").replaceAll("\\s{2,}", " ");
    }

    public static boolean isPhraseOrSentence(String text) {
        assert !text.isEmpty() && !text.isBlank();
        return text.split(" ", 2).length >= 2;
    }

    private static String[] lastThreeLetters(String text) {
        if (text.trim().length() < 1) {
            return new String[]{};
        }

        return new String[]{String.valueOf(text.charAt(text.length() - 3)),
                String.valueOf(text.charAt(text.length() - 2)), String.valueOf(text.charAt(text.length() - 1))};
    }

    public static boolean isConsonant(String text) {
        boolean isConsonant = false;

        if (text.equals("b") || text.equals("c") || text.equals("d") || text.equals("f") || text.equals("g") || text.equals("h") || text.equals("j")
                || text.equals("k") || text.equals("l") || text.equals("m") || text.equals("n") || text.equals("p") || text.equals("q") || text.equals("r")
                || text.equals("s") || text.equals("t") || text.equals("v") || text.equals("w") || text.equals("x") || text.equals("y") || text.equals("z")
                || text.equals("B") || text.equals("C") || text.equals("D") || text.equals("F") || text.equals("G") || text.equals("H") || text.equals("J")
                || text.equals("K") || text.equals("L") || text.equals("M") || text.equals("N") || text.equals("P") || text.equals("Q") || text.equals("R")
                || text.equals("S") || text.equals("T") || text.equals("V") || text.equals("W") || text.equals("X") || text.equals("Y")
                || text.equals("Z")) {
            isConsonant = true;
        }
        return isConsonant;
    }

    public static boolean isVowel(String text) {
        boolean isVowel = false;
        if (text.equals("a") || text.equals("e") || text.equals("i") || text.equals("o") || text.equals("u") || text.equals("A") || text.equals("E")
                || text.equals("I") || text.equals("O") || text.equals("U")) {
            isVowel = true;
        }
        return isVowel;
    }

    public static boolean isAlphabet(String text) {
        boolean isAlphabet = false;

        if (isVowel(text) || isConsonant(text)) {
            isAlphabet = true;
        }
        return isAlphabet;
    }

    /**
     * Turns phrase or sentence to continuous tense, and appends it with specified text.
     * @param text phrase or sentence
     * @param suffix what to append to the output
     * @return string in continuous tense form
     */
    public static String toContinuousTense(String text, String suffix) {
        if (text.trim().length() < 1) {
            return "";
        }

        String prefx = "";
        String[] lastThree = lastThreeLetters(text);

        if (!isAlphabet(lastThree[0]) || !isAlphabet(lastThree[1])
                || !isAlphabet(lastThree[2])) {
            return "";
        }

        String a = lastThree[0].toLowerCase();
        String b = lastThree[1].toLowerCase();
        String c = lastThree[2].toLowerCase();


        if (a.equals("i") && b.equals("n") && c.equals("g")) {
            return text;
        }

        // If IsConsonant(a) And IsVowel(b) And IsConsonant(c) Then
        // prefx = text & Mid(text.Trim, text.Length, 1).Trim & "ing"
        // ElseIf b = "i" And c = "e" Then
        // prefx = Mid(text.Trim, 1, text.Length - 2).Trim & "ying"
        // ElseIf IsVowel(a) And IsConsonant(b) And c = "e" Then
        // prefx = Mid(text.Trim, 1, text.Length - 1).Trim & "ing"
        // Else
        // prefx = text.Trim & "ing"
        // End If

        if (isConsonant(a) && isVowel(b) && isConsonant(c)) {
            // swim, stop, run, begin
            prefx = text + text.substring(text.length() - 1).trim() + "ing";
        } else if (b.equals("i") && c.equals("e")) {
            //lie, die
            prefx = text.substring(0, text.length() - 2).trim() + "ying";
        } else if (isVowel(a) && isConsonant(b) && c.equals("e")) {
            //come, mistake
            prefx = text.substring(0, text.length() - 1).trim() + "ing";
        } else {
            prefx = text.trim() + "ing";
        }
        //mix, deliver

        // Return RTrim(prefx) & " " & LTrim(suffix)
        return prefx + " " + suffix;

    }

    /**
     * Turns phrase or sentence to continuous tense.
     * @param text phrase or sentence
     * @return string in continuous tense form
     */
    public static String ToContinuousTense(String text) {
        if (text.trim().length() < 1) {
            return "";
        }

        String prefx = "";
        String[] lastThree = lastThreeLetters(text);

        if (!isAlphabet(lastThree[0]) || !isAlphabet(lastThree[1])
                || !isAlphabet(lastThree[2])) {
            return "";
        }

        String a = lastThree[0].toLowerCase();
        String b = lastThree[1].toLowerCase();
        String c = lastThree[2].toLowerCase();


        if (a.equals("i") && b.equals("n") && c.equals("g")) {
            return text;
        }

        // If IsConsonant(a) And IsVowel(b) And IsConsonant(c) Then
        // prefx = text & Mid(text.Trim, text.Length, 1).Trim & "ing"
        // ElseIf b = "i" And c = "e" Then
        // prefx = Mid(text.Trim, 1, text.Length - 2).Trim & "ying"
        // ElseIf IsVowel(a) And IsConsonant(b) And c = "e" Then
        // prefx = Mid(text.Trim, 1, text.Length - 1).Trim & "ing"
        // Else
        // prefx = text.Trim & "ing"
        // End If

        if (isConsonant(a) && isVowel(b) && isConsonant(c)) {
            // swim, stop, run, begin
            prefx = text + text.substring(text.length() - 1).trim() + "ing";
        } else if (b.equals("i") && c.equals("e")) {
            //lie, die
            prefx = text.substring(0, text.length() - 2).trim() + "ying";
        } else if (isVowel(a) && isConsonant(b) && c.equals("e")) {
            //come, mistake
            prefx = text.substring(0, text.length() - 1).trim() + "ing";
        } else {
            prefx = text.trim() + "ing";
        }
        //mix, deliver, cradle, juggle

        // Return RTrim(prefx) & " " & LTrim(suffx)
        return prefx;
    }


    public static String createDefaultMessage(String prefix){
        return prefix + DEFAULT_ERROR_MESSAGE_SUFFIX;
    }

    public static String createDefaultErrorMessage(String prefix){
        return prefix + DEFAULT_FAILURE_MESSAGE_SUFFIX;
    }

    public static ArrayAdapter<String> dtoDropDownAdapter(Context context){
        return new ArrayAdapter<String>(context, R.layout.default_drop_down, DATA_TRANSFER_OBJECTS);

    }

    public static ArrayAdapter<String> configureTimeUnitDropDownAdapter(Context context){
        return new ArrayAdapter<String>(context, R.layout.default_drop_down, TIME_UNITS);
    }

    public static ArrayAdapter<String> configureInstalledAppsDropDownAdapter(Context context){
        return new ArrayAdapter<String>(context, R.layout.default_drop_down, DomainObjects.installedApps);
    }


    public static boolean responseIsValidString(String responseFromServer) {
        return !responseFromServer.contains("<html>") && !responseFromServer.contains("<title>");
    }

    public static boolean statusCodeIsAcceptable(int code) {
        return code >= 200 && code <= 299;
    }

    public static void hideControls(boolean isPreview, List<TextView> textViewsToHide, List<TextView> textViewsToShow, List<CardView> cardViewsToHide, List<CardView> cardViewsToShow, List<FloatingActionButton> floatingActionButtonsToHide, List<FloatingActionButton> floatingActionButtonsToShow){

        if(textViewsToHide != null){
            for(TextView view : textViewsToHide){
                if(isPreview){
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (textViewsToShow != null){
            for(TextView view : textViewsToShow){
                if(isPreview){
                    view.setVisibility(View.VISIBLE);
                }
            }
        }

        if (cardViewsToHide != null){
            for(CardView view : cardViewsToHide){
                if(isPreview){
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (cardViewsToShow != null){
            for(CardView view : cardViewsToShow){
                if(isPreview){
                    view.setVisibility(View.VISIBLE);
                }
            }
        }

        if (floatingActionButtonsToHide != null){
            for(FloatingActionButton view : floatingActionButtonsToHide){
                if(isPreview){
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }

        if(floatingActionButtonsToShow != null){
            for(FloatingActionButton view : floatingActionButtonsToShow){
                if(isPreview){
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public static String capitalize(String string){
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String formatOutput(Context context, String string, SharedPreferencesManager store, DeviceClient device){
        return store.shouldAppendUuidToOutput(context) ? string + formatSystemIdForOutput(context, device, store) : string;

    }

    public static String booleanToString(boolean value){
        return value ? "Yes" : "No";
    }

    public static String getTimezone(){
        TimeZone timeZone = TimeZone.getDefault();
        int offsetInMillis = timeZone.getOffset(System.currentTimeMillis());
        int offsetInHours = offsetInMillis / (1000 * 60 * 60);
        return timeZone.getID() + " (UTC" + (offsetInHours >= 0 ? "+" : "") + offsetInHours + ")";
    }
    private static String formatSystemIdForOutput(Context context, DeviceClient device, SharedPreferencesManager store){
        return "\n\nFrom " + store.getSender(context) + "\n(Android ID: " + device.getSystemId(context) + ")" + (store.shouldAppendTimezoneToOutput(context) ? "\nTimezone: " + getTimezone() : "");
    }

    public static String formatOutput(Context context, String string, SharedPreferencesManager store, DeviceClient device, Language dest){
        return store.shouldAppendUuidToOutput(context) ? string + formatSystemIdForOutput(context, device, store, dest) : string;
    }
    private static String formatSystemIdForOutput(Context context, DeviceClient device, SharedPreferencesManager store, Language dest){
        String result = "";
        if (dest == Language.English) result = "\n\nFrom " + store.getSender(context) + "\n(Android ID: " + device.getSystemId(context) + ")";
        //if (dest == Language.Bulgarian) return "\n\nот " + store.getSender(context) + "  (Идентификатор за Android: " + device.getSystemId(context) + ")";
        result = "\n\nLati " + store.getSender(context) + "\n(Idanimọ Android: " + device.getSystemId(context) + ")";

        if (store.shouldAppendTimezoneToOutput(context)){
            result += dest == Language.English ? "\nTimezone: " + getTimezone() : "\nAgo Agbègbè: " + getTimezone();
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String createTimestamp(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

        return timeFormatter.format(now) + ", " + dateFormatter.format(now);
    }
}
