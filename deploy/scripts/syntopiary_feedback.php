<?php

	$message = ""
		.	"Name: "
			. preg_replace('=((<CR>|<LF>|0x0A/%0A|0x0D/%0D|\\n|\\r)\S).*=i', null, $_POST['name'])
			. "\n"
		. 	"Email: " 
			. preg_replace('=((<CR>|<LF>|0x0A/%0A|0x0D/%0D|\\n|\\r)\S).*=i', null, $_POST['email'])
			. "\n"
		.	"Comments\n\n"
			. preg_replace('=((<CR>|<LF>|0x0A/%0A|0x0D/%0D|\\n|\\r)\S).*=i', null, $_POST['feedback'])
			. "\n"
		//~ . 	"\n" . print_r($_POST, true) . "\n"
		//~ . 	"\n" . print_r($_GET, true) . "\n"
		;
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