# MyToysTechTest
I decided to use a DrawerLayout and Listfragments to create the menu structure.

My App uses an Activity with a DrawerLayout and a Webview. The DrawerLayout contains a framlayout to be replaced by a
listfragment that inflates the menu when the data is loaded from the server.
I am using volley to connect to the Server and to download the data. Once finished MenuFragment is added to the drawer and 
the data is passed as a bundle.
In the fragments onActivitycreated method I call a function that recursivly adds the navigationEntries to a custom adapter.
when the user clicks on a menu item depending on the type of the item I open a new fragment as sub menu and add the current one to 
the backstack or I call a function from the MainActivity to open a link in the webview.
Beside of basic testing I was considering doing testing using Esspresso but ended up being a bit short on time..
