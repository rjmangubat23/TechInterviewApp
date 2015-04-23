package com.techinterview.elementz.helpers;

import android.graphics.Bitmap;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.io.ByteArrayOutputStream;

public class Utility {
    //convert bitmap to Byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
/*
    public static Bitmap getPhoto(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
*/

    //This is for the auto computation of age, Note: LocalDate is a datatype of JODA lib
    public static int getAge(LocalDate dateOfBirth) {

       // LocalDate now = new LocalDate(DateTimeZone.getDefault());
        LocalDate now = new LocalDate();
        Years age = Years.yearsBetween(dateOfBirth, now);

        return age.getYears();
    }

    //This is for the autocomputation of next soonest birthday, Note: LocalDate is a datatype of JODA lib
    public static long getNextBirthday(LocalDate dateOfBirth){

       // LocalDate currDate = new LocalDate(DateTimeZone.getDefault());
        LocalDate currDate = new LocalDate();
        LocalDate dateTimeBday = new LocalDate(dateOfBirth);
        Days nextBirthday;

        //if birthday already finished use next soonest birthday instead
        if(dateTimeBday.getMonthOfYear()  <  currDate.getMonthOfYear()){
            dateTimeBday = dateTimeBday.withYear(currDate.getYear()).plusYears(1);
            nextBirthday = Days.daysBetween(dateTimeBday, currDate);
        }
        else{
            //Set year of birthday to current year
            dateTimeBday = dateTimeBday.withYear(currDate.getYear());
            nextBirthday = Days.daysBetween(currDate, dateTimeBday);

        }

       return nextBirthday.getDays();
    }


}
