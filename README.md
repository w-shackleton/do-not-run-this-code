# Do not run this code

This repo is a collection of all the code I wrote during secondary school. The
oldest code in this repo is from when I was 13.

## roughly-2008/rsa-crypto

An ill-fated program to encrypt and decrypt messages using RSA. This software
directly encrypts the message using raw RSA, which is woefully insecure and
easy to crack :)

This in itself would be fine, however I published this software on the
internet. If I recall correctly it got several thousand downloads..

## roughly-2009/balman

This was the first C code I wrote. An (attempted) implementation of the game
[Bao](https://en.wikipedia.org/wiki/Bao_(game))

## roughly-2009/bf2c

A Brainfuck to C "compiler"....

## roughly-2009/droidpad

Older iterations of [DroidPad](https://www.digitalsquid.co.uk/droidpad/).

* AndroidPad: original C# client (this is the first C# I wrote, oh boy does it show)
* droidpad-android: a much earlier version of the Android server.
* droidpad-linux: original C++ Linux GTK client (before I wrote a cross-platform client in wxWidgets C++)

## roughly-2009/example-c

Me figuring out how C and C++ work.

## roughly-2009/Mandelbrot proof

"A better written version of the Mandelbrot fractal". I don't think so ;)

## roughly-2009/patchworker

An older version of [Patchworker](https://www.digitalsquid.co.uk/patchworker/).

## roughly-2009/scripts

Check out bright.c. I used to have this file setuid root on my laptop. Little
did I know about input sanitisation ;)

## roughly-2009/image-stitch

A program that takes a set of images and stitches them together.

## roughly-2009/image-splitter

A program that takes an image and splits it into small pieces.

## roughly-2009/maze

An interactive click-to-move maze, written in PHP and SQL injection ;)

## roughly-2009/pennybottle

A fully implemented but never shipped multiplayer trading simulation game,
written in PHP.

## roughly-2010/bright-day

[Bright Day](https://www.digitalsquid.co.uk/brightday/)

Back before Android phones had light sensors, this app set your brightness to
bright in daytime, and dim at night.

## roughly-2010/gamev2

Some friends and I wanted to write a PC game. We... didn't get very far.

## roughly-2010/PhysicsLandEdit

This is a level editor for roughly-2011/Physics Land.

## roughly-2010/spaceedit

This is a level editor for roughly-2011/spacegame, written in C++.

## roughly-2010/sudoku

An incomplete Sudoku game.

## roughly-2011/android-netspoof

[Network Spoofer](https://www.digitalsquid.co.uk/brightday/) v1. This version
started life as a series of shell scripts packed into a Debian chroot ;)

## roughly-2011/pennysms

A webapp that aggregates user-suggested estimations via SMS message for a class

## roughly-2011/OWLRecorder

An attempt at recording home energy usage data from
[this home energy monitor solution](https://www.theowl.com/).

## roughly-2011/powermon

After realising that "TheOwl" home energy monitor mentioned above was (in my
opinion) unreliable and inaccurate I cut the end off the CT coil and attached
it to a Voltage divider and a microcontroller. This was much more interesting
as you could see the actual 50Hz waveform. I (with parental supervision)
drilled a hole through the wall in my parents' house so that I could wire this
up to the home server and collected per-second power usage for 2 years.

## roughly-2011/pennybottle

A fully implemented but never shipped multiplayer trading simulation game, this
time written in Java.

## roughly-2011/Physics Land

Some sort of physics simulation game. I don't think this got very far.

## roughly-2011/spacegame

A very-nearly completed game in which the player bounces around planets with
simulated gravity. I never published this game as my plan was to sell it:
perfect was clearly not better than done :(

## roughly-2012/contact-recall

A game to help you put names to faces. Never finished.

## roughly-2012/internet-restore

An unfinished but in my opinion fascinating project. This Android app built on
Network Spoofer to use ARP cache poisoning to bring internet back to your home
network when your ISP went down.

This app overrode Android's wifi manager to connect to Wifi and cellular data
at the same time, then used ARP cache poisoning to steal all traffic from the
Wifi network. It then routed it out over the cellular network.

tl;dr: your home internet goes down, you start this app up and you magically
have internet on your existing network again.

## roughly-2013/llamaorduck

A game that shows you a picture of a llama or duck and you have to guess which
one it is. Built in freshers week of university with
[Alex Bate](https://github.com/alexbate).

The fun part about this app is that someone tried to sue me over it; they also
had an app that showed you a picture of a llama or a duck, and mine was (a)
better (as measured by Google Play ratings) and (b) didn't have ads.
Fortunately they never got lawyers involved ;)
