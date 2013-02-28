upload:
	rsync dist/* kirills@athena.dialup.mit.edu:www/syntopiary/ "--exclude=*external-libs.jar"