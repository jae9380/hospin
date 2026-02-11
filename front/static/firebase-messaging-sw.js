importScripts('https://www.gstatic.com/firebasejs/9.23.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.23.0/firebase-messaging-compat.js');

// Firebase 초기화
firebase.initializeApp({
  apiKey: import.meta.env.FIREBASE_API_KEY_ID,
  authDomain: 'hospin-57573.firebaseapp.com',
  projectId: 'hospin-57573',
  storageBucket: 'hospin-57573.firebasestorage.app',
  messagingSenderId: '732747242912',
  appId: '1:732747242912:web:b02467bafaa64e95b858c5',
  measurementId: 'G-measurement-id'
});

// 백그라운드 메시지 수신
const messaging = firebase.messaging();

messaging.onBackgroundMessage(function(payload) {
  console.log('[FCM SW] Background message received:', payload);

  const notificationTitle = payload.notification?.title || 'Background Message';
  const notificationOptions = {
    body: payload.notification?.body,
    icon: '/firebase-logo.png', // 앱 아이콘
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});

// 알림 클릭 처리
self.addEventListener('notificationclick', function(event) {
  event.notification.close();

  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then(function(clientList) {
      if (clientList.length > 0) {
        clientList[0].focus();
      } else {
        clients.openWindow('http://localhost:5173');
      }
    })
  );
});