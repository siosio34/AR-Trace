<?php
	error_reporting(E_ALL);
        ini_set("display_errors", 1);

	$json = file_get_contents('php://input');
	$jsonArray = json_decode($json, true);
	$jsonArray = $jsonArray["nameValuePairs"];
	$registrationIds = array($jsonArray["userToken"]);	
	unset($jsonArray["userToken"]);
	$bodyJson = json_encode($jsonArray);

	$msg = array(
		'body'  => $bodyJson,
		'title'     => "AR-Trace",
		'vibrate'   => 1,
		'sound'     => 1,
	);
	$fields = array(
		'registration_ids'  => $registrationIds,
		//'notification'      => $msg,
		'data' => array(
			'message'=>$bodyJson,
		),
    	);

	$headers = array(
		'Authorization: key=AAAAyxTINDE:APA91bHzjZ8fckcj0DHb-6kpJ9nye_JysPJRRXq92Mc5n5uRXk42Q7mY-FiXa6HN5LRbZZV3HQGAxoV2fdqXP56MqpEGEbVlOEem-tG2RLNjsKK8SUBBlHD8bDttTibLRHF6MpGQ1XOlDEDFn3M6d9nwXB9C_1aWDw',
		'Content-Type: application/json'
    	);

	$ch = curl_init();
	curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
	curl_setopt( $ch,CURLOPT_POST, true );
	curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
	curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
	curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
	curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
	$result = curl_exec($ch );
	curl_close( $ch );
	echo $result;
?>
