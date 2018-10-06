package skr_developer.bakingapp;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;
/*
    This class is used for the click event of the items in the RecyclerView.
 */
public class CustomViewAction {
    public static ViewAction clickViewWithID(final int id){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "recipe image view";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View view1 = view.findViewById(id);
                view1.performClick();
            }
        };
    }
}
