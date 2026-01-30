// // import { browser } from '$app/environment';
// // import { getMessaging, onMessage } from 'firebase/messaging';
// // import { app } from './firebase';

// // export function listenForegroundMessages() {
// // 	// 수정: SSR 차단
// // 	if (!browser) return;

// // 	const messaging = getMessaging(app);

// // 	onMessage(messaging, (payload) => {
// // 		console.log('FCM foreground message:', payload);
// // 	});
// // }

// // lib/fcm/messaging.ts
// import { getMessaging, getToken, onMessage } from 'firebase/messaging';
// import { app } from './firebase';

// const VAPID_KEY = import.meta.env.VITE_FIREBASE_VAPID_KEY;

// /**
//  * 🔔 알림 권한 요청 + FCM 토큰 발급
//  */
// export async function requestPermissionAndGetToken(): Promise<string | null> {
// 	if (!('Notification' in window)) {
// 		console.warn('이 브라우저는 알림을 지원하지 않습니다.');
// 		return null;
// 	}

// 	const permission = await Notification.requestPermission();
// 	if (permission !== 'granted') {
// 		console.warn('알림 권한이 거부되었습니다.');
// 		return null;
// 	}

// 	try {
// 		const messaging = getMessaging(app);
// 		const token = await getToken(messaging, {
// 			vapidKey: VAPID_KEY
// 		});

// 		if (!token) {
// 			console.warn('FCM 토큰 발급 실패');
// 			return null;
// 		}

// 		return token;
// 	} catch (error) {
// 		console.error('FCM 토큰 요청 중 오류', error);
// 		return null;
// 	}
// }

// /**
//  * 📩 포그라운드 메시지 수신
//  */
// export function listenForegroundMessages(callback: (payload: any) => void) {
// 	const messaging = getMessaging(app);

// 	return onMessage(messaging, (payload) => {
// 		console.log('포그라운드 메시지 수신:', payload);
// 		callback(payload);
// 	});
// }
