# Gamekins IntelliJ Plugin

This is the repository of the IntelliJ plugin for [Gamekins](https://github.com/jenkinsci/gamekins-plugin) and allows 
interactions with Gamekins directly in the IDE.

## Installation

- Using IDE built-in plugin system (in the future):
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "IntelliGame"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the repository and run the gradle intelliJ/buildPlugin task. 
By simply dragging and dropping the resulting zip file over an IntelliJ window, the plugin can be installed.

## Login

After successfully installing the plugin, a new entry on the right tab bar of IntelliJ with the name **Gamekins** is 
visible. Upon opening the tab, the login mask is shown with the following fields to fill in:

- Username: The username used to login to Jenkins by the user
- Password: The password used to login to Jenkins by the user
- URL: The URL Jenkins is reachable
- Project: The project the user wants to connect to. If nested into folders, separate each level by a "/".

Please be aware that the plugin does only work with Gamekins version 0.6 and above.