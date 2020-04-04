These files can be used to create your own copy of android-netspoof.
To create the debian.img image, follow the instructions in 'Howto create image.txt' on a LINUX computer. Windows users will probably want to download the prebuilt version.

Once this is created, put this image along with all of the files in 'android-scripts' into a folder on the SD card called 'android-netspoof'. Make sure these files are directly in this folder, not in a subfolder.
Then copy the *folders* 'rewriters', 'other' and 'img-files' into the android-netspoof folder. The file structure should look like this:
<sdcard>/
	android-netspoof/
		config
		install
		setup
		start
		img-files/
			chroot-setup
			spoof
			spoof-clean
		other/
			squid.conf
		rewriters/
			flip.pl
			blur.pl

Once the files are copied across, eject the USB storage, and either in a terminal emulator (or Connectbot) on the phone, or through 'adb shell' from the computer (you will need the Android SDK for this), run the following:

Firstly, turn in Wifi and connect to the internet. A few more files need to be downloaded first.
Commands beginning with '#' are comments.

# Get root priveledges
su
# Enter the android-netspoof folder
cd /sdcard/android-netspoof
# Setup the image with the base system, correct programs, scripts and settings with this command:
. ./setup
# (dot space dot slash 'setup')

This will take a long time, so plug in the phone for more power.
Once it is finished, run the script 'install':
. ./install

This will allow you to type 'startspoof' at the command line on the phone to start android-netspoof.
