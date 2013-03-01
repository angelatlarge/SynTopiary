<?php

	$message = 
			$_POST['name'] . "\n"
		. 	$_POST['email'] . "\n"
		. 	$_POST['feedback'] . "\n"
		. 	"\n" . print_r($_POST, true) . "\n"
		. 	"\n" . print_r($_GET, true) . "\n";
	mail ( 
		"kirills@mit.edu", 
		"Syntopiary feedback", 
		$message);
		
?>
<html>
<head>
</head>
<body>
feedback maybe submitted?
</body>
</html>