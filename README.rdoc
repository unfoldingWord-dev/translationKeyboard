Translation Keyboard 1.0
--

A GUI interface to build your own keyboard that can be deployed to a mobile device.

###Contributing

**Minimum Ruby Version**: 1.9.3p545
**Minimum Rails Version**: 4.1.5
**Database**: PostgresSQL 9.3
**IDE**: Anything you want, even just a text editor. RubyMine is the best IDE I have found.


###Setup

If you are familiar with writing Rails code there should not really be anything out of the ordinary to set this up app.

If your Rails environment is setup you should be able to run the following commands and then be up and running

1. bundle install
2. rake db:load

That's it!

###Import Hackers Keyboard Files

In the bin folder there is a file called importHackersKeyboardXml. Change the directory path on line 11 to point to the directory on your local machine and then run the script by typing ruby importHackersKeyboardXml.

Running this will automatically import all of the keyboards contained in the hacker_keyboard_data folder