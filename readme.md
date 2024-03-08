# Dogs Api

## Brief

### 📱Specification
Develop using Jetpack Compose
The first screen should request a list of dog breeds from the Dogs API (https://dog.ceo/dog-api/) and present the result in a scrolling list.
Tapping a breed from the first list should present the second screen.
The second screen should show 10 random dog images of the selected breed.
Please zip and send the project when you're done, and include a README containing a brief description of your implementation.

### ✅ Acceptance Criteria
As a user running the application I can select breed from the list So that I can view pictures of that breed
- **Scenario:** Viewing the breed list When I launch the app Then I see a list of dog breeds
- **Scenario:** Viewing pictures of breed Given I have launched the app When I select a breed from the list Then I see 10 images of the breed

### 🎨 Making your mark
We'd love to see you make any nice touches that showcase your creative ability, we haven't provided any designs for this reason. 
However, please remember that the most important thing is that the application works as outlined above and is structured.

The test should be sent back by sharing it through one of code collaboration tools GitHub, Bitbucket, Gitlab.


## Author's Comments

### Development approach
I approached this project in steps that felt comfortable to me.

First I read the API, to get an understanding if there were any authentication complications, what
the shape of the data I'll be receiving is, and what the endpoints are.

Then I built the main UI Composables, using Previews to quickly block out my first thoughts and defining the
data I'd need for presentation. I iterated on these designs later as I added loading, refresh and error elements.

I then built out the networking and data layer using a Repository/DataSource/Service pattern; a bit 
overkill for the nature of the project but it's a way I'm comfortable with working. I opted to see
how far I could get with coroutines and Retrofit and was pleasantly surprised at how little extra framework
was required for simple requests like this. 
Didn't even need to use Flows - `suspend fun` and `async` seemed to cover everything.

With the Repository providing the data available from the API and the Composables defining what was needed
for presentation the last major architecture step was to bring them together. 
I've been using the Android architecture component ViewModel for my side-projects as a convenient way to 
manage persistence through configuration changes and to provide a lifecycle-scoped coroutine scope,
so used that here too. The ViewModel manages the screen's state, performs the initial data loading,
handles Events sent by the UI, and maps the repository data models to ViewState.

Navigation between the screens is based on Jetpack Compose navigation with some wrappers that I built 
for a side-project that make it convenient to provide through Dagger Hilt and improve the type-safety 
of the navigation framework.

I wrote a basic set of tests for the BreedsListViewMode and DogPicsViewModel. I've not used Mockk before,
so I took this on as an extra little challenge to see how it fared compared to Mockito. I time-boxed 
my test writing to avoid going down rabbit holes with a new framework, and whilst I've implemented
general coverage there's more that could be done with properly verifying sequences of state changes,
eg transitioning through the refresh behaviour. I would probably refactor the tests to be parameterized 
tests if I were to expand them in that way to reduce the repetition between cases and make it more
manageable to maintain them.

### Stretch Goal
Instead of pushing further with the unit tests I wanted to something fun and added an example photo 
to the breed list items, which was a fun little test of my networking and async setup. 

I found the breeds lazy list performance in debug mode dropped dramatically when I added an image to each element,
but when I went to profile the screen the performance dramatically improved. I believe this is down to 
the lack of compiler optimisations with debug builds, known to be an issue with lazy lists.

I was very tempted to go further and add a simple search filter to the breeds list screen - 
the 98 results would benefit from a bit of filtering. However, I'd already spent enough time and figured I should probably
look to complete the test and not get too far beyond the brief.

### Organisation
In terms of project organisation, I grouped ViewModel and associated screen together in a "features" package
as that made navigating between these two coupled elements easier. One could also pull out the various data classes
associated with the screen into the package; I like to define them in-file as I build the UI for ease of 
iteration in my own projects, but obviously adapt to whatever the practice is in a professional setting.

In `/ui/components` I've put Composables that are shared between the features, and in `/utils` I've put a couple
of functions that I've developed for use on my side-projects that simplify providing and consuming state from ViewModels.

Everything between the Repository and the API-calling service is contained in `/data`, and the
simple dagger modules providing the repository and api service singletons are in `/di`. 

### Screenshots
<img src="https://github.com/Ninjars/DogsApiDemo/assets/5053926/5460bb87-9472-4ec4-9e71-11556722f369" width="100">
<img src="https://github.com/Ninjars/DogsApiDemo/assets/5053926/a9bc039e-90eb-4677-88b5-6c950362cd61" width="100">
<img src="https://github.com/Ninjars/DogsApiDemo/assets/5053926/94e163c2-520f-4c28-baaa-6c390e806235" width="100">
<img src="https://github.com/Ninjars/DogsApiDemo/assets/5053926/fa18733c-9e86-459c-baf0-9274498fe169" height="100">









