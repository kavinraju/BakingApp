package skr_developer.bakingapp.Fragments;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import skr_developer.bakingapp.CustomViewAction;
import skr_developer.bakingapp.HomeActivity;
import skr_developer.bakingapp.IdilingResource.SimpleIdlingResource;
import skr_developer.bakingapp.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
/*
   * HomeActivity is launched using ActivityTestRule.
   *@Before init_Fragment_IdilingResource -
           fragment RecipeListFragment is attached
           idlingResource instance is set and then registered

   *@Test check_ToolbarText -
           check if toolbar contains the text "Recipies"

   *@Test check_FABisDisplayed -
           check if FAB with ID fab is displayed

   *@Test runTestOnRecyclerviewItem -
           item at position 1 is clicked

   *@After unregisterIdlingResource -
           idlingResource is unregistered
 */
@RunWith(AndroidJUnit4.class)
public class RecipeFragmentTest {

    private HomeActivity homeActivity = null;
    private RecipeListFragment recipeListFragment = new RecipeListFragment();
    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<HomeActivity> activityActivityTestRule = new ActivityTestRule<>(HomeActivity.class);

    @Before
    public void init_Fragment_IdilingResource() {
        homeActivity = activityActivityTestRule.getActivity();
        homeActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_home_recipe, recipeListFragment, "recipe_home_frag").commit();

        idlingResource = new SimpleIdlingResource(recipeListFragment);
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Test
    public void check_ToolbarText(){
        onView(allOf(instanceOf(TextView.class),withParent(withId(R.id.toolbar))))
                .check(matches(withText("Recipies")));
    }

    @Test
    public void check_FABisDisplayed(){
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
        //onView(withId(R.id.fab)).perform(click());
    }

    @Test
    public void runTestOnRecyclerviewItem() {
      onView(withId(R.id.recyclerview_home)).perform(
              RecyclerViewActions.actionOnItemAtPosition(1,CustomViewAction.clickViewWithID(R.id.iv_recipecard)));
    }
    /*
    @Test
    public void checkToolBarText(){
        onView(withId(R.id.toolbar))
               .check(matches(hasDescendant(withText("Brownies"))));
        }*/

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}