package eu.applabs.crowdsensingfitnesslibrary.util;

import android.content.Context;

import eu.applabs.crowdsensingfitnesslibrary.R;

public class QuestionChecker {

    public enum QuestionType {
        Undefined,
        Steps,
        Activity_Duration,
        Activity_Count,
        Heart_Rate
    }

    public static QuestionType check(Context context, String string) {
        if(context != null && string != null) {
            String temp = string.toLowerCase();

            String[] steps_keywords = context.getResources().getStringArray(R.array.steps_keywords);
            String[] heart_rate_keywords = context.getResources().getStringArray(R.array.heart_rate_keywords);

            String[] activity_keywords = context.getResources().getStringArray(R.array.activity_keywords);
            String[] duration_keywords = context.getResources().getStringArray(R.array.duration_keywords);
            String[] count_keywords = context.getResources().getStringArray(R.array.count_keywords);

            boolean activity_keyword_found = false;
            boolean duration_keyword_found = false;
            boolean count_keyword_found = false;

            for(int i = 0; i < steps_keywords.length; ++i) {
                if(temp.contains(steps_keywords[i])) {
                    return QuestionType.Steps;
                }
            }

            for(int i = 0; i < heart_rate_keywords.length; ++i) {
                if(temp.contains(heart_rate_keywords[i])) {
                    return QuestionType.Heart_Rate;
                }
            }

            for(int i = 0; i < activity_keywords.length; ++i) {
                if(temp.contains(activity_keywords[i])) {
                    activity_keyword_found = true;
                    break;
                }
            }

            for(int i = 0; i < duration_keywords.length; ++i) {
                if(temp.contains(duration_keywords[i])) {
                    duration_keyword_found = true;
                    break;
                }
            }

            for(int i = 0; i < count_keywords.length; ++i) {
                if(temp.contains(count_keywords[i])) {
                    count_keyword_found = true;
                    break;
                }
            }

            // Evaluation

            if(activity_keyword_found && duration_keyword_found) {
                return QuestionType.Activity_Duration;
            }

            if(activity_keyword_found && count_keyword_found) {
                return QuestionType.Activity_Count;
            }
        }

        return QuestionType.Undefined;
    }
}
