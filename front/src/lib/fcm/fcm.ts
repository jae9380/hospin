// import { app } from './firebase';

// let messaging: any = null;

// // async 함수로 만들기
// export async function getBrowserMessaging() {
// 	if (typeof window === 'undefined') return null; // SSR이면 null 반환

// 	if (!messaging) {
// 		const { getMessaging } = await import('firebase/messaging'); // 이제 정상
// 		messaging = getMessaging(app);
// 	}

// 	return messaging;
// }

// export async function requestPermissionAndGetToken() {
// 	const mg = await getBrowserMessaging();
// 	if (!mg) return null;

// 	const { getToken } = await import('firebase/messaging');
// 	try {
// 		const permission = await Notification.requestPermission();
// 		if (permission !== 'granted') return null;

// 		const token = await getToken(mg, {
// 			vapidKey:
// 				'BER1hvItqu83xUEaToJPKmtZNotDY3SeyG3gu5mlv9fEFx4-1czR75xok6JRSbdg5u_p_lLQyIeBHP2U0lphQSI'
// 		});
// 		return token;
// 	} catch (err) {
// 		console.error('FCM token error:', err);
// 		return null;
// 	}
// }

// export async function listenForegroundMessages(callback: (payload: any) => void) {
// 	const mg = await getBrowserMessaging();
// 	if (!mg) return;

// 	const { onMessage } = await import('firebase/messaging');
// 	onMessage(mg, callback);
// }
