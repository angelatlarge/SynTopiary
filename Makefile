upload:
	rsync dist/syntopiary/* kirills@athena.dialup.mit.edu:www/syntopiary/ "--exclude=*external-libs.jar"
	rsync dist/scripts/* kirills@athena.dialup.mit.edu:web_scripts/