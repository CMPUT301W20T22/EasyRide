
'use-strict'



const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotificationRider = functions.firestore.document("rider/{userEmail}/notification/{notificationId}").onWrite((change, context) => {
	const userEmail = context.params.userEmail;
	const notificationId = context.params.notificationId;

	return admin.firestore().collection("rider").doc(userEmail).collection("notification").doc(notificationId).get().then(queryResult => {
		const senderUserEmail = queryResult.data().senderUserEmail;
		const notificationMessage = queryResult.data().notificationMessage;

		const fromUser = admin.firestore().collection("driver").doc(senderUserEmail).get();
		const toUser = admin.firestore().collection("rider").doc(userEmail).get();

		return Promise.all([fromUser, toUser]).then(result => {
			//const fromUserName = result[0].data().Name;
			//const toUserName = result[1].data().Name;
			const tokenId = result[1].data().token;

			const notificationContent = {
				notification: {
					title: "Your request is accepted!",
					body: notificationMessage,
					icon: "default"
				}
			};

			return admin.messaging().sendToDevice(tokenId, notificationContent).then(result => {
				return console.log("Notification sent!");
				//admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).delete();
			});
		});
	});
});

exports.sendNotificationDriver = functions.firestore.document("driver/{userEmail}/notification/{notificationId}").onWrite((change, context) => {
	const userEmail = context.params.userEmail;
	const notificationId = context.params.notificationId;

	return admin.firestore().collection("driver").doc(userEmail).collection("notification").doc(notificationId).get().then(queryResult => {
		const senderUserEmail = queryResult.data().senderUserEmail;
		const notificationMessage = queryResult.data().notificationMessage;

		const fromUser = admin.firestore().collection("rider").doc(senderUserEmail).get();
		const toUser = admin.firestore().collection("driver").doc(userEmail).get();

		return Promise.all([fromUser, toUser]).then(result => {
			//const fromUserName = result[0].data().Name;
			//const toUserName = result[1].data().Name;
			const tokenId = result[1].data().token;

			const notificationContent = {
				notification: {
					title: "Request confirmed. You may proceed now.!",
					body: notificationMessage,
					icon: "default"
				}
			};

			return admin.messaging().sendToDevice(tokenId, notificationContent).then(result => {
				return console.log("Notification sent!");
				//admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).delete();
			});
		});
	});
});

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });



