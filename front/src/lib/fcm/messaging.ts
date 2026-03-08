import { app } from './firebase';

let messaging: any = null;

export async function getBrowserMessaging() {
	if (typeof window === 'undefined') return null;

	if (!messaging) {
		const { getMessaging } = await import('firebase/messaging');
		messaging = getMessaging(app);
	}
	return messaging;
}

export async function requestPermissionAndGetToken(): Promise<string | null> {
	const mg = await getBrowserMessaging();
	if (!mg) return null;

	const { getToken } = await import('firebase/messaging');

	const permission = await Notification.requestPermission();
	if (permission !== 'granted') return null;

	return await getToken(mg, {
		vapidKey: import.meta.env.VITE_FIREBASE_VAPID_KEY,
		serviceWorkerRegistration: await navigator.serviceWorker.register('/firebase-messaging-sw.js')
	});
}

export async function listenForegroundMessages(callback: (payload: any) => void) {
	const mg = await getBrowserMessaging();
	if (!mg) return;

	const { onMessage } = await import('firebase/messaging');
	onMessage(mg, callback);
}
