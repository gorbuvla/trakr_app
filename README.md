# Trakr.
Trakr. is a simple app that lets you track your activities.

## Overall
Purpose of the project is to try out some Android-related libraries/frameworks,
app is not fully reactive, RxJava is used only to deal with concurrency.
<div style="display: inline-block">
  <img src="/screenshots/screen_login.png?raw=true" alt="alt text" width="200" height="350">
</div>

<div style="display: inline-block; word-spacing: 10">
  <img src="/screenshots/screen_form.png?raw=true" alt="alt text" width="200" height="350">
</div>
<div style="margin-left:10"></div>

<div style="display: block;">
  <img src="/screenshots/screen_list.png?raw=true" alt="alt text" width="200" height="350">
</div>

<div style="display: block">
  <img src="/screenshots/screen_detail.png?raw=true" alt="alt text" width="200" height="350">
</div>


### Disclaimer
Not working with play services v11.0.55, tested with v10.2.98
Place picker crashes with v11.0.55, place auto complete works fine, still very slow (see Quirks)

## Installation
1. clone the project
2. setup firebase add put google-services.json into app/ dir.
3. get Google Places API key and put it into string resources.
4. build and install

### Quirks
1. Time picker is a bit dull, for the sake of simplicity available pick range is 00:00 - 23:59
2. Place auto complete is slow
3. Place auto complete shows slightly irrelevant results

## TODO
1. Proper state maintainer mechanism
2. Back navigation stack
3. Dimensions for different dpi screens
4. "Internet connection required" dialog
5. Auth re-signin on token expiration, etc.

### The bad stuff...
StorageService.java is some sort of stub only. 
There are 3 scopes defined in the app: 

@FragmentScope -(component depends on)-> @ActivityScope -(component depends on)-> @Singleton (app scope)

fragment scoped component cannot be dependant on 2 scoped components 

(RemoteDBService & LocalDBService) are needed in fragment scoped (data fetching, CRUD) 
and activity scoped(create user record on successful registration) components

RDBService and LDBService are provided from appModule and then wrapped into StorageService for future usage in FragmentScope'd code.

## Things that took some time
1. Place Picker is not working with play services v11.0.55, took me a couple of days to figure it out and try older version. Still, not solved...
2. Implement listView with dynamic sorted (by timestamp) data with pagination, fetching data from multiple sources

### ListView 
exactly N sorted items (or less) are loaded from each data source -> merge -> take first N (or less) ->
load static map images -> add to listview

-> if loaded more than N (combined/merged), then put rest into threshold -> update starting/ending points

-> if scrolled to end of current displayed data, then load more

Drawback -> let's say there are 100 remote and 20 local records and all local happened before all remote ones.
Current implementation will keep all 20 local records in threshold and display them only if user scrolls to the end.


